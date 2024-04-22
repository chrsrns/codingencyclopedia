package com.chrsrns.codingencyclopedia.ui.pages

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.chrsrns.codingencyclopedia.ui.ProfilePhoto
import com.example.compose.CodingEncyclopediaTheme

@Composable
fun ProfilePage(
    username: String,
    email: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 12.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp), Alignment.Center
        ) {
            ProfilePhoto(email)
        }
        Row {
            Text(
                "Username: ",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                username,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                "Email Address: ",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Text(
                email,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_Light() {
    CodingEncyclopediaTheme {
        Surface {
            ProfilePage(username = "Christian Aranas", email = "aranaschristianlouise@gmail.com")
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview_Dark() {
    Preview_Light()
}
