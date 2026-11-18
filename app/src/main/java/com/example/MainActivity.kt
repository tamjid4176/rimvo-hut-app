package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.BanglaStrings
import com.example.core.i18n.EnglishStrings
import com.example.core.i18n.LocalStrings
import com.example.data.model.InstallmentTenure
import com.example.data.model.KycStatus
import com.example.data.repository.RimvoCatalog
import com.example.ui.screens.CartCheckoutScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.DigitalReceiptDialog
import com.example.ui.screens.FloatingWhatsappSupportButton
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InstallmentsDashboardScreen
import com.example.ui.screens.KycWizardScreen
import com.example.ui.screens.PinLockOverlay
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashOnboardingScreen
import com.example.ui.screens.WhatsappSupportDialog
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.RimvoHutTheme
import com.example.ui.viewmodel.RimvoViewModel
import com.example.ui.viewmodel.RimvoViewModelFactory

object RimvoRoutes {
    const val HOME = "home"
    const val CATEGORIES = "categories"
    const val INSTALLMENTS = "installments"
    const val CART = "cart"
    const val PROFILE = "profile"
    const val KYC = "kyc"
    const val PRODUCT_DETAIL = "product/{productId}"

    fun productDetail(productId: String) = "product/$productId"
}

private data class BottomTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as RimvoHutApp
        val factory = RimvoViewModelFactory(app.repository)

        setContent {
            val viewModel: RimvoViewModel = viewModel(factory = factory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val isBangla = uiState.prefs.language == AppLanguage.BN
            val strings = if (isBangla) BanglaStrings else EnglishStrings

            CompositionLocalProvider(LocalStrings provides strings) {
                RimvoHutTheme(isBangla = isBangla) {
                    RimvoRootApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun RimvoRootApp(viewModel: RimvoViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val strings = LocalStrings.current
    val context = LocalContext.current

    if (!uiState.isPinUnlocked) {
        PinLockOverlay(onVerifyPin = viewModel::verifyAppPin)
        return
    }

    if (!uiState.prefs.onboardingCompleted) {
        SplashOnboardingScreen(
            language = uiState.prefs.language,
            onToggleLanguage = viewModel::toggleLanguage,
            onRequestOtp = viewModel::requestOtp,
            onVerifyOtp = { phone, otp, onError ->
                viewModel.verifyOtp(phone, otp, onSuccess = {}, onError = onError)
            },
            onExploreAsGuest = viewModel::completeOnboardingAsGuest
        )
        return
    }

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    var showWhatsappDialog by remember { mutableStateOf(false) }
    var checkoutInstallmentMode by remember { mutableStateOf(true) }
    var checkoutTenure by remember { mutableStateOf(InstallmentTenure.THREE_MONTHS) }

    LaunchedEffect(uiState.statusMessage) {
        val msg = uiState.statusMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    val tabs = listOf(
        BottomTab(RimvoRoutes.HOME, strings.navHome, Icons.Filled.Home, Icons.Outlined.Home),
        BottomTab(RimvoRoutes.CATEGORIES, strings.navCategories, Icons.Filled.GridView, Icons.Outlined.GridView),
        BottomTab(RimvoRoutes.INSTALLMENTS, strings.navInstallments, Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
        BottomTab(RimvoRoutes.CART, strings.navCart, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        BottomTab(RimvoRoutes.PROFILE, strings.navProfile, Icons.Filled.Person, Icons.Outlined.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomChrome = tabs.any { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    val navigateToTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                if (showBottomChrome) {
                    FloatingWhatsappSupportButton(
                        onClick = { showWhatsappDialog = true }
                    )
                }
            },
            bottomBar = {
                if (showBottomChrome && !useNavRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        tabs.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    if (tab.route == RimvoRoutes.CART && uiState.cartCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge {
                                                    Text("${uiState.cartCount}")
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.label
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldDark,
                                    selectedTextColor = EmeraldDark,
                                    indicatorColor = EmeraldSoft
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (showBottomChrome && useNavRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        tabs.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label, maxLines = 1) }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = RimvoRoutes.HOME
                    ) {
                        composable(RimvoRoutes.HOME) {
                            HomeScreen(
                                uiState = uiState,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onSearchChange = viewModel::updateSearch,
                                onProductClick = { product ->
                                    navController.navigate(RimvoRoutes.productDetail(product.id))
                                },
                                onQuickAdd = { product -> viewModel.addToCart(product) },
                                onOpenCategories = { cat ->
                                    viewModel.selectCategory(cat)
                                    navigateToTab(RimvoRoutes.CATEGORIES)
                                },
                                onSelectBrand = viewModel::selectBrand,
                                onOpenKyc = { navController.navigate(RimvoRoutes.KYC) },
                                onOpenInstallments = { navigateToTab(RimvoRoutes.INSTALLMENTS) }
                            )
                        }

                        composable(RimvoRoutes.CATEGORIES) {
                            CategoriesScreen(
                                uiState = uiState,
                                onSearchChange = viewModel::updateSearch,
                                onSelectCategory = viewModel::selectCategory,
                                onSelectBrand = viewModel::selectBrand,
                                onProductClick = { product ->
                                    navController.navigate(RimvoRoutes.productDetail(product.id))
                                },
                                onQuickAdd = { product -> viewModel.addToCart(product) }
                            )
                        }

                        composable(RimvoRoutes.INSTALLMENTS) {
                            InstallmentsDashboardScreen(
                                uiState = uiState,
                                onPayInstallment = viewModel::payInstallment,
                                onViewReceipt = viewModel::showReceipt,
                                onOpenKyc = { navController.navigate(RimvoRoutes.KYC) }
                            )
                        }

                        composable(RimvoRoutes.CART) {
                            CartCheckoutScreen(
                                uiState = uiState,
                                initialInstallmentMode = checkoutInstallmentMode,
                                initialTenure = checkoutTenure,
                                onUpdateQuantity = viewModel::updateCartQuantity,
                                onBrowseStore = { navigateToTab(RimvoRoutes.HOME) },
                                onOpenKyc = { navController.navigate(RimvoRoutes.KYC) },
                                onPlaceOrder = { addr, useInst, tenure, channel, tx ->
                                    viewModel.placeOrder(addr, useInst, tenure, channel, tx) {}
                                },
                                onViewInstallments = { navigateToTab(RimvoRoutes.INSTALLMENTS) },
                                onClearConfirmedOrder = viewModel::clearConfirmedOrder
                            )
                        }

                        composable(RimvoRoutes.PROFILE) {
                            ProfileScreen(
                                uiState = uiState,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onOpenKyc = { navController.navigate(RimvoRoutes.KYC) },
                                onToggleWhatsappReminders = viewModel::setWhatsappReminders,
                                onTogglePushReminders = viewModel::setPushReminders,
                                onSetPinLock = viewModel::setPinLock,
                                onOpenWhatsappSupport = { showWhatsappDialog = true },
                                onSignOut = viewModel::signOut
                            )
                        }

                        composable(
                            route = RimvoRoutes.PRODUCT_DETAIL,
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: ""
                            val product = RimvoCatalog.findById(productId) ?: RimvoCatalog.products.first()
                            ProductDetailScreen(
                                product = product,
                                language = uiState.prefs.language,
                                onBack = { navController.popBackStack() },
                                onAddToCart = { p -> viewModel.addToCart(p) },
                                onBuyWithInstallment = { p, tenure ->
                                    checkoutInstallmentMode = true
                                    checkoutTenure = tenure
                                    viewModel.addToCart(p)
                                    navigateToTab(RimvoRoutes.CART)
                                }
                            )
                        }

                        composable(RimvoRoutes.KYC) {
                            KycWizardScreen(
                                initialKyc = uiState.kycState,
                                onBack = { navController.popBackStack() },
                                onSubmitKyc = { form, status ->
                                    viewModel.submitKyc(form, status)
                                },
                                onSimulateStatus = { status: KycStatus ->
                                    viewModel.simulateKycStatus(status)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showWhatsappDialog) {
        WhatsappSupportDialog(
            context = context,
            onDismiss = { showWhatsappDialog = false }
        )
    }

    uiState.activeReceipt?.let { receipt ->
        DigitalReceiptDialog(
            receipt = receipt,
            context = context,
            onDismiss = viewModel::dismissReceipt
        )
    }
}
