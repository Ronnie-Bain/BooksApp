package com.example.books.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.books.R
import com.example.books.data.Book
import com.example.books.dataApplication.BookListApplication
import com.example.books.databinding.FragmentBookEditBinding
import com.example.books.viewModels.BookViewModel
import com.example.books.viewModels.BookViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BookEditFragment : Fragment() {

    private val navArgs: BookEditFragmentArgs by navArgs()
    private val viewModel: BookViewModel by activityViewModels {
        BookViewModelFactory(
            (activity?.application as BookListApplication).database.bookDao()
        )
    }
    private lateinit var book: Book
    private var _binding: FragmentBookEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this

        book = viewModel.retrieveRawBook(navArgs.bookEditId)
        bindInfos(book)

        binding.updateButton.setOnClickListener { updateBookInfos() }
        binding.cancelButton.setOnClickListener { findNavController().navigateUp() }

        // transitions
    }

    private fun bindInfos(book: Book) {
        binding.book = book
    }

    private fun updateBookInfos() {
        if (!areTextFieldsEmpty()) {
            viewModel.prepareToUpdateBooks(
                book.id, book.bookImage,
                binding.bookNameField.text.toString(),
                binding.authorField.text.toString(),
                binding.topicField.text.toString(),
                binding.chapterField.text.toString(),
                binding.pageReadField.text.toString(),
                binding.totalPageField.text.toString(),
                book.dateCreated.toString()
            )
            findNavController().navigateUp()
        }
        else {
            showAlertDialog()
        }
    }

    private fun areTextFieldsEmpty() : Boolean {
        return viewModel.isUpdateFieldsEmpty(
            binding.bookNameField.text.toString(),
            binding.authorField.text.toString(),
            binding.topicField.text.toString(),
            binding.pageReadField.text.toString(),
            binding.totalPageField.text.toString(),
            binding.chapterField.text.toString()
        )
    }

    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.book_edit_null_field_alert_dialog))
            .setCancelable(true)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ -> }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}