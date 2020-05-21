package com.hour.onegoal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.hour.onegoal.Data.WorkoutRoom
import kotlinx.android.synthetic.main.activity_workout.*


class WorkoutActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // 뒤로가기
        fab_backBtn.setOnClickListener {
            onBackPressed()
        }

        // 방 생성
        fab_addBtn.setOnClickListener {
            val intent = Intent(this,NewPostActivity::class.java)
            startActivity(intent)
        }


        // 레퍼런스
        var ref: DatabaseReference?= null



        ref = FirebaseDatabase.getInstance().getReference("workOutRooms")

        // 글 목록
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var roomList: ArrayList<WorkoutRoom>?= null
                    roomList = ArrayList<WorkoutRoom>()
                    for ( h in p0.children){
                        val room = h.getValue(WorkoutRoom::class.java)
                        roomList.add(room!!)
                        workOut_recyclerview?.adapter = WorkOutAdapter(applicationContext,roomList){

                        }
                        workOut_recyclerview?.layoutManager = LinearLayoutManager(applicationContext)
                        workOut_recyclerview.setHasFixedSize(true)
                        //TODO: 버벅임
                        //TODO: Scrool To End
                        workOut_recyclerview.post {
                            workOut_recyclerview.smoothScrollToPosition(1)

                        }


                    }
                }
            }
        })


    }


}
