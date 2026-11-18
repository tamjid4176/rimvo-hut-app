package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalStrings
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.repository.RimvoCatalog
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.RimvoUiState

@Composable
fun CategoriesScreen(
    uiState: RimvoUiState,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (ProductCategory) -> Unit,
    onSelectBrand: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onQuickAdd: (Product) -> Unit
) {
    val strings = LocalStrings.current
    val isBangla = uiState.prefs.language == AppLanguage.BN
    val filtered = uiState.filteredProducts

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = strings.navCategories,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text(strings.searchPlaceholder) },
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
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("categories_search_input")
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ProductCategory.entries) { category ->
                    val selected = uiState.selectedCategory == category
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectCategory(category) },
                        label = {
                            Text(
                                text = if (isBangla) category.titleBn else category.titleEn,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("category_chip_${category.name}")
                    )
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(RimvoCatalog.featuredBrands) { brand ->
                    val selected = uiState.selectedBrand == brand
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectBrand(brand) },
                        label = { Text(brand) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("cat_brand_chip_$brand")
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isBangla) "কোনো পণ্য পাওয়া যায়নি" else "No matching products",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isBangla) "অন্য কীওয়ার্ড বা ক্যাটাগরি চেষ্টা করুন।" else "Try clearing a brand filter or searching another keyword.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val pairs = filtered.chunked(2)
            LazyColumn(
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pairs) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
    }
}
