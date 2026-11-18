package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalStrings
import com.example.data.model.KycStatus
import com.example.data.network.NetworkModule
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.DueSoonAmber
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.OutlineHairline
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenSoft
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.RimvoUiState

@Composable
fun ProfileScreen(
    uiState: RimvoUiState,
    onToggleLanguage: () -> Unit,
    onOpenKyc: () -> Unit,
    onToggleWhatsappReminders: (Boolean) -> Unit,
    onTogglePushReminders: (Boolean) -> Unit,
    onSetPinLock: (Boolean, String) -> Unit,
    onOpenWhatsappSupport: () -> Unit,
    onSignOut: () -> Unit
) {
    val strings = LocalStrings.current
    var showPinSetupDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = strings.profileTitle,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // User Identity & KYC Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldSoft,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = uiState.kycState.fullName.ifBlank { "Rimvo Hut Shopper" },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (uiState.prefs.phoneNumber.isNotBlank()) {
                                "+88${uiState.prefs.phoneNumber}"
                            } else {
                                "Guest Mode • rimvohut.com"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    StatusBadge(
                        text = when (uiState.kycState.status) {
                            KycStatus.VERIFIED -> strings.statusVerified
                            KycStatus.PENDING_REVIEW -> strings.statusPending
                            KycStatus.ACTION_REQUIRED -> strings.statusActionRequired
                            KycStatus.NOT_STARTED -> strings.statusNotStarted
                        },
                        containerColor = if (uiState.kycState.status == KycStatus.VERIFIED) {
                            VerifiedGreenSoft
                        } else {
                            AmberSoft
                        },
                        contentColor = if (uiState.kycState.status == KycStatus.VERIFIED) {
                            VerifiedGreen
                        } else {
                            DueSoonAmber
                        },
                        showVerifiedIcon = uiState.kycState.status == KycStatus.VERIFIED
                    )
                }
            }
        }

        // KYC Entry & Bilingual Language Row
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenKyc)
                            .padding(16.dp)
                            .testTag("profile_open_kyc"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.kycTitle, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "NID, Utility Bill & Nominee Verification",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    }

                    HorizontalDivider(color = OutlineHairline)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onToggleLanguage)
                            .padding(16.dp)
                            .testTag("profile_language_row"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("App Language / ভাষা", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = if (uiState.prefs.language == AppLanguage.BN) "বাংলা (সক্রিয়)" else "English (Active)",
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldDark
                            )
                        }
                        LanguageTogglePill(
                            language = uiState.prefs.language,
                            onToggle = onToggleLanguage
                        )
                    }
                }
            }
        }

        // Security & PIN Lock Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(strings.securitySection, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.pinLockTitle, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = strings.pinLockSubtitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.prefs.pinLockEnabled,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    showPinSetupDialog = true
                                } else {
                                    onSetPinLock(false, uiState.prefs.appPin)
                                }
                            },
                            modifier = Modifier.testTag("profile_pin_switch")
                        )
                    }
                }
            }
        }

        // Notifications & WhatsApp 3-Day Reminders
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(strings.notificationsSection, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = WhatsAppGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.whatsappReminderTitle, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = strings.whatsappReminderSubtitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.prefs.whatsappRemindersEnabled,
                            onCheckedChange = onToggleWhatsappReminders,
                            modifier = Modifier.testTag("profile_whatsapp_reminder_switch")
                        )
                    }

                    HorizontalDivider(color = OutlineHairline)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.pushReminderTitle,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = uiState.prefs.pushRemindersEnabled,
                            onCheckedChange = onTogglePushReminders,
                            modifier = Modifier.testTag("profile_push_switch")
                        )
                    }
                }
            }
        }

        // WooCommerce + Rimvo Finance API Connectivity Summary
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = EmeraldPrimary)
                        Text(strings.apiStatusTitle, style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "Store: ${NetworkModule.WOOCOMMERCE_BASE_URL}wp-json/wc/v3/\nFinance: ${NetworkModule.RIMVO_FINANCE_BASE_URL}api/v1/\nEndpoints: /kyc, /credit-limit, /installment-plans, /repayments",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onOpenWhatsappSupport,
                        shape = RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_whatsapp_support_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.openWhatsappBtn, color = Color.White)
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onSignOut,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("profile_signout_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.logoutBtn)
            }
        }
    }

    if (showPinSetupDialog) {
        var newPin by remember { mutableStateOf("1234") }
        AlertDialog(
            onDismissRequest = { showPinSetupDialog = false },
            title = { Text(strings.pinLockTitle) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Set a 4-digit PIN to protect your Rimvo Finance account.")
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPin = it },
                        label = { Text("4-Digit PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetPinLock(true, newPin)
                        showPinSetupDialog = false
                    },
                    enabled = newPin.length == 4
                ) {
                    Text("Enable PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinSetupDialog = false }) {
                    Text(strings.skipBtn)
                }
            }
        )
    }
}
