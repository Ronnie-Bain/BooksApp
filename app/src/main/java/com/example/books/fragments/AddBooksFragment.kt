package com.example.books.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.books.R
import com.example.books.data.SearchedBook
import com.example.books.data.getFormattedAuthors
import com.example.books.data.getFormattedTopic
import com.example.books.dataApplication.BookListApplication
import com.example.books.databinding.FragmentAddBooksBinding
import com.example.books.viewModels.BookViewModel
import com.example.books.viewModels.BookViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddBooksFragment : Fragment() {

    private val navigationArgs: AddBooksFragmentArgs by navArgs()
    private var _binding: FragmentAddBooksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookViewModel by activityViewModels {
        BookViewModelFactory(
            (activity?.application as BookListApplication)
                .database
                .bookDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val book = viewModel.retrieveCachedBook(navigationArgs.bookId)
        fillTextFields(book)

        binding.apply {
            inputAddButton.setOnClickListener {
                if (book != null) {
                    addNewBook(book)
                }
            }
            inputCancelButton.setOnClickListener {
                findNavController().navigate(R.id.action_addBooksFragment_to_bookListFragment)
            }
        }
    }

    private fun fillTextFields(book: SearchedBook?) {
        binding.book = book
        binding.inAuthorName.setText(   //removing brackets of author
            book?.getFormattedAuthors()
        )
        binding.inTopic.setText(        //removing brackets of topic
            book?.getFormattedTopic()
        )
    }

    private fun addNewBook(book: SearchedBook) {
        if (isEntryValid()) {
            viewModel.addNewRecord(
                book.imgUrl,
                binding.inBookName.text.toString(),
                binding.inAuthorName.text.toString(),
                binding.inTopic.text.toString(),
                binding.inChapter.text.toString(),
                binding.inPagesRead.text.toString(),
                binding.inTotalPage.text.toString()
            )
            findNavController().navigate(R.id.action_addBooksFragment_to_bookListFragment)
        }
        else showAlertDialog()
    }

    private fun isEntryValid(): Boolean {
        return viewModel.areCaseSensitiveFilled(
            binding.inBookName.text.toString(),
            binding.inAuthorName.text.toString(),
            binding.inPagesRead.text.toString(),
            binding.inTotalPage.text.toString()
        )
    }

    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.Add_dialog_alert))
            .setMessage(getString(R.string.dialog_fields_tip))
            .setCancelable(true)
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }
}