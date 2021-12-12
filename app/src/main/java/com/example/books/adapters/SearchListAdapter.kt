package com.example.books.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.books.data.SearchedBook
import com.example.books.data.getFormattedAuthors
import com.example.books.data.getFormattedSubtitle
import com.example.books.data.getFormattedTitle
import com.example.books.databinding.SearchBookCardBinding

class SearchListAdapter(private val onBookClicked: (SearchedBook) -> Unit) :
    ListAdapter<SearchedBook, SearchListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            SearchBookCardBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onBookClicked(current)
        }
        holder.bind(current)
    }

    class ViewHolder(private var binding: SearchBookCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: SearchedBook) {
            binding.book = book
            binding.apply {
                bookName.text = book.getFormattedTitle()
                subtitle.text = book.getFormattedSubtitle()
                authorName.text = book.getFormattedAuthors()
            }
        }
    }

    companion object {

        val DiffCallback = object : DiffUtil.ItemCallback<SearchedBook>() {
            override fun areItemsTheSame(oldItem: SearchedBook, newItem: SearchedBook): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: SearchedBook, newItem: SearchedBook): Boolean {
                return oldItem.title == newItem.title
            }
        }
    } 
}