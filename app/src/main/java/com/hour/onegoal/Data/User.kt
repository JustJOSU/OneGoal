package com.hour.onegoal.Data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(var uid: String="",
                var birth:String="",
                var gender:String="",
                var username:String?=null,
                var photoUrl:String=""
                ){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            //"enterId" to enterId,
            "uid" to uid,
            "birth" to birth,
            "gender" to gender,
            "username" to username,
            "photoUrl" to photoUrl
        )
    }
}
class GetUser(var birth: String="", var gender: String="", var uid: String="", var username: Any?=null)