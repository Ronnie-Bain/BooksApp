package com.example.books.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.books.R
import com.example.books.dataApplication.BookListApplication
import com.example.books.databinding.FragmentStartMenuBinding
import com.example.books.viewModels.BookViewModel
import com.example.books.viewModels.BookViewModelFactory

class StartFragment : Fragment() {

    private var binding: FragmentStartMenuBinding? = null
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
    ) : View? {
        binding = FragmentStartMenuBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {

            lifecycleOwner = viewLifecycleOwner

            menuButton1.setOnClickListener {
                findNavController().navigate(R.id.action_startFragment_to_searchFragment)
            }
            menuButton2.setOnClickListener {
                viewModel.FromBooksToRead = true
                findNavController().navigate(R.id.action_startFragment_to_searchFragment)
            }
            menuButton3.setOnClickListener {
                findNavController().navigate(R.id.action_startFragment_to_bookListFragment)
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