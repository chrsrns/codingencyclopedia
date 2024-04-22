package com.chrsrns.codingencyclopedia.ui

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.chrsrns.codingencyclopedia.AppDatabase
import com.chrsrns.codingencyclopedia.R
import com.chrsrns.codingencyclopedia.User
import com.chrsrns.codingencyclopedia.utils.BitmapConverter
import com.example.compose.CodingEncyclopediaTheme
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopNavBar(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onNavSearchTextChanged: (String) -> Unit,
    navSearchText: String
) {

    TopAppBar(title = { Text("") }, modifier = Modifier.padding(horizontal = 8.dp), actions = {
        IconButton(onClick = onHomeClick) {
            Icon(
                imageVector = Icons.Outlined.Home, contentDescription = "Go to Home"
            )
        }
        NavSearchTextField(placeholderText = "Search here...",
            modifier = Modifier.width(156.dp),
            navSearchText = navSearchText,
            onNavSearchTextChanged = {
                onNavSearchTextChanged(it)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Go to Home",
                    modifier = Modifier.padding(end = 8.dp)
                )
            })

    }, navigationIcon = {
        Row {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Outlined.Menu, contentDescription = "Menu"
                )
            }
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack, contentDescription = "Go back"
                )
            }
        }
    })
}

@Composable
fun NavSearchTextField(
    modifier: Modifier = Modifier,
    navSearchText: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onNavSearchTextChanged: (String) -> Unit,
    placeholderText: String = "Type here...",
    fontSize: TextUnit = MaterialTheme.typography.bodySmall.fontSize
) {
    BasicTextField(modifier = modifier.background(
        MaterialTheme.colorScheme.primaryContainer,
        RoundedCornerShape(16.dp),
    ),
        value = navSearchText,
        onValueChange = {
            onNavSearchTextChanged(it)
        },
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = fontSize
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(Modifier.weight(1f)) {
                    if (navSearchText.isEmpty()) {
                        Text(
                            placeholderText, style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                fontSize = fontSize,
                                fontStyle = FontStyle.Italic
                            )
                        )
                    }
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        })
}

@Composable
fun ProfilePhoto(
    userEmail: String
) {
    val context = LocalContext.current
    var bitmap by remember {
        mutableStateOf(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.placeholder
            )
        )
    }

    var bytes by remember {
        mutableStateOf<ByteArray?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { result ->
        val item = context.contentResolver.openInputStream(result!!)
        bytes = item?.readBytes()
        item?.close()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        val bitmapCopy =
            bitmap ?: BitmapFactory.decodeResource(
                context.resources,
                R.drawable.placeholder
            );
        if (bitmapCopy != null) {

            Image(
                bitmap = bitmapCopy.asImageBitmap(),
                contentDescription = "Profile photo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                launcher.launch("image/*")
            }) {
            Text(text = "Change profile picture")
        }
    }

    bytes?.let {
        val bos = ByteArrayOutputStream()
        BitmapFactory.decodeByteArray(it, 0, it.size)
            .compress(Bitmap.CompressFormat.JPEG, 30, bos)
        val decodedByteArray =
            BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.toByteArray().size)
        bitmap =
            decodedByteArray
        val db = AppDatabase.getInstance(LocalContext.current).userDao()
        runBlocking {
            val user = db.findByEmail(email = userEmail).collect { user ->
                if (user != null) {
                    println("Creating new user...")
                    val newUser =
                        User(
                            user.email,
                            user.name,
                            BitmapConverter.convertBitmapToString(decodedByteArray)
                        )
                    println("Inserting user...")
                    db.insertAll(newUser)
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun TopNavBar_Preview_Light() {
    CodingEncyclopediaTheme {
        Surface {
            TopNavBar(
                onBackClick = { },
                onHomeClick = { },
                onMenuClick = { },
                onNavSearchTextChanged = { _ -> },
                navSearchText = ""
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopNavBar_Preview_Dark() {
    TopNavBar_Preview_Light()
}