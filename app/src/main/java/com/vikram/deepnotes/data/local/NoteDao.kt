package com.vikram.deepnotes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: Note)

    @Query("SELECT * FROM note_table")
    suspend fun getAllNotes(): List<Note>

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getAllNotesFlow(): Flow<List<Note>>

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun update(note: Note)

    // New function to fetch note by ID
    @Query("SELECT * FROM note_table WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: Int): Note?
}
