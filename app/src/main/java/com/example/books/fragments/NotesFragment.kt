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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.books.adapters.NoteListAdapter
import com.example.books.dataApplication.BookListApplication
import com.example.books.databinding.FragmentNotesBinding
import com.example.books.viewModels.NoteViewModel
import com.example.books.viewModels.NoteViewModelFactory

class NotesFragment : Fragment() {

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as BookListApplication).noteDatabase.noteDao()
        )
    }
    private val navArgument: NotesFragmentArgs by navArgs()
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this

        binding.noteRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        //setVisibilityOfStatusLayout(binding.noteListStatusLayout)

        val adapter = NoteListAdapter()

        binding.noteRecyclerView.adapter = adapter

        viewModel.getAllNotes(navArgument.bookId).observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        binding.noteAddFloatingButton.setOnClickListener {
            findNavController().navigate(
                NotesFragmentDirections.actionNotesFragmentToNoteCreateFragment(navArgument.bookId)
            )
        }
    }


    /*private fun setVisibilityOfStatusLayout(view: View) {
        when (viewModel.noteAddedBookList.contains(navArgument.bookId)) {
            true -> view.visibility = View.GONE
            else -> view.visibility = View.VISIBLE
        }
    }*/
}