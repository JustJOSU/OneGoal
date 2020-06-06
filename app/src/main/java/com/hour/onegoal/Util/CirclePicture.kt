package com.hour.onegoal.Util

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hour.onegoal.R
import de.hdodenhof.circleimageview.CircleImageView

fun CircleImageView.loadImage(uri:String?){
    val options = RequestOptions()
        .placeholder(R.drawable.user)
        .error(R.color.colorPrimary)

    if(uri != null){
        Glide.with(context)
            .setDefaultRequestOptions(options)
            .load(uri)
            .into(this)
    } else {
        //TODO: error 처리
        Glide.with(context)
            .load(R.drawable.account)
            .into(this)
    }
}