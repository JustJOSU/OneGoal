package com.hour.onegoal.Util

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hour.onegoal.R

fun ImageView.loadImage(uri:String?){
    val options = RequestOptions()
        .placeholder(R.drawable.user)
        .circleCrop()
        .error(R.drawable.account)


    if(uri != null){
        Glide.with(this.context)
            .setDefaultRequestOptions(options)
            .load(uri)
            .circleCrop()
            .into(this)
    } else {
        //TODO: error 처리
        Glide.with(this.context)
            .load(R.drawable.account)
            .circleCrop()
            .into(this)
    }
}