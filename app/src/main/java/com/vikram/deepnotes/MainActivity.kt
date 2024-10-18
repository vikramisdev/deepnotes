package com.vikram.deepnotes

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.vikram.deepnotes.data.local.AppDatabase
import com.vikram.deepnotes.data.local.Note
import com.vikram.deepnotes.ui.theme.DeepNotesTheme
import kotlinx.coroutines.CoroutineScope


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

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val db = AppDatabase.getDatabase(this)
            val scope = rememberCoroutineScope()

            val props = GlobalProps(
                context = this,
                scope = scope,
                navController = rememberNavController(),
                currentTheme = remember {
                    mutableStateOf(Theme.SYSTEM_THEME.toString())
                },
                db = db,
                maxNoteLineLimit = 10,
                searchBarQuery = remember {
                    mutableStateOf("")
                },
                isSearchBarActive = remember {
                    mutableStateOf(false)
                },
                showProfileDialog = remember {
                    mutableStateOf(false)
                },
                notesList = remember {
                    mutableStateOf<List<Note>>(emptyList())
                },
                noteId = remember {
                    mutableStateOf(0)
                },
                title = remember {
                    mutableStateOf("")
                },
                content = remember {
                    mutableStateOf("")
                },
                updating = remember {
                    mutableStateOf(false)
                },
                showSettingsDialog = remember {
                    mutableStateOf(false)
                }
            )

            MainComposable(props)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainComposable(props: GlobalProps) {

    DeepNotesTheme(
        theme = props.currentTheme
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            @Suppress("DEPRECATION")
            AnimatedNavHost(
                navController = props.navController as NavHostController,
                startDestination = "home",
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn(
                        animationSpec = tween(
                            700
                        )
                    )
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut(
                        animationSpec = tween(
                            700
                        )
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn(
                        animationSpec = tween(
                            700
                        )
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut(
                        animationSpec = tween(
                            700
                        )
                    )
                }
            ) {
                composable("home") { Home(props) }
                composable("settings") { Settings(props) }
                composable("about") { About(props) }

                // Route with parameters
                composable("noteEditor?noteId={noteId}&title={title}&content={content}&updating={updating}") { backStackEntry ->
                    props.noteId.value = backStackEntry.arguments?.getString(("noteId"))?.toInt() ?: 0
                    props.title.value = backStackEntry.arguments?.getString("title") ?: ""
                    props.content.value = backStackEntry.arguments?.getString("content") ?: ""
                    props.updating.value =
                        backStackEntry.arguments?.getString("updating")?.toBoolean() ?: false

                    NoteEditor(props)
                }
            }
        }
    }
}