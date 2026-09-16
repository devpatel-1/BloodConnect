package com.dev.bloodconnect.repository

import com.dev.bloodconnect.data.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun currentUserId(): String? {
        return auth.currentUser?.uid
    }

    // --------------------------------------------------
    // SIGN UP
    // --------------------------------------------------

    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        phone: String,
        bloodGroup: String,
        city: String
    ): Result<Unit> {

        return try {

            val authResult =
                auth.createUserWithEmailAndPassword(
                    email,
                    password
                ).await()

            val uid =
                authResult.user?.uid
                    ?: throw Exception(
                        "Signup failed: no UID returned"
                    )

            val newUser =
                User(
                    uid = uid,
                    name = name,
                    phone = phone,
                    bloodGroup = bloodGroup,
                    city = city,
                    isAvailable = true,
                    lastDonationDate = null,
                    createdAt = Timestamp.now()
                )

            usersCollection
                .document(uid)
                .set(newUser)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // LOGIN
    // --------------------------------------------------

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            auth.signInWithEmailAndPassword(
                email,
                password
            ).await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // LOGOUT
    // --------------------------------------------------

    fun logout() {
        auth.signOut()
    }

    // --------------------------------------------------
    // GET CURRENT USER PROFILE
    // ALWAYS READ FROM FIREBASE SERVER
    // --------------------------------------------------

    suspend fun getCurrentUserProfile(): Result<User> {

        val uid =
            currentUserId()
                ?: return Result.failure(
                    Exception("Not logged in")
                )

        return try {

            val snapshot =
                usersCollection
                    .document(uid)
                    .get(Source.SERVER)
                    .await()

            val user =
                snapshot.toObject(User::class.java)
                    ?: throw Exception(
                        "Profile not found"
                    )

            Result.success(user)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // UPDATE AVAILABILITY
    // --------------------------------------------------

    suspend fun updateAvailability(
        isAvailable: Boolean
    ): Result<Unit> {

        val uid =
            currentUserId()
                ?: return Result.failure(
                    Exception("Not logged in")
                )

        return try {

            usersCollection
                .document(uid)
                .update(
                    "isAvailable",
                    isAvailable
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}