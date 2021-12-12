package com.example.books.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM book ORDER BY `last read` DESC")
    fun getBooks(): Flow<List<Book>>

    @Query("SELECT * FROM book WHERE id = :id")
    fun getBook(id: Int) : Flow<Book>

    @Query("SELECT * FROM book WHERE id = :id")
    fun getRawBook(id: Int) : Book

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book)
    @Update
    suspend fun update(book: Book)
    @Delete
    suspend fun delete(book: Book)
}