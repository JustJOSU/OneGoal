package com.hour.onegoal.Data

import com.google.firebase.database.Exclude
import java.text.SimpleDateFormat
import java.util.*

data class TodayMission(
    var todaymissionId: String?= "",
    var todaymissionTitle:String?= "",
    var todaymissionDescription:String? ="",
    var todaymissionDate:String = ""

){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            //"enterId" to enterId,
            "todaymissionId" to todaymissionId,
            "todaymissionTitle" to todaymissionTitle,
            "todaymissionDescription" to todaymissionDescription,
            "todaymissionDate" to todaymissionDate
        )
    }
    companion object {
        const val DATE_TYPE = 0
        const val IMAGE_TYPE = 1
        const val IMAGE_TYPE_2 = 2
    }
}

