package com.anils.timeout

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LockActivity : ComponentActivity() {

    private var soruCozuldu = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hafizaDefteri = getSharedPreferences("UygulamaAyarlari", Context.MODE_PRIVATE)
        val secilenSeviye = hafizaDefteri.getString("secilenSeviye", "Tümü") ?: "Tümü"
        val secilenSoru = WordBank.rastgeleSoruUret(this, secilenSeviye)

        setContent {
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    window.statusBarColor = Color(0xFFF7F8F3).toArgb()
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
                }
            }

            LockScreen(
                soru = secilenSoru,
                onCorrectAnswer = {
                    kilidiAc()
                },
                onWrongAnswer = {
                    Toast.makeText(this, "Yanlış! Diğer seçenekleri dene.", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun kilidiAc() {
        soruCozuldu = true

        val hafizaDefteri = getSharedPreferences("UygulamaAyarlari", Context.MODE_PRIVATE)
        val yazar = hafizaDefteri.edit()

        val secilenSayiStr = hafizaDefteri.getString("secilenSayi", "15") ?: "15"
        val secilenBirim = hafizaDefteri.getString("secilenBirim", "Dakika") ?: "Dakika"

        val sureDegeri = secilenSayiStr.toLongOrNull() ?: 15L

        val eklenecekSüreMilisaniye = when (secilenBirim) {
            "Saniye" -> sureDegeri * 1000L
            "Dakika" -> sureDegeri * 60 * 1000L
            "Saat" -> sureDegeri * 60 * 60 * 1000L
            else -> 15 * 60 * 1000L
        }

        Toast.makeText(this, "Tebrikler! $sureDegeri $secilenBirim kazandın.", Toast.LENGTH_SHORT).show()

        val bitisZamani = System.currentTimeMillis() + eklenecekSüreMilisaniye
        yazar.putLong("izinBitisZamani", bitisZamani)

        val mevcutPuan = hafizaDefteri.getInt("ogrenilenKelime", 0)
        yazar.putInt("ogrenilenKelime", mevcutPuan + 1)

        yazar.apply()
        finish()
    }

    override fun onStop() {
        super.onStop()
        if (!soruCozuldu) {
            val hafizaDefteri = getSharedPreferences("UygulamaAyarlari", Context.MODE_PRIVATE)
            hafizaDefteri.edit().putLong("izinBitisZamani", 0L).apply()
            finish()
        }
    }
}

@Composable
fun LockScreen(
    soru: Soru,
    onCorrectAnswer: () -> Unit,
    onWrongAnswer: () -> Unit
) {
    // Renk Paleti
    val backgroundColor = Color(0xFFF7F8F3)
    val primaryGreen = Color(0xFF86AE7C)
    val lightGreen = Color(0xFFE4EDE0)
    val orangeAccent = Color(0xFFF09A36)
    val darkText = Color(0xFF2D312E)
    val grayText = Color(0xFF757575)

    val disabledButtons = remember { mutableStateListOf<Int>() }
    var correctSelectedIndex by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    BackHandler {
        // Geri tuşunu engelliyoruz
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo ve Üst Bilgi
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .background(lightGreen, shape = CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "TIME TO LEARN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = primaryGreen,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Soru Kelimesi (Signature Orange)
        Text(
            text = soru.kelime,
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            color = orangeAccent,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "What does this mean?",
            fontSize = 16.sp,
            color = grayText
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Seçenekler
        soru.secenekler.forEachIndexed { index, secenek ->
            val isEnabled = !disabledButtons.contains(index)
            val isCorrectSelection = correctSelectedIndex == index
            
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(enabled = isEnabled && correctSelectedIndex == null) {
                        if (index == soru.dogruCevapIndex) {
                            correctSelectedIndex = index
                            scope.launch {
                                delay(1000)
                                onCorrectAnswer()
                            }
                        } else {
                            disabledButtons.add(index)
                            onWrongAnswer()
                        }
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = when {
                        isCorrectSelection -> primaryGreen
                        !isEnabled -> Color(0xFFF0F2EC)
                        else -> Color.White
                    }
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (isEnabled && !isCorrectSelection) 2.dp else 0.dp
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = secenek,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCorrectSelection -> Color.White
                            !isEnabled -> Color.LightGray
                            else -> darkText
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Alt Bilgi
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = grayText,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Solve to unlock your apps",
                fontSize = 13.sp,
                color = grayText,
                textAlign = TextAlign.Center
            )
        }
    }
}
