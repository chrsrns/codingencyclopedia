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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrsrns.codingencyclopedia.AppDatabase
import com.chrsrns.codingencyclopedia.User
import com.chrsrns.codingencyclopedia.Utilities.validateEmail
import com.example.compose.CodingEncyclopediaTheme
import kotlinx.coroutines.launch

@Composable
fun SignUpPage(
    onSignUpSwitchClicked: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }

    var signUpResultMsg by remember { mutableStateOf("") }
    val successMsg = "Sign up successful."

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
                "Sign Up",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                fontWeight = FontWeight(700)
            )
            Text(
                "to create your own account", style = TextStyle(
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        if (signUpResultMsg == successMsg) {
            Text(
                successMsg,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
        } else if (signUpResultMsg != "") {
            Text(
                signUpResultMsg,
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
                signUpResultMsg = ""
            },
            label = { Text("Enter your name here") })
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = {
                email = it
                signUpResultMsg = ""
            },
            label = { Text("Enter your email address here") })
        Spacer(modifier = Modifier.height(20.dp))
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    scope.launch {
                        if (!(validateEmail(email.text))) {
                            signUpResultMsg = "Email is invalid"
                            AppDatabase
                                .getInstance(context)
                                .userDao().insertAll(
                                    User(
                                        name = username.text,
                                        email = email.text,
                                    )
                                )
                        } else signUpResultMsg = successMsg
                    }
                }) {
                Text("Sign in", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Already have an account?",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                TextButton(onClick = { onSignUpSwitchClicked() }) {
                    Text("Sign in", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpPagePreview() {
    CodingEncyclopediaTheme {
        Surface {
            SignUpPage(onSignUpSwitchClicked = {})
        }
    }
}
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SignUpPagePreview_Dark() {
    SignUpPagePreview()
}