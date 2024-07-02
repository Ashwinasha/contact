package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityContactDetailsBinding

class ContactDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactDetailsBinding
    private lateinit var contact: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var contacts: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)

        // Load contacts from SharedPreferences
        loadContactsFromPrefs()

        // Get the contact details from the intent
        contact = intent.getStringExtra("contact") ?: ""

        // Populate the UI with contact details
        if (contact.isNotEmpty()) {
            val parts = contact.split(" - ")
            val name = parts[0]
            val number = parts[1]
            val email = if (parts.size >= 3) parts[2] else "Not provided"

            binding.contactNameTextView.text = "$name"
            binding.contactNumberTextView.text = "Number: $number"
            binding.contactEmailTextView.text = "Email: $email"

            // Save the contact index for later use
            // Note: Implement this if needed
        } else {
            // If contact is not provided, finish the activity
            finish()
        }

        // Set click listener for the Close button
        binding.closeButton.setOnClickListener {
            // Create an intent to navigate back to the main activity
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            // Finish the current activity
            finish()
        }
    }

    // Function to load contacts from SharedPreferences
    private fun loadContactsFromPrefs() {
        val contactsSet = sharedPreferences.getStringSet(MainActivity.CONTACTS_KEY, HashSet<String>())
        contacts = ArrayList(contactsSet ?: emptySet())
    }
}
