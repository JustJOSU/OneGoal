package com.hour.onegoal.Data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val uid: String,  var birth:String, val gender:String,var username:String?=null)