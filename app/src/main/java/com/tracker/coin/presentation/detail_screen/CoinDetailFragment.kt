package com.tracker.coin.presentation.detail_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tracker.coin.common.Constants.COIN_ID
import com.tracker.coin.common.Resource
import com.tracker.coin.databinding.FragmentCoinDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoinDetailFragment : Fragment() {
    private lateinit var binding: FragmentCoinDetailBinding
    private val viewModel: CoinDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           arguments?.getString(COIN_ID)!!
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCoinDetailBinding.inflate(layoutInflater)

        observeCoinDetail()

        return binding.root
    }

    private fun observeCoinDetail() {
        with(viewModel) {
            coinLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        binding.coin = state.data!!
                    }
                    is Resource.Error -> {

                    }
                }
            }
        }
    }
}