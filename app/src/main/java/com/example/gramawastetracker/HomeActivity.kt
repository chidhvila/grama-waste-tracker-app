package com.example.gramawastetracker

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : ComponentActivity() {

    private lateinit var mapView: MapView
    private lateinit var tractorMarker: Marker
    private lateinit var statusText: TextView
    private lateinit var arrivalAlertBox: TextView

    private val handler = Handler(Looper.getMainLooper())

    private val stops = listOf(
        Stop("Panchayat", "ಪಂಚಾಯತ್", GeoPoint(12.9769, 77.5902), 2),
        Stop("Post Office", "ಅಂಚೆ ಕಚೇರಿ", GeoPoint(12.9764, 77.5918), 4),
        Stop("School Colony", "ಶಾಲಾ ಕಾಲೊನಿ", GeoPoint(12.9777, 77.5935), 6),
        Stop("Old Market", "ಹಳೆಯ ಮಾರುಕಟ್ಟೆ", GeoPoint(12.9762, 77.5956), 8),
        Stop("Bus Stand", "ಬಸ್ ನಿಲ್ದಾಣ", GeoPoint(12.9749, 77.5950), 10),
        Stop("Library Corner", "ಗ್ರಂಥಾಲಯ ಮೂಲೆ", GeoPoint(12.9742, 77.5932), 12),
        Stop("Temple Sq", "ದೇವಾಲಯ ವೃತ್ತ", GeoPoint(12.9755, 77.5916), 14),
        Stop("Dumping Hub", "ಕಸ ಸಂಗ್ರಹ ಕೇಂದ್ರ", GeoPoint(12.9758, 77.5928), 16)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_home)

        statusText = findViewById(R.id.statusText)
        arrivalAlertBox = findViewById(R.id.arrivalAlertBox)

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(17.0)
        mapView.controller.setCenter(stops[0].point)

        applyLanguage()
        drawRouteLine()
        addStopMarkers()
        addTractorMarker()
        setupBottomNavigation()
        startTractorMovement()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        applyLanguage()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun drawRouteLine() {
        val routeLine = Polyline()
        routeLine.setPoints(stops.map { it.point })
        routeLine.outlinePaint.color = Color.rgb(0, 190, 100)
        routeLine.outlinePaint.strokeWidth = 8f
        mapView.overlays.add(routeLine)
    }

    private fun addStopMarkers() {
        stops.forEach { stop ->
            val marker = Marker(mapView)
            marker.position = stop.point
            marker.title = getStopName(stop)
            marker.snippet = if (isKannada()) {
                "ಟ್ರಾಕ್ಟರ್ ${getReachTime(stop.minutesFromNow)}ಕ್ಕೆ ತಲುಪುತ್ತದೆ"
            } else {
                "Tractor reaches at ${getReachTime(stop.minutesFromNow)}"
            }
            marker.icon = createIcon("📍")
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)
        }
    }

    private fun addTractorMarker() {
        tractorMarker = Marker(mapView)
        tractorMarker.position = stops[0].point
        tractorMarker.title = if (isKannada()) "ತ್ಯಾಜ್ಯ ಸಂಗ್ರಹ ಟ್ರಾಕ್ಟರ್" else "Waste Collection Tractor"
        tractorMarker.snippet = if (isKannada()) "ಸಂಗ್ರಹ ಪ್ರಾರಂಭವಾಗುತ್ತಿದೆ" else "Starting collection"
        tractorMarker.icon = createIcon("🚜")
        tractorMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        mapView.overlays.add(tractorMarker)
    }

    private fun startTractorMovement() {
        statusText.text = if (isKannada()) {
            "🚜 ಟ್ರಾಕ್ಟರ್ ಪ್ರಾರಂಭವಾಗುತ್ತಿದೆ\nಮುಂದಿನ ಸ್ಥಳ: ${getStopName(stops[1])}\nತಲುಪುವ ಸಮಯ: ${getReachTime(stops[1].minutesFromNow)}"
        } else {
            "🚜 TRACTOR STARTING\nNext Stop: ${getStopName(stops[1])}\nReach Time: ${getReachTime(stops[1].minutesFromNow)}"
        }

        handler.postDelayed({
            moveToNextStop(0)
        }, 1500)
    }

    private fun moveToNextStop(index: Int) {
        if (index >= stops.size - 1) {
            statusText.text = if (isKannada()) {
                "✅ ಸಂಗ್ರಹ ಪೂರ್ಣಗೊಂಡಿದೆ\nಎಲ್ಲಾ ಗ್ರಾಮದ ಸ್ಥಳಗಳು ಮುಗಿದಿವೆ"
            } else {
                "✅ COLLECTION COMPLETED\nAll village points covered"
            }

            showArrivalAlert(
                if (isKannada()) "✅ ಸಂಗ್ರಹ ಪೂರ್ಣಗೊಂಡಿದೆ" else "✅ Collection Completed"
            )
            return
        }

        val startStop = stops[index]
        val endStop = stops[index + 1]

        statusText.text = if (isKannada()) {
            "🚜 ${getStopName(endStop)} ಕಡೆಗೆ ಸಾಗುತ್ತಿದೆ\nಇಂದ: ${getStopName(startStop)}\nತಲುಪುವ ಸಮಯ: ${getReachTime(endStop.minutesFromNow)}"
        } else {
            "🚜 MOVING TO ${getStopName(endStop)}\nFrom: ${getStopName(startStop)}\nReach Time: ${getReachTime(endStop.minutesFromNow)}"
        }

        animateTractor(startStop.point, endStop.point) {
            statusText.text = if (isKannada()) {
                "✅ ಟ್ರಾಕ್ಟರ್ ತಲುಪಿದೆ\n${getStopName(endStop)}\nತಲುಪಿದ ಸಮಯ: ${getReachTime(endStop.minutesFromNow)}"
            } else {
                "✅ TRACTOR REACHED\n${getStopName(endStop)}\nReached at ${getReachTime(endStop.minutesFromNow)}"
            }

            showArrivalAlert(
                if (isKannada()) {
                    "🚜 ಆಗಮನ ಎಚ್ಚರಿಕೆ\nಟ್ರಾಕ್ಟರ್ ${getStopName(endStop)} ತಲುಪಿದೆ"
                } else {
                    "🚜 Arrival Alert\nTractor reached ${getStopName(endStop)}"
                }
            )

            handler.postDelayed({
                moveToNextStop(index + 1)
            }, 10000)
        }
    }

    private fun showArrivalAlert(message: String) {
        arrivalAlertBox.text = message
        arrivalAlertBox.visibility = View.VISIBLE

        handler.postDelayed({
            arrivalAlertBox.visibility = View.GONE
        }, 4000)
    }

    private fun animateTractor(start: GeoPoint, end: GeoPoint, onEnd: () -> Unit) {
        val steps = 120
        var step = 0

        val latDiff = (end.latitude - start.latitude) / steps
        val lonDiff = (end.longitude - start.longitude) / steps

        val runnable = object : Runnable {
            override fun run() {
                if (step <= steps) {
                    val newPoint = GeoPoint(
                        start.latitude + latDiff * step,
                        start.longitude + lonDiff * step
                    )

                    tractorMarker.position = newPoint
                    mapView.controller.animateTo(newPoint)
                    mapView.invalidate()

                    step++
                    handler.postDelayed(this, 90)
                } else {
                    onEnd()
                }
            }
        }

        handler.post(runnable)
    }

    private fun setupBottomNavigation() {
        findViewById<TextView>(R.id.navMap).setOnClickListener {
            showArrivalAlert(if (isKannada()) "ನೀವು ಈಗಾಗಲೇ ನಕ್ಷೆಯಲ್ಲಿ ಇದ್ದೀರಿ" else "You are already on Live Map")
        }

        findViewById<TextView>(R.id.navReport).setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        findViewById<TextView>(R.id.navGuide).setOnClickListener {
            startActivity(Intent(this, GuideActivity::class.java))
        }

        findViewById<TextView>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun applyLanguage() {
        if (isKannada()) {
            findViewById<TextView>(R.id.navMap).text = "🗺️\nನಕ್ಷೆ"
            findViewById<TextView>(R.id.navReport).text = "📷\nವರದಿ"
            findViewById<TextView>(R.id.navGuide).text = "📖\nಮಾರ್ಗದರ್ಶಿ"
            findViewById<TextView>(R.id.navProfile).text = "👤\nಪ್ರೊಫೈಲ್"
        } else {
            findViewById<TextView>(R.id.navMap).text = "🗺️\nMAP"
            findViewById<TextView>(R.id.navReport).text = "📷\nREPORT"
            findViewById<TextView>(R.id.navGuide).text = "📖\nGUIDE"
            findViewById<TextView>(R.id.navProfile).text = "👤\nPROFILE"
        }
    }

    private fun isKannada(): Boolean {
        return getSharedPreferences("settings", MODE_PRIVATE)
            .getString("lang", "en") == "kn"
    }

    private fun getStopName(stop: Stop): String {
        return if (isKannada()) stop.kannadaName else stop.englishName
    }

    private fun createIcon(text: String): android.graphics.drawable.Drawable {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        circlePaint.color = Color.WHITE
        canvas.drawCircle(50f, 50f, 45f, circlePaint)

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint.color = Color.rgb(0, 180, 95)
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 5f
        canvas.drawCircle(50f, 50f, 45f, borderPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = 48f
        textPaint.textAlign = Paint.Align.CENTER

        canvas.drawText(text, 50f, 67f, textPaint)

        return android.graphics.drawable.BitmapDrawable(resources, bitmap)
    }

    private fun getReachTime(minutesFromNow: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, minutesFromNow)
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
    }

    data class Stop(
        val englishName: String,
        val kannadaName: String,
        val point: GeoPoint,
        val minutesFromNow: Int
    )
}