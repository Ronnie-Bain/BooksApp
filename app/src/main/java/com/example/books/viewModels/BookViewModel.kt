package com.example.books.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.example.books.data.Book
import com.example.books.data.BookDao
import com.example.books.data.SearchedBook
import com.example.books.network.BookSearchApi
import com.example.books.network.OnSearch
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

enum class SearchListStatus { SUCCESS, LOADING, ERROR }

class BookViewModel(private val bookDao: BookDao) : ViewModel() {

    private val QUERY_URL = "https://www.googleapis.com/books/v1/volumes?q="
    var BOOKS = MutableLiveData<MutableList<SearchedBook>>()
    var FromBooksToRead = false

    private val _allItems: LiveData<List<Book>> = bookDao.getBooks().asLiveData()
    val allItems get() = _allItems

    private val _status = MutableLiveData<SearchListStatus>()
    val status: LiveData<SearchListStatus> = _status


    /**
     * Public methods to access private inner functionalities
     */
    fun isBookListEmpty() = !_allItems.value.isNullOrEmpty()

    fun areCaseSensitiveFilled(bookName: String, author: String, pageRead: String, totalPage: String) : Boolean {
        if (bookName.isBlank() || author.isBlank() || pageRead.isBlank() || totalPage.isBlank()) {
            return false
        }
        return true
    }


    fun isUpdateFieldsEmpty(
        bookName: String, author: String, topic: String,
        pageRead: String, totalPage: String, chapter: String
    ) : Boolean {
        if (bookName.isBlank() || author.isBlank() || topic.isBlank()) {
            return true
        }
        else if (pageRead.isBlank() || totalPage.isBlank() || chapter.isBlank()) {
            return true
        }
        return false
    }


    fun prepareToUpdateBooks(
        id: Int, img: String,
        name: String, author: String,
        topic: String, chapter: String,
        pageRead: String, totalPage: String,
        dateCreated: String
    ) {
        val updatedBook = setUpdatedBookContent(
            id, img, name, author,
            topic, chapter, pageRead,
            totalPage, dateCreated
        )
        updateRecord(updatedBook)
    }


    fun addNewRecord(
        img: String,
        name: String, author: String,
        topic: String, chapter: String,
        pageRead: String, totalPage: String
    ) {
        val newBook = if (topic.isBlank() && chapter.isBlank()) {
            getNewBookRecordEntry(img, name, author, "Not Provided", "Not Provided", pageRead, totalPage)
        }
        else if (chapter.isBlank()) {
            getNewBookRecordEntry(img, name, author, topic, "Not Provided", pageRead, totalPage)
        }
        else if (topic.isBlank()) {
            getNewBookRecordEntry(img, name, author, "Not Provided", chapter, pageRead, totalPage)
        }
        else {
            getNewBookRecordEntry(img, name, author, topic, chapter, pageRead, totalPage)
        }
        insertRecord(newBook)
    }


    fun retrieveBook(id: Int) : LiveData<Book> {
        return bookDao.getBook(id).asLiveData()
    }


    @Throws(ExecutionException::class, InterruptedException::class)
    fun retrieveRawBook(id: Int) : Book {
        val callable = Callable {
            bookDao.getRawBook(id)
        }
        val future = Executors.newSingleThreadExecutor().submit(callable)
        return future!!.get()
    }


    fun deleteBookRecord(book : Book) {
        deleteRecord(book)
    }


    private fun getNewBookRecordEntry(
        img: String,
        name: String, author: String,
        topic: String, chapter: String,
        pageRead: String, totalPage: String
    ) : Book {
        val percent = (pageRead.toDouble() / totalPage.toDouble()) * 100
        val status = if (percent != 100.toDouble()) "Not Finished" else "Finished"

        return Book(
            bookImage = img,
            bookName = name, bookAuthor = author,

            pagesRead = pageRead.toInt(), totalPage = totalPage.toInt(),
            readPercent = percent,

            bookTopic = topic,
            chapter = chapter,
            bookStatus = status,

            dateCreated = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            lastRead = null
        )
    }


    private fun setUpdatedBookContent(
        bookId: Int, img: String,
        name: String, author: String,
        topic: String, chapter: String,
        pageRead: String, totalPage: String,
        dateCreated: String
    ) : Book {
        val percent = ( pageRead.toDouble() / totalPage.toDouble() ) * 100
        val status = if (percent != 100.toDouble()) "Not Finished" else "Finished"

        return Book(
            id = bookId, bookImage = img,
            bookName = name, bookAuthor = author,

            pagesRead = pageRead.toInt(), totalPage = totalPage.toInt(),
            readPercent = percent,

            bookTopic = topic,
            chapter = chapter,
            bookStatus = status,

            dateCreated = dateCreated,
            lastRead = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        )
    }


    private fun insertRecord(book: Book) {
        viewModelScope.launch {
            bookDao.insert(book)
        }
    }


    private fun updateRecord(book: Book) {
        viewModelScope.launch {
            bookDao.update(book)
        }
    }


    private fun deleteRecord(book: Book) {
        viewModelScope.launch {
            bookDao.delete(book)
        }
    }



    /**
     * SearchViewModel part
     */
    fun isQueryEmpty(query: String): Boolean {
        if (query.isBlank()) {
            return true
        }
        return false
    }


    fun searchBooks(query: String) {
        val url = QUERY_URL.plus(query)
        getBooks(url)
    }


    fun retrieveCachedBook(id: Int) : SearchedBook? {
        return BOOKS.value?.get(id)
    }


    private fun getBooks(url : String) {
        _status.value = SearchListStatus.LOADING
        val returnedJsonObject = BookSearchApi.retrofitService.getBooks(url)

        returnedJsonObject.enqueue(object : Callback<OnSearch> {

            override fun onResponse(call: Call<OnSearch>, response: Response<OnSearch>) {
                val books = mutableListOf<SearchedBook>()
                response.body().let {
                    for (each in it?.items!!) {
                        val book = SearchedBook(
                            //Book list starts with index -1. To overcome that:
                            books.lastIndex + 1,
                            each?.volumeInfo?.imageLinks?.thumbnail.toString(),
                            each?.volumeInfo?.title.toString(),
                            each?.volumeInfo?.subtitle.toString(),
                            each?.volumeInfo?.authors.toString(),
                            each?.volumeInfo?.pageCount,
                            each?.volumeInfo?.categories.toString()
                        )
                        books.add(book)
                    }
                }
                BOOKS.value = books
                _status.value = SearchListStatus.SUCCESS
                Log.i("BooksViewModel", "Books for $url: ${BOOKS.value.toString()}")
            }

            override fun onFailure(call: Call<OnSearch>, t: Throwable) {
                _status.value = SearchListStatus.ERROR
                Log.e("SearchBookViewModel", "Error in retrieving data for $url", t)
            }
        })
    }
}




class BookViewModelFactory(private val bookDao: BookDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(bookDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}