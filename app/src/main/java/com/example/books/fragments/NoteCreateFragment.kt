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
import com.example.books.dataApplication.BookListApplication
import com.example.books.databinding.FragmentNoteCreateBinding
import com.example.books.viewModels.NoteViewModel
import com.example.books.viewModels.NoteViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NoteCreateFragment : Fragment() {

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as BookListApplication)
                .noteDatabase
                .noteDao()
        )
    }

    private val navArg: NoteCreateFragmentArgs by navArgs()

    private var _binding: FragmentNoteCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoteCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this

        binding.apply {
            noteCancelButton.setOnClickListener {
                findNavController().navigateUp()
            }
            noteAddDoneButton.setOnClickListener {
                addNewNote()
            }
        }
    }

    private fun testFields() : Boolean {
        return viewModel.areFieldsFilled (
            binding.noteAddTitle.text.toString(),
            binding.noteContent.text.toString()
        )
    }

    private fun addNewNote() {
        if (testFields()) {
            viewModel.addNewNoteRecord(
                navArg.bookId,
                binding.noteAddTitle.text.toString(),
                binding.noteContent.text.toString()
            )
            findNavController().navigateUp()
        }
        else showAlertDialog()
    }


    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Please fill the fields")
            .setMessage("Either note title or note content is empty")
            .setCancelable(true)
            .setPositiveButton("Ok") { _, _ -> }
            .show()
    }
}