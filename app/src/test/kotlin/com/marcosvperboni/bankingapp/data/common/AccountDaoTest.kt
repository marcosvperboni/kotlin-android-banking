package com.marcosvperboni.bankingapp.data.common

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.marcosvperboni.bankingapp.data.account.local.AccountEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AccountDaoTest {

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `get returns null when nothing was cached yet`() = runTest {
        assertNull(database.accountDao().get("acc-1"))
    }

    @Test
    fun `upsert then get returns the stored account`() = runTest {
        val entity = AccountEntity("acc-1", "Demo User", "100.00", "USD")

        database.accountDao().upsert(entity)

        assertEquals(entity, database.accountDao().get("acc-1"))
    }

    @Test
    fun `upsert replaces the previous value for the same id`() = runTest {
        database.accountDao().upsert(AccountEntity("acc-1", "Demo User", "100.00", "USD"))
        database.accountDao().upsert(AccountEntity("acc-1", "Demo User", "150.00", "USD"))

        assertEquals("150.00", database.accountDao().get("acc-1")?.balance)
    }

    @Test
    fun `observe emits every upsert for the same account`() = runTest {
        database.accountDao().observe("acc-1").test {
            assertNull(awaitItem())

            database.accountDao().upsert(AccountEntity("acc-1", "Demo User", "100.00", "USD"))
            assertEquals("100.00", awaitItem()?.balance)

            database.accountDao().upsert(AccountEntity("acc-1", "Demo User", "200.00", "USD"))
            assertEquals("200.00", awaitItem()?.balance)
        }
    }
}
