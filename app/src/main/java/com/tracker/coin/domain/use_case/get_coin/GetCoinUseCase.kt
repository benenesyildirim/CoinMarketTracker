package com.tracker.coin.domain.use_case.get_coin

import com.tracker.coin.domain.repository.CoinRepository
import javax.inject.Inject

class GetCoinUseCase @Inject constructor(private val repository: CoinRepository) {
    suspend fun getCoinDetail(coinId: String) = repository.getCoinDetail(coinId)
}