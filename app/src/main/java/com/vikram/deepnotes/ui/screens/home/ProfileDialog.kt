package com.vikram.deepnotes.ui.screens.home

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.vikram.deepnotes.logOutFromGoogle
import com.vikram.deepnotes.logToGoogleUsingOneTap

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun ProfileDialog(
    showDialog: MutableState<Boolean>,
    navController: NavController,
    isLoggedIn: MutableState<Boolean>
) {
    val activity = LocalContext.current as Activity

    Dialog(onDismissRequest = { showDialog.value = false }) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Profile", fontSize = 26.sp, modifier = Modifier.padding(start = 10.dp, bottom = 15.dp))

            if (isLoggedIn.value) {
                GoogleProfileSection(activity) { showDialog.value = false }
            } else {
                DialogButton("Sign In Google", {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }) {
                    logToGoogleUsingOneTap(activity)
                }
            }

            DialogButton("Settings", {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }) {
                showDialog.value = false
                navController.navigate("settings")
            }
            DialogButton("About", {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }) {
                showDialog.value = false
                navController.navigate("about")
            }
        }
    }
}

@Composable
fun DialogButton(text: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(10.dp, 15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // The icon here already should receive the tint from its caller, no extra changes needed
        icon()
        Text(text, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}


@Composable
fun GoogleProfileSection(activity: Activity, onDismiss: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val userState = remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            userState.value = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    userState.value?.let { user ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(user.photoUrl),
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(40.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(user.displayName ?: "Unknown User", style = MaterialTheme.typography.titleMedium)
            Text(user.email ?: "", style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = { logOutFromGoogle(activity) { onDismiss() } }) {
                Text("Sign Out")
            }
        }
    }
}
