package com.chrsrns.codingencyclopedia

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chrsrns.codingencyclopedia.ui.TopNavBar
import com.chrsrns.codingencyclopedia.ui.pages.CategoriesPage
import com.chrsrns.codingencyclopedia.ui.pages.HelpPage
import com.chrsrns.codingencyclopedia.ui.pages.MenuPage
import com.chrsrns.codingencyclopedia.ui.pages.ProfilePage
import com.chrsrns.codingencyclopedia.ui.pages.SignInPage
import com.chrsrns.codingencyclopedia.ui.pages.SignUpPage
import com.chrsrns.codingencyclopedia.ui.pages.TermDefinitionsPage
import com.chrsrns.codingencyclopedia.ui.pages.TermListPage
import com.chrsrns.codingencyclopedia.utils.BitmapConverter
import com.chrsrns.codingencyclopedia.utils.MenuItem
import com.chrsrns.codingencyclopedia.utils.toMap
import com.example.compose.CodingEncyclopediaTheme
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rawJson =
            resources.openRawResource(R.raw.terms).readBytes().toString(Charsets.UTF_8)
        val jsonObj = JSONObject(rawJson)

        val mappedJson = jsonObj.toMap() as Map<String, Map<String, Map<String, String>>>

        setContent {
            CodingEncyclopediaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedCategory by remember { mutableStateOf("") }
                    var selectedTerm by remember { mutableStateOf("") }
                    var searchText by remember { mutableStateOf("") }

                    val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }
                    val username = remember { mutableStateOf("") }
                    val email = remember { mutableStateOf("") }

                    val expandMenu = remember { mutableStateOf(false) }
                    var selectedMenuItem by remember { mutableStateOf(MenuItem.HOME) }

                    val isSignUp = remember { mutableStateOf(true) }

                    Scaffold(topBar = {
                        TopNavBar(navSearchText = searchText,
                            onNavSearchTextChanged = {
                                searchText = it
                            },
                            onHomeClick = {
                                selectedCategory = ""
                                selectedTerm = ""
                                searchText = ""
                                expandMenu.value = false
                                selectedMenuItem = MenuItem.HOME
                            }, onBackClick = {
                                if (selectedMenuItem == MenuItem.HOME)
                                    if (selectedTerm != "") selectedTerm = ""
                                    else if (searchText != "") searchText = ""
                                    else if (selectedCategory != "") selectedCategory =
                                        ""
                                if (!expandMenu.value)
                                    selectedMenuItem = MenuItem.HOME
                                else
                                    expandMenu.value = false
                            }, onMenuClick = {
                                expandMenu.value = !expandMenu.value
                            })
                    }) { innerPadding ->
                        Column(
                            modifier = Modifier.padding(innerPadding),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            if (expandMenu.value) {
                                MenuPage(
                                    selectedMenuItem = selectedMenuItem,
                                    onMenuItemClick = { menuItem ->
                                        selectedMenuItem = menuItem
                                        expandMenu.value = false
                                    },
                                    showProfile = !(username.value == "" && email.value == "")
                                )
                            } else when (selectedMenuItem) {
                                MenuItem.HOME -> {
                                    if (selectedCategory == "" && searchText == "") {
                                        if (username.value == "" && email.value == "") {
                                            if (isSignUp.value)
                                                SignUpPage(onSignUpSwitchClicked = {
                                                    isSignUp.value = false
                                                })
                                            else SignInPage(
                                                onSignInClicked = { e, n, photoB64 ->
                                                    username.value = n
                                                    email.value = e
                                                    bitmap.value =
                                                        BitmapConverter.convertStringToBitmap(
                                                            photoB64
                                                        )

                                                },
                                                onSignInSwitchClicked = { isSignUp.value = true })
                                        } else {
                                            CategoriesPage(
                                                categoryList = mappedJson.keys.toList(),
                                                onSelectCategory = { selectedCategory = it })
                                        }
                                    } else if (selectedTerm != "") {
                                        var allDefs = HashMap<String, String>()
                                        for (system in mappedJson.keys) {
                                            val systemAsData = mappedJson[system]
                                            if (systemAsData != null) {
                                                for (termChild in systemAsData.keys) {
                                                    val termDefs = systemAsData[termChild]
                                                    if (termChild == selectedTerm && termDefs != null)
                                                        allDefs = HashMap(termDefs)
                                                }
                                            }
                                        }
                                        TermDefinitionsPage(
                                            termToDisplay = selectedTerm,
                                            definitions = allDefs
                                        )
                                    } else if (selectedCategory != "" || searchText != "") {
                                        println("Init list")
                                        val allTerms = ArrayList<String>()
                                        println("Init for loop")
                                        for (system in mappedJson.keys) {
                                            val systemAsData = mappedJson[system]
                                            println(systemAsData)
                                            if (systemAsData != null) {
                                                for (termChild in systemAsData.keys) {
                                                    if (searchText != "" && !termChild.lowercase()
                                                            .contains(searchText.lowercase())
                                                    ) continue
                                                    allTerms.add(termChild)
                                                }
                                            }
                                        }
                                        println("Number of terms: ${allTerms.size}")
                                        TermListPage(
                                            selectedCategory = selectedCategory,
                                            searchText = searchText,
                                            termsList = allTerms,
                                            onTermClick = { term ->
                                                selectedTerm = term
                                            }
                                        )
                                    }
                                }
                                MenuItem.PROFILE -> {
                                    ProfilePage(
                                        username = username.value,
                                        email = email.value,
                                    )
                                }
                                MenuItem.HELP -> HelpPage()
                            }
                        }

                    }
                }
            }
        }
    }
}