package com.dev.bloodconnect.repository

import com.dev.bloodconnect.data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges

/**
 * Handles fetching donor data from Firestore.
 *
 * Uses a real-time listener so the donor list updates whenever
 * donor information changes in Firestore.
 */
class DonorRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun listenToDonors(
        onUpdate: (List<User>) -> Unit
    ): ListenerRegistration {

        return usersCollection
            .addSnapshotListener(
                MetadataChanges.INCLUDE
            ) { snapshot, error ->

                if (error != null || snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val donors =
                    snapshot.documents.mapNotNull { document ->
                        document.toObject(User::class.java)
                    }

                onUpdate(donors)
            }
    }
}