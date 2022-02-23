package com.tracker.coin.domain.repository

import com.tracker.coin.data.remote.dto.CoinDetailDto
import com.tracker.coin.data.remote.dto.CoinListResponse
import retrofit2.Response

interface CoinRepository {
    suspend fun searchCoin(query: String):  Response<CoinListResponse>

    suspend fun getCoinDetail(coinId: String): Response<CoinDetailDto>
}