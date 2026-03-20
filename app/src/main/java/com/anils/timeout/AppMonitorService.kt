package com.anils.timeout

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class AppMonitorService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var sonBilinenUygulama: String = ""
    private var sonFirlatmaZamani = 0L // YENİ: Soğuma sayacımız
    private var sonKilitlenmeZamani: Long = 0

    private val devriyeGorevi = object : Runnable {
        override fun run() {
            val enUsttekiUygulama = enUsttekiUygulamayiBul()

            if (enUsttekiUygulama.isNotEmpty()) {
                // Sisteme ve kendi uygulamamıza karışma
                if (enUsttekiUygulama == packageName || enUsttekiUygulama.contains("systemui") ||
                    enUsttekiUygulama == "android" || enUsttekiUygulama.contains("launcher")) {
                    handler.postDelayed(this, 500)
                    return
                }

                // YENİ SİSTEME GÖRE OKUMA: Virgülle ayrılmış metni alıp listeye çeviriyoruz
                val hafizaDefteri = getSharedPreferences("UygulamaAyarlari", Context.MODE_PRIVATE)
                val kilitliUygulamalarString = hafizaDefteri.getString("kilitliUygulamalar", "") ?: ""
                val kilitliListe = kilitliUygulamalarString.split(",")
                val kilitliDefter = getSharedPreferences("KilitliUygulamalar", Context.MODE_PRIVATE)
                val buUygulamaKilitliMi = kilitliDefter.getBoolean(enUsttekiUygulama, false)

                if (kilitliListe.contains(enUsttekiUygulama)) {
                    val izinBitisZamani = hafizaDefteri.getLong("izinBitisZamani", 0L)
                    val suAnkiZaman = System.currentTimeMillis()

                    if (suAnkiZaman > izinBitisZamani) {
                        sayfayiFirlat()
                    }
                }
            }
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate() {
        super.onCreate()

        // ÇÖKME KALKANI: Android 14/15/16 güvenlik duvarlarına çarpıp çökmeyi önler
        try {
            val kanalId = "DilKilidiKanal"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val kanal = NotificationChannel(
                    kanalId,
                    "Dil Kilidi Arka Plan Servisi",
                    NotificationManager.IMPORTANCE_LOW
                )
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(kanal)
            }

            val bildirim = NotificationCompat.Builder(this, kanalId)
                .setContentTitle("Dil Kilidi Aktif")
                .setContentText("Seni korumaya devam ediyorum...")
                .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
                .build()

            // EVRENSEL BAŞLATICI: Hangi Android sürümü olduğuna bakarak doğru kimliği sunar
            if (Build.VERSION.SDK_INT >= 34) { // Android 14, 15 ve 16 için
                startForeground(1, bildirim, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else { // Android 13 ve altı için
                startForeground(1, bildirim)
            }

            handler.post(devriyeGorevi)

        } catch (e: Exception) {
            e.printStackTrace()
            // Çökmek yerine loglara veya ekrana mesaj atar
            Toast.makeText(this, "Ajan Başlatılamadı! Lütfen tüm izinleri verin.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun enUsttekiUygulamayiBul(): String {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val suAn = System.currentTimeMillis()
        val olaylar = usageStatsManager.queryEvents(suAn - 2000, suAn)
        val olay = UsageEvents.Event()

        while (olaylar.hasNextEvent()) {
            olaylar.getNextEvent(olay)
            if (olay.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                sonBilinenUygulama = olay.packageName
            }
        }
        return sonBilinenUygulama
    }

    private fun sayfayiFirlat() {
        val suAnkiZaman = System.currentTimeMillis()

        // Eğer son fırlatmanın üzerinden 2 saniye (2000 milisaniye) geçtiyse tetikle
        if (suAnkiZaman - sonKilitlenmeZamani > 2000) {

            val intent = Intent(this, LockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)

            // Zamanı güncelle ki arka arkaya fırlatmasın
            sonKilitlenmeZamani = suAnkiZaman

            Log.d("DilKilidi", "Kilit başarıyla fırlatıldı!")
        } else {
            Log.d("DilKilidi", "Kilit zaten fırlatılıyor, hiperaktif çağrı engellendi.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(devriyeGorevi)
    }
}