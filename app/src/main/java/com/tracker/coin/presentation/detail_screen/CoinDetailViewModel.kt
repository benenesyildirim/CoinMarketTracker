package com.tracker.coin.presentation.detail_screen

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.tracker.coin.common.Constants.COIN_ID
import com.tracker.coin.common.Resource
import com.tracker.coin.data.remote.dto.CoinDetailDto
import com.tracker.coin.domain.use_case.get_coin.GetCoinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val getCoinUseCase: GetCoinUseCase,
    private val firebaseAuth: FirebaseAuth,
    savedStateHandle: SavedStateHandle) : ViewModel() {

    init {
        savedStateHandle.get<String>(COIN_ID)?.let { coinId ->
            getCoinDetail(coinId)
        }
        firebaseAuth.createUserWithEmailAndPassword("asirigereksiz01@gmail.com","ARAMAM").addOnCompleteListener {
            Log.d("AUTH -|-|-|-|-|-|-|",it.isSuccessful.toString())
        }
    }

    private val _coinLiveData = MutableLiveData<Resource<CoinDetailDto>>()
    val coinLiveData: LiveData<Resource<CoinDetailDto>> get() = _coinLiveData

    private fun getCoinDetail(coinId: String) = viewModelScope.launch {
        try {
            getCoinUseCase.getCoinDetail(coinId).let {
                if (it.isSuccessful)
                    _coinLiveData.postValue(Resource.Success(it.body()!!))
                else
                    _coinLiveData.postValue(Resource.Error(it.errorBody().toString()))
            }
        } catch (e: HttpException) {
            _coinLiveData.postValue(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            _coinLiveData.postValue(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}