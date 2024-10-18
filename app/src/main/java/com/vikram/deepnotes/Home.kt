package com.vikram.deepnotes

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.vikram.deepnotes.data.local.AppDatabase
import com.vikram.deepnotes.data.local.Note
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Home(props: GlobalProps) {
    if(props.showProfileDialog.value) {
        ShowProfileDialog(props.showProfileDialog, props.navController)
    }

    LaunchedEffect(props.notesList.value.size) {
        loadNotes(props.db, props.notesList)
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { props.navController.navigate("noteEditor") },
                modifier = Modifier
                    .padding(10.dp),
                text = {
                    Text(
                        text = "New Note"
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Note"
                    )
                }
            )
        }
    ) {
        // main container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // note searchbar
            SearchBar(
                query = props.searchBarQuery.value,
                onQueryChange = {
                    props.searchBarQuery.value = it
                },
                onSearch = {
                    // Handle search action here
                },
                placeholder = {
                    Text(text = "Search Notes")
                },
                active = props.isSearchBarActive.value,
                onActiveChange = {
                    props.isSearchBarActive.value = it
                    if (props.isSearchBarActive.value) {
                        props.searchBarQuery.value = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (!props.isSearchBarActive.value) 10.dp else 0.dp),
                leadingIcon = {
                    IconButton(
                        onClick = {
                            props.isSearchBarActive.value = !props.isSearchBarActive.value
                        }
                    ) {
                        Icon(
                            imageVector = if (props.isSearchBarActive.value) Icons.AutoMirrored.Filled.ArrowBack else Icons.Filled.Search,
                            contentDescription = "Search Icon"
                        )
                    }
                },
                trailingIcon = {
                    if (!props.isSearchBarActive.value) {
                        IconButton(
                            onClick = {
                                props.showProfileDialog.value = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Account Icon"
                            )
                        }
                    }
                }
            ) {
                SearchResults(props)
            }


            // this shows the notes
            if (props.notesList.value.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .weight(0.8f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Notes",
                        fontSize = 20.sp
                    )
                }
            } else {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 10.dp,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    content = {
                        items(props.notesList.value.size) { index ->
                            val note = props.notesList.value[index]
                            val offsetX = remember { Animatable(0f) }

                            Column(
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(15.dp))
                                    .clickable(
                                        true,
                                        onClick = {
                                            props.navController.navigate(
                                                "noteEditor?noteId=${note.id}&title=${
                                                    Uri.encode(
                                                        note.title
                                                    )
                                                }&content=${Uri.encode(note.content)}&updating=true"
                                            )
                                        }
                                    )
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures(
                                            onDragEnd = {
                                                // Animate back to the original position
                                                props.scope.launch {
                                                    offsetX.animateTo(
                                                        0f,
                                                        animationSpec = tween(300)
                                                    )
                                                }
                                            }
                                        ) { change, dragAmount ->
                                            // Update the offset while dragging
                                            props.scope.launch {
                                                val newOffset = offsetX.value + dragAmount
                                                if (newOffset > 250) { // Swipe threshold to delete
                                                    // Delete from database
                                                    props.db
                                                        .noteDao()
                                                        .delete(note)
                                                    // reload the list
                                                    loadNotes(props.db, props.notesList)
                                                } else {
                                                    offsetX.snapTo(
                                                        newOffset.coerceIn(
                                                            0f,
                                                            300f
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.onBackground,
                                        RoundedCornerShape(15.dp)
                                    )
                                    .padding(15.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = note.title,
                                    fontSize = 16.sp,
                                    lineHeight = 25.sp
                                )
                                Text(
                                    text = note.content,
                                    fontSize = 14.sp,
                                    maxLines = note.content.split(" ").size % props.maxNoteLineLimit + 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SearchResults(props: GlobalProps) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(props.notesList.value.size) { index ->
            var note = props.notesList.value[index]

            if (props.searchBarQuery.value.isNotEmpty() && (note.title.lowercase()
                    .contains(props.searchBarQuery.value.lowercase()) || note.content.lowercase()
                    .contains(props.searchBarQuery.value.lowercase()))
            ) {
                Column(
                    modifier = Modifier
                        .clickable(
                            true,
                            onClick = {
                                props.navController.navigate(
                                    "noteEditor?noteId=${note.id}&title=${
                                        Uri.encode(
                                            note.title
                                        )
                                    }&content=${Uri.encode(note.content)}&updating=${true}"
                                )
                            }
                        )
                        .padding(20.dp, 10.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = note.title,
                        fontSize = 16.sp,
                        lineHeight = 25.sp,
                        maxLines = 1
                    )
                    Text(
                        text = note.content,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )


                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun ShowProfileDialog(
    showDialog: MutableState<Boolean>,
    navController: NavController,
) {
    Dialog(
        onDismissRequest = {
            showDialog.value = false
        }
    ) {
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Profile",
                style = TextStyle(
                    fontSize = 26.sp
                ),
                modifier = Modifier
                    .padding(start = 10.dp, bottom = 15.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            DialogButton(
                text = "Sign In Google",
                icon = {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Account",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                onClick = {
                }
            )

            DialogButton(
                text = "Settings",
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                onClick = {
                    showDialog.value = false
                    navController.navigate("settings")
                }
            )

            DialogButton(
                text = "About",
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                onClick = {
                    showDialog.value = false
                    navController.navigate("about")
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogButton(text: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(15.dp))
            .clickable(
                true,
                onClick = onClick
            )
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(10.dp, 15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        icon()

        Text(
            text = text,
            style = TextStyle(
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// async functions
suspend fun loadNotes(db: AppDatabase, notesList: MutableState<List<Note>>) {
    try {
        notesList.value = db.noteDao().getAllNotes()
    } catch (e: Exception) {
        Log.e("LoadNotes", "Error loading notes: ${e.message}")
    }
}
