package com.anils.timeout

import android.content.Context
import org.json.JSONObject

data class Soru(val kelime: String, val secenekler: List<String>, val dogruCevapIndex: Int)

object WordBank {

    // 3. ÇÖZÜM: Veriyi diskten değil, RAM'den okumak için hafıza değişkenleri
    private var jsonObject: JSONObject? = null
    private var isInitialized = false

    // Uygulama ilk açıldığında (MainActivity'de) çalışıp veriyi RAM'e kazıyacak
    fun baslat(context: Context) {
        if (!isInitialized) {
            val jsonString = context.assets.open("kelimeler.json").bufferedReader().use { it.readText() }
            jsonObject = JSONObject(jsonString)
            isInitialized = true
        }
    }

    fun rastgeleSoruUret(context: Context, secilenSeviye: String): Soru {
        // Önlem: Eğer RAM'den silindiyse tekrar yükle
        if (!isInitialized) baslat(context)

        val db = jsonObject!!
        val gecerliSeviye = if (secilenSeviye == "Tümü") {
            listOf("A1", "A2", "B1", "B2", "C1", "C2").random()
        } else {
            secilenSeviye
        }

        val seviyeDizisi = db.getJSONArray(gecerliSeviye)
        val toplamKelimeSayisi = seviyeDizisi.length()

        val dogruIndex = (0 until toplamKelimeSayisi).random()
        val dogruObje = seviyeDizisi.getJSONObject(dogruIndex)
        val sorulacakIngilizce = dogruObje.getString("ing")
        val dogruTurkce = dogruObje.getString("tr")

        val yanlisCevaplar = mutableSetOf<String>()
        while (yanlisCevaplar.size < 3) {
            val rastgeleYanlisIndex = (0 until toplamKelimeSayisi).random()
            if (rastgeleYanlisIndex != dogruIndex) {
                val yanlisTurkce = seviyeDizisi.getJSONObject(rastgeleYanlisIndex).getString("tr")
                yanlisCevaplar.add(yanlisTurkce)
            }
        }

        val tumSecenekler = mutableListOf<String>()
        tumSecenekler.add(dogruTurkce)
        tumSecenekler.addAll(yanlisCevaplar)
        tumSecenekler.shuffle()

        val gercekDogruIndex = tumSecenekler.indexOf(dogruTurkce)

        return Soru(sorulacakIngilizce, tumSecenekler, gercekDogruIndex)
    }
}