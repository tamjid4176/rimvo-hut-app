package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalStrings
import com.example.data.model.PaymentReceipt
import com.example.data.model.Product
import com.example.data.model.formatBdt
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.MonoNumberStyle
import com.example.ui.theme.OutlineHairline
import com.example.ui.theme.OverdueRed
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenSoft
import com.example.ui.theme.WhatsAppGreen

@Composable
fun LanguageTogglePill(
    language: AppLanguage,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onToggle,
        shape = CircleShape,
        color = EmeraldSoft,
        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
        modifier = modifier.testTag("language_toggle_pill")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = "Switch language",
                tint = EmeraldDark,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (language == AppLanguage.EN) "বাংলা" else "EN",
                style = MaterialTheme.typography.labelLarge,
                color = EmeraldDark,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    showVerifiedIcon: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = containerColor,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (showVerifiedIcon) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    isBangla: Boolean,
    onClick: () -> Unit,
    onQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val title = if (isBangla) product.nameBn else product.nameEn
    val badgeText = if (isBangla) product.badgeBn else product.badgeEn

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("product_card_${product.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                StatusBadge(
                    text = badgeText,
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.brand.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${product.rating}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Installment pill highlighted cleanly
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldSoft,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strings.fromPerMonth.format(
                            formatBdt(product.lowestMonthlyBdt).removePrefix("৳")
                        ),
                        style = MaterialTheme.typography.labelMedium.merge(MonoNumberStyle),
                        color = EmeraldDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        maxLines = 1
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formatBdt(product.cashPriceBdt),
                            style = MaterialTheme.typography.titleMedium.merge(MonoNumberStyle),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (product.originalPriceBdt > product.cashPriceBdt) {
                            Text(
                                text = formatBdt(product.originalPriceBdt),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    textDecoration = TextDecoration.LineThrough
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        onClick = onQuickAdd,
                        shape = CircleShape,
                        color = EmeraldPrimary,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("quick_add_${product.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = strings.addToCart,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingWhatsappSupportButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = WhatsAppGreen,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = modifier
            .size(52.dp)
            .testTag("floating_whatsapp_support")
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = "WhatsApp Customer Support",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun WhatsappSupportDialog(
    context: Context,
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(WhatsAppGreen.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = WhatsAppGreen
                )
            }
        },
        title = {
            Text(
                text = strings.whatsappSupportTitle,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = strings.whatsappSupportDesc,
                    style = MaterialTheme.typography.bodyMedium
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "Support Desk: +880 1711-009988\nHours: 9:00 AM – 10:00 PM BST",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val uri = Uri.parse(
                        "https://wa.me/8801711009988?text=" +
                            Uri.encode("Hello Rimvo Hut Support, I need help with my Rimvo Finance account.")
                    )
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                modifier = Modifier.testTag("confirm_open_whatsapp")
            ) {
                Text(strings.openWhatsappBtn, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.skipBtn)
            }
        }
    )
}

@Composable
fun DigitalReceiptDialog(
    receipt: PaymentReceipt,
    context: Context,
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("digital_receipt_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = EmeraldPrimary
                        )
                        Text(
                            text = strings.digitalReceiptTitle,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = VerifiedGreenSoft,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = VerifiedGreen
                        )
                        Column {
                            Text(
                                text = formatBdt(receipt.amountPaidBdt),
                                style = MaterialTheme.typography.headlineSmall.merge(MonoNumberStyle),
                                color = VerifiedGreen
                            )
                            Text(
                                text = "${strings.statusPaid} • ${receipt.paidDateLabel}",
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldDark
                            )
                        }
                    }
                }

                ReceiptRow("Receipt No", receipt.receiptNumber)
                ReceiptRow("Order ID", receipt.orderId)
                ReceiptRow("Item", receipt.productTitle)
                ReceiptRow("Installment", "#${receipt.installmentNumber}")
                ReceiptRow("Channel", receipt.channel)
                ReceiptRow("TrxID", receipt.transactionId)

                HorizontalDivider(color = OutlineHairline)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareText = buildString {
                                appendLine("Rimvo Finance Payment Receipt")
                                appendLine("Receipt: ${receipt.receiptNumber}")
                                appendLine("Order: ${receipt.orderId}")
                                appendLine("Amount: ${formatBdt(receipt.amountPaidBdt)}")
                                appendLine("Channel: ${receipt.channel} (${receipt.transactionId})")
                                appendLine("Date: ${receipt.paidDateLabel}")
                            }
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            runCatching {
                                context.startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_receipt_button"),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share / Save")
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PinLockOverlay(
    onVerifyPin: (String) -> Boolean
) {
    var pinInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(EmeraldSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "Rimvo Finance Lock",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Enter your 4-digit PIN to unlock your store & installment dashboard.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        if (it.length <= 4 && it.all { ch -> ch.isDigit() }) {
                            pinInput = it
                            isError = false
                        }
                    },
                    label = { Text("4-Digit PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Incorrect PIN. Please try again.", color = OverdueRed) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pin_unlock_input")
                )
                Button(
                    onClick = {
                        if (!onVerifyPin(pinInput)) {
                            isError = true
                        }
                    },
                    enabled = pinInput.length == 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("pin_unlock_submit")
                ) {
                    Text("Unlock Rimvo Hut", fontSize = 15.sp)
                }
            }
        }
    }
}
