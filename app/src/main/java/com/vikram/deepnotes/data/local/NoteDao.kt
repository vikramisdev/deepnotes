package com.vikram.deepnotes.data.local

import androidx.room.*

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: Note)

    @Query("SELECT * FROM note_table")
    suspend fun getAllNotes(): List<Note>

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun update(note: Note)
}
