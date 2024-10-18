package com.vikram.deepnotes

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Settings(props: GlobalProps) {
    if (props.showSettingsDialog.value) {
        ShowThemeDialog(props)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Settings",
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
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Arrow Back"
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(10.dp)
        ) {
            SettingTheme(props)
        }
    }
}


@Composable
fun SettingTheme(props: GlobalProps) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {


        Text(
            modifier = Modifier.padding(10.dp),
            text = "Select Theme",
            style = TextStyle(
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Button(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp)),
            onClick = {
                if (!props.showSettingsDialog.value) {
                    props.showSettingsDialog.value = true
                }
            }
        ) {
            @Suppress("DEPRECATION")
            Text(
                text = props.currentTheme.value.toString().replace("_", " ").capitalize(),
                style = TextStyle(
                    fontSize = 18.sp
                )
            )
        }
    }
}

@Composable
fun ShowThemeDialog(props: GlobalProps) {
    Dialog(
        onDismissRequest = {
            props.showSettingsDialog.value = false
        }
    ) {
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            ThemeOption(
                name = "Light Theme",
                currentTheme = props.currentTheme,
                selectedTheme = Theme.LIGHT_THEME,
                showDialog = props.showSettingsDialog
            )
            ThemeOption(
                name = "Dark Theme",
                currentTheme = props.currentTheme,
                selectedTheme = Theme.DARK_THEME,
                showDialog = props.showSettingsDialog
            )
            ThemeOption(
                name = "System Theme",
                currentTheme = props.currentTheme,
                selectedTheme = Theme.SYSTEM_THEME,
                showDialog = props.showSettingsDialog
            )
        }
    }
}

@Composable
fun ThemeOption(
    name: String,
    currentTheme: MutableState<String>,
    selectedTheme: Theme,
    showDialog: MutableState<Boolean>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                true,
                onClick = {
                    showDialog.value = false
                    currentTheme.value = selectedTheme.toString()
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = currentTheme.value == selectedTheme.name, onClick = {
            showDialog.value = false
            currentTheme.value = selectedTheme.toString()
        })
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
