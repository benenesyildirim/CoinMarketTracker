package com.tracker.coin.presentation.favorites_screen

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.tracker.coin.common.Constants.USER_ID
import com.tracker.coin.common.Resource
import com.tracker.coin.data.remote.dto.CoinDto
import com.tracker.coin.domain.use_case.get_favorites.FavoriteCoinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteCoinsUseCase: FavoriteCoinsUseCase,
    sharedPreferences: SharedPreferences) : ViewModel() {

    init {
        val userId = sharedPreferences.getString(USER_ID, "")!!
        getCoins(userId)
    }

    private val _favoritesLiveData = MutableLiveData<Resource<List<CoinDto>>>()
    val favoritesLiveData: LiveData<Resource<List<CoinDto>>> get() = _favoritesLiveData

    private fun getCoins(userId: String) = viewModelScope.launch {
        try {
            favoriteCoinsUseCase.getFavorites(userId).addOnCompleteListener {
                if (it.isSuccessful) {
                    val coinList: MutableList<CoinDto> = mutableListOf()

                    val documentList = it.result.documents

                    for (coin in documentList) {
                        coinList.add(CoinDto(coin.get("id").toString(), coin.get("name")
                            .toString(), coin.get("symbols").toString()))
                    }

                    _favoritesLiveData.postValue(Resource.Success(coinList))
                } else {
                    _favoritesLiveData.postValue(Resource.Error(it.exception!!.localizedMessage ?: "An unexpected error occurred"))
                }
            }
        } catch (e: HttpException) {
            _favoritesLiveData.postValue(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            _favoritesLiveData.postValue(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}