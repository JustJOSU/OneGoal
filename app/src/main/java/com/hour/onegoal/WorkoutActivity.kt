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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.hour.onegoal.Data.WorkoutRoom
import kotlinx.android.synthetic.main.activity_workout.*

class WorkoutActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // 액션바
        setSupportActionBar(findViewById(R.id.toolbar))
        toolbar.setTitleTextColor(Color.WHITE)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
        toolbar.elevation = 3.0F

        val firedatabase:FirebaseDatabase?= null
        var ref: DatabaseReference?= null
        var roomList: ArrayList<WorkoutRoom>?= null
        var mRecyclerView:RecyclerView?= null

        mRecyclerView = findViewById(R.id.workOut_recyclerview)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView?.layoutManager = LinearLayoutManager(applicationContext)

        roomList = ArrayList<WorkoutRoom>()
        ref = FirebaseDatabase.getInstance().getReference("workOutRooms")

        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
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

    //setting menu in action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.navigation_home -> {

                return true
            }
            R.id.navigation_dashboard -> {

                return true
            }
            R.id.navigation_notifications -> {

                return true
            }
            R.id.navigation_make-> {
                val intent = Intent(this,NewPostActivity::class.java)
                startActivity(intent)
                return true
            }

        }


        return super.onOptionsItemSelected(item)
    }
}
