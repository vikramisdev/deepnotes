package com.vikram.deepnotes

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.vikram.deepnotes.data.local.AppDatabase
import com.vikram.deepnotes.data.local.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun NoteEditor(noteId: Int = 0,  title: String = "", content: String = "", updating: Boolean = false, navController: NavController, db: AppDatabase) {
    var title = remember { mutableStateOf(title) }
    var content = remember { mutableStateOf(content) }
    val scope = CoroutineScope(GlobalScope.coroutineContext)
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = if(updating) "Update" else "Save"
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Check"
                    )
                },
                onClick = {
                    if(updating) {
                        if(title.value.isNotEmpty() && content.value.isNotEmpty()) {
                            scope.launch {
                                db.noteDao().update(
                                    Note(id = noteId, title = title.value, content = content.value)
                                )

                                withContext(Dispatchers.Main) {
                                    showToast(context = context, text = "Note Updated " + noteId.toString())
                                }
                            }
                        }
                    } else {
                        if(title.value.isNotEmpty() && content.value.isNotEmpty()) {
                            scope.launch {
                                db.noteDao().insert(
                                    Note(title = title.value, content = content.value)
                                )

                                withContext(Dispatchers.Main) {
                                    showToast(context = context, text = "Note Saved")
                                }
                            }
                        }
                    }

                    navController.popBackStack()
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
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Arrow Back"
                        )
                    }
                }
            )

            // title input field

            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = title.value,
                onValueChange = { newText ->
                    title.value = newText
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
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = content.value,
                onValueChange = { newText ->
                    content.value = newText
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

@Preview
@Composable
fun NoteEditorPreview() {
    var navController = rememberNavController()
    var context = LocalContext.current
    NoteEditor(navController = navController, db = AppDatabase.getDatabase(context))
}