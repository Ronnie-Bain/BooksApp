package com.example.books.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.books.R
import com.example.books.data.Book
import com.example.books.dataApplication.BookListApplication
import com.example.books.databinding.FragmentBookDetailsBinding
import com.example.books.util.themeColor
import com.example.books.viewModels.BookViewModel
import com.example.books.viewModels.BookViewModelFactory
import com.google.android.material.transition.MaterialContainerTransform

class BookDeatilsFragment : Fragment() {

    private val viewModel: BookViewModel by activityViewModels {
        BookViewModelFactory(
            (activity?.application as BookListApplication)
                .database
                .bookDao()
        )
    }

    private val navArgs: BookDeatilsFragmentArgs by navArgs()
    lateinit var book: Book

    private var _binding: FragmentBookDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                findNavController().navigate(R.id.action_bookDeatilsFragment_to_startFragment)
                true
            }
            R.id.dark_theme -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 300.toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBookDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navArgs.bookId
        viewModel.retrieveBook(id).observe(this.viewLifecycleOwner) { selectedItem ->
            book = selectedItem
            bind(book)
        }
    }

    private fun bind(book: Book) {
        binding.book = book
    }
}