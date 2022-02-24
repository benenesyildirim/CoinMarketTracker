package com.tracker.coin.presentation.search_screen

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.coin.common.Constants
import com.tracker.coin.common.Resource
import com.tracker.coin.data.remote.dto.CoinDto
import com.tracker.coin.domain.use_case.get_coins.GetCoinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getCoinsUseCase: GetCoinsUseCase,
    private val sharedPreferences: SharedPreferences) : ViewModel() {

    private var coinsList: List<CoinDto> = listOf()

    init {
        getCoins()
    }

    private val _coinsLiveData = MutableLiveData<Resource<List<CoinDto>>>()
    val coinsLiveData: LiveData<Resource<List<CoinDto>>> get() = _coinsLiveData

    private fun getCoins() = viewModelScope.launch {

        try {
            getCoinsUseCase.getCoins().let {
                if (it.isSuccessful) coinsList = it.body()!!
                else _coinsLiveData.postValue(Resource.Error(it.errorBody().toString()))
            }
        } catch (e: HttpException) {
            _coinsLiveData.postValue(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            _coinsLiveData.postValue(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    fun searchCoin(value: String){
        val coinsResultList: MutableList<CoinDto> = mutableListOf()
        _coinsLiveData.postValue(Resource.Loading())

        if (coinsList.isNotEmpty()){
            for (coin in coinsList){
                if (coin.name.toLowerCase().contains(value.toLowerCase())
                    || coin.symbol.toLowerCase().contains(value.toLowerCase())){
                    coinsResultList.add(coin)
                }
            }
        }

        if(coinsResultList.isNotEmpty())
            _coinsLiveData.postValue(Resource.Success(coinsResultList))
        else
            _coinsLiveData.postValue(Resource.Error("Coin can not found."))
    }

    fun isThereAnyUser(): Boolean {
        return sharedPreferences.getString(Constants.USER_ID, "") != ""
    }
}