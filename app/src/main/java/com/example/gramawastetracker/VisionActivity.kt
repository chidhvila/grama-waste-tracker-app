package com.example.gramawastetracker

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class VisionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vision)

        findViewById<TextView>(R.id.backBtnVision).setOnClickListener {
            finish()
        }

        applyLanguage()
    }

    private fun applyLanguage() {
        val lang = getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en")

        if (lang == "kn") {
            findViewById<TextView>(R.id.visionTitle).text = "ನಮ್ಮ ದೃಷ್ಟಿ"
            findViewById<TextView>(R.id.visionSubtitle).text = "ಸ್ಮಾರ್ಟ್ ವಿಲೇಜ್ ಇನಿಶಿಯೇಟಿವ್"

            findViewById<TextView>(R.id.swachhTitleVision).text = "ಸ್ವಚ್ಛ ಭಾರತ"
            findViewById<TextView>(R.id.swachhDescVision).text =
                "ರಾಷ್ಟ್ರೀಯ ಮಿಷನ್‌ನಿಂದ ಪ್ರೇರಿತವಾಗಿ, ಗ್ರಾಮ-ವೇಸ್ಟ್ ಗ್ರಾಮೀಣ ಸ್ವಚ್ಛತೆಯನ್ನು ಡಿಜಿಟಲ್ ಮಾಡಲು ಸಹಾಯ ಮಾಡುತ್ತದೆ. ನಾಗರಿಕರನ್ನು ಪಂಚಾಯತ್ ಸಂಗ್ರಹ ವ್ಯವಸ್ಥೆಯೊಂದಿಗೆ ಸಂಪರ್ಕಿಸಿ, ಗುಪ್ತ ತ್ಯಾಜ್ಯ ಪ್ರದೇಶಗಳನ್ನು ಕಡಿಮೆ ಮಾಡುವುದು ಇದರ ಉದ್ದೇಶ."

            findViewById<TextView>(R.id.trackingTitleVision).text = "ರಿಯಲ್-ಟೈಮ್ ಟ್ರ್ಯಾಕಿಂಗ್"
            findViewById<TextView>(R.id.trackingDescVision).text =
                "ಸಂಗ್ರಹ ಟ್ರಾಕ್ಟರ್ ಮಾರ್ಗಗಳನ್ನು ನೇರವಾಗಿ ನೋಡಿ."

            findViewById<TextView>(R.id.blackspotTitleVision).text = "ಶೂನ್ಯ ಬ್ಲ್ಯಾಕ್‌ಸ್ಪಾಟ್"
            findViewById<TextView>(R.id.blackspotDescVision).text =
                "ಕಸದ ಪ್ರದೇಶಗಳನ್ನು ತಕ್ಷಣ ವರದಿ ಮಾಡಿ."

            findViewById<TextView>(R.id.impactTitleVision).text = "ಪರಿಣಾಮ ಗುರಿಗಳು"
            findViewById<TextView>(R.id.impactPointsVision).text =
                "• ರೋಗಗಳನ್ನು ಕಡಿಮೆ ಮಾಡುವುದು\n• ಸ್ಥಳೀಯ ಮಣ್ಣಿನ ಗುಣಮಟ್ಟ ಉಳಿಸುವುದು\n• ನಿಯಮಿತ ಸ್ವಚ್ಛತಾ ಮಾರ್ಗಗಳು\n• ಸಮುದಾಯದ ಸ್ವಚ್ಛತೆ"
        } else {
            findViewById<TextView>(R.id.visionTitle).text = "Our Vision"
            findViewById<TextView>(R.id.visionSubtitle).text = "SMART VILLAGE INITIATIVE"

            findViewById<TextView>(R.id.swachhTitleVision).text = "Swachh Bharat"
            findViewById<TextView>(R.id.swachhDescVision).text =
                "Inspired by the national mission, Grama-Waste aims to digitize sanitation in rural India. By connecting citizens directly with the Panchayat collection system, we eliminate hidden waste pockets and ensure a cleaner environment."

            findViewById<TextView>(R.id.trackingTitleVision).text = "REAL-TIME TRACKING"
            findViewById<TextView>(R.id.trackingDescVision).text =
                "Monitoring tractor routes for precise collection times."

            findViewById<TextView>(R.id.blackspotTitleVision).text = "ZERO BLACKSPOTS"
            findViewById<TextView>(R.id.blackspotDescVision).text =
                "Instant reporting of illegal dumping spots in the village."

            findViewById<TextView>(R.id.impactTitleVision).text = "Impact Goals"
            findViewById<TextView>(R.id.impactPointsVision).text =
                "• REDUCE VECTOR-BORNE DISEASES\n• PRESERVE LOCAL SOIL QUALITY\n• PREDICTABLE SANITATION ROUTES\n• COMMUNITY-LED CLEANLINESS"
        }
    }
}