package com.tracker.coin.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

class Utils {
    companion object{
        @BindingAdapter("loadImage")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(imageView.context)
                    .load(imageUrl)
                    .into(imageView)
            }
        }
    }
}