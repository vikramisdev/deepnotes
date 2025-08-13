package com.vikram.deepnotes.ui.screens.home

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.vikram.deepnotes.core.GlobalProps

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(props: GlobalProps) {
    val context = LocalContext.current
    val activity = context as? Activity

    val auth = remember { FirebaseAuth.getInstance() }
    val isLoggedIn = remember { mutableStateOf(auth.currentUser != null) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            isLoggedIn.value = firebaseAuth.currentUser != null
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    LaunchedEffect(isLoggedIn.value) { loadNotes(props.db, props.notesList) }

    Scaffold(
        floatingActionButton = {
            // New Note FAB
            ExtendedFloatingActionButton(
                onClick = { props.navController.navigate("noteEditor/0") }, // 0 = new note
                modifier = Modifier.padding(10.dp),
                text = { Text("New Note") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Note") }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            SearchBarSection(props)

            if (props.notesList.value.isEmpty()) {
                EmptyState()
            } else {
                // Use NoteGrid to display notes
                NoteGrid(props)
            }
        }
    }
}
