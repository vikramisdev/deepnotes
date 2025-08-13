package com.vikram.deepnotes.ui.screens.noteeditor

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.vikram.deepnotes.core.GlobalProps
import com.vikram.deepnotes.data.local.Note
import com.vikram.deepnotes.ui.screens.home.loadNotes
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(props: GlobalProps, noteId: Int) {
    val scope = props.scope
    val focusManager = LocalFocusManager.current
    val isDarkTheme = isSystemInDarkTheme()

    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var updating by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }

    val undoTitleStack = remember { mutableStateListOf<String>() }
    val redoTitleStack = remember { mutableStateListOf<String>() }
    val undoContentStack = remember { mutableStateListOf<String>() }
    val redoContentStack = remember { mutableStateListOf<String>() }

    val titleFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { titleFocusRequester.requestFocus() }

    // Load note from DB if editing
    LaunchedEffect(noteId) {
        if (noteId != 0) {
            val note = props.db.noteDao().getNoteById(noteId)
            noteTitle = note?.title ?: ""
            noteContent = note?.content ?: ""
            updating = note != null
        }
    }

    // Track changes
    LaunchedEffect(noteTitle, noteContent) { hasChanges = true }

    fun saveAndExit() {
        if (noteTitle.isBlank()) return
        scope.launch {
            isSaving = true
            saveNote(props, noteId, noteTitle.trim(), noteContent.trim(), updating)
            isSaving = false
            hasChanges = false
            props.navController.popBackStack()
        }
    }

    BackHandler(enabled = hasChanges) { showDiscardDialog = true }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to discard them?") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    hasChanges = false
                    props.navController.popBackStack()
                }) { Text("Discard") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasChanges) showDiscardDialog = true
                        else props.navController.popBackStack()
                    }) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                },
                title = { Text(if (updating) "Edit Note" else "New Note") },
                actions = {
                    IconButton(enabled = hasChanges && !isSaving, onClick = { saveAndExit() }) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Filled.Check, contentDescription = "Save")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasicTextField(
                value = noteTitle,
                onValueChange = { newValue ->
                    undoTitleStack.add(noteTitle)
                    noteTitle = newValue
                    redoTitleStack.clear()
                },
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                singleLine = true,
                cursorBrush = SolidColor(if (isDarkTheme) Color.White else Color.Black),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocusRequester)
            )

            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))

            BasicTextField(
                value = noteContent,
                onValueChange = { newValue ->
                    undoContentStack.add(noteContent)
                    noteContent = newValue
                    redoContentStack.clear()
                },
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                cursorBrush = SolidColor(if (isDarkTheme) Color.White else Color.Black),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private suspend fun saveNote(
    props: GlobalProps,
    id: Int,
    title: String,
    content: String,
    updating: Boolean
) {
    val noteDao = props.db.noteDao()
    if (updating && id != 0) {
        noteDao.update(Note(id, title, content))
    } else {
        noteDao.insert(Note(0, title, content))
    }
    loadNotes(props.db, props.notesList)
}
