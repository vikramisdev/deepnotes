package com.vikram.deepnotes

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.datastore.preferences.core.intPreferencesKey
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth

enum class Theme(val theme: String) {
    LIGHT_THEME("Light Theme"),
    DARK_THEME("Dark Theme"),
    SYSTEM_THEME("System Theme")
}

fun showToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

private val GRID_COLUMNS_KEY = intPreferencesKey("grid_columns")

/**
 * Remember the grid columns preference (1 or 2)
 */
@Composable
fun rememberGridColumns(defaultColumns: Int = 2): MutableState<Int> {
    return rememberPreference(GRID_COLUMNS_KEY, defaultColumns)
}

/**
 * Toggle the grid columns between 1 and 2
 */
fun toggleGridColumns(current: Int): Int {
    return if (current == 1) 2 else 1
}

fun getWebClientId(context: Context): String {
    return context.getString(R.string.default_web_client_id)
}

fun logOutFromGoogle(context: Context, onComplete: (() -> Unit)? = null) {
    // 1. Sign out from Firebase
    FirebaseAuth.getInstance().signOut()

    // 2. Sign out from Google One Tap
    val oneTapClient = Identity.getSignInClient(context)
    oneTapClient.signOut()
        .addOnCompleteListener {
            onComplete?.invoke()
        }
}

fun logToGoogleUsingOneTap(activity: Activity) {
    val REQ_ONE_TAP = 1001

    val oneTapClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(getWebClientId(activity))
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener(activity) { result ->
            try {
                activity.startIntentSenderForResult(
                    result.pendingIntent.intentSender,
                    REQ_ONE_TAP,
                    null, 0, 0, 0
                )
            } catch (e: IntentSender.SendIntentException) {
                Log.e("ONE_TAP", "Couldn't start One Tap UI: ${e.localizedMessage}")
            }
        }
        .addOnFailureListener(activity) { e ->
            Log.e("ONE_TAP", "Sign-in failed: ${e.localizedMessage}")
        }
}
