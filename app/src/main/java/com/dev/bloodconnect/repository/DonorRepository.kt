package com.dev.bloodconnect.repository

import com.dev.bloodconnect.data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * Handles fetching donor data from Firestore.
 * Uses a real-time listener so the donor list updates live across devices
 * whenever anyone signs up or changes their availability.
 */
class DonorRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    /**
     * Listens for real-time changes to the donors list.
     * Call this once (e.g. in onCreate/onStart) and remove the listener
     * in onStop/onDestroy using the returned ListenerRegistration.
     */
    fun listenToDonors(onUpdate: (List<User>) -> Unit): ListenerRegistration {
        return usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                onUpdate(emptyList())
                return@addSnapshotListener
            }
            val donors = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
            onUpdate(donors)
        }
    }
}