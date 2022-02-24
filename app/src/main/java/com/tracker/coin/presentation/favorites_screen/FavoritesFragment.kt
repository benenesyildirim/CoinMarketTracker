package com.tracker.coin.presentation.favorites_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.tracker.coin.R
import com.tracker.coin.common.Constants
import com.tracker.coin.common.Resource
import com.tracker.coin.common.RowMarginDecoration
import com.tracker.coin.databinding.FragmentFavoritesBinding
import com.tracker.coin.presentation.CoinListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var binding: FragmentFavoritesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(layoutInflater)
        observeFavorites()

        binding.favoritesRv.addItemDecoration(RowMarginDecoration(10))

        return binding.root
    }

    private fun observeFavorites() {
        with(viewModel){
            favoritesLiveData.observe(viewLifecycleOwner){ state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        binding.favoriteCoinsLoading.visibility = GONE
                        binding.favoritesRv.adapter = CoinListAdapter(state.data!!) {
                            val bundle = bundleOf(Constants.COIN_ID to it.id)
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.action_favoritesFragment_to_coinDetailFragment,bundle)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(context, state.message ?: "There is a problem to find coin!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }

    }
}