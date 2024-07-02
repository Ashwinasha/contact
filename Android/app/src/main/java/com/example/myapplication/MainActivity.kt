package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * The main activity of the application responsible for displaying the list of contacts,
 * adding new contacts, editing existing ones, and deleting contacts.
 */

class MainActivity : AppCompatActivity() {


    private lateinit var listView: ListView
    private lateinit var adapter: ContactsAdapter
    private var contacts = ArrayList<String>()
    private var longPressedPosition: Int = -1

    companion object {
        const val ADD_CONTACT_REQUEST_CODE = 100
        const val PREFS_NAME = "MyContactsPrefs"
        const val CONTACTS_KEY = "contacts"
        const val EDIT_CONTACT_REQUEST_CODE = 101
        const val DELETE_CONTACT_REQUEST_CODE = 102
    }


    // SharedPreferences for storing contacts data
    private lateinit var sharedPreferences: SharedPreferences


    /**
     * Called when the activity is starting. This is where most initialization
     * should go: calling setContentView(int) to inflate the activity's UI,
     * initializing views, and retrieving data.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). Note: Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.contactList)
        adapter = ContactsAdapter(this, contacts)
        listView.adapter = adapter

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadContactsFromPrefs()

        // Sort contacts alphabetically
        contacts.sort()

        /**
         * Sets a click listener on the FloatingActionButton to handle adding new contacts.
         * When the FloatingActionButton is clicked, it launches the AddContactActivity
         * to allow the user to add a new contact.
         */

        val fabAddContact = findViewById<FloatingActionButton>(R.id.fabAddContact)
        fabAddContact.setOnClickListener {
            startActivityForResult(Intent(this, AddContactActivity::class.java), ADD_CONTACT_REQUEST_CODE)
        }

        listView.setOnItemLongClickListener { _, view, position, _ ->
            longPressedPosition = position
            false // Do not consume the long click event
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val contact = contacts[position]
            val intent = Intent(this@MainActivity, ContactDetailsActivity::class.java).apply {
                putExtra("contact", contact)
                putExtra("contactIndex", position)
            }
            startActivity(intent)
        }




    }

    /**
     * Opens the EditContactActivity to edit the contact at the specified position.
     * @param contact The contact string.
     * @param position The position of the contact in the list.
     */

    private fun editContact(contact: String, position: Int) {
        val intent = Intent(this, EditContactActivity::class.java).apply {
            putExtra("contact", contact)
            putExtra("position", position)
        }
        startActivityForResult(intent, EDIT_CONTACT_REQUEST_CODE)
    }

    /**
     * Deletes the contact at the specified position.
     * @param position The position of the contact in the list.
     */

    private fun deleteContact(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Confirm Deletion")
        alertDialogBuilder.setMessage("Are you sure you want to delete this contact?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // User confirmed deletion
            contacts.removeAt(position)
            adapter.notifyDataSetChanged()
            saveContactsToPrefs()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            // User canceled deletion, do nothing
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    /**
     * Saves the list of contacts to SharedPreferences.
     */
    private fun saveContactsToPrefs() {
        val editor = sharedPreferences.edit()
        val contactsSet = HashSet<String>(contacts)
        editor.putStringSet(CONTACTS_KEY, contactsSet)
        editor.apply()
    }


    /**
     * Loads the list of contacts from SharedPreferences.
     */
    private fun loadContactsFromPrefs() {
        val contactsSet = sharedPreferences.getStringSet(CONTACTS_KEY, HashSet<String>())
        contacts.clear()
        contacts.addAll(contactsSet ?: emptySet())
        adapter.notifyDataSetChanged()
    }

    /**
     * Called when an activity launched by this activity exits, giving the result.
     * This method is responsible for handling the result of adding or editing a contact.
     * @param requestCode The request code that was used to launch the activity.
     * @param resultCode The result code returned by the child activity.
     * @param data The data returned from the child activity.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ADD_CONTACT_REQUEST_CODE -> handleAddContactResult(data)
                EDIT_CONTACT_REQUEST_CODE -> handleEditContactResult(data)
            }
        }
    }

    /**
     * Handles the result of adding a new contact.
     * @param data The Intent containing the result data.
     */
    private fun handleAddContactResult(data: Intent?) {
        val name = data?.getStringExtra("name")
        val phoneNumber = data?.getStringExtra("phoneNumber")
        val email = data?.getStringExtra("email") ?: "" // Retrieve email data

        if (!name.isNullOrEmpty() || !phoneNumber.isNullOrEmpty()) {
            val contact = "$name - $phoneNumber - $email" // Include email in the contact string
            contacts.add(contact)
            adapter.notifyDataSetChanged()
            saveContactsToPrefs()
        }
    }

    /**
     * Handles the result of editing an existing contact.
     * @param data The Intent containing the result data.
     */

    private fun handleEditContactResult(data: Intent?) {
        val editedContact = data?.getStringExtra("editedContact")
        val position = data?.getIntExtra("position", -1)
        if (!editedContact.isNullOrEmpty() && position != -1) {
            contacts[position!!] = editedContact
            adapter.notifyDataSetChanged()
            saveContactsToPrefs()
        }
    }

    /**
     * Custom ArrayAdapter for displaying a list of contacts.
     *
     * @property context The context in which the adapter is being used.
     * @property contacts The list of contacts to be displayed.
     */

    inner class ContactsAdapter(context: Context, contacts: ArrayList<String>) :
        ArrayAdapter<String>(context, R.layout.list_item_contact, contacts) {

        /**
         * Returns a view for displaying the data at the specified position in the data set.
         *
         * @param position The position of the item within the adapter's data set.
         * @param convertView The old view to reuse, if possible.
         * @param parent The parent that this view will eventually be attached to.
         * @return A View corresponding to the data at the specified position.
         */

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            val viewHolder: ViewHolder

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false)
                viewHolder = ViewHolder()
                viewHolder.iconImageView = view.findViewById(R.id.icon)
                viewHolder.contactNameTextView = view.findViewById(R.id.contactNameTextView)
                view.tag = viewHolder
            } else {
                viewHolder = view.tag as ViewHolder
            }

            // Extract contact name, number, and email
            val contact = getItem(position)
            val parts = contact?.split(" - ")
            val name = parts?.get(0)
            val number = parts?.get(1)
            val email = parts?.get(2) ?: ""

            val truncatedName = name?.take(15) ?: ""

            // Set truncated contact name to the TextView
            viewHolder.contactNameTextView.text = truncatedName

            // Set contact name and number


            // Set click listener on listView to view contact details
            view?.setOnClickListener {
                val intent = Intent(context, ContactDetailsActivity::class.java).apply {
                    putExtra("contact", contact)
                }
                context.startActivity(intent)
            }

            // Set click listener for the edit icon
            viewHolder.iconImageView.setOnClickListener {
                editContact(contact!!, position)
            }

            // Set long click listener to show popup menu
            view?.setOnLongClickListener {
                showPopupMenu(view!!, position)
                true // Indicate that we've consumed the long click event
            }



            return view!!
        }


        /**
         * ViewHolder pattern to cache views for better ListView performance.
         * It holds references to the views of each list item layout.
         */
        private inner class ViewHolder {
            lateinit var iconImageView: ImageView
            lateinit var contactNameTextView: TextView
        }

        private fun showPopupMenu(anchorView: View, position: Int) {
            val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu, null)
            val popupMenu = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

            val yOffset = context.resources.getDimensionPixelOffset(R.dimen.popup_menu_y_offset)
            val xOffset = context.resources.getDimensionPixelOffset(R.dimen.popup_menu_x_offset)

            // Find the edit and delete icons in the popup view
            val editIcon = popupView.findViewById<ImageView>(R.id.edit_icon)
            val deleteIcon = popupView.findViewById<ImageView>(R.id.delete_icon)

            // Set click listeners for edit and delete icons
            editIcon.setOnClickListener {
                editContact(getItem(position)!!, position)
                popupMenu.dismiss() // Dismiss the popup after handling the action
            }

            deleteIcon.setOnClickListener {
                deleteContact(position)
                popupMenu.dismiss() // Dismiss the popup after handling the action
            }

            // Calculate the coordinates to position the popup below the clicked item
            val itemLocation = IntArray(2)
            anchorView.getLocationOnScreen(itemLocation)
            val x = itemLocation[0] + anchorView.width / 2 - popupView.width / 2 - xOffset // Center horizontally
            val y = itemLocation[1] + anchorView.height / 2 - popupView.height / 2 - yOffset

            // Show the popup menu below the clicked item
            popupMenu.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y)
        }
    }

}
