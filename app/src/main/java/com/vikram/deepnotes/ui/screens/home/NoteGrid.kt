package com.vikram.deepnotes.ui.screens.home

import NoteCard
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vikram.deepnotes.core.GlobalProps
import com.vikram.deepnotes.data.local.Note

@Composable
fun NoteGrid(props: GlobalProps) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        items(props.notesList.value.size) { idx ->
            NoteCard(props.notesList.value[idx], props)
        }
    }
}

@Composable
fun NoteRow(note: Note, props: GlobalProps) {
    Column(
        modifier = Modifier
            .clickable {
                // Pass all note data to the editor screen
                props.noteId.value = note.id.toInt()
                props.title.value = note.title
                props.content.value = note.content
                props.updating.value = true
                props.navController.navigate("noteEditor")
            }
            .padding(20.dp, 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            note.title,
            fontSize = 16.sp,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            note.content,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    Divider()
}
