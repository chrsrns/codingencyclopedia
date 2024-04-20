package com.chrsrns.codingencyclopedia.ui.pages

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrsrns.codingencyclopedia.AppDatabase
import com.example.compose.CodingEncyclopediaTheme
import kotlinx.coroutines.launch

@Composable
fun SignInPage(
    onSignInClicked: (email: String, name: String, photoB64: String) -> Unit,
    onSignInSwitchClicked: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }

    var showSignInError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Sign In",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                fontWeight = FontWeight(700)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        if (showSignInError) {
            Text(
                "Email or Name is incorrect or not registered",
                fontSize = 12.sp,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = {
                username = it
                showSignInError = false
            },
            label = { Text("Enter your name here") })
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = {
                email = it
                showSignInError = false
            },
            label = { Text("Enter your email address here") })
        Spacer(modifier = Modifier.height(12.dp))
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { scope.launch {
                    AppDatabase
                        .getInstance(context)
                        .userDao()
                        .findByEmailAndName(email.text, username.text)
                        .collect { userFromDb ->
                            showSignInError = if (userFromDb != null) {
                                onSignInClicked(email.text, username.text, userFromDb.profilePhoto)
                                false
                            } else
                                true
                        }
                }},) {
                Text("Sign in", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Don't have an account yet?",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                TextButton(onClick = { onSignInSwitchClicked() }) {
                    Text("Sign up", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInPagePreview() {
    CodingEncyclopediaTheme {
        Surface {
            SignInPage(onSignInClicked = {_, _, _ ->}, onSignInSwitchClicked = {})
        }
    }
}
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SignUpPagePreview_Dark() {
    SignInPagePreview()
}