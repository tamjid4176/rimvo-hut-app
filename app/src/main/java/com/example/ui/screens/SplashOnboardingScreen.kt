package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalStrings
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.OverdueRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashOnboardingScreen(
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onRequestOtp: (String) -> Unit,
    onVerifyOtp: (phone: String, otp: String, onError: (String) -> Unit) -> Unit,
    onExploreAsGuest: () -> Unit
) {
    val strings = LocalStrings.current
    var showLogoSplash by remember { mutableStateOf(true) }
    var showOtpLogin by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(950)
        showLogoSplash = false
    }

    if (showLogoSplash) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(EmeraldDark, EmeraldPrimary)
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + scaleIn(
                    initialScale = 0.85f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier.size(92.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_rimvo_icon_fg),
                            contentDescription = strings.appName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text(
                        text = strings.appName,
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "rimvohut.com • Rimvo Finance",
                        style = MaterialTheme.typography.labelLarge,
                        color = EmeraldSoft
                    )
                }
            }
        }
        return
    }

    if (showOtpLogin) {
        MobileOtpAuthScreen(
            language = language,
            onToggleLanguage = onToggleLanguage,
            onRequestOtp = onRequestOtp,
            onVerifyOtp = onVerifyOtp,
            onBrowseGuest = onExploreAsGuest
        )
        return
    }

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val slides = listOf(
        Triple(strings.onboardingTitle1, strings.onboardingDesc1, Icons.Default.ShoppingBag),
        Triple(strings.onboardingTitle2, strings.onboardingDesc2, Icons.Default.AccountBalanceWallet),
        Triple(strings.onboardingTitle3, strings.onboardingDesc3, Icons.Default.VerifiedUser)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(34.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_rimvo_icon_fg),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    text = strings.appName,
                    style = MaterialTheme.typography.titleLarge,
                    color = EmeraldDark
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LanguageTogglePill(language = language, onToggle = onToggleLanguage)
                TextButton(
                    onClick = onExploreAsGuest,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(strings.skipBtn)
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            val (title, desc, icon) = slides[page]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner),
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(14.dp)
                                .size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                StatusBadge(
                    text = "3 • 6 • 12 Months BNPL",
                    containerColor = AmberSoft,
                    contentColor = Color(0xFF92400E)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { idx ->
                    val selected = pagerState.currentPage == idx
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (selected) 26.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) EmeraldPrimary else EmeraldPrimary.copy(alpha = 0.25f)
                            )
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        showOtpLogin = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("onboarding_primary_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage < 2) strings.continueBtn else strings.getStarted,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }

            OutlinedButton(
                onClick = { showOtpLogin = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("onboarding_otp_login_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.loginTitle)
            }
        }
    }
}

@Composable
private fun MobileOtpAuthScreen(
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onRequestOtp: (String) -> Unit,
    onVerifyOtp: (phone: String, otp: String, onError: (String) -> Unit) -> Unit,
    onBrowseGuest: () -> Unit
) {
    val strings = LocalStrings.current
    var phone by remember { mutableStateOf("01712345678") }
    var otpCode by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.appName,
                style = MaterialTheme.typography.titleLarge,
                color = EmeraldDark
            )
            LanguageTogglePill(language = language, onToggle = onToggleLanguage)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = CircleShape,
            color = EmeraldSoft,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Text(
            text = strings.loginTitle,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = strings.loginSubtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        if (it.length <= 14) phone = it
                        errorMsg = null
                    },
                    label = { Text(strings.mobileLabel) },
                    placeholder = { Text(strings.mobileHint) },
                    leadingIcon = { Text("+88", fontWeight = FontWeight.Bold, color = EmeraldPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("otp_phone_input")
                )

                if (otpSent) {
                    Text(
                        text = "${strings.otpSentHint} +88$phone",
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldPrimary
                    )
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = {
                            if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                otpCode = it
                                errorMsg = null
                            }
                        },
                        label = { Text(strings.otpLabel) },
                        placeholder = { Text("1234") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_code_input")
                    )
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        style = MaterialTheme.typography.labelMedium,
                        color = OverdueRed
                    )
                }

                Button(
                    onClick = {
                        if (!otpSent) {
                            if (phone.length < 10) {
                                errorMsg = "Enter a valid Bangladeshi mobile number"
                            } else {
                                onRequestOtp(phone)
                                otpSent = true
                                otpCode = "1234"
                            }
                        } else {
                            onVerifyOtp(phone, otpCode) { err -> errorMsg = err }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("otp_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text(if (otpSent) strings.verifyOtp else strings.sendOtp)
                }
            }
        }

        TextButton(
            onClick = onBrowseGuest,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .testTag("explore_guest_button")
        ) {
            Text(strings.browseAsGuest)
        }
    }
}
