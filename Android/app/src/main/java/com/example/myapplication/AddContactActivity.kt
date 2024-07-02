package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

/**
 * Activity for adding a new contact.
 */

class AddContactActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        // Initialize EditText fields for name, phone number, and email
        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextPhoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
        val editTextEmail = findViewById<EditText>(R.id.editTextemail) // Corrected variable name
        val btnSaveContact = findViewById<Button>(R.id.btnSaveContact)

        // Retrieve entered contact information from EditText fields
        btnSaveContact.setOnClickListener {
            val name = editTextName.text.toString()
            val phoneNumber = editTextPhoneNumber.text.toString()
            val email = editTextEmail.text.toString() // Retrieve email text

            // Create an intent to pass back the contact information to the calling activity
            val intent = Intent()
            intent.putExtra("name", name)
            intent.putExtra("phoneNumber", phoneNumber)
            intent.putExtra("email", email) // Add email to intent extras
            setResult(Activity.RESULT_OK, intent)
            finish()
        }


        // Initialize Button for closing the activity
        val btnClose = findViewById<Button>(R.id.btnClose)
        btnClose.setOnClickListener {
            // Close this activity without saving
            finish()
        }
    }

}
