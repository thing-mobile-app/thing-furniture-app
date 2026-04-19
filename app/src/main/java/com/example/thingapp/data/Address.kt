package com.example.thingapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a user's shipping or billing address.
 * Implements [Parcelable] to allow seamless data transfer of Address objects between fragments.
 *
 * @property addressTitle The title of the address (e.g., "Home", "Work").
 * @property fullName The full name of the person receiving the delivery.
 * @property street The street address, including building and apartment numbers.
 * @property phone The contact phone number for this address.
 * @property city The city where the address is located.
 * @property state The state, province, or region.
 */
@Parcelize
data class Address(
    val addressTitle: String,
    val fullName: String,
    val street: String,
    val phone: String,
    val city: String,
    val state: String
): Parcelable {

    /**
     * An empty (no-argument) constructor is strictly required by Firebase Firestore.
     * Without this, Firestore cannot deserialize (read and map) the document fields back into this object.
     */
    constructor() : this("", "", "", "", "", "")
}