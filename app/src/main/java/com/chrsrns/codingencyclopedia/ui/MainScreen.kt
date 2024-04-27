package com.chrsrns.codingencyclopedia.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chrsrns.codingencyclopedia.MainViewModel
import com.chrsrns.codingencyclopedia.ui.pages.CategoriesPage
import com.chrsrns.codingencyclopedia.ui.pages.HelpPage
import com.chrsrns.codingencyclopedia.ui.pages.MenuItem
import com.chrsrns.codingencyclopedia.ui.pages.MenuPage
import com.chrsrns.codingencyclopedia.ui.pages.ProfilePage
import com.chrsrns.codingencyclopedia.ui.pages.SignInPage
import com.chrsrns.codingencyclopedia.ui.pages.SignUpPage
import com.chrsrns.codingencyclopedia.ui.pages.TermDefinitionsPage
import com.chrsrns.codingencyclopedia.ui.pages.TermListPage
import com.chrsrns.codingencyclopedia.utils.BitmapConverter
import kotlinx.coroutines.launch

private enum class AppScreen {
    CATEGORIES, HELP, PROFILE, SIGN_IN, SIGN_UP, TERM_DEFINITION, TERM_LIST
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel(),
    mappedJson: Map<String, Map<String, Map<String, String>>>
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    var selectedMenuItem by remember { mutableStateOf(MenuItem.HOME) }

    val uiState by mainViewModel.uiState.collectAsState()

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            navController.addOnDestinationChangedListener { _, navDestination: NavDestination, _ ->
                selectedMenuItem =
                    when (navDestination.route?.let { routeStr ->
                        println("selected = $routeStr")
                        AppScreen.valueOf(
                            routeStr
                        )
                    } ?: AppScreen.CATEGORIES) {
                        AppScreen.HELP -> MenuItem.HELP
                        AppScreen.PROFILE -> MenuItem.PROFILE
                        else -> MenuItem.HOME
                    }
            }
            MenuPage(
                selectedMenuItem = selectedMenuItem,
                onMenuItemClick = { menuItem ->
                    navController.navigate(
                        when (menuItem) {
                            MenuItem.HOME -> AppScreen.CATEGORIES.name
                            MenuItem.PROFILE -> AppScreen.PROFILE.name
                            MenuItem.HELP -> AppScreen.HELP.name
                            MenuItem.SIGN_IN -> AppScreen.SIGN_IN.name
                        }
                    )
                    scope.launch {
                        drawerState.apply {
                            close()
                        }
                    }

                },
                isLoggedIn = !(uiState.username == "" && uiState.email == "")
            )
        }
    }) {
        Scaffold(topBar = {
            TopNavBar(navSearchText = uiState.searchText, onNavSearchTextChanged = {
                mainViewModel.setSearchText(it)
                navController.currentBackStackEntry?.let { backStackEntry ->
                    if (backStackEntry.destination.route != AppScreen.TERM_LIST.name) {
                        println("ID: ${backStackEntry.destination.route}")
                        println("Navigating to term list page...")
                        navController.navigate(AppScreen.TERM_LIST.name)
                    }
                }
            }, onHomeClick = {
                mainViewModel.clearSelections()
                navController.navigate(AppScreen.CATEGORIES.name)
            }, onMenuClick = {
                scope.launch {
                    drawerState.apply {
                        if (isClosed) open() else close()
                    }
                }

            })
        }) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                NavHost(
                    navController = navController,
                    startDestination = AppScreen.SIGN_IN.name
                ) {
                    composable(route = AppScreen.SIGN_IN.name) {
                        SignInPage(
                            onSignInClicked = { e, n, photoB64 ->
                                mainViewModel.setCredentials(username = n, email = e)
                                BitmapConverter.convertStringToBitmap(
                                    photoB64
                                )?.let { photo ->
                                    mainViewModel.setPhoto(
                                        photo
                                    )
                                }
                                navController.navigate(AppScreen.CATEGORIES.name) {
                                    popUpTo(navController.graph.id) {
                                        inclusive = true
                                    }
                                }
                            }, onSignInSwitchClicked = {
                                navController.navigate(AppScreen.SIGN_UP.name)
                            })
                    }
                    composable(route = AppScreen.SIGN_UP.name) {
                        SignUpPage(onSignUpSwitchClicked = {
                            navController.navigate(AppScreen.SIGN_IN.name)
                        })
                    }
                    composable(route = AppScreen.CATEGORIES.name) {
                        CategoriesPage(
                            categoryList = mappedJson.keys.toList(),
                            onSelectCategory = {
                                mainViewModel.setSelectedCategory(it)
                                navController.navigate(AppScreen.TERM_LIST.name)
                            }
                        )
                    }
                    composable(route = AppScreen.TERM_LIST.name) {
                        val allTerms = ArrayList<String>()
                        for (system in mappedJson.keys) {
                            val systemAsData = mappedJson[system]
                            if (systemAsData != null) {
                                for (termChild in systemAsData.keys) {
                                    if (uiState.searchText != "" && !termChild.lowercase()
                                            .contains(uiState.searchText.lowercase())
                                    ) continue
                                    allTerms.add(termChild)
                                }
                            }
                        }
                        TermListPage(
                            selectedCategory = uiState.selectedCategory,
                            searchText = uiState.searchText,
                            termsList = allTerms,
                            onTermClick = { term ->
                                println("Attempt navigate to definitions...")
                                mainViewModel.setSelectedTerm(term)
                                navController.navigate(AppScreen.TERM_DEFINITION.name)
                            }
                        )
                    }
                    composable(route = AppScreen.TERM_DEFINITION.name) {
                        val allDefs: HashMap<String, String> by lazy {
                            var tmp: HashMap<String, String> = HashMap()
                            for (system in mappedJson.keys) {
                                val systemAsData = mappedJson[system]
                                if (systemAsData != null) {
                                    for (termChild in systemAsData.keys) {
                                        val termDefs = systemAsData[termChild]
                                        if (termChild == uiState.selectedTerm && termDefs != null)
                                            tmp = HashMap(termDefs)
                                    }
                                }
                            }
                            return@lazy tmp
                        }
                        TermDefinitionsPage(
                            termToDisplay = uiState.selectedTerm,
                            definitions = allDefs
                        )
                    }
                    composable(route = AppScreen.HELP.name) {
                        HelpPage()
                    }
                    composable(route = AppScreen.PROFILE.name) {
                        ProfilePage(username = uiState.username, email = uiState.email)
                    }
                }
            }

        }
    }
}