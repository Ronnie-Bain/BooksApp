package com.example.books.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(

    @PrimaryKey(autoGenerate = true)
    val key: Int = 0,

    @ColumnInfo(name = "secondary key")
    val bookId: Int,

    @ColumnInfo(name = "note title")
    val noteTitle: String,

    @ColumnInfo(name = "note content")
    val noteContent: String,

    @ColumnInfo(name = "note date")
    val noteDate: String
)
