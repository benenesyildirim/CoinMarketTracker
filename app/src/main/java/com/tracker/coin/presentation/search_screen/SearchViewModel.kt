package com.tracker.coin.presentation.search_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.coin.common.Resource
import com.tracker.coin.data.remote.dto.CoinDto
import com.tracker.coin.data.remote.dto.CoinListResponse
import com.tracker.coin.domain.use_case.search_coin.SearchCoinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchCoinUseCase: SearchCoinUseCase) : ViewModel() {

    private val _coinsLiveData = MutableLiveData<Resource<CoinListResponse>>()
    val coinsLiveData: LiveData<Resource<CoinListResponse>> get() = _coinsLiveData

    fun searchCoin(query: String) = viewModelScope.launch {
        try {
            searchCoinUseCase.searchCoin(query).let {
                if (it.isSuccessful)
                    _coinsLiveData.postValue(Resource.Success(it.body()!!))
                else
                    _coinsLiveData.postValue(Resource.Error(it.errorBody().toString()))
            }
        } catch (e: HttpException) {
            _coinsLiveData.postValue(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            _coinsLiveData.postValue(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}