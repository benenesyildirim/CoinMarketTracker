package com.tracker.coin.presentation.search_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.tracker.coin.R
import com.tracker.coin.common.Constants.COIN_ID
import com.tracker.coin.common.Resource
import com.tracker.coin.databinding.FragmentSearchBinding
import com.tracker.coin.presentation.CoinListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        binding.searchCoinEt.addTextChangedListener {
            if (it.toString().length >= 3){
                lifecycleScope.launch {
                    viewModel.searchCoin(it.toString())
                }
            }
        }

        observeResult()

        return binding.root
    }

    private fun observeResult() {
        with(viewModel) {
            coinsLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is Resource.Loading -> {
                        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success -> {
                        binding.coinsListRv.adapter = CoinListAdapter(state.data!!) {
                            val bundle = bundleOf(COIN_ID to it.id)
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.action_searchFragment_to_coinDetailFragment,bundle)
                        }
                    }
                    is Resource.Error -> {
                        binding.coinsListRv.adapter = CoinListAdapter(listOf()) {}
                        Toast.makeText(context, state.message ?: "There is a problem to find coin!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}