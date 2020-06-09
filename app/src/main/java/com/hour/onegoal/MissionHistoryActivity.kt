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

                Log.d("date11","${p0.getValue()}")
                for ( h in p0.children){
                    val todayMissions = h.getValue(TodayMission::class.java)
                    todayMissionList.add(todayMissions!!)
                    Log.d("members","${h.getValue()}")

                    val photoUrlList : ArrayList<String> = ArrayList()
                    val child_test = h.child("members").children

                    //TODO: 조성재 이거좀 해줘 !
                    for(l in child_test){
                        photoUrlList.add(l.value.toString())
                        Log.d("이근희",l.value.toString())
                        Log.d("값 불러와짐", "${h.child("members").child("test").getValue(String::class.java)}")
                    }
                    missionHistory_rv?.adapter = TodayMissionHistoryAdapter(applicationContext,todayMissionList,photoUrlList){

                    }
                    missionHistory_rv?.layoutManager = GridLayoutManager(applicationContext,1)
                    missionHistory_rv.setHasFixedSize(true)
                    missionHistory_rv.post {
                        missionHistory_rv.smoothScrollToPosition(1)
                    }
                }
            }
        })
    }

}
