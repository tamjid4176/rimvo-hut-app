package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.LocalStrings
import com.example.data.model.InstallmentEntry
import com.example.data.model.InstallmentOrderItem
import com.example.data.model.InstallmentPaymentStatus
import com.example.data.model.KycStatus
import com.example.data.model.PaymentChannel
import com.example.data.model.formatBdt
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DueSoonAmber
import com.example.ui.theme.DueSoonAmberSoft
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.MonoNumberStyle
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.OutlineHairline
import com.example.ui.theme.OverdueRed
import com.example.ui.theme.OverdueRedSoft
import com.example.ui.theme.RocketPurple
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenSoft
import com.example.ui.viewmodel.RimvoUiState

@Composable
fun InstallmentsDashboardScreen(
    uiState: RimvoUiState,
    onPayInstallment: (InstallmentOrderItem, InstallmentEntry, PaymentChannel, String) -> Unit,
    onViewReceipt: (InstallmentOrderItem, InstallmentEntry) -> Unit,
    onOpenKyc: () -> Unit
) {
    val strings = LocalStrings.current
    var expandedOrderIds by remember { mutableStateOf(setOf<String>()) }
    var repayTarget by remember { mutableStateOf<Pair<InstallmentOrderItem, InstallmentEntry>?>(null) }

    val activeOrders = uiState.installmentOrders.filter { !it.isCompleted }
    val completedOrders = uiState.installmentOrders.filter { it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.dashboardTitle,
                    style = MaterialTheme.typography.headlineMedium
                )
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

        // Visual Credit Limit Progress Bar Card
        item {
            val total = uiState.totalCreditLimitBdt.coerceAtLeast(1)
            val usedFraction = (uiState.usedCreditBdt.toFloat() / total.toFloat()).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(listOf(EmeraldDark, EmeraldPrimary))
                    )
                    .padding(18.dp)
                    .testTag("credit_limit_progress_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = EmeraldSoft
                            )
                            Text(
                                text = strings.availableLimit,
                                style = MaterialTheme.typography.titleSmall,
                                color = EmeraldSoft
                            )
                        }
                        if (uiState.kycState.status != KycStatus.VERIFIED) {
                            TextButton(
                                onClick = onOpenKyc,
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.verifyNow)
                            }
                        }
                    }

                    Text(
                        text = formatBdt(uiState.availableCreditLimitBdt),
                        style = MaterialTheme.typography.displaySmall.merge(MonoNumberStyle),
                        color = Color.White
                    )

                    LinearProgressIndicator(
                        progress = { 1f - usedFraction },
                        color = EmeraldBright,
                        trackColor = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${strings.usedLimitLabel}: ${formatBdt(uiState.usedCreditBdt)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = EmeraldSoft
                        )
                        Text(
                            text = "${strings.totalLimitLabel}: ${formatBdt(uiState.totalCreditLimitBdt)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Upcoming Payments Highlight Card
        val nextDue = uiState.nextDueInstallment
        if (nextDue != null) {
            val (order, entry) = nextDue
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, AmberAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upcoming_payment_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = DueSoonAmber
                                )
                                Text(
                                    text = strings.upcomingPaymentTitle,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            StatusBadge(
                                text = strings.statusDueSoon,
                                containerColor = DueSoonAmberSoft,
                                contentColor = DueSoonAmber
                            )
                        }

                        Text(
                            text = "${order.productTitle} (#${entry.installmentNumber}/${order.tenureMonths})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = formatBdt(entry.amountBdt),
                                    style = MaterialTheme.typography.headlineSmall.merge(MonoNumberStyle),
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "Due: ${entry.dueDateLabel}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DueSoonAmber
                                )
                            }

                            Button(
                                onClick = { repayTarget = order to entry },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("upcoming_pay_now_btn")
                            ) {
                                Text(strings.payNowBtn, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Active Installment Plans
        item {
            Text(
                text = strings.activePlansTitle,
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(activeOrders, key = { it.orderId }) { order ->
            val expanded = expandedOrderIds.contains(order.orderId) || activeOrders.size == 1
            InstallmentOrderPlanCard(
                order = order,
                expanded = expanded,
                onToggleExpand = {
                    expandedOrderIds = if (expandedOrderIds.contains(order.orderId)) {
                        expandedOrderIds - order.orderId
                    } else {
                        expandedOrderIds + order.orderId
                    }
                },
                onPayEntry = { entry -> repayTarget = order to entry },
                onViewReceipt = { entry -> onViewReceipt(order, entry) }
            )
        }

        // Past / Completed Plans
        if (completedOrders.isNotEmpty()) {
            item {
                Text(
                    text = strings.pastPlansTitle,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            items(completedOrders, key = { it.orderId }) { order ->
                val expanded = expandedOrderIds.contains(order.orderId)
                InstallmentOrderPlanCard(
                    order = order,
                    expanded = expanded,
                    onToggleExpand = {
                        expandedOrderIds = if (expandedOrderIds.contains(order.orderId)) {
                            expandedOrderIds - order.orderId
                        } else {
                            expandedOrderIds + order.orderId
                        }
                    },
                    onPayEntry = {},
                    onViewReceipt = { entry -> onViewReceipt(order, entry) }
                )
            }
        }
    }

    repayTarget?.let { (order, entry) ->
        RepaymentDialog(
            order = order,
            entry = entry,
            onDismiss = { repayTarget = null },
            onConfirmPay = { channel, txId ->
                onPayInstallment(order, entry, channel, txId)
                repayTarget = null
            }
        )
    }
}

@Composable
private fun InstallmentOrderPlanCard(
    order: InstallmentOrderItem,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onPayEntry: (InstallmentEntry) -> Unit,
    onViewReceipt: (InstallmentEntry) -> Unit
) {
    val strings = LocalStrings.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plan_card_${order.orderId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = order.orderId,
                            style = MaterialTheme.typography.labelLarge.merge(MonoNumberStyle),
                            color = EmeraldPrimary
                        )
                        StatusBadge(
                            text = if (order.isCompleted) strings.statusPaid else strings.statusActive,
                            containerColor = if (order.isCompleted) VerifiedGreenSoft else EmeraldSoft,
                            contentColor = if (order.isCompleted) VerifiedGreen else EmeraldDark
                        )
                    }
                    Text(
                        text = order.productTitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${order.paidCount}/${order.tenureMonths} paid • Remaining ${formatBdt(order.remainingBalanceBdt)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle timeline",
                    tint = EmeraldPrimary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = OutlineHairline)

                    order.schedule.forEach { entry ->
                        val (badgeText, badgeBg, badgeFg) = when (entry.status) {
                            InstallmentPaymentStatus.PAID -> Triple(strings.statusPaid, VerifiedGreenSoft, VerifiedGreen)
                            InstallmentPaymentStatus.UPCOMING -> Triple(strings.statusUpcoming, DueSoonAmberSoft, DueSoonAmber)
                            InstallmentPaymentStatus.OVERDUE -> Triple(strings.statusOverdue, OverdueRedSoft, OverdueRed)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (entry.status == InstallmentPaymentStatus.PAID) {
                                        Icons.Default.CheckCircle
                                    } else {
                                        Icons.Default.Schedule
                                    },
                                    contentDescription = null,
                                    tint = badgeFg,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Installment #${entry.installmentNumber} • ${entry.dueDateLabel}",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Text(
                                        text = formatBdt(entry.amountBdt),
                                        style = MaterialTheme.typography.titleSmall.merge(MonoNumberStyle),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (entry.status == InstallmentPaymentStatus.PAID) {
                                OutlinedButton(
                                    onClick = { onViewReceipt(entry) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("view_receipt_${order.orderId}_${entry.installmentNumber}")
                                ) {
                                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(strings.viewReceiptBtn, style = MaterialTheme.typography.labelSmall)
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    StatusBadge(text = badgeText, containerColor = badgeBg, contentColor = badgeFg)
                                    Button(
                                        onClick = { onPayEntry(entry) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("pay_entry_${order.orderId}_${entry.installmentNumber}")
                                    ) {
                                        Text(strings.payNowBtn, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RepaymentDialog(
    order: InstallmentOrderItem,
    entry: InstallmentEntry,
    onDismiss: () -> Unit,
    onConfirmPay: (PaymentChannel, String) -> Unit
) {
    val strings = LocalStrings.current
    var channel by remember { mutableStateOf(PaymentChannel.BKASH) }
    var txId by remember { mutableStateOf("BKH${(100000..999999).random()}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${strings.payNowBtn} • #${entry.installmentNumber}",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${order.productTitle}\nAmount Due: ${formatBdt(entry.amountBdt)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(PaymentChannel.BKASH, PaymentChannel.NAGAD, PaymentChannel.ROCKET).forEach { ch ->
                        val selected = ch == channel
                        val color = when (ch) {
                            PaymentChannel.BKASH -> BkashPink
                            PaymentChannel.NAGAD -> NagadOrange
                            else -> RocketPurple
                        }
                        Surface(
                            onClick = { channel = ch },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) color else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ch.label,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = txId,
                    onValueChange = { txId = it },
                    label = { Text(strings.txIdLabel) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("repay_txid_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmPay(channel, txId) },
                enabled = txId.isNotBlank(),
                modifier = Modifier.testTag("repay_confirm_btn")
            ) {
                Text("Confirm ${formatBdt(entry.amountBdt)}")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.skipBtn)
            }
        }
    )
}
