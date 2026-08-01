package com.dev.bloodconnect.repository

import com.dev.bloodconnect.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Handles all Firebase Auth + Firestore user-profile operations.
 * Keeping this separate from the Activities/Fragments follows a simple
 * repository pattern, which makes the code easier to test and demo.
 */
class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    /** Currently logged-in user's UID, or null if nobody is signed in. */
    fun currentUserId(): String? = auth.currentUser?.uid

    /**
     * Signs up a new donor: creates the Firebase Auth account, then
     * writes their profile into Firestore under users/{uid}.
     */
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        phone: String,
        bloodGroup: String,
        city: String
    ): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Signup failed: no UID returned")

            val newUser = User(
                uid = uid,
                name = name,
                phone = phone,
                bloodGroup = bloodGroup,
                city = city,
                isAvailable = true,
                lastDonationDate = null,
                createdAt = com.google.firebase.Timestamp.now()
            )
            usersCollection.document(uid).set(newUser).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Logs in an existing donor with email/password. */
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    /** Fetches the logged-in user's own profile from Firestore. */
    suspend fun getCurrentUserProfile(): Result<User> {
        val uid = currentUserId() ?: return Result.failure(Exception("Not logged in"))
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
                ?: throw Exception("Profile not found")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    /** Updates just the availability flag for the current user. */
    suspend fun updateAvailability(isAvailable: Boolean): Result<Unit> {
        val uid = currentUserId() ?: return Result.failure(Exception("Not logged in"))
        return try {
            usersCollection.document(uid).update("isAvailable", isAvailable).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}