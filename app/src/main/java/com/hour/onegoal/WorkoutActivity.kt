package com.hour.onegoal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hour.onegoal.Data.WorkoutRoom
import com.hour.onegoal.Login.ProfileActivity
import com.hour.onegoal.Util.toast
import kotlinx.android.synthetic.main.activity_workout.*


class WorkoutActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        database = FirebaseDatabase.getInstance().reference

        // 뒤로가기
        fab_backBtn.setOnClickListener {
            onBackPressed()
        }

        // 방 생성
        fab_addBtn.setOnClickListener {
            validUser()
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

                        workOut_recyclerview.post {
                            workOut_recyclerview.smoothScrollToPosition(1)

                        }


                    }
                }
            }
        })
    }

    companion object{
        val TAG = WorkoutActivity::class.qualifiedName
    }

    private fun validUser(){
        // [START single_value_read]
        val firebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser = firebaseAuth.currentUser!!
        val userId = user.uid

        database.child("users").child(userId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.child("username").value

                    if(username == null){
                        val profileIntent = Intent(this@WorkoutActivity, ProfileActivity::class.java).apply {
                            toast("프로필을 완성시켜야만 방을 만들 수가 있습니다!!")
                        }
                        startActivity(profileIntent)
                        finish()
                    } else{
                        val intent = Intent(this@WorkoutActivity,NewPostActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                    // [END_EXCLUDE]
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException())
                }
            })
        // [END single_value_read]
    }

}
