package com.hour.onegoal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hour.onegoal.Data.Mission
import com.hour.onegoal.Data.User
import kotlinx.android.synthetic.main.activity_mission_history.*
import kotlinx.android.synthetic.main.activity_participants.*
import java.lang.StringBuilder

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

        FirebaseDatabase.getInstance().reference.child("/workOutRooms/$roomId/TodayMission").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {

                // 팀원일 경우 멤버 텍스트뷰 밑에 배치

                var missionList: ArrayList<Mission>?= null
                missionList = ArrayList()

                for ( h in p0.children){
                    val missions = h.getValue(Mission::class.java)
                    missionList.add(missions!!)
                    missionHistory_rv?.adapter = MissionHistoryAdapter(applicationContext,missionList){

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
