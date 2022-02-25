package com.tracker.coin.presentation.detail_screen

import android.os.Bundle
import android.util.Patterns
import android.view.Gravity.BOTTOM
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.tracker.coin.R
import com.tracker.coin.common.Constants.COIN_ID
import com.tracker.coin.common.Resource
import com.tracker.coin.data.remote.dto.CoinDto
import com.tracker.coin.databinding.FragmentCoinDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class CoinDetailFragment : Fragment() {
    private lateinit var binding: FragmentCoinDetailBinding
    private val viewModel: CoinDetailViewModel by viewModels()

    private var refreshInterval: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            arguments?.getString(COIN_ID)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCoinDetailBinding.inflate(layoutInflater)

        listenCoinData()
        observeCoinDetail()
        observeRegister()

        binding.refreshIntervalEt.addTextChangedListener {
            if (it.toString().isNotEmpty())
                refreshInterval = it.toString().toLong() * 1000
        }

        binding.addToFavBtn.setOnClickListener {
            addToFavorites()
        }

        return binding.root
    }

    private fun listenCoinData() {
        lifecycleScope.launch {
            while (true) {
                delay(refreshInterval)

                viewModel.getCoinDetail(binding.coin?.id!!)
            }
        }
    }

    private fun addToFavorites() {
        if (viewModel.addToFavorite(CoinDto(binding.coin!!.id, binding.coin!!.name, binding.coin!!.symbol))) {

            navigateToFavorites()
        } else {
            showEmailDialog()
        }
    }

    private fun navigateToFavorites() {
        Navigation.findNavController(binding.root)
                .navigate(R.id.action_coinDetailFragment_to_favoritesFragment)
    }

    private fun observeCoinDetail() {
        with(viewModel) {
            coinLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        binding.coin = state.data!!
                        binding.coinDetailLoading.visibility = GONE
                    }
                    is Resource.Error -> {
                        Toast.makeText(context, state.message
                                ?: "There is a problem to find coin!", Toast.LENGTH_SHORT)
                                .show()
                    }
                }
            }
        }
    }

    private fun observeRegister() {
        with(viewModel) {
            registerLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.coinDetailLoading.visibility = VISIBLE
                    }
                    is Resource.Success -> {
                        binding.coinDetailLoading.visibility = GONE

                        viewModel.addToFavorite(CoinDto(binding.coin!!.id, binding.coin!!.name, binding.coin!!.symbol))
                        navigateToFavorites()
                    }
                    is Resource.Error -> {
                        binding.coinDetailLoading.visibility = GONE

                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showEmailDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.dialog_email_design, null)
        val okBtn = view.findViewById<Button>(R.id.ok_btn)
        val emailEt = view.findViewById<EditText>(R.id.email_et)
        val passwordEt = view.findViewById<EditText>(R.id.password_et)

        okBtn.setOnClickListener {
            val email = emailEt.text.toString()
            val password = passwordEt.text.toString()
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 5) {
                viewModel.registerUser(email, password)
                builder.dismiss()
            } else {
                Toast.makeText(context, "Please check your credentials.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setView(view)

        builder.setCanceledOnTouchOutside(false)
        builder.window!!.attributes.gravity = BOTTOM
        builder.window!!.attributes.verticalMargin = 0.01f
        builder.show()
    }
}