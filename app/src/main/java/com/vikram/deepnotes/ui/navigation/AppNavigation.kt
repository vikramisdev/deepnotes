package com.vikram.deepnotes.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.vikram.deepnotes.core.GlobalProps
import com.vikram.deepnotes.ui.screens.about.AboutScreen
import com.vikram.deepnotes.ui.screens.home.HomeScreen
import com.vikram.deepnotes.ui.screens.noteeditor.NoteEditorScreen
import com.vikram.deepnotes.ui.screens.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(navController: NavHostController, props: GlobalProps) {
    AnimatedNavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn(tween(700)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut(tween(700)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn(tween(700)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut(tween(700)) }
    ) {
        composable("home") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                HomeScreen(props)
            }
        }
        composable("settings") { SettingsScreen(props) }
        composable("about") { AboutScreen(props) }
        // Pass only noteId
        composable("noteEditor/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull() ?: 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                NoteEditorScreen(props, noteId)
            }
        }
    }
}
