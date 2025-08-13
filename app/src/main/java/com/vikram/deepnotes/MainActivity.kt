package com.vikram.deepnotes

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vikram.deepnotes.core.GlobalProps
import com.vikram.deepnotes.data.local.AppDatabase
import com.vikram.deepnotes.data.local.Note
import com.vikram.deepnotes.ui.navigation.AppNavigation
import com.vikram.deepnotes.ui.theme.DeepNotesTheme

class MainActivity : ComponentActivity() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val REQ_ONE_TAP = 1001
        val firebaseAuth = Firebase.auth

        if (requestCode == REQ_ONE_TAP) {
            try {
                val credential = Identity.getSignInClient(this)
                    .getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                Log.d("FIREBASE", "Sign-in successful: ${user?.email}")
                            } else {
                                Log.e("FIREBASE", "Firebase sign-in failed", task.exception)
                            }
                        }
                } else {
                    Log.e("ONE_TAP", "No ID token found")
                }
            } catch (e: ApiException) {
                Log.e("ONE_TAP", "Sign-in failed: ${e.statusCode}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val db = AppDatabase.getDatabase(this)
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()

            val props = GlobalProps(
                context = this,
                scope = scope,
                navController = navController,
                currentTheme = remember { mutableStateOf(Theme.SYSTEM_THEME.toString()) },
                db = db,
                maxNoteLineLimit = 10,
                searchBarQuery = remember { mutableStateOf("") },
                isSearchBarActive = remember { mutableStateOf(false) },
                showProfileDialog = remember { mutableStateOf(false) },
                notesList = remember { mutableStateOf<List<Note>>(emptyList()) },
                noteId = remember { mutableStateOf(0) },
                title = remember { mutableStateOf("") },
                content = remember { mutableStateOf("") },
                updating = remember { mutableStateOf(false) },
                showSettingsDialog = remember { mutableStateOf(false) }
            )

            DeepNotesTheme(theme = props.currentTheme) {
                AppNavigation(navController, props)
            }
        }
    }
}
