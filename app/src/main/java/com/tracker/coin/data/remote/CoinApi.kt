package com.tracker.coin.data.remote

import com.tracker.coin.data.remote.dto.CoinDetailDto
import com.tracker.coin.data.remote.dto.CoinListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinApi {
    @GET("api/v3/search")
    suspend fun searchCoin(@Query("query") query: String): Response<CoinListResponse>

    @GET("api/v3/coins/{coinId}")
    suspend fun getCoinDetail(@Path("coinId") coinId: String): Response<CoinDetailDto>
}