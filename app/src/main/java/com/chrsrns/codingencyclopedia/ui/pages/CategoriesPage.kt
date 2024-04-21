package com.chrsrns.codingencyclopedia.ui.pages

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.chrsrns.codingencyclopedia.R
import com.example.compose.CodingEncyclopediaTheme

@Composable
fun CategoriesPage(category: MutableState<String>) {

    val items = arrayOf(
        "Category 1",
        "Category 2",
        "Category 3",
        "Category 4",
    )

    Column(
        modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier
                .clickable { Log.d("TEST", "Big Button clicked") }
                .padding(15.dp),
                contentAlignment = Alignment.TopCenter) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(50),
                            )
                            .height(4 * 48.dp)
                            .aspectRatio(1f), contentAlignment = Alignment.TopCenter
                    ) {
                        Image(modifier = Modifier.fillMaxHeight(0.85f),
                            painter = painterResource(id = R.drawable.encyc_logo),
                            contentDescription = "content description"
                        )
                    }
                    Text(
                        modifier = Modifier.offset(y=(-28).dp),
                        text = "Coding\nEncyclopedia", style = TextStyle(
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            fontWeight = FontWeight(700), textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
            Divider()
            for (item in items) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        textAlign = TextAlign.Center
                    )
                }
                Divider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_Light() {
    val category = remember { mutableStateOf("") }
    CodingEncyclopediaTheme {
        Surface {
            CategoriesPage(category = category)
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview_Dark() {
    Preview_Light()
}