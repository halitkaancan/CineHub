package com.example.cinehub.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.cinehub.MainActivity
import com.example.cinehub.R
import com.example.cinehub.databinding.FragmentDetailBinding
import com.example.cinehub.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<DetailViewModel>()

    private val args by navArgs<DetailFragmentArgs>()

    private var isFavorite: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        viewModel.getMovieDetail(movieId = args.moiveId)
        observeEvents()
        return binding?.root
    }
    private fun observeEvents() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding?.progressBarDetail?.isVisible = it
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            binding?.textViewErrorDetail?.text = it
            binding?.textViewErrorDetail?.isVisible = true
        }
        viewModel.movieResponse.observe(viewLifecycleOwner) { movie ->
            binding?.imageViewDetail?.loadImage(movie.backdropPath)

            binding?.textViewDetailVote?.text = movie.voteAverage.toString()
            binding?.textViewDetailStudio?.text = movie.productionCompanies?.first()?.name
            binding?.textViewDetailLanguage?.text = movie.spokenLanguages?.first()?.englishName
            binding?.textViewDetailOverview?.text = movie.overview
            binding?.movieDetailGroup?.isVisible = true

            (requireActivity() as MainActivity).supportActionBar?.title = movie.title

            viewModel.getLikeStatusLiveData(args.moiveId.toString())
                .observe(viewLifecycleOwner) { likeStatus ->
                    isFavorite = likeStatus?.isLiked ?: false
                    updateFavoriteButton(isFavorite)
                }

            binding?.detailFavoriteButton?.setOnClickListener {
                viewModel.toggleLikeStatus(args.moiveId.toString())
            }
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        // Set the empty heart or full heart icon depending on the favorite status
        val heartIcon = if (isFavorite) R.drawable.ic_liked else R.drawable.ic_nonlike
        binding?.detailFavoriteButton?.setImageResource(heartIcon)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true) // onCreateOptionsMenu() yöntemini çağırılmasını sağlar
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear() //
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
