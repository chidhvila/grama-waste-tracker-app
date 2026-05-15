package com.example.gramawastetracker

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportActivity : ComponentActivity() {

    private lateinit var issueText: EditText
    private lateinit var previewImage: ImageView
    private lateinit var locationText: TextView

    private var selectedImageUri: Uri? = null
    private var isPhotoAdded = false
    private var isLocationPinned = false
    private var savedLocationText = "Location not available"

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap

                if (bitmap != null) {
                    previewImage.setImageBitmap(bitmap)
                    isPhotoAdded = true
                    getLiveLocation()
                    Toast.makeText(this, "Photo captured successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Photo not received", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Camera cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                previewImage.setImageURI(uri)
                isPhotoAdded = true
                getLiveLocation()
                Toast.makeText(this, "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
            }
        }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineGranted || coarseGranted) {
                getLiveLocation()
            } else {
                isLocationPinned = false
                locationText.text = "📍 Location permission denied"
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        issueText = findViewById(R.id.issueText)
        previewImage = findViewById(R.id.previewImage)
        locationText = findViewById(R.id.locationText)

        findViewById<TextView>(R.id.backBtnReport).setOnClickListener {
            finish()
        }

        previewImage.setOnClickListener {
            checkCameraPermission()
        }

        findViewById<Button>(R.id.btnCapturePhoto).setOnClickListener {
            checkCameraPermission()
        }

        findViewById<Button>(R.id.btnExamplePhoto).setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        findViewById<Button>(R.id.btnSubmitReport).setOnClickListener {
            submitReport()
        }

        setupTag(R.id.tagPlastic, "Plastic waste")
        setupTag(R.id.tagLarge, "Large dump")
        setupTag(R.id.tagSmell, "Foul smell")
        setupTag(R.id.tagBlocking, "Blocking path")
        setupTag(R.id.tagLandmark, "Near landmark")
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            cameraLauncher.launch(cameraIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "Camera app not found. Please use Upload Photo.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getLiveLocation() {
        locationText.text = "📍 Getting live location..."

        val finePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val coarsePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (finePermission == PackageManager.PERMISSION_GRANTED ||
            coarsePermission == PackageManager.PERMISSION_GRANTED
        ) {
            fetchCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun fetchCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->

                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val address = getAddress(latitude, longitude)

                    savedLocationText = if (address.isNotEmpty()) {
                        "$address\nLat: $latitude\nLng: $longitude"
                    } else {
                        "Lat: $latitude\nLng: $longitude"
                    }

                    isLocationPinned = true

                    locationText.text = "📍 Live location pinned:\n$savedLocationText"
                    locationText.setTextColor(android.graphics.Color.parseColor("#00B85C"))

                    Toast.makeText(this, "Live location pinned", Toast.LENGTH_SHORT).show()
                } else {
                    isLocationPinned = false
                    savedLocationText = "Location not found"
                    locationText.text = "📍 Location not found. Turn on GPS and try again."
                }

            }.addOnFailureListener { e ->
                isLocationPinned = false
                savedLocationText = "Location failed"
                locationText.text = "📍 Failed to get location: ${e.message}"
            }

        } catch (e: SecurityException) {
            isLocationPinned = false
            savedLocationText = "Location permission error"
            locationText.text = "📍 Location permission error"
        }
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]

                listOfNotNull(
                    address.featureName,
                    address.subLocality,
                    address.locality,
                    address.adminArea,
                    address.countryName
                ).filter { it.isNotBlank() }
                    .distinct()
                    .joinToString(", ")
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun setupTag(buttonId: Int, tagText: String) {
        findViewById<Button>(buttonId).setOnClickListener {
            val currentText = issueText.text.toString()
            issueText.setText("$currentText #$tagText ")
            issueText.setSelection(issueText.text.length)
        }
    }

    private fun submitReport() {
        val text = issueText.text.toString().trim()

        if (!isPhotoAdded) {
            Toast.makeText(this, "Please capture or upload photo", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isLocationPinned) {
            Toast.makeText(this, "Please wait until live location is pinned", Toast.LENGTH_SHORT).show()
            return
        }

        if (text.isEmpty()) {
            Toast.makeText(this, "Please describe the issue", Toast.LENGTH_SHORT).show()
            return
        }

        saveReport(text)

        Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun saveReport(reportText: String) {
        val prefs = getSharedPreferences("reports", MODE_PRIVATE)
        val count = prefs.getInt("count", 0)
        val newCount = count + 1

        val time = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

        prefs.edit()
            .putString("report_$newCount", reportText)
            .putString("report_time_$newCount", time)
            .putString("report_location_$newCount", savedLocationText)
            .putBoolean("report_photo_$newCount", isPhotoAdded)
            .putInt("count", newCount)
            .apply()
    }
}