package com.marcosvperboni.bankingapp.data.account

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.marcosvperboni.bankingapp.core.error.AppError
import com.marcosvperboni.bankingapp.data.common.AppDatabase
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Retrofit
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
class AccountRepositoryImplTest {

    private lateinit var server: MockWebServer
    private lateinit var database: AppDatabase
    private lateinit var repository: AccountRepositoryImpl
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        repository = AccountRepositoryImpl(
            api = retrofit.create(AccountApiService::class.java),
            accountDao = database.accountDao(),
            transactionDao = database.transactionDao(),
            json = json,
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
        database.close()
    }

    @Test
    fun `getAccount maps a successful response and caches it`() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"id":"acc-1","ownerName":"Demo User","balance":"250.00","currency":"USD"}""",
            ),
        )

        val result = repository.getAccount("acc-1")

        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("250.00"), result.getOrNull()?.balance)
        assertEquals(BigDecimal("250.00"), database.accountDao().get("acc-1")?.let { BigDecimal(it.balance) })
    }

    @Test
    fun `getAccount maps a 404 response to AppError NotFound`() = runTest {
        server.enqueue(MockResponse().setResponseCode(404).setBody("""{"message":"Account not found"}"""))

        val result = repository.getAccount("missing")

        assertTrue(result.exceptionOrNull() is AppError.NotFound)
    }

    @Test
    fun `getAccount maps a 401 response to AppError Unauthorized`() = runTest {
        server.enqueue(MockResponse().setResponseCode(401).setBody("""{"message":"Invalid token"}"""))

        val result = repository.getAccount("acc-1")

        assertTrue(result.exceptionOrNull() is AppError.Unauthorized)
    }

    @Test
    fun `getAccount maps a 500 response to AppError Server`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500).setBody("""{"message":"boom"}"""))

        val result = repository.getAccount("acc-1")

        assertTrue(result.exceptionOrNull() is AppError.Server)
    }

    @Test
    fun `getStatement falls back to the cached first page when the network is unreachable`() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"items":[{"id":"txn-1","accountId":"acc-1","description":"Coffee","amount":"5.00","direction":"DEBIT","timestamp":1}],"page":0,"size":20,"hasMore":false}""",
            ),
        )
        repository.getStatement("acc-1", 0, 20)
        server.shutdown()

        val result = repository.getStatement("acc-1", 0, 20)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.items?.size)
    }
}
