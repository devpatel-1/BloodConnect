package com.dev.bloodconnect.data

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val isAvailable: Boolean = true,
    val lastDonationDate: Timestamp? = null,
    val createdAt: Timestamp? = null
) {
    fun isActuallyAvailable(): Boolean {
        if (!isAvailable) return false
        val last = lastDonationDate ?: return true
        val ninetyDaysMillis = 90L * 24 * 60 * 60 * 1000
        val now = Timestamp.now().toDate().time
        return (now - last.toDate().time) >= ninetyDaysMillis
    }
}

val BLOOD_GROUPS = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")