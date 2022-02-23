package com.tracker.coin.data.remote

import com.tracker.coin.data.remote.dto.CoinDetailDto
import com.tracker.coin.data.remote.dto.CoinDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CoinApi {
    @GET("/search")
    suspend fun searchCoin(@Query("query") query: String): Response<MutableList<CoinDto>>

    @GET("/coins")
    suspend fun getCoinDetail(coinId: String): Response<CoinDetailDto>
}