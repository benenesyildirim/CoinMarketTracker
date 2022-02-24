package com.tracker.coin.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracker.coin.data.remote.dto.CoinDto
import com.tracker.coin.databinding.CoinRowDesignBinding

class CoinListAdapter (private val coins: MutableList<CoinDto>, val listener: (CoinDto) -> Unit
) : RecyclerView.Adapter<CoinListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CoinRowDesignBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(coins[position])

    override fun getItemCount(): Int = coins.size

    inner class ViewHolder(private val binding: CoinRowDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coin: CoinDto) {
            binding.apply {
                this.coin = coin
                executePendingBindings()
                root.setOnClickListener { listener(coin) }
            }
        }
    }
}