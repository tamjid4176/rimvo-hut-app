package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalStrings
import com.example.data.model.InstallmentCalculator
import com.example.data.model.InstallmentTenure
import com.example.data.model.Product
import com.example.data.model.formatBdt
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.MonoNumberStyle
import com.example.ui.theme.OutlineHairline
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenSoft

@Composable
fun ProductDetailScreen(
    product: Product,
    language: AppLanguage,
    onBack: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onBuyWithInstallment: (Product, InstallmentTenure) -> Unit
) {
    BackHandler(onBack = onBack)

    val strings = LocalStrings.current
    val isBangla = language == AppLanguage.BN
    val title = if (isBangla) product.nameBn else product.nameEn
    val specs = if (isBangla) product.specsBn else product.specsEn

    var selectedTenure by remember { mutableStateOf(InstallmentTenure.THREE_MONTHS) }
    val breakdown = remember(product.cashPriceBdt, selectedTenure) {
        InstallmentCalculator.calculate(product.cashPriceBdt, selectedTenure)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Product Hero Image & Top Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Image(
                        painter = painterResource(id = product.imageRes),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.92f),
                            shadowElevation = 2.dp
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.testTag("product_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = EmeraldDark
                                )
                            }
                        }

                        StatusBadge(
                            text = if (isBangla) product.badgeBn else product.badgeEn,
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White,
                            showVerifiedIcon = true
                        )
                    }
                }
            }

            // Product Title, Brand, Rating & Cash Price
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(
                            text = product.brand,
                            containerColor = EmeraldSoft,
                            contentColor = EmeraldDark
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${product.rating} (${product.reviewCount})",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = formatBdt(product.cashPriceBdt),
                            style = MaterialTheme.typography.headlineMedium.merge(MonoNumberStyle),
                            color = EmeraldDark
                        )
                        if (product.originalPriceBdt > product.cashPriceBdt) {
                            Text(
                                text = formatBdt(product.originalPriceBdt),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    textDecoration = TextDecoration.LineThrough
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = strings.cashPrice,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Live Rimvo Finance Installment Calculator Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("live_installment_calculator")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = EmeraldPrimary
                                )
                                Text(
                                    text = strings.liveCalculatorTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EmeraldDark
                                )
                            }
                        }

                        Text(
                            text = strings.selectPlan,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // 3M / 6M / 12M Interactive Tenure Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InstallmentTenure.entries.forEach { tenure ->
                                val isSelected = tenure == selectedTenure
                                val tenureTitle = when (tenure) {
                                    InstallmentTenure.THREE_MONTHS -> strings.months3
                                    InstallmentTenure.SIX_MONTHS -> strings.months6
                                    InstallmentTenure.TWELVE_MONTHS -> strings.months12
                                }
                                val feeTag = if (tenure.markupPercent == 0) {
                                    strings.zeroMarkupBadge
                                } else {
                                    strings.transparentFeeBadge.format(tenure.markupPercent)
                                }

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (isSelected) null else BorderStroke(1.dp, OutlineHairline),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { selectedTenure = tenure }
                                        .testTag("plan_selector_${tenure.months}m")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = tenureTitle,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = feeTag,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) EmeraldSoft else EmeraldPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        // Real-Time Breakdown Rows
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = EmeraldSoft.copy(alpha = 0.65f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BreakdownRow(
                                    label = strings.productPriceLabel,
                                    value = formatBdt(breakdown.cashPriceBdt)
                                )
                                BreakdownRow(
                                    label = "${strings.transparentMarkupLabel} (${selectedTenure.markupPercent}%)",
                                    value = if (breakdown.markupAmountBdt == 0) "৳0 (0%)" else "+${formatBdt(breakdown.markupAmountBdt)}",
                                    highlightColor = if (breakdown.markupAmountBdt == 0) VerifiedGreen else MaterialTheme.colorScheme.onSurface
                                )
                                HorizontalDivider(color = EmeraldPrimary.copy(alpha = 0.2f))
                                BreakdownRow(
                                    label = strings.downPaymentTodayLabel,
                                    value = formatBdt(breakdown.downPaymentTodayBdt),
                                    bold = true,
                                    highlightColor = EmeraldDark
                                )
                                BreakdownRow(
                                    label = "${strings.monthlyInstallmentLabel} (${selectedTenure.months}x)",
                                    value = "${formatBdt(breakdown.monthlyAmount)} / mo",
                                    bold = true,
                                    highlightColor = EmeraldPrimary
                                )
                                BreakdownRow(
                                    label = strings.totalPayableLabel,
                                    value = formatBdt(breakdown.totalPayableBdt)
                                )
                            }
                        }

                        // Exact Monthly Installment Dates & Amounts Timeline Preview
                        Text(
                            text = strings.schedulePreviewTitle,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            breakdown.schedule.take(6).forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "#${item.installmentNumber} • ${item.dueDateLabel}",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                    Text(
                                        text = formatBdt(item.amountBdt),
                                        style = MaterialTheme.typography.labelLarge.merge(MonoNumberStyle),
                                        color = EmeraldDark
                                    )
                                }
                            }
                            if (breakdown.schedule.size > 6) {
                                Text(
                                    text = "+ ${breakdown.schedule.size - 6} more monthly installments of ${formatBdt(breakdown.monthlyAmount)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Key Specifications Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = strings.specsTitle,
                            style = MaterialTheme.typography.titleMedium
                        )
                        specs.forEach { spec ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = VerifiedGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = spec,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sticky Bottom Action Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onAddToCart(product) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("detail_add_to_cart")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.addToCart, maxLines = 1)
                }

                Button(
                    onClick = { onBuyWithInstallment(product, selectedTenure) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier
                        .weight(1.35f)
                        .height(52.dp)
                        .testTag("detail_buy_installments")
                ) {
                    Text(
                        text = "${strings.buyWithInstallment} (${selectedTenure.months}M)",
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    bold: Boolean = false,
    highlightColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (bold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            color = if (bold) highlightColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.merge(MonoNumberStyle),
            color = highlightColor
        )
    }
}
