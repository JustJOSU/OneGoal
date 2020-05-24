package com.hour.onegoal.Util

import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hour.onegoal.R

fun ImageView.loadImage(uri:String?){
    val options = RequestOptions()
        .placeholder(R.drawable.user)
        .error(R.mipmap.ic_launcher_round)

    if(uri != null){
        Glide.with(this.context)
            .setDefaultRequestOptions(options)
            .load(uri)
            .into(this)
    } else {
        //TODO: error 처리
        Glide.with(this.context)
            .load(R.drawable.account)
            .into(this)
    }
}