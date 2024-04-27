package com.chrsrns.codingencyclopedia

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.chrsrns.codingencyclopedia.ui.MainScreen
import com.chrsrns.codingencyclopedia.utils.toMap
import com.example.compose.CodingEncyclopediaTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject

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
                    MainScreen(mappedJson = mappedJson)
                }
            }
        }
    }
}


class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainState())

    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

    fun clearSelections() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategory = "",
                selectedTerm = "",
                searchText = "",
            )
        }
    }

    fun setSearchText(text: String) {
        _uiState.update {
            it.copy(
                searchText = text
            )
        }
    }

    fun setSelectedCategory(category: String) {
        _uiState.update {
            it.copy(
                selectedCategory = category
            )
        }
    }

    fun setSelectedTerm(term: String) {
        _uiState.update {
            it.copy(
                selectedTerm = term
            )
        }
    }

    fun setCredentials(username: String, email: String) {
        _uiState.update {
            it.copy(
                username = username, email = email
            )
        }
    }

    fun setPhoto(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                bitmap = bitmap
            )
        }
    }
}

data class MainState(
    val selectedCategory: String = "",
    val selectedTerm: String = "",
    val searchText: String = "",

    val bitmap: Bitmap? = null,
    val username: String = "",
    val email: String = "",
)