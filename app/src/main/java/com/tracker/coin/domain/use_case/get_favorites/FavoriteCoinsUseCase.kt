package com.tracker.coin.domain.use_case.get_favorites

import com.google.firebase.firestore.FirebaseFirestore
import com.tracker.coin.domain.repository.CoinRepository
import javax.inject.Inject

class FavoriteCoinsUseCase @Inject constructor(private val firestore: FirebaseFirestore) {
    fun getFavorites(userId: String) = firestore.collection(userId).get()
}