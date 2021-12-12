package com.example.books.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.books.R
import com.example.books.data.Book
import com.example.books.data.getFormattedPercent
import com.example.books.databinding.BookListCardBinding
import com.example.books.fragments.BookListFragment

class BookListAdapter(private val listener: BookListAdapterListener) :
    ListAdapter<Book, BookListAdapter.BookViewHolder>(DiffCallback) {

    interface BookListAdapterListener {
        fun onBookClicked(cardView: View, book: Book)
        fun onBookLongClicked(cardView: View, book: Book): Boolean
        fun goToNoteScreen(book: Book)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(
            BookListCardBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            ),
            listener
        )
    }


    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }


    class BookViewHolder(
        private var binding: BookListCardBinding,
        listener: BookListAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.run { this.listener = listener }
        }
        fun bind(book: Book) {
            binding.book = book
            binding.totalReadResult.text = book.getFormattedPercent()
        }
    }


    companion object {

        private val DiffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return (oldItem === newItem)
            }
            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return (oldItem.bookName == newItem.bookName)
            }
        }
    }
}