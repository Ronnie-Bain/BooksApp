package com.example.books.adapters

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.books.R
import com.example.books.data.Note
import com.example.books.data.SearchedBook
import com.example.books.viewModels.SearchListStatus

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<SearchedBook>?) {
    val adapter = recyclerView.adapter as SearchListAdapter
    adapter.submitList(data)
}

@BindingAdapter("srcUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            placeholder(R.drawable.book_image)
        }
    }
}

@BindingAdapter("searchResponse")
fun setImageBasedOnStatus(view: ImageView, status: SearchListStatus?) {
    when (status) {
        SearchListStatus.LOADING -> {
            view.visibility = View.VISIBLE
            view.setImageResource(R.drawable.loading_animation)
        }
        SearchListStatus.ERROR -> {
            view.visibility = View.VISIBLE
            view.setImageResource(R.drawable.search_list_error)
        }
        SearchListStatus.SUCCESS -> {
            view.visibility = View.GONE
        }
    }
}

/*@BindingAdapter("setBookListVisibility")
fun setVisibility(view: View, value: Boolean) {
    Log.d("BindingAdapters", "Books List status value : $value")
    when (value) {
        true -> view.visibility = View.GONE
        false -> view.visibility = View.VISIBLE
        else -> Log.i("BindingAdapter", "Problem is viewModelCalculations")
    }
}*/

@BindingAdapter("setVisibilityNoteData")
fun setVisibilityOfNoteStatus(view: View, list: LiveData<List<Note>>) {
    when (list.value.isNullOrEmpty()) {
        true -> view.visibility = View.VISIBLE
        else -> view.visibility = View.GONE
    }
}