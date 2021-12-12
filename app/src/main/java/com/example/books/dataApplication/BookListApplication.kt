package com.example.books.dataApplication

import android.app.Application
import com.example.books.data.BookDatabase
import com.example.books.data.NoteDatabase

class BookListApplication : Application() {

    val database : BookDatabase by lazy { BookDatabase.getDatabase(this) }

    val noteDatabase: NoteDatabase by lazy {NoteDatabase.getNoteDataBase(this)}
}