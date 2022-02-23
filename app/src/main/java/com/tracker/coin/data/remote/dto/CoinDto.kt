package com.tracker.coin.data.remote.dto

data class CoinDto(
    val id: String,
    val name: String,
    val symbol: String,
    val market_cap_rank: Int,
    val thumb: String,
    val large: String)
