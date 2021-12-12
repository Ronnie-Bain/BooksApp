package com.example.books.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "book image")
    val bookImage: String,
    @ColumnInfo(name = "book name")
    val bookName: String,
    @ColumnInfo(name = "author name")
    val bookAuthor: String,

    @ColumnInfo(name = "topic")
    val bookTopic: String,
    @ColumnInfo(name = "status")
    val bookStatus: String,
    @ColumnInfo(name = "chapter")
    val chapter: String,

    @ColumnInfo(name = "read percent")
    val readPercent: Double,
    @ColumnInfo(name = "pages read")
    val pagesRead: Int,
    @ColumnInfo(name = "total page")
    val totalPage: Int,

    @ColumnInfo(name = "date")
    val dateCreated: String?,
    @ColumnInfo(name = "last read")
    val lastRead: String?
)

fun Book.getFormattedPercent() = "%.2f".format(readPercent)