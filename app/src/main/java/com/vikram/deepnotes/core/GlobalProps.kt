package com.vikram.deepnotes.core

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.vikram.deepnotes.data.local.AppDatabase
import com.vikram.deepnotes.data.local.Note
import com.vikram.deepnotes.data.local.NoteDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

data class GlobalProps(
    val context: Context,
    val scope: CoroutineScope,
    val navController: NavController,
    val currentTheme: MutableState<String>,
    val db: AppDatabase,
    val maxNoteLineLimit: Int,
    val searchBarQuery: MutableState<String>,
    val isSearchBarActive: MutableState<Boolean>,
    val showProfileDialog: MutableState<Boolean>,
    val notesList: MutableState<List<Note>>,
    var noteId: MutableState<Int>,
    var title: MutableState<String>,
    var content: MutableState<String>,
    var updating: MutableState<Boolean>,
    var showSettingsDialog: MutableState<Boolean>,
)

@SuppressLint("UnrememberedMutableState")
@Composable
fun fakeGlobalProps(): GlobalProps {
    val context = LocalContext.current
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    val navController = rememberNavController()

    // For preview, create dummy AppDatabase or mock it if possible
    // Here, assuming db can be null or you provide a stub
    val fakeDb = object : AppDatabase() {
        // implement abstract members if needed or leave empty
        override fun noteDao(): NoteDao {
            TODO("Not yet implemented")
        }

        override fun clearAllTables() {
            TODO("Not yet implemented")
        }

        override fun createInvalidationTracker(): InvalidationTracker {
            TODO("Not yet implemented")
        }

        override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
            TODO("Not yet implemented")
        }
    }

    return GlobalProps(
        context = context,
        scope = scope,
        navController = navController,
        currentTheme = mutableStateOf("Light"),
        db = fakeDb,
        maxNoteLineLimit = 3,
        searchBarQuery = mutableStateOf(""),
        isSearchBarActive = mutableStateOf(false),
        showProfileDialog = mutableStateOf(false),
        notesList = mutableStateOf(listOf<Note>()),
        noteId = mutableStateOf(-1),
        title = mutableStateOf(""),
        content = mutableStateOf(""),
        updating = mutableStateOf(false),
        showSettingsDialog = mutableStateOf(false),
    )
}