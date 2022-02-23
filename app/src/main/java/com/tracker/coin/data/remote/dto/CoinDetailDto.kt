package com.tracker.coin.data.remote.dto

data class CoinDetailDto(
    val hashing_algorithm: String,
    val description: DescriptionModel,
    val image: ImageModel,
    val market_data: MarketDataModel,
    val price_change_percentage_24hr: Double)
