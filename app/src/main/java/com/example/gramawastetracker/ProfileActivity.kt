package com.example.gramawastetracker

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class ProfileActivity : ComponentActivity() {

    private var currentLang = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        currentLang = getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en") ?: "en"

        setupClicks()
        applyLanguage()
        loadReports()
    }

    override fun onResume() {
        super.onResume()

        currentLang = getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en") ?: "en"

        applyLanguage()
        loadReports()
    }

    private fun setupClicks() {

        findViewById<Button>(R.id.btnLanguage).setOnClickListener {
            currentLang = if (currentLang == "en") "kn" else "en"

            getSharedPreferences("settings", MODE_PRIVATE)
                .edit()
                .putString("lang", currentLang)
                .apply()

            applyLanguage()
            loadReports()

            Toast.makeText(
                this,
                if (currentLang == "kn") "Kannada enabled" else "English enabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<LinearLayout>(R.id.visionCard).setOnClickListener {
            startActivity(Intent(this, VisionActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.logoutCard).setOnClickListener {
            logoutUser()
        }

        findViewById<TextView>(R.id.navMap).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.navReport).setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        findViewById<TextView>(R.id.navGuide).setOnClickListener {
            startActivity(Intent(this, GuideActivity::class.java))
        }

        findViewById<TextView>(R.id.navProfile).setOnClickListener {
            Toast.makeText(
                this,
                if (currentLang == "kn") {
                    "ನೀವು ಈಗಾಗಲೇ ಪ್ರೊಫೈಲ್ ಪುಟದಲ್ಲಿದ್ದೀರಿ"
                } else {
                    "You are already on Profile"
                },
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun logoutUser() {
        getSharedPreferences("user_profile", MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadReports() {
        val container = findViewById<LinearLayout>(R.id.myReportsContainer)
        container.removeAllViews()

        val prefs = getSharedPreferences("reports", MODE_PRIVATE)
        val count = prefs.getInt("count", 0)

        if (count == 0) {
            val emptyText = TextView(this)

            emptyText.text = if (currentLang == "kn") {
                "ಇನ್ನೂ ಯಾವುದೇ ವರದಿ ಇಲ್ಲ"
            } else {
                "NO REPORTS SUBMITTED YET"
            }

            emptyText.setTextColor(Color.parseColor("#8FA0BD"))
            emptyText.textSize = 14f
            emptyText.gravity = Gravity.CENTER
            emptyText.setTypeface(null, Typeface.BOLD)

            val emptyParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                120
            )

            emptyText.layoutParams = emptyParams
            container.addView(emptyText)

            return
        }

        for (i in count downTo 1) {
            val report = prefs.getString("report_$i", "") ?: ""
            val time = prefs.getString("report_time_$i", "") ?: ""
            val location = prefs.getString("report_location_$i", "") ?: ""

            if (report.isEmpty()) {
                continue
            }

            val reportCard = LinearLayout(this)
            reportCard.orientation = LinearLayout.VERTICAL
            reportCard.setPadding(24, 20, 24, 20)
            reportCard.setBackgroundResource(R.drawable.report_card_bg)

            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            cardParams.setMargins(0, 0, 0, 18)
            reportCard.layoutParams = cardParams

            val reportText = TextView(this)

            reportText.text = if (currentLang == "kn") {
                "📍 ವರದಿ #$i\n$report\nಸ್ಥಳ: $location\nಸಮಯ: $time"
            } else {
                "📍 Report #$i\n$report\nLocation: $location\nTime: $time"
            }

            reportText.setTextColor(Color.parseColor("#061736"))
            reportText.textSize = 14f
            reportText.setTypeface(null, Typeface.BOLD)

            val deleteButton = Button(this)
            deleteButton.text = "DELETE"
            deleteButton.setTextColor(Color.WHITE)
            deleteButton.textSize = 12f
            deleteButton.setTypeface(null, Typeface.BOLD)
            deleteButton.setBackgroundColor(Color.parseColor("#E53935"))

            val deleteParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                52
            )
            deleteParams.setMargins(0, 16, 0, 0)
            deleteButton.layoutParams = deleteParams

            deleteButton.setOnClickListener {
                deleteReport(i)
            }

            reportCard.addView(reportText)
            reportCard.addView(deleteButton)

            container.addView(reportCard)
        }
    }

    private fun deleteReport(reportNumber: Int) {
        val prefs = getSharedPreferences("reports", MODE_PRIVATE)

        prefs.edit()
            .remove("report_$reportNumber")
            .remove("report_time_$reportNumber")
            .remove("report_location_$reportNumber")
            .remove("report_photo_$reportNumber")
            .apply()

        Toast.makeText(this, "Report deleted", Toast.LENGTH_SHORT).show()

        loadReports()
    }

    private fun applyLanguage() {
        if (currentLang == "kn") {
            findViewById<TextView>(R.id.profileTitle).text = "ನನ್ನ ಖಾತೆ"
            findViewById<TextView>(R.id.profileRole).text = "ನಾಗರಿಕ"
            findViewById<TextView>(R.id.profileName).text = "ಗ್ರಾಮ ಸದಸ್ಯ"
            findViewById<TextView>(R.id.myReportsTitle).text = "ನನ್ನ ವರದಿಗಳು"
            findViewById<TextView>(R.id.liveSyncText).text = "ಲೈವ್"
            findViewById<TextView>(R.id.visionText).text = "ನಮ್ಮ ದೃಷ್ಟಿ ಮತ್ತು ಗುರಿಗಳು"
            findViewById<TextView>(R.id.switchLanguageText).text = "ಭಾಷೆ\nಬದಲಿಸಿ"
            findViewById<Button>(R.id.btnLanguage).text = "English"
            findViewById<TextView>(R.id.logoutText).text = "ಲಾಗ್ ಔಟ್"

            findViewById<TextView>(R.id.navMap).text = "🗺️\nನಕ್ಷೆ"
            findViewById<TextView>(R.id.navReport).text = "📷\nವರದಿ"
            findViewById<TextView>(R.id.navGuide).text = "📖\nಮಾರ್ಗದರ್ಶಿ"
            findViewById<TextView>(R.id.navProfile).text = "👤\nಪ್ರೊಫೈಲ್"
        } else {
            findViewById<TextView>(R.id.profileTitle).text = "My Account"
            findViewById<TextView>(R.id.profileRole).text = "CITIZEN"
            findViewById<TextView>(R.id.profileName).text = "Grama Member"
            findViewById<TextView>(R.id.myReportsTitle).text = "MY REPORTS"
            findViewById<TextView>(R.id.liveSyncText).text = "LIVESYNC"
            findViewById<TextView>(R.id.visionText).text = "OUR VISION & GOALS"
            findViewById<TextView>(R.id.switchLanguageText).text = "SWITCH\nLANGUAGE"
            findViewById<Button>(R.id.btnLanguage).text = "ಕನ್ನಡ"
            findViewById<TextView>(R.id.logoutText).text = "LOGOUT"

            findViewById<TextView>(R.id.navMap).text = "🗺️\nMAP"
            findViewById<TextView>(R.id.navReport).text = "📷\nREPORT"
            findViewById<TextView>(R.id.navGuide).text = "📖\nGUIDE"
            findViewById<TextView>(R.id.navProfile).text = "👤\nPROFILE"
        }
    }
}