package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalStrings
import com.example.data.model.KycStatus
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.formatBdt
import com.example.data.repository.RimvoCatalog
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.MonoNumberStyle
import com.example.ui.viewmodel.RimvoUiState

@Composable
fun HomeScreen(
    uiState: RimvoUiState,
    onToggleLanguage: () -> Unit,
    onSearchChange: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onQuickAdd: (Product) -> Unit,
    onOpenCategories: (ProductCategory) -> Unit,
    onSelectBrand: (String) -> Unit,
    onOpenKyc: () -> Unit,
    onOpenInstallments: () -> Unit
) {
    val strings = LocalStrings.current
    val isBangla = uiState.prefs.language == AppLanguage.BN

    val groceryCombos = uiState.products.filter { it.category == ProductCategory.GROCERY }
    val electronicsList = uiState.products.filter {
        it.category == ProductCategory.ELECTRONICS || it.category == ProductCategory.APPLIANCES
    }
    val displayProducts = if (uiState.searchQuery.isNotBlank() || uiState.selectedBrand != null) {
        uiState.filteredProducts
    } else {
        uiState.products
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header + Rimvo Finance Credit Banner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_rimvo_icon_fg),
                                contentDescription = strings.appName,
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column {
                            Text(
                                text = strings.appName,
                                style = MaterialTheme.typography.titleLarge,
                                color = EmeraldDark
                            )
                            Text(
                                text = "rimvohut.com",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    LanguageTogglePill(
                        language = uiState.prefs.language,
                        onToggle = onToggleLanguage
                    )
                }

                // Credit Limit Banner
                val isVerified = uiState.kycState.status == KycStatus.VERIFIED
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(EmeraldDark, EmeraldPrimary)
                            )
                        )
                        .clickable {
                            if (isVerified) onOpenInstallments() else onOpenKyc()
                        }
                        .padding(16.dp)
                        .testTag("home_credit_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = EmeraldSoft,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "RIMVO FINANCE BNPL",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldSoft,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (isVerified) {
                                Text(
                                    text = "${strings.availableLimit}: ${formatBdt(uiState.availableCreditLimitBdt)}",
                                    style = MaterialTheme.typography.titleLarge.merge(MonoNumberStyle),
                                    color = Color.White
                                )
                                Text(
                                    text = "${strings.totalLimitLabel}: ${formatBdt(uiState.totalCreditLimitBdt)} • 3, 6, 12M Plans",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EmeraldSoft.copy(alpha = 0.9f)
                                )
                            } else {
                                Text(
                                    text = strings.verifyNidPrompt,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = { if (isVerified) onOpenInstallments() else onOpenKyc() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = EmeraldDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("home_credit_cta")
                        ) {
                            Icon(
                                imageVector = if (isVerified) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isVerified) strings.navInstallments else strings.verifyNow,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            text = strings.searchPlaceholder,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = EmeraldPrimary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )
            }
        }

        // Featured Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable { onOpenCategories(ProductCategory.ELECTRONICS) }
                    .testTag("home_featured_banner")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "Rimvo Hut Featured Installments",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        EmeraldDark.copy(alpha = 0.88f),
                                        EmeraldDark.copy(alpha = 0.25f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.CenterStart),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StatusBadge(
                                text = "0% MARKUP • 3 MONTHS",
                                containerColor = AmberSoft,
                                contentColor = Color(0xFF92400E)
                            )
                            Text(
                                text = if (isBangla) {
                                    "ওয়ালটন, ভিশন ও সিঙ্গার মেগা সেল"
                                } else {
                                    "Walton, Vision & Singer Mega Deal"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = if (isBangla) {
                                    "আজই কিনুন, ৩-১২ মাসের সহজ কিস্তিতে পরিশোধ করুন"
                                } else {
                                    "Shop today, split transparently over 3–12 months"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldSoft
                            )
                        }
                    }
                }
            }
        }

        // Electronics Brand Chips (Walton, Vision, Singer, Miyako, Rimvo Fresh)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isBangla) "অফিশিয়াল ব্র্যান্ডসমূহ" else "Trusted Brands on Rimvo Hut",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(RimvoCatalog.featuredBrands) { brand ->
                        val selected = uiState.selectedBrand == brand
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectBrand(brand) },
                            label = { Text(brand, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (brand) {
                                        "Rimvo Fresh" -> Icons.Default.LocalGroceryStore
                                        "Walton", "Vision" -> Icons.Default.Tv
                                        "Singer" -> Icons.Default.Kitchen
                                        else -> Icons.Default.ElectricBolt
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            modifier = Modifier.testTag("brand_chip_$brand")
                        )
                    }
                }
            }
        }

        // Curated Grocery Combos Section
        if (uiState.searchQuery.isBlank() && uiState.selectedBrand == null) {
            item {
                SectionHeader(
                    title = strings.groceryCombos,
                    actionLabel = strings.seeAll,
                    onAction = { onOpenCategories(ProductCategory.GROCERY) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(groceryCombos, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isBangla = isBangla,
                            onClick = { onProductClick(product) },
                            onQuickAdd = { onQuickAdd(product) },
                            modifier = Modifier.width(210.dp)
                        )
                    }
                }
            }

            // Electronics & Appliances Highlight Row
            item {
                SectionHeader(
                    title = strings.electronicsTitle,
                    actionLabel = strings.seeAll,
                    onAction = { onOpenCategories(ProductCategory.ELECTRONICS) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(electronicsList, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isBangla = isBangla,
                            onClick = { onProductClick(product) },
                            onQuickAdd = { onQuickAdd(product) },
                            modifier = Modifier.width(210.dp)
                        )
                    }
                }
            }
        }

        // Full / Filtered Product Grid (2-column pairs)
        item {
            SectionHeader(
                title = if (uiState.selectedBrand != null) {
                    "${uiState.selectedBrand} Products"
                } else {
                    strings.allProducts
                },
                actionLabel = strings.seeAll,
                onAction = { onOpenCategories(ProductCategory.ALL) }
            )
        }

        val pairs = displayProducts.chunked(2)
        items(pairs) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { product ->
                    ProductCard(
                        product = product,
                        isBangla = isBangla,
                        onClick = { onProductClick(product) },
                        onQuickAdd = { onQuickAdd(product) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onAction) {
            Text(
                text = actionLabel,
                color = EmeraldBright,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
