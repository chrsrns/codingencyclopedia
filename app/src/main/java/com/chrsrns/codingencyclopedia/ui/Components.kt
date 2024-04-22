package com.chrsrns.codingencyclopedia.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.chrsrns.codingencyclopedia.AppDatabase
import com.chrsrns.codingencyclopedia.R
import com.chrsrns.codingencyclopedia.User
import com.chrsrns.codingencyclopedia.utils.BitmapConverter
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream

@Composable
fun ProfilePhoto(
    bitmap: MutableState<Bitmap?>,
    userEmail: String
) {
    val context = LocalContext.current

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
            bitmap.value ?: BitmapFactory.decodeResource(
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
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFBEE6DC), contentColor = Color(0xD9000000)
            ), onClick = {
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
        bitmap.value =
            decodedByteArray
        val db = AppDatabase.getInstance(LocalContext.current).userDao()
        runBlocking {
            val user = db.findByEmail(email = userEmail).collect {user ->
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
