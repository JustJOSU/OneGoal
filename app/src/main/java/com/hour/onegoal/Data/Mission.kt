package com.hour.onegoal.Data

import com.google.firebase.database.Exclude

data class Mission(
    var missionId: String?= "",
    var missionWriteTime:String = "",
    var missionPhotoUrl:String?= "",
    var missionUser:String? ="",
    var missionUserPhotoUrl: String? = ""
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            //"enterId" to enterId,
            "missionId" to missionId,
            "missionWriteTime" to missionWriteTime,
            "missionPhotoUrl" to missionPhotoUrl,
            "missionUser" to missionUser,
            "missionUserPhotoUrl" to missionUserPhotoUrl
        )
    }
    companion object{
        const val Mission = 0
    }
}
