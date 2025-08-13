import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vikram.deepnotes.core.GlobalProps
import com.vikram.deepnotes.data.local.Note
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun NoteCard(
    note: Note,
    props: GlobalProps
) {
    val offsetX = remember { Animatable(0f) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .clickable {
                // Navigate using noteId
                props.navController.navigate("noteEditor/${note.id}")
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        scope.launch {
                            val newOffset = (offsetX.value + dragAmount).coerceIn(0f, 300f)
                            offsetX.snapTo(newOffset)
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            if (offsetX.value > 200f) {
                                // Temporarily remove note
                                val deletedNote = note
                                props.notesList.value = props.notesList.value - note

                                // Show snackbar with Undo
                                val result = snackbarHostState.showSnackbar(
                                    message = "Note deleted",
                                    actionLabel = "Undo"
                                )

                                if (result == SnackbarResult.ActionPerformed) {
                                    // Undo deletion
                                    props.notesList.value = props.notesList.value + deletedNote
                                } else {
                                    // Permanently delete from DB
                                    props.db.noteDao().delete(deletedNote)
                                }
                            }
                            offsetX.animateTo(0f, tween(300))
                        }
                    }
                )
            }
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(15.dp))
            .padding(15.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = note.title,
            fontSize = 16.sp,
            lineHeight = 25.sp,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = note.content,
            fontSize = 14.sp,
            maxLines = 10,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

    SnackbarHost(hostState = snackbarHostState)
}
