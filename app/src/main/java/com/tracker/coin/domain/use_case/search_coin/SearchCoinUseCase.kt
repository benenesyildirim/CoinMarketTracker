package com.tracker.coin.domain.use_case.search_coin

import com.tracker.coin.domain.repository.CoinRepository
import javax.inject.Inject

class SearchCoinUseCase @Inject constructor(private val repository: CoinRepository) {
    suspend fun searchCoin(query: String) = repository.searchCoin(query)
}