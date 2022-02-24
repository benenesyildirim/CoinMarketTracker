package com.tracker.coin.presentation.detail_screen

import android.os.Bundle
import android.util.Patterns
import android.view.Gravity.BOTTOM
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tracker.coin.R
import com.tracker.coin.common.Constants.COIN_ID
import com.tracker.coin.common.Resource
import com.tracker.coin.data.remote.dto.CoinDto
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
        observeRegister()

        binding.addToFavBtn.setOnClickListener {
            if (viewModel.addToFavorite(CoinDto(binding.coin!!.id,binding.coin!!.name,binding.coin!!.symbol))){
                Toast.makeText(context, "Added to Favorites.", Toast.LENGTH_SHORT).show()
            }else{
                showEmailDialog()
            }
        }

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
                        Toast.makeText(context, state.message ?: "There is a problem to find coin!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun observeRegister(){
        with(viewModel) {
            registerLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Toast.makeText(context, "Logged In.", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
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
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 5){
                viewModel.registerUser(email,password)
                builder.dismiss()
            }else{
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