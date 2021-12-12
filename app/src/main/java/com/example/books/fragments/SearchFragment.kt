package com.example.books.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.books.R
import com.example.books.adapters.SearchListAdapter
import com.example.books.dataApplication.BookListApplication
import com.example.books.databinding.FragmentSearchBooksBinding
import com.example.books.viewModels.BookViewModel
import com.example.books.viewModels.BookViewModelFactory

class SearchFragment : Fragment() {

    private val viewModel: BookViewModel by activityViewModels {
        BookViewModelFactory(
            (activity?.application as BookListApplication)
                .database
                .bookDao()
        )
    }

    private lateinit var binding: FragmentSearchBooksBinding

    private fun isQueryNull(query: String): Boolean {
        return viewModel.isQueryEmpty(query)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchBooksBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = SearchListAdapter {
            if (viewModel.FromBooksToRead) {
                findNavController().navigate(R.id.action_searchFragment_to_booksToReadFragment)
            }
            this.findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToAddBooksFragment(it.id)
            )
        }

        binding.recyclerView.adapter = adapter

        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val query = binding.searchField.text.toString()
                    if (!isQueryNull(query)) {
                        viewModel.searchBooks(query)
                    }
                    //hiding the keyboard
                    val inputMethodManager =
                        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    true
                }
                else -> false
            }
        }
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }


    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}