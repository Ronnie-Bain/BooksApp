package com.example.books.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM Note WHERE `secondary key` = :bookId")
    fun getNotes(bookId: Int): Flow<List<Note>>

    @Query("DELETE FROM Note WHERE `secondary key` = :bookId")
    suspend fun deleteAllNotesOfBookId(bookId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)
}