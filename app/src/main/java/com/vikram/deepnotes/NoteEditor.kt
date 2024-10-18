package com.vikram.deepnotes

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.vikram.deepnotes.data.local.Note
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun NoteEditor(props: GlobalProps) {

    BackHandler {
        insertOrUpdateNote(props = props)
        props.navController.popBackStack()
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = if (props.updating.value) "Update" else "Save"
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Check"
                    )
                },
                onClick = {
                    insertOrUpdateNote(props = props)
                    props.navController.popBackStack()
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopAppBar(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background),
                title = {
                    Text(
                        text = "",
                        style = TextStyle(
                            fontSize = 20.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        props.navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Arrow Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            props.scope.launch {
                                props.db.noteDao().delete(
                                    Note(
                                        id = props.noteId.value,
                                        title = props.title.toString(),
                                        content = props.content.toString()
                                    )
                                )

                                props.navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            )

            // title input field

            @Suppress("DEPRECATION")
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = props.title.value,
                onValueChange = { newText ->
                    props.title.value = newText
                },
                placeholder = {
                    Text(text = "Title")
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RectangleShape,
                textStyle = TextStyle(
                    fontSize = 20.sp
                )
            )

            // content input field
            @Suppress("DEPRECATION")
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = props.content.value,
                onValueChange = { newText ->
                    props.content.value = newText
                },
                placeholder = {
                    Text(text = "Write Something ...")
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RectangleShape
            )
        }
    }
}

fun insertOrUpdateNote(props: GlobalProps) {
    if (props.updating.value) {
        if (props.title.value.isNotEmpty() || props.content.value.isNotEmpty()) {
            props.scope.launch {
                props.db.noteDao().update(
                    Note(
                        id = props.noteId.value,
                        title = props.title.value,
                        content = props.content.value
                    )
                )

                withContext(Dispatchers.Main) {
                    showToast(
                        context = props.context,
                        text = "Note Updated " + props.noteId.value
                    )
                }
            }
        }
    } else {
        if (props.title.value.isNotEmpty() || props.content.value.isNotEmpty()) {
            props.scope.launch {
                props.db.noteDao().insert(
                    Note(title = props.title.value, content = props.content.value)
                )

                withContext(Dispatchers.Main) {
                    showToast(context = props.context, text = "Note Saved")
                }
            }
        }
    }
}
