package com.vikram.deepnotes

import android.annotation.SuppressLint
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.vikram.deepnotes.data.local.AppDatabase
import com.vikram.deepnotes.ui.theme.DeepNotesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Root()
        }
    }
}

@OptIn(DelicateCoroutinesApi::class, ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun Root() {
    val context = LocalContext.current
    val coroutineContext = GlobalScope.coroutineContext
    val db = AppDatabase.getDatabase(context)
    val scope = CoroutineScope(Dispatchers.Default)
    val navController = rememberNavController()

    var currentTheme = remember {
        mutableStateOf(Theme.SYSTEM_THEME)
    }
    var notesSize = remember {
        mutableIntStateOf(0)
    }

    DeepNotesTheme(
        theme = currentTheme
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            AnimatedNavHost(
                navController = navController,
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
                composable("home") {
                    Home(navController, db)
                }
                composable("settings") { Settings(navController, currentTheme) }
                composable("about") { About(navController) }

                // Route with parameters
                composable("noteEditor?noteId={noteId}&title={title}&content={content}&updating={updating}") { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString(("noteId")) ?: "0"
                    val title = backStackEntry.arguments?.getString("title") ?: ""
                    val content = backStackEntry.arguments?.getString("content") ?: ""
                    val updating =
                        backStackEntry.arguments?.getString("updating")?.toBoolean() ?: false

                    NoteEditor(
                        noteId = noteId.toInt(),
                        title = title,
                        updating = updating,
                        content = content,
                        navController = navController,
                        db = db
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun RootPreview() {
    Root()
}