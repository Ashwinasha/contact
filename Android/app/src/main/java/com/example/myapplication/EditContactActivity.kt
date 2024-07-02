package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity for editing a contact's information.
 */
class EditContactActivity : AppCompatActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var editTextEmail: EditText // Corrected variable name
    private lateinit var btnSaveContact: Button
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)

        editTextName = findViewById(R.id.editTextName)
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        editTextEmail = findViewById(R.id.editTextemail)
        btnSaveContact = findViewById(R.id.btnSaveContact) // Initialize btnSaveContact

        // Get the contact details and position from the intent
        val contact = intent.getStringExtra("contact")
        position = intent.getIntExtra("position", -1)

        // Populate the EditText fields with the contact details
        if (!contact.isNullOrEmpty()) {
            val parts = contact.split(" - ")
            if (parts.size == 3) {
                editTextName.setText(parts[0])
                editTextPhoneNumber.setText(parts[1])
                editTextEmail.setText(parts[2])
            }
        }

        // Save the edited contact when the save button is clicked
        btnSaveContact.setOnClickListener {
            val name = editTextName.text.toString()
            val phoneNumber = editTextPhoneNumber.text.toString()
            val email = editTextEmail.text.toString()
            val editedContact = "$name - $phoneNumber - $email"

            val resultIntent = Intent().apply {
                putExtra("editedContact", editedContact)
                putExtra("position", position)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // Close the activity when the close button is clicked
        val btnClose = findViewById<Button>(R.id.btnClose)
        btnClose.setOnClickListener {
            // Close this activity without saving
            finish()
        }
    }

}
