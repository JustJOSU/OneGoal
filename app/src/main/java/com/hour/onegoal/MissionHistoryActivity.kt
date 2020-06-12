package com.hour.onegoal

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hour.onegoal.Data.TodayMission
import com.hour.onegoal.Util.toast
import kotlinx.android.synthetic.main.activity_mission_history.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType


class MissionHistoryActivity : AppCompatActivity() {

    private lateinit var roomId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission_history)

        // 미션히스토리 정보

        roomId = intent.getStringExtra("roomId")
        val firebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser = firebaseAuth.currentUser!!
        val uid = user.uid
        // 파이어베이스 방 Id

        FirebaseDatabase.getInstance().reference.child("/workOutRooms/$roomId/MissionHistory").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {

                var todayMissionList: ArrayList<TodayMission>?= null
                todayMissionList = ArrayList()

                val photoUrlList : ArrayList<ArrayList<String>> = ArrayList()

                Log.d("date11","${p0.ref}")
                for ( h in p0.children) {
                    val todayMissions = h.getValue(TodayMission::class.java)
                    todayMissionList.add(todayMissions!!)
                    Log.d("members", "${h.getValue()}")
                    val child_test = h.child("members").children

                    val result: ArrayList<String> = ArrayList()
                    child_test.forEach {
                        result.add(it.value.toString())
                        print("foreach")
                        println(it.value)
                    }
                    photoUrlList.add(result)
                }
                photoUrlList.forEach{
                    Log.d("MissionActivity : ", "$it")
                }
                missionHistory_rv?.adapter = TodayMissionHistoryAdapter(applicationContext,todayMissionList,photoUrlList){

                }
                missionHistory_rv?.layoutManager = GridLayoutManager(applicationContext,1)
                missionHistory_rv.setHasFixedSize(true)
                missionHistory_rv.post {
                    missionHistory_rv.smoothScrollToPosition(1)
                }
            }
        })
    }
}
