package com.hour.onegoal.Data

import com.google.firebase.database.Exclude

data class WorkoutRoom(
    var roomId: String? = "",
    var teamHead: String?= "",
    var title: String? = "",
    var summary:String?="",
    var description: String?="",
    var photoUrl: String?= null
){
    // [START post_to_map]
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "roomId" to roomId,
            "teamHead" to teamHead,
            "title" to title,
            "summary" to summary,
            "description" to description,
            "photoUrl" to photoUrl
        )
    }
    // [END post_to_map]
}
