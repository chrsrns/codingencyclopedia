package com.chrsrns.codingencyclopedia.ui.pages

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.CodingEncyclopediaTheme

@Composable
fun TermListPage(
    selectedCategory: String,
    searchText: String,
    termsList: List<String>,
    onTermClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedCategory == "" && searchText == "") Text(
            "Terms Glossary",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        else if (searchText != "") Text(
            "Searching for \"$searchText\"",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        else Text(
            "Terms in $selectedCategory",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Spacer(Modifier.height(24.dp))
        LazyColumn {
            println("Rendering list...")
            items(termsList) {term ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTermClick(term)
                        }) {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = term,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Divider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_Light() {
    val map = listOf(
        "Persistent Data Structure",
        "Trie",
        "Van Emde Boas Tree",
        "Skip List",
        "Fenwick Tree (Binary Indexed Tree)",
        "Segment Tree",
        "Splay Tree",
        "Treap",
        "Bloom Filter",
        "Count-Min Sketch",
        "Suffix Array",
        "Wavelet Tree",
        "Persistent Segment Tree",
        "B-Tree",
        "Range Tree",
        "Suffix Tree",
        "Interval Tree",
        "Hash Array Mapped Trie (HAMT)",
        "HyperLogLog",
        "Rope"
    )
    CodingEncyclopediaTheme {
        Surface {
            TermListPage(
                selectedCategory = "Category 1",
                searchText = "",
                termsList = map,
                onTermClick = { _ -> })
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview_Dark() {
    Preview_Light()
}