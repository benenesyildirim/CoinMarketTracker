package com.tracker.coin.domain.use_case.get_coins

import com.tracker.coin.domain.repository.CoinRepository
import javax.inject.Inject

class GetCoinsUseCase @Inject constructor(private val repository: CoinRepository) {
    suspend fun getCoins() = repository.getCoins()
}