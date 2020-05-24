package com.hour.onegoal.Data

import com.google.firebase.database.Exclude

data class EnterRoom(
    var enterId: String? = "",
    var roomId: String?= "",
    var userId: String?= "",
    var username: String? = "",
    var photoUrl: String?=""
){
    // [START post_to_map]
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "enterId" to enterId,
            "userId" to userId,
            "username" to username,
            "roomId" to roomId,
            "photoUrl" to photoUrl
        )
    }
    // [END post_to_map]
}
