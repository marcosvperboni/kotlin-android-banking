package com.marcosvperboni.bankingapp.backend.store

import com.marcosvperboni.bankingapp.backend.domain.Account
import com.marcosvperboni.bankingapp.backend.domain.IdGenerator
import com.marcosvperboni.bankingapp.backend.domain.Notification
import com.marcosvperboni.bankingapp.backend.domain.Transaction
import com.marcosvperboni.bankingapp.backend.domain.TransactionDirection
import com.marcosvperboni.bankingapp.backend.domain.Transfer
import com.marcosvperboni.bankingapp.backend.domain.User
import com.marcosvperboni.bankingapp.backend.security.PasswordHasher
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap

/**
 * Process-local storage for the demo backend. No Postgres/Redis needed for a portfolio app;
 * state resets whenever the process restarts.
 */
object InMemoryStore {
    val usersByEmail = ConcurrentHashMap<String, User>()
    val usersById = ConcurrentHashMap<String, User>()
    val accounts = ConcurrentHashMap<String, Account>()
    val transactions = ConcurrentHashMap<String, MutableList<Transaction>>()
    val transfers = ConcurrentHashMap<String, Transfer>()
    val notifications = ConcurrentHashMap<String, MutableList<Notification>>()

    init {
        seedDemoUser()
    }

    fun registerUser(email: String, password: String, fullName: String): User {
        val salt = PasswordHasher.generateSalt()
        val user = User(
            id = IdGenerator.next("usr"),
            email = email,
            fullName = fullName,
            passwordHash = PasswordHasher.hash(password, salt),
            passwordSalt = salt,
        )
        usersByEmail[email] = user
        usersById[user.id] = user
        accounts[user.id] = Account(id = user.id, ownerName = fullName, balance = BigDecimal("500.00"))
        transactions[user.id] = mutableListOf()
        notifications[user.id] = mutableListOf(
            Notification(IdGenerator.next("ntf"), user.id, "Welcome", "Your account was created.", false, System.currentTimeMillis()),
        )
        return user
    }

    private fun seedDemoUser() {
        val demo = registerUser(email = "demo@bank.com", password = "password123", fullName = "Demo User")
        val account = accounts.getValue(demo.id)
        account.balance = BigDecimal("2500.00")
        val history = transactions.getValue(demo.id)
        history += Transaction(IdGenerator.next("txn"), demo.id, "Initial deposit", BigDecimal("2500.00"), TransactionDirection.CREDIT, System.currentTimeMillis() - 86_400_000)
        history += Transaction(IdGenerator.next("txn"), demo.id, "Coffee shop", BigDecimal("12.50"), TransactionDirection.DEBIT, System.currentTimeMillis() - 3_600_000)
    }

    fun recordTransaction(accountId: String, description: String, amount: BigDecimal, direction: TransactionDirection) {
        transactions.getOrPut(accountId) { mutableListOf() } += Transaction(
            id = IdGenerator.next("txn"),
            accountId = accountId,
            description = description,
            amount = amount,
            direction = direction,
            timestamp = System.currentTimeMillis(),
        )
    }

    fun addNotification(accountId: String, title: String, message: String) {
        notifications.getOrPut(accountId) { mutableListOf() } += Notification(
            id = IdGenerator.next("ntf"),
            accountId = accountId,
            title = title,
            message = message,
            read = false,
            createdAt = System.currentTimeMillis(),
        )
    }
}
