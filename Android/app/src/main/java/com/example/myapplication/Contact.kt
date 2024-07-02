package com.example.myapplication

/**
 * Represents a contact entity in the application.
 * @property name The name of the contact.
 * @property phoneNumber The phone number of the contact.
 * @property email The email address of the contact.
 * the primary constructor takes three parameters: name, phoneNumber, and email.
 * This data class serves as a blueprint for creating contact objects with specified properties.
 */

data class Contact(
    val name: String,
    val phoneNumber: Int,
    val email: String,
)
