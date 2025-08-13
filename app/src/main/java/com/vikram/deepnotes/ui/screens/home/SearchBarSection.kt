package com.vikram.deepnotes.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.vikram.deepnotes.core.GlobalProps

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarSection(props: GlobalProps) {
    val auth = remember { FirebaseAuth.getInstance() }
    val userState = remember { mutableStateOf(auth.currentUser) }

    // Listen for login/logout changes
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            userState.value = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    SearchBar(
        query = props.searchBarQuery.value,
        onQueryChange = { props.searchBarQuery.value = it },
        onSearch = {},
        placeholder = { Text("Search Notes") },
        active = props.isSearchBarActive.value,
        onActiveChange = {
            props.isSearchBarActive.value = it
            if (it) props.searchBarQuery.value = ""
        },
        modifier = Modifier.padding(if (!props.isSearchBarActive.value) 20.dp else 0.dp),
        leadingIcon = {
            IconButton(onClick = {
                props.isSearchBarActive.value = !props.isSearchBarActive.value
            }) {
                Icon(
                    imageVector = if (props.isSearchBarActive.value)
                        Icons.AutoMirrored.Filled.ArrowBack
                    else Icons.Filled.Search,
                    contentDescription = null
                )
            }
        },
        trailingIcon = {
            if (!props.isSearchBarActive.value) {
                IconButton(onClick = { props.showProfileDialog.value = true }) {
                    if (userState.value?.photoUrl != null) {
                        // Show Firebase profile image
                        Image(
                            painter = rememberAsyncImagePainter(userState.value?.photoUrl),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        // Fallback icon if no profile image
                        Icon(Icons.Filled.AccountCircle, contentDescription = null)
                    }
                }
            }
        }
    ) {
        SearchResults(props)
    }
}

@Composable
fun SearchResults(props: GlobalProps) {
    val filteredNotes = props.notesList.value.filter {
        props.searchBarQuery.value.isNotEmpty() &&
                (it.title.contains(props.searchBarQuery.value, true) ||
                        it.content.contains(props.searchBarQuery.value, true))
    }
    LazyColumn {
        items(filteredNotes.size) { idx ->
            NoteRow(filteredNotes[idx], props)
        }
    }
}
