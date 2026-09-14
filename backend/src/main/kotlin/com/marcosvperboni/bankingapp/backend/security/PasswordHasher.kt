package com.marcosvperboni.bankingapp.backend.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * Salted SHA-256 hashing. Good enough for a portfolio demo backend with no real user data;
 * a production system would use bcrypt/argon2 via a vetted library instead.
 */
object PasswordHasher {
    private val random = SecureRandom()

    fun generateSalt(): String {
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(Base64.getDecoder().decode(salt))
        val hashed = digest.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hashed)
    }

    fun matches(password: String, salt: String, expectedHash: String): Boolean =
        hash(password, salt) == expectedHash
}
