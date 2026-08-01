package com.dev.bloodconnect.data

import com.google.firebase.Timestamp

/**
 * Represents a blood request sent from one user (requester) to a donor.
 * Stored in Firestore under the "requests" collection.
 */
data class Request(
    val id: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val donorId: String = "",
    val donorName: String = "",
    val bloodGroup: String = "",
    val status: String = "pending",   // "pending", "accepted", "declined"
    val createdAt: Timestamp? = null
)

