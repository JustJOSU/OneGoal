package com.hour.onegoal

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        var ref: DatabaseReference?= null

        var mRecyclerView:RecyclerView?= null

        mRecyclerView = findViewById(R.id.workOut_recyclerview)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView?.layoutManager = GridLayoutManager(applicationContext,2)


        ref = FirebaseDatabase.getInstance().getReference("workOutRooms")

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
                    }
                    mRecyclerView?.adapter = WorkOutAdapter(applicationContext,roomList){
                    }

                }
            }

        })
    }


}
