package com.example.books.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.example.books.data.Note
import com.example.books.data.NoteDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {

    /*private val _noteAddedBookList = noteDao.getNotes(bookId)
    val noteAddedBookList get() = _noteAddedBookList*/


    /**
     * Public methods to access private inner functionalities
     */
    fun getAllNotes(bookId: Int) : LiveData<List<Note>> {
        return noteDao.getNotes(bookId).asLiveData()
    }


    fun areFieldsFilled(noteTitle: String, noteContent: String) : Boolean {
        if (noteTitle.isBlank() || noteContent.isBlank()) {
            return false
        }
        return true
    }


    fun addNewNoteRecord(bookId: Int, noteTitle: String, noteContent: String) {
        val newNote = setNoteContents(bookId, noteTitle, noteContent)
        insertRecord(newNote)
        //addToNoteAddedBookList(bookId)
    }


    fun deleteNoteRecords(note: Note) {
        deleteRecord(note)
    }


    fun prepareToDeleteAttachedNotes(bookId: Int) {
        deleteAttachedNotes(bookId)
    }



    /**
     * ViewModel functionalities
     */

    private fun setNoteContents(bookNoteId: Int, noteTitle: String, noteContent: String) : Note {
        return Note(
            bookId = bookNoteId,
            noteTitle = noteTitle,
            noteContent = noteContent,
            noteDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        )
    }


    /*private fun addToNoteAddedBookList(bookId: Int) {
        _noteAddedBookList.add(bookId)
    }*/


    private fun deleteAttachedNotes(bookId: Int) {
        viewModelScope.launch {
            noteDao.deleteAllNotesOfBookId(bookId)
        }
    }


    private fun insertRecord(newNote: Note) {
        viewModelScope.launch {
            noteDao.insert(newNote)
        }
    }


    private fun deleteRecord(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }
}




class NoteViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}