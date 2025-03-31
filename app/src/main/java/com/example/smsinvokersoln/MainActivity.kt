package com.example.smsinvokersoln

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private lateinit var phoneNumberEditText: EditText
    private lateinit var messageEditText: EditText

    // Permission launcher for reading SMS
    private val requestReadSmsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, open SMS app
            Toast.makeText(this, "SMS read permission granted", Toast.LENGTH_SHORT).show()
            openSmsApp()
        } else {
            // Permission denied
            Toast.makeText(this, "SMS read permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize EditText fields
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        messageEditText = findViewById(R.id.messageEditText)

        // Set up the SMS button
        findViewById<Button>(R.id.button).setOnClickListener {
            checkReadSmsPermission()
        }
    }

    // Function to open the SMS app, pre-filling fields if provided
    private fun openSmsApp() {
        val phoneNumber = phoneNumberEditText.text.toString().trim()
        val message = messageEditText.text.toString().trim()

        val intent = Intent(Intent.ACTION_VIEW)

        // If we have values, include them in the SMS intent
        if (phoneNumber.isNotEmpty()) {
            // If we have both phone number and message
            if (message.isNotEmpty()) {
                intent.data = Uri.parse("sms:$phoneNumber")
                intent.putExtra("sms_body", message)
            } else {
                // Just phone number, no message
                intent.data = Uri.parse("sms:$phoneNumber")
            }
        } else {
            // No specific recipient
            intent.data = Uri.parse("sms:")

            // If we only have a message but no recipient
            if (message.isNotEmpty()) {
                intent.putExtra("sms_body", message)
            }
        }

        startActivity(intent)
    }

    // Function to check READ_SMS permission
    private fun checkReadSmsPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                Toast.makeText(this, "SMS read permission already granted", Toast.LENGTH_SHORT).show()
                openSmsApp()
            }
            else -> {
                // Request permission
                requestReadSmsPermissionLauncher.launch(Manifest.permission.READ_SMS)
            }
        }
    }
}