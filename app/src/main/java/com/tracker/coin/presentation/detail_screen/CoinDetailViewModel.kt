package com.tracker.coin.presentation.detail_screen

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tracker.coin.common.Constants.COIN_ID
import com.tracker.coin.common.Constants.USER_ID
import com.tracker.coin.common.Resource
import com.tracker.coin.data.remote.dto.CoinDetailDto
import com.tracker.coin.data.remote.dto.CoinDto
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
        private val firestore: FirebaseFirestore,
        private val sharedPreferences: SharedPreferences,
        savedStateHandle: SavedStateHandle) : ViewModel() {

    var userId = sharedPreferences.getString(USER_ID, "")

    init {
        savedStateHandle.get<String>(COIN_ID)?.let { coinId ->
            getCoinDetail(coinId)
        }
    }

    private val _coinLiveData = MutableLiveData<Resource<CoinDetailDto>>()
    val coinLiveData: LiveData<Resource<CoinDetailDto>> get() = _coinLiveData

    fun getCoinDetail(coinId: String) = viewModelScope.launch {
        try {
            getCoinUseCase.getCoinDetail(coinId).let {
                if (it.isSuccessful) _coinLiveData.postValue(Resource.Success(it.body()!!))
                else _coinLiveData.postValue(Resource.Error(it.errorBody().toString()))
            }
        } catch (e: HttpException) {
            _coinLiveData.postValue(Resource.Error(e.localizedMessage
                    ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            _coinLiveData.postValue(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    private val _registerLiveData = MutableLiveData<Resource<FirebaseUser>>()
    val registerLiveData: LiveData<Resource<FirebaseUser>> get() = _registerLiveData

    fun registerUser(email: String, password: String) {
        _registerLiveData.postValue(Resource.Loading())

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { registerResult ->
            if (registerResult.isSuccessful) {
                _registerLiveData.postValue(Resource.Success(firebaseAuth.currentUser!!))
                userId = firebaseAuth.currentUser!!.uid
                setUserIdToSp()
            } else {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { loginResult ->
                    if (loginResult.isSuccessful) {
                        _registerLiveData.postValue(Resource.Success(firebaseAuth.currentUser!!))
                        userId = firebaseAuth.currentUser!!.uid
                        setUserIdToSp()
                    } else {
                        _registerLiveData.postValue(Resource.Error(loginResult.exception?.localizedMessage
                                ?: "Can not sign in."))
                    }
                }
            }
        }
    }

    fun addToFavorite(coin: CoinDto): Boolean {
        if (isUserLoggedIn()) {
            if (userId!!.isNotEmpty()) {
                firestore.collection(userId!!).document(coin.name).set(coin)
                return true
            }
        }

        return false
    }

    private fun isUserLoggedIn(): Boolean {
        return !userId.isNullOrEmpty()
    }

    private fun setUserIdToSp() {
        sharedPreferences.edit().putString(USER_ID, userId).apply()
    }
}