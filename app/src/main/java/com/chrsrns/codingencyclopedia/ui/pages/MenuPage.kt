package com.chrsrns.codingencyclopedia.ui.pages

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.CodingEncyclopediaTheme
import java.util.Locale

enum class MenuItem(val string: String) {
    HOME("HOME"),
    SIGN_IN("SIGN IN"),
    PROFILE("PROFILE"),
    HELP("HELP");

    override fun toString(): String {
        return string
    }
}

@Composable
fun MenuPage(
    selectedMenuItem: MenuItem,
    onMenuItemClick: (MenuItem) -> Unit,
    isLoggedIn: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            for (menuItem in MenuItem.entries.filter {
                ((isLoggedIn && it != MenuItem.SIGN_IN) || (!isLoggedIn && it != MenuItem.HOME && it != MenuItem.PROFILE))
            }) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onMenuItemClick(menuItem)
                        }
                        .background(
                            color = if (selectedMenuItem == menuItem) MaterialTheme.colorScheme.secondaryContainer
                            else Color.Unspecified
                        )
                        .padding(8.dp)) {
                    Text(
                        menuItem.toString().uppercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))


            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_Light() {
    CodingEncyclopediaTheme {
        Surface {
            MenuPage(
                selectedMenuItem = MenuItem.HOME,
                onMenuItemClick = { _ -> },
                isLoggedIn = true
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview_Dark() {
    Preview_Light()
}