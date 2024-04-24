package com.chrsrns.codingencyclopedia

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chrsrns.codingencyclopedia.ui.TopNavBar
import com.chrsrns.codingencyclopedia.ui.pages.CategoriesPage
import com.chrsrns.codingencyclopedia.ui.pages.MenuPage
import com.chrsrns.codingencyclopedia.ui.pages.SignInPage
import com.chrsrns.codingencyclopedia.ui.pages.SignUpPage
import com.chrsrns.codingencyclopedia.ui.pages.TermDefinitionsPage
import com.chrsrns.codingencyclopedia.ui.pages.TermListPage
import com.chrsrns.codingencyclopedia.utils.BitmapConverter
import com.chrsrns.codingencyclopedia.utils.MenuItem
import com.chrsrns.codingencyclopedia.utils.toMap
import com.example.compose.CodingEncyclopediaTheme
import kotlinx.coroutines.launch
import org.json.JSONObject

private enum class AppScreen {
    CATEGORIES, HELP, PROFILE, SIGN_IN, SIGN_UP, TERM_DEFINITION, TERM_LIST
}


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rawJson = resources.openRawResource(R.raw.terms).readBytes().toString(Charsets.UTF_8)
        val jsonObj = JSONObject(rawJson)

        val mappedJson = jsonObj.toMap() as Map<String, Map<String, Map<String, String>>>

        setContent {
            CodingEncyclopediaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    var selectedCategory by remember { mutableStateOf("") }
                    var selectedTerm by remember { mutableStateOf("") }
                    var searchText by remember { mutableStateOf("") }

                    val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }
                    val username = remember { mutableStateOf("") }
                    val email = remember { mutableStateOf("") }

                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()

                    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
                        ModalDrawerSheet {
                            var selectedMenuItem by remember { mutableStateOf(MenuItem.HOME) }
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
                                        }
                                    )
                                },
                                showProfile = !(username.value == "" && email.value == "")
                            )
                        }
                    }) {
                        Scaffold(topBar = {
                            TopNavBar(navSearchText = searchText, onNavSearchTextChanged = {
                                searchText = it
                                navController.currentBackStackEntry?.let { backStackEntry ->
                                    if (backStackEntry.destination.route != AppScreen.TERM_LIST.name) {
                                        println("ID: ${backStackEntry.destination.route}")
                                        println("Navigating to term list page...")
                                        navController.navigate(AppScreen.TERM_LIST.name)
                                    }
                                }
                            }, onHomeClick = {
                                selectedCategory = ""
                                selectedTerm = ""
                                searchText = ""
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
                                                username.value = n
                                                email.value = e
                                                bitmap.value =
                                                    BitmapConverter.convertStringToBitmap(
                                                        photoB64
                                                    )
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
                                                selectedCategory = it
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
                                                    if (searchText != "" && !termChild.lowercase()
                                                            .contains(searchText.lowercase())
                                                    ) continue
                                                    allTerms.add(termChild)
                                                }
                                            }
                                        }
                                        TermListPage(
                                            selectedCategory = selectedCategory,
                                            searchText = searchText,
                                            termsList = allTerms,
                                            onTermClick = { term ->
                                                println("Attempt navigate to definitions...")
                                                selectedTerm = term
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
                                                        if (termChild == selectedTerm && termDefs != null)
                                                            tmp = HashMap(termDefs)
                                                    }
                                                }
                                            }
                                            return@lazy tmp
                                        }
                                        TermDefinitionsPage(
                                            termToDisplay = selectedTerm,
                                            definitions = allDefs
                                        )
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