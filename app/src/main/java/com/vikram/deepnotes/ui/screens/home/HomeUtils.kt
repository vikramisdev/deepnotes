package com.vikram.deepnotes.ui.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.vikram.deepnotes.core.GlobalProps
import com.vikram.deepnotes.data.local.AppDatabase
import com.vikram.deepnotes.data.local.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Loads all notes from the database into the notesList state
suspend fun loadNotes(db: AppDatabase, notesList: MutableState<List<Note>>) {
    try {
        val notes = withContext(Dispatchers.IO) {
            db.noteDao().getAllNotes()
        }
        notesList.value = notes
    } catch (e: Exception) {
        Log.e("HomeUtils", "Error loading notes: ${e.message}")
    }
}

// Navigates to the note editor screen with encoded note data for editing
fun navigateToEditor(note: Note, navController: NavController, props: GlobalProps) {
    // Set props to the clicked note
    props.noteId.value = note.id.toInt()
    props.title.value = note.title
    props.content.value = note.content
    props.updating.value = true

    navController.navigate("noteEditor")
}

// Optional: Helper to delete a note and reload list within a CoroutineScope
fun deleteNoteAndReload(
    note: Note,
    db: AppDatabase,
    notesList: MutableState<List<Note>>,
    scope: CoroutineScope
) {
    scope.launch {
        try {
            withContext(Dispatchers.IO) {
                db.noteDao().delete(note)
            }
            loadNotes(db, notesList)
        } catch (e: Exception) {
            Log.e("HomeUtils", "Error deleting note: ${e.message}")
        }
    }
}
