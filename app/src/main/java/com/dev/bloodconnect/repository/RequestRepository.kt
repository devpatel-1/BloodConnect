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
     * Checks whether the currently logged-in user
     * already has a pending request for this donor.
     */
    suspend fun hasPendingRequest(
        donorId: String
    ): Result<Boolean> {

        val requesterId = auth.currentUser?.uid
            ?: return Result.failure(
                Exception("Not logged in")
            )

        return try {

            val existing = requestsCollection
                .whereEqualTo("requesterId", requesterId)
                .whereEqualTo("donorId", donorId)
                .whereEqualTo("status", "pending")
                .get()
                .await()

            Result.success(!existing.isEmpty)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    /**
     * Sends a blood request from the current user to a donor.
     */
    suspend fun sendRequest(
        donorId: String,
        donorName: String,
        requesterName: String,
        bloodGroup: String
    ): Result<Unit> {

        val requesterId = auth.currentUser?.uid
            ?: return Result.failure(
                Exception("Not logged in")
            )

        return try {

            // Prevent sending request to yourself
            if (requesterId == donorId) {
                return Result.failure(
                    Exception(
                        "You cannot send a request to yourself"
                    )
                )
            }

            // Prevent duplicate pending requests
            val existing = requestsCollection
                .whereEqualTo("requesterId", requesterId)
                .whereEqualTo("donorId", donorId)
                .whereEqualTo("status", "pending")
                .get()
                .await()

            if (!existing.isEmpty) {
                return Result.failure(
                    Exception(
                        "You already have a pending request to this donor"
                    )
                )
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

            requestsCollection
                .add(newRequest)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    /**
     * Real-time listener for requests sent by the current user.
     */
    fun listenToMyRequests(
        onUpdate: (List<Request>) -> Unit
    ): ListenerRegistration {

        val myUid = auth.currentUser?.uid

        if (myUid == null) {

            onUpdate(emptyList())

            return requestsCollection
                .document("dummy")
                .addSnapshotListener { _, _ -> }
        }

        return requestsCollection
            .whereEqualTo("requesterId", myUid)
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val requests = snapshot.documents
                    .mapNotNull { document ->

                        document
                            .toObject(Request::class.java)
                            ?.copy(id = document.id)
                    }
                    .sortedByDescending { request ->
                        request.createdAt
                    }

                onUpdate(requests)
            }
    }

    /**
     * Real-time listener for requests received by the current user
     * as a donor.
     */
    fun listenToIncomingRequests(
        onUpdate: (List<Request>) -> Unit
    ): ListenerRegistration {

        val myUid = auth.currentUser?.uid

        if (myUid == null) {

            onUpdate(emptyList())

            return requestsCollection
                .document("dummy")
                .addSnapshotListener { _, _ -> }
        }

        return requestsCollection
            .whereEqualTo("donorId", myUid)
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val requests = snapshot.documents
                    .mapNotNull { document ->

                        document
                            .toObject(Request::class.java)
                            ?.copy(id = document.id)
                    }
                    .sortedByDescending { request ->
                        request.createdAt
                    }

                onUpdate(requests)
            }
    }

    /**
     * Accepts or declines an incoming request.
     */
    suspend fun updateRequestStatus(
        requestId: String,
        newStatus: String
    ): Result<Unit> {

        val myUid = auth.currentUser?.uid
            ?: return Result.failure(
                Exception("Not logged in")
            )

        if (newStatus != "accepted" &&
            newStatus != "declined"
        ) {
            return Result.failure(
                Exception("Invalid request status")
            )
        }

        return try {

            val requestDocument =
                requestsCollection
                    .document(requestId)
                    .get()
                    .await()

            if (!requestDocument.exists()) {
                return Result.failure(
                    Exception("Request not found")
                )
            }

            val request =
                requestDocument
                    .toObject(Request::class.java)
                    ?: return Result.failure(
                        Exception("Invalid request data")
                    )

            // Make sure only the donor can respond
            if (request.donorId != myUid) {
                return Result.failure(
                    Exception(
                        "You are not allowed to update this request"
                    )
                )
            }

            if (request.status != "pending") {
                return Result.failure(
                    Exception(
                        "This request has already been processed"
                    )
                )
            }

            requestsCollection
                .document(requestId)
                .update(
                    "status",
                    newStatus
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}