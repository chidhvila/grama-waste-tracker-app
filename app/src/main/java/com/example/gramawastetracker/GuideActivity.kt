package com.example.gramawastetracker

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class GuideActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        findViewById<TextView>(R.id.backBtnGuide).setOnClickListener {
            finish()
        }

        applyLanguage()
    }

    private fun applyLanguage() {
        val lang = getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en")

        if (lang == "kn") {
            findViewById<TextView>(R.id.guideTitle).text = "ತ್ಯಾಜ್ಯ ಮಾರ್ಗದರ್ಶಿ"

            findViewById<TextView>(R.id.dryWasteTitle).text = "ಒಣ ತ್ಯಾಜ್ಯ"
            findViewById<TextView>(R.id.dryWasteDesc).text =
                "• ಪ್ಲಾಸ್ಟಿಕ್ ಬಾಟಲಿಗಳು\n• ಹಾಲಿನ ಪ್ಯಾಕೆಟ್‌ಗಳು\n• ಕಾಗದ ಮತ್ತು ಕಾರ್ಡ್‌ಬೋರ್ಡ್\n• ಗಾಜಿನ ಬಾಟಲಿಗಳು\n• ಲೋಹದ ಡಬ್ಬಿಗಳು\n• ಇ-ತ್ಯಾಜ್ಯ\n• ಕಾರ್ಟನ್ ಮತ್ತು ಪೆಟ್ಟಿಗೆಗಳು"

            findViewById<TextView>(R.id.wetWasteTitle).text = "ಒದ್ದೆ ತ್ಯಾಜ್ಯ"
            findViewById<TextView>(R.id.wetWasteDesc).text =
                "• ಆಹಾರ ತ್ಯಾಜ್ಯ\n• ತರಕಾರಿ ಸಿಪ್ಪೆಗಳು\n• ಹಣ್ಣಿನ ಸಿಪ್ಪೆಗಳು\n• ಉಳಿದ ಆಹಾರ\n• ಚಹಾ ಪುಡಿ\n• ಹೂವುಗಳು ಮತ್ತು ಎಲೆಗಳು\n• ಮೊಟ್ಟೆಯ ಸಿಪ್ಪೆಗಳು"

            findViewById<TextView>(R.id.habitsTitle).text = "ಸ್ವಚ್ಛ ಗ್ರಾಮದ ಅಭ್ಯಾಸಗಳು"

            findViewById<TextView>(R.id.habitOneTitle).text = "1. ಮೂಲದಲ್ಲೇ ಬೇರ್ಪಡಿಸಿ"
            findViewById<TextView>(R.id.habitOneDesc).text =
                "ಒಣ ತ್ಯಾಜ್ಯಕ್ಕೆ ನೀಲಿ ಡಬ್ಬಿ, ಒದ್ದೆ ತ್ಯಾಜ್ಯಕ್ಕೆ ಹಸಿರು ಡಬ್ಬಿ ಬಳಸಿ."

            findViewById<TextView>(R.id.habitTwoTitle).text = "2. ಕಸ ಹಾಕಬೇಡಿ"
            findViewById<TextView>(R.id.habitTwoDesc).text =
                "ಯಾವಾಗಲೂ ತ್ಯಾಜ್ಯವನ್ನು ಪಂಚಾಯತ್ ಟ್ರಾಕ್ಟರ್‌ಗೆ ನೀಡಿ."

            findViewById<TextView>(R.id.habitThreeTitle).text = "3. ತೊಳೆದು ಒಣಗಿಸಿ"
            findViewById<TextView>(R.id.habitThreeDesc).text =
                "ಪ್ಲಾಸ್ಟಿಕ್ ಆಹಾರ ಡಬ್ಬಿಗಳನ್ನು ಹಾಕುವ ಮೊದಲು ತೊಳೆದು ಒಣಗಿಸಿ."

            findViewById<TextView>(R.id.habitFourTitle).text = "4. ಸಮುದಾಯ ಜಾಗೃತಿ"
            findViewById<TextView>(R.id.habitFourDesc).text =
                "ಅಕ್ರಮ ಕಸ ಹಾಕುವ ಸ್ಥಳಗಳನ್ನು ಈ ಆಪ್ ಮೂಲಕ ವರದಿ ಮಾಡಿ."
        }
    }
}