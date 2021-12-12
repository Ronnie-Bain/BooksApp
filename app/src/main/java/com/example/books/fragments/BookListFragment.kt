package com.example.books.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.books.R
import com.example.books.adapters.BookListAdapter
import com.example.books.data.Book
import com.example.books.dataApplication.BookListApplication
import com.example.books.databinding.FragmentBookListBinding
import com.example.books.viewModels.BookViewModel
import com.example.books.viewModels.BookViewModelFactory
import com.example.books.viewModels.NoteViewModel
import com.example.books.viewModels.NoteViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialElevationScale

class BookListFragment: Fragment(), BookListAdapter.BookListAdapterListener {

    private val viewModel: BookViewModel by activityViewModels {
        BookViewModelFactory(
            (activity?.application as BookListApplication).database.bookDao()
        )
    }
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(
            (activity?.application as BookListApplication).noteDatabase.noteDao()
        )
    }
    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!
    lateinit var book: Book
    private var actionMode: ActionMode? = null

    /**
     * Contextual menu setUp
     */
    private val actionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.contextual_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.menu_delete -> {
                    showConfirmationDialog()
                    mode?.finish()
                    true
                }
                R.id.menu_edit -> {
                    editBook()
                    mode?.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
        }
    }


    /**
     * Options menu items set-up for action bar
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                findNavController().navigate(R.id.action_bookListFragment_to_startFragment)
                true
            }
            R.id.dark_theme -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    /**
     * Fragment life-cycle functions
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        setVisibility(binding.bookListStatusLayout)
        val adapter = BookListAdapter(this)

        binding.recyclerView.adapter = adapter

        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
    }


    /**
     * private methods
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_alert_dialog_message))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ -> deleteRecord() }
            .show()
    }

    private fun deleteRecord() {
        viewModel.deleteBookRecord(book)
        deleteAttachedNotes()
    }

    //Deleting the notes that was attached to the Book that is deleted
    private fun deleteAttachedNotes() {
        noteViewModel.prepareToDeleteAttachedNotes(book.id)
    }

    private fun editBook() {
        this.findNavController().navigate(
            BookListFragmentDirections.actionBookListFragmentToBookEditFragment(book.id)
        )
    }

    private fun setVisibility(view: View) {
        Log.d("BindingAdapters", "Books List status value : ${viewModel.isBookListEmpty()}")
        when (viewModel.isBookListEmpty()) {
            true -> view.visibility = View.GONE
            else -> view.visibility = View.VISIBLE
        }
    }


    /**
     * Overriding [BookListAdapterListener] interface methods
     */
    override fun onBookClicked(cardView: View, book: Book) {
        val bookDetailsTransitionName = getString(R.string.book_details)
        val extras = FragmentNavigatorExtras(cardView to bookDetailsTransitionName)
        val directions = BookListFragmentDirections.actionBookListFragmentToBookDeatilsFragment(book.id)
        findNavController().navigate(directions, extras)

        exitTransition = MaterialElevationScale(false).apply {
            duration = 300.toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 300.toLong()
        }
    }

    override fun onBookLongClicked(cardView: View, book: Book) : Boolean {
        this.book = book
        if (actionMode == null) {
            actionMode = activity?.startActionMode(actionModeCallback)
            cardView.isSelected = true
            return true
        }
        return false
    }

    override fun goToNoteScreen(book: Book) {
        findNavController().navigate(
            BookListFragmentDirections.actionBookListFragmentToNotesFragment(book.id)
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}