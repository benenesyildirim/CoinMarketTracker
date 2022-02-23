package com.tracker.coin.data.repository

import com.tracker.coin.data.remote.CoinApi
import com.tracker.coin.domain.repository.CoinRepository
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(private val api: CoinApi) : CoinRepository {
    override suspend fun searchCoin(query: String) = api.searchCoin(query)

    override suspend fun getCoinDetail(coinId: String) = api.getCoinDetail(coinId)
}