package com.dev.bloodconnect.repository

import com.dev.bloodconnect.data.Request
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class RequestRepository {

    private val db = FirebaseFirestore.getInstance()
    private val requestsCollection = db.collection("requests")
    private val auth = FirebaseAuth.getInstance()

    /**
     * Sends a blood request from the current logged-in user to the given donor.
     */
    suspend fun sendRequest(
        donorId: String,
        donorName: String,
        requesterName: String,
        bloodGroup: String
    ): Result<Unit> {
        val requesterId = auth.currentUser?.uid
            ?: return Result.failure(Exception("Not logged in"))

        return try {
            // Check if a pending request already exists to this donor
            val existing = requestsCollection
                .whereEqualTo("requesterId", requesterId)
                .whereEqualTo("donorId", donorId)
                .whereEqualTo("status", "pending")
                .get()
                .await()

            if (!existing.isEmpty) {
                return Result.failure(Exception("You already have a pending request to this donor"))
            }

            val newRequest = Request(
                requesterId = requesterId,
                requesterName = requesterName,
                donorId = donorId,
                donorName = donorName,
                bloodGroup = bloodGroup,
                status = "pending",
                createdAt = Timestamp.now()
            )
            requestsCollection.add(newRequest).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Real-time listener for requests sent BY the current user.
     */
    fun listenToMyRequests(onUpdate: (List<Request>) -> Unit): ListenerRegistration {
        val myUid = auth.currentUser?.uid
        if (myUid == null) {
            onUpdate(emptyList())
            return requestsCollection.document("dummy").addSnapshotListener { _, _ -> }
        }
        return requestsCollection
            .whereEqualTo("requesterId", myUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }
                val requests = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Request::class.java)?.copy(id = doc.id)
                }
                onUpdate(requests)
            }
    }
}