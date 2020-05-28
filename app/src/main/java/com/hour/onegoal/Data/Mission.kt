package com.hour.onegoal.Data

import com.google.firebase.database.Exclude

data class Mission(
    var missionId: String?= "",
    var missionWriteTime:Any = Any(),
    var missionPhotoUrl:String?= "",
    var missionTitle:String? =""
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            //"enterId" to enterId,
            "missionId" to missionId,
            "missionWriteTime" to missionWriteTime,
            "missionPhotoUrl" to missionPhotoUrl,
            "missionTitle" to missionTitle
        )
    }
}
