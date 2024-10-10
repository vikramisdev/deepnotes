package com.vikram.deepnotes.data.local

import androidx.room.*

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(user: Note)

    @Query("SELECT * FROM note_table")
    suspend fun getAllNotes(): List<Note>

    @Delete
    suspend fun delete(user: Note)

    @Update
    suspend fun update(note: Note)
}
