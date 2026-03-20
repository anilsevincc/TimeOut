package com.anils.timeout

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WordBank.baslat(this)

        setContent {
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    window.statusBarColor = Color(0xFFF7F8F3).toArgb()
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
                }
            }

            AppNavigation()
        }
    }
}

@Composable
fun WelcomeScreen(onNextClick: () -> Unit) {
    val backgroundColor = Color(0xFFF7F8F3)
    val primaryGreen = Color(0xFF86AE7C)
    val lightGreen = Color(0xFFE4EDE0)
    val darkText = Color(0xFF2D312E)
    val grayText = Color(0xFF757575)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Your time is\nyours again.",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = darkText,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .background(lightGreen, shape = CircleShape)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .background(primaryGreen, shape = CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "TimeOut",
                fontSize = 18.sp,
                color = grayText,
                fontWeight = FontWeight.Medium
            )
        }

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Next",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun PermissionInfoScreen(onNextClick: () -> Unit) {
    val backgroundColor = Color(0xFFF7F8F3)
    val primaryGreen = Color(0xFF86AE7C)
    val lightGreen = Color(0xFFE4EDE0)
    val darkText = Color(0xFF2D312E)
    val grayText = Color(0xFF757575)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(lightGreen, shape = RoundedCornerShape(16.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Security Icon",
                    tint = primaryGreen,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Why we need access",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = darkText
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            val paragraphText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = grayText, fontSize = 16.sp)) {
                    append("To help you reduce screen time effectively, EndScreen needs permission to ")
                }
                withStyle(
                    style = SpanStyle(
                        color = darkText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("monitor app usage")
                }
                withStyle(style = SpanStyle(color = grayText, fontSize = 16.sp)) {
                    append(" and ")
                }
                withStyle(
                    style = SpanStyle(
                        color = darkText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("display the language lock")
                }
                withStyle(style = SpanStyle(color = grayText, fontSize = 16.sp)) {
                    append(" over your distracting apps.")
                }
            }

            Text(
                text = paragraphText,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "We respect your privacy and only use these permissions to help you focus.",
                fontSize = 15.sp,
                color = grayText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Next",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun AppNavigation() {

    val context = LocalContext.current
    val hafizaDefteri = context.getSharedPreferences("UygulamaAyarlari", Context.MODE_PRIVATE)

    val onboardingTamamlandi = hafizaDefteri.getBoolean("onboardingTamamlandi", false)

    val baslangicEkrani = if (onboardingTamamlandi) "dashboard_screen" else "welcome_screen"

    val navController = rememberNavController()
    val animDuration = 400

    NavHost(navController = navController, startDestination = baslangicEkrani) {
        composable(
            route = "welcome_screen",
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(animDuration)
                ) + fadeOut(animationSpec = tween(animDuration))
            }
        ) {
            WelcomeScreen(
                onNextClick = {
                    navController.navigate("permission_info_screen")
                }
            )
        }

        composable(
            route = "permission_info_screen",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(animDuration)
                ) + fadeIn(animationSpec = tween(animDuration))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(animDuration)
                ) + fadeOut(animationSpec = tween(animDuration))
            }
        ) {
            PermissionInfoScreen(
                onNextClick = {
                    navController.navigate("set_things_up_screen")
                }
            )
        }

        composable(
            route = "set_things_up_screen",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(animDuration)
                ) + fadeIn(animationSpec = tween(animDuration))
            }
        ) {
            SetThingsUpScreen(
                onGrantClick = {
                    navController.navigate("level_selection_screen")
                }
            )
        }
        composable(
            route = "level_selection_screen",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(animDuration)
                ) + fadeIn(animationSpec = tween(animDuration))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(animDuration)
                ) + fadeOut(animationSpec = tween(animDuration))
            }
        ) {
            val isEdit = hafizaDefteri.getBoolean("onboardingTamamlandi", false)
            LevelSelectionScreen(
                onNextClick = {
                    if (isEdit) {
                        navController.popBackStack()
                    } else {
                        navController.navigate("frequency_selection_screen")
                    }
                },
                buttonText = if (isEdit) "Save" else "Next"
            )
        }
        composable(
            route = "frequency_selection_screen",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(animDuration)
                ) + fadeIn(animationSpec = tween(animDuration))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(animDuration)
                ) + fadeOut(animationSpec = tween(animDuration))
            }
        ) {
            val isEdit = hafizaDefteri.getBoolean("onboardingTamamlandi", false)
            FrequencySelectionScreen(
                onNextClick = {
                    if (isEdit) {
                        navController.popBackStack()
                    } else {
                        navController.navigate("app_selection_screen")
                    }
                },
                buttonText = if (isEdit) "Save" else "Next"
            )
        }
        composable(
            route = "app_selection_screen",
            enterTransition = {
                slideInVertically(initialOffsetY = { it }, animationSpec = tween(animDuration)) + fadeIn(animationSpec = tween(animDuration))
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = { -it / 3 }, animationSpec = tween(animDuration)) + fadeOut(animationSpec = tween(animDuration))
            }
        ) {
            val isEdit = hafizaDefteri.getBoolean("onboardingTamamlandi", false)
            AppSelectionScreen(
                onFinishClick = {
                    if (isEdit) {
                        navController.popBackStack()
                    } else {
                        navController.navigate("dashboard_screen") {
                            popUpTo("welcome_screen") { inclusive = true }
                        }
                    }
                },
                buttonText = if (isEdit) "Save" else "Finish"
            )
        }
        composable(
            route = "dashboard_screen",
            enterTransition = { fadeIn(animationSpec = tween(animDuration)) },
            exitTransition = { fadeOut(animationSpec = tween(animDuration)) }
        ) {
            DashboardScreen(navController)
        }
    }
}

@Composable
fun SetThingsUpScreen(onGrantClick: () -> Unit) {
    val context = LocalContext.current

    val backgroundColor = Color(0xFFF7F8F3)
    val primaryGreen = Color(0xFF86AE7C)
    val lightGreen = Color(0xFFE4EDE0)
    val darkText = Color(0xFF2D312E)
    val grayText = Color(0xFF757575)
    val grayDeactivated = Color(0xFFE0E0E0)

    val isUsageGranted = remember { mutableStateOf(checkUsageAccess(context)) }
    val isDisplayOverAppsGranted = remember { mutableStateOf(checkOverlayPermission(context)) }
    val isNotificationsGranted = remember { mutableStateOf(checkNotificationPermission(context)) }
    val isBatteryOptimizationsIgnored = remember { mutableStateOf(checkBatteryOptimization(context)) }

    val isGrantEnabled =
        isUsageGranted.value && isDisplayOverAppsGranted.value && isNotificationsGranted.value && isBatteryOptimizationsIgnored.value

    val usageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            isUsageGranted.value = checkUsageAccess(context)
        }

    val overlayLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            isDisplayOverAppsGranted.value = checkOverlayPermission(context)
        }

    val notificationLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isNotificationsGranted.value = isGranted
        }

    val batteryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            isBatteryOptimizationsIgnored.value = checkBatteryOptimization(context)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 32.dp, bottom = 48.dp)
        ) {
            Text(
                text = "Let\u0027s set things up",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = darkText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Just four quick toggles to get started.",
                fontSize = 16.sp,
                color = grayText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PermissionCard(
                icon = Icons.Default.Info,
                title = "Usage Access",
                description = "Monitor screen time",
                isGranted = isUsageGranted.value,
                onCheckedChange = {
                    usageLauncher.launch(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                },
                primaryColor = primaryGreen,
                lightColor = lightGreen,
                darkTextColor = darkText,
                grayTextColor = grayText
            )

            PermissionCard(
                icon = Icons.Default.Lock,
                title = "Display over apps",
                description = "Show the language lock",
                isGranted = isDisplayOverAppsGranted.value,
                onCheckedChange = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    overlayLauncher.launch(intent)
                },
                primaryColor = primaryGreen,
                lightColor = lightGreen,
                darkTextColor = darkText,
                grayTextColor = grayText
            )

            PermissionCard(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                description = "Receive important updates",
                isGranted = isNotificationsGranted.value,
                onCheckedChange = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
                primaryColor = primaryGreen,
                lightColor = lightGreen,
                darkTextColor = darkText,
                grayTextColor = grayText
            )

            PermissionCard(
                icon = Icons.Default.BatteryChargingFull,
                title = "Battery Saver",
                description = "Keep the app running reliably",
                isGranted = isBatteryOptimizationsIgnored.value,
                onCheckedChange = {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    batteryLauncher.launch(intent)
                },
                primaryColor = primaryGreen,
                lightColor = lightGreen,
                darkTextColor = darkText,
                grayTextColor = grayText
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onGrantClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 8.dp),
            enabled = isGrantEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryGreen,
                contentColor = Color.White,
                disabledContainerColor = grayDeactivated,
                disabledContentColor = grayText
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Grant Permissions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun checkUsageAccess(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

fun checkOverlayPermission(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

fun checkNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun checkBatteryOptimization(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isIgnoringBatteryOptimizations(context.packageName)
}

@Composable
fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primaryColor: Color,
    lightColor: Color,
    darkTextColor: Color,
    grayTextColor: Color
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(lightColor, shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Permission Icon",
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkTextColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        color = grayTextColor
                    )
                }
            }

            Switch(
                checked = isGranted,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = primaryColor,
                    uncheckedThumbColor = grayTextColor,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}

data class LevelItem(val id: String, val title: String, val subtitle: String, val icon: ImageVector)

@Composable
fun LevelSelectionScreen(onNextClick: () -> Unit, buttonText: String = "Next") {
    val context = LocalContext.current
    val hafizaDefteri = context.getSharedPreferences("UygulamaAyarlari", Context.MODE_PRIVATE)

    val selectedLevel = remember { mutableStateOf("") }

    val backgroundColor = Color(0xFFF7F8F3)
    val primaryGreen = Color(0xFF86AE7C)
    val orangeAccent = Color(0xFFF09A36)
    val lightOrangeBg = Color(0xFFFFF8F0)
    val darkText = Color(0xFF2D312E)
    val grayText = Color(0xFF757575)
    val iconBoxGray = Color(0xFFF0F2EC)
    val grayDeactivated = Color(0xFFE0E0E0)

    val levels = listOf(
        LevelItem("A1", "A1", "Beginner", Icons.Default.Book),
        LevelItem("A2", "A2", "Elementary", Icons.Default.Explore),
        LevelItem("B1", "B1", "Intermediate", Icons.Default.ImportContacts),
        LevelItem("B2", "B2", "Upper Int.", Icons.Default.School),
        LevelItem("C1", "C1", "Advanced", Icons.Default.WorkspacePremium),
        LevelItem("C2", "C2", "Proficient", Icons.Default.EmojiEvents)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 32.dp, bottom = 32.dp)
        ) {
            Text(
                text = "Choose your\nchallenge level",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = darkText,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(levels) { level ->
                val isSelected = selectedLevel.value == level.id

                val cardBgColor = if (isSelected) lightOrangeBg else Color.White
                val borderColor = if (isSelected) orangeAccent else Color.Transparent
                val titleColor = if (isSelected) orangeAccent else darkText
                val iconBgColor = if (isSelected) orangeAccent else iconBoxGray
                val iconColor = if (isSelected) Color.White else darkText

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.9f)
                        .background(cardBgColor, shape = RoundedCornerShape(20.dp))
                        .border(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedLevel.value = level.id }
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .background(iconBgColor, shape = RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = level.icon,
                                contentDescription = level.title,
                                tint = iconColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = level.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = titleColor
                            )
                            Text(
                                text = level.subtitle,
                                fontSize = 13.sp,
                                color = grayText
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                hafizaDefteri.edit().putString("secilenSeviye", selectedLevel.value).apply()
                onNextClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 8.dp),
            enabled = selectedLevel.value.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryGreen,
                contentColor = Color.White,
                disabledContainerColor = grayDeactivated,
                disabledContentColor = grayText
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = buttonText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FrequencySelectionScreen(onNextClick: () -> Unit, buttonText: String = "Next") {
    val context = LocalContext.current
    val hafizaDefteri = context.getSharedPreferences("UygulamaAyarlari", Context.MODE_PRIVATE)

    val selectedFrequency = remember { mutableStateOf("") }

    val backgroundColor = Color(0xFFF7F8F3)
    val primaryGreen = Color(0xFF86AE7C)
    val lightGreen = Color(0xFFE4EDE0)
    val darkText = Color(0xFF2D312E)
    val grayDeactivated = Color(0xFFE0E0E0)
    val grayText = Color(0xFF757575)

    val frequencies = listOf("1 min", "5 min", "10 min", "15 min", "30 min", "1 hour")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(vertical = 62.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 32.dp, bottom = 48.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(lightGreen, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Time Icon",
                    tint = primaryGreen,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "How often should\nwe check in?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = darkText,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp),
            contentPadding = PaddingValues(bottom = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(frequencies) { freq ->
                val isSelected = selectedFrequency.value == freq

                val cardBgColor = if (isSelected) primaryGreen else Color.White
                val textColor = if (isSelected) Color.White else darkText

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clickable { selectedFrequency.value = freq },
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = cardBgColor),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = if (isSelected) 0.dp else 2.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = freq,
                            fontSize = 18.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val parts = selectedFrequency.value.split(" ")
                if (parts.size == 2) {
                    val sayi = parts[0]
                    val birim = if (parts[1] == "min") "Dakika" else "Saat"

                    hafizaDefteri.edit()
                        .putString("secilenSayi", sayi)
                        .putString("secilenBirim", birim)
                        .apply()
                }
                onNextClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 8.dp),
            enabled = selectedFrequency.value.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryGreen,
                contentColor = Color.White,
                disabledContainerColor = grayDeactivated,
                disabledContentColor = grayText
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = buttonText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class AppItem(val packageName: String, val name: String, val icon: Drawable)

@Composable
fun AppSelectionScreen(onFinishClick: () -> Unit, buttonText: String = "Finish") {
    val context = LocalContext.current
    val hafizaDefteri = context.getSharedPreferences("UygulamaAyarlari", Context.MODE_PRIVATE)

    val selectedApps = remember { mutableStateListOf<String>() }

    val appsList = remember { mutableStateListOf<AppItem>() }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val installedApps = getInstalledApps(context)
            withContext(Dispatchers.Main) {
                appsList.addAll(installedApps)
                isLoading.value = false
            }
        }
    }

    val backgroundColor = Color(0xFFF7F8F3)
    val primaryGreen = Color(0xFF86AE7C)
    val lightGreen = Color(0xFFE4EDE0)
    val darkText = Color(0xFF2D312E)
    val grayText = Color(0xFF757575)
    val grayDeactivated = Color(0xFFE0E0E0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .background(lightGreen, shape = RoundedCornerShape(16.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Smartphone,
                    contentDescription = "Phone Icon",
                    tint = primaryGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Which apps should\nwe lock?",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = darkText,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )
        }

        if (isLoading.value) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(appsList) { app ->
                    val isSelected = selectedApps.contains(app.packageName)

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Image(
                                    bitmap = app.icon.toBitmap(width = 120, height = 120).asImageBitmap(),
                                    contentDescription = app.name,
                                    modifier = Modifier.size(40.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = app.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = darkText,
                                    maxLines = 1,
                                )
                            }

                            Switch(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedApps.add(app.packageName)
                                    } else {
                                        selectedApps.remove(app.packageName)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = primaryGreen,
                                    uncheckedThumbColor = grayText,
                                    uncheckedTrackColor = Color.LightGray
                                )
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val secilenlerString = selectedApps.joinToString(",")
                val edit = hafizaDefteri.edit()
                    .putString("kilitliUygulamalar", secilenlerString)
                    .putBoolean("onboardingTamamlandi", true)
                
                if (!hafizaDefteri.getBoolean("onboardingTamamlandi", false)) {
                    edit.putBoolean("sistemKilitli", true)
                }
                edit.apply()

                val servisIntent = Intent(context, AppMonitorService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(servisIntent)
                } else {
                    context.startService(servisIntent)
                }

                onFinishClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 8.dp),
            enabled = selectedApps.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryGreen,
                contentColor = Color.White,
                disabledContainerColor = grayDeactivated,
                disabledContentColor = grayText
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = buttonText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun getInstalledApps(context: Context): List<AppItem> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val resolveInfoList = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)

    return resolveInfoList.map {
        AppItem(
            packageName = it.activityInfo.packageName,
            name = it.loadLabel(pm).toString(),
            icon = it.loadIcon(pm)
        )
    }
        .filter { it.packageName != context.packageName }
        .sortedBy { it.name.lowercase() }
}

@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val hafizaDefteri = context.getSharedPreferences("UygulamaAyarlari", Context.MODE_PRIVATE)

    val isSystemLocked = remember { mutableStateOf(hafizaDefteri.getBoolean("sistemKilitli", false)) }
    val wordsLearned = remember { mutableStateOf(hafizaDefteri.getInt("ogrenilenKelime", 0)) }
    val savedLevel = hafizaDefteri.getString("secilenSeviye", "A1") ?: "A1"
    val savedSayi = hafizaDefteri.getString("secilenSayi", "5") ?: "5"
    val savedBirim = hafizaDefteri.getString("secilenBirim", "Dakika") ?: "Dakika"
    val timeIntervalText = "Every $savedSayi ${if (savedBirim == "Dakika") "mins" else "hours"}"

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                wordsLearned.value = hafizaDefteri.getInt("ogrenilenKelime", 0)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val backgroundColor = Color(0xFFF7F8F3)
    val primaryGreen = Color(0xFF86AE7C)
    val lightGreen = Color(0xFFE4EDE0)
    val darkText = Color(0xFF2D312E)
    val grayText = Color(0xFF757575)
    val orangeAccent = Color(0xFFF09A36)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, primaryGreen)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .background(lightGreen, shape = RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security",
                                tint = primaryGreen
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "System Lock",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkText
                        )
                    }

                    Switch(
                        checked = isSystemLocked.value,
                        onCheckedChange = { isChecked ->
                            isSystemLocked.value = isChecked
                            hafizaDefteri.edit().putBoolean("sistemKilitli", isChecked).apply()

                            val servisIntent = Intent(context, AppMonitorService::class.java)

                            if (isChecked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(servisIntent)
                                } else {
                                    context.startService(servisIntent)
                                }
                            } else {
                                context.stopService(servisIntent)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = primaryGreen,
                            uncheckedThumbColor = grayText,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "WORDS LEARNED",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = grayText,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = wordsLearned.value.toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = orangeAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = "Your Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkText
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingCard(
                title = "LANGUAGE LEVEL",
                value = savedLevel,
                buttonText = "Change",
                onClick = { navController.navigate("level_selection_screen") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingCard(
                title = "TIME INTERVAL",
                value = timeIntervalText,
                buttonText = "Change",
                onClick = { navController.navigate("frequency_selection_screen") }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingCard(title: String, value: String, buttonText: String, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF757575),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D312E)
                )
            }

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF0F2EC),
                    contentColor = Color(0xFF2D312E)
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = buttonText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, primaryColor: Color) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { navController.navigate("dashboard_screen") {
                popUpTo("dashboard_screen") { inclusive = true }
            } },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryColor,
                selectedTextColor = primaryColor,
                indicatorColor = Color(0xFFE4EDE0)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("app_selection_screen") },
            icon = { Icon(Icons.Default.Apps, contentDescription = "Apps") },
            label = { Text("Apps") },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
        )
    }
}
