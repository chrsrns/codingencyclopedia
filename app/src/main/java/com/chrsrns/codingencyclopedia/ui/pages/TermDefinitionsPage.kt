package com.chrsrns.codingencyclopedia.ui.pages

import android.content.res.AssetManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrsrns.codingencyclopedia.utils.Utilities
import com.example.compose.CodingEncyclopediaTheme

@Composable
fun TermDefinitionsPage(
    termToDisplay: String,
    definitions:  Map<String, String>,
    assetsIndex: Map<String, String>,
    assets: AssetManager
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Text(
                termToDisplay,
                fontSize = 36.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            for (definition in definitions) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "• ${definition.key}",
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Text(
                    definition.value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight(400),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            val cleanTerm = Utilities.cleanFileName(termToDisplay)

            if (cleanTerm in assetsIndex.keys) {
                println("image available")
                val imageStream = assets.open(assetsIndex[cleanTerm]!!)
                val image = BitmapFactory.decodeStream(imageStream)

                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(image.width.toFloat() / image.height.toFloat()),
                        contentScale = ContentScale.FillWidth,
                        bitmap = image.asImageBitmap(),
                        contentDescription = termToDisplay
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_Light() {
    CodingEncyclopediaTheme {
        Surface {
            TermDefinitionsPage(
                termToDisplay = "Persistent Data Structure",
                definitions = mapOf(
                    "Definition 1" to "A persistent data structure is a data structure that preserves the previous versions of itself when modified, allowing access to both the old and new versions.",
                    "Definition 2" to "This property enables efficient time-traveling queries and immutable data manipulation, making persistent data structures useful in functional programming and concurrent environments."
                ),
                assetsIndex = mapOf("" to ""),
                assets = LocalContext.current.assets
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview_Dark() {
    Preview_Light()
}