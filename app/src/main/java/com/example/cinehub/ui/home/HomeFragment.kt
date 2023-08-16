package com.example.cinehub.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cinehub.R
import com.example.cinehub.databinding.FragmentHomeBinding
import com.example.cinehub.ui.detail.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var movieAdapter: MovieAdapter
    private var isGridView: Boolean = false
    private val sharedPreferences by lazy { requireActivity().getSharedPreferences("CineHubPrefs", Context.MODE_PRIVATE) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        movieAdapter = MovieAdapter(

            object : MovieClickListener {
                override fun onMovieClicked(movieId: Int?) {
                    movieId?.let {
                        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(it)
                        findNavController().navigate(action)
                    }
                }
            },
            viewModel,
            viewLifecycleOwner,
            requireContext(),
        )

        binding.homeRecyclerView.adapter = movieAdapter
        observeEvents()
        val isGridView = sharedPreferences.getBoolean("isGridView", false)
        binding.homeRecyclerView.layoutManager = GridLayoutManager(requireContext(), if (isGridView) 2 else 1)
        movieAdapter.setGridMode(isGridView)
        return binding.root
    }

    private fun observeEvents() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding.textViewHomeError.text = error
            binding.textViewHomeError.isVisible = true
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movieList.collect { pagingData ->
                movieAdapter.submitData(pagingData)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.custom_action_bar, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.searchMovies(query).collect { pagingData ->
                        movieAdapter.submitData(pagingData)
                        Log.d("SearchMovies", "Returned data: $pagingData")
                    }
                }
                return true
            }

            //Arama metninde değüşlik oldukca tetiklenen fonks.
            override fun onQueryTextChange(newText: String): Boolean {
                // Arama metni değiştiğinde eğer boşsa, orjinal listeyi gösteren fonk.
                if (newText.isBlank()) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.movieList.collect { pagingData ->
                            movieAdapter.submitData(pagingData)
                        }
                    }
                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.searchMovies(newText).collect { pagingData ->
                            movieAdapter.submitData(pagingData)
                            Log.d("SearchMovies", "Returned data: $pagingData")
                        }
                    }
                }
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {


            R.id.grid_view -> {
                toggleView()
                return true

            }

            R.id.list_view -> {
                toggleView()
                return true

            }
        }
        return true
    }
    private fun toggleView() {
        val layoutManager = binding.homeRecyclerView.layoutManager as GridLayoutManager
        val isGridView = layoutManager.spanCount == 2
        layoutManager.spanCount = if (isGridView) 1 else 2
        sharedPreferences.edit().putBoolean("isGridView", !isGridView).apply()
        movieAdapter.setGridMode(!isGridView)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
