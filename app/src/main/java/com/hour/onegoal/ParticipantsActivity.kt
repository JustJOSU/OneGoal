package com.hour.onegoal

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hour.onegoal.Data.User
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_participants.*
import org.w3c.dom.Text
/**
 *  참가자
 * **/
class ParticipantsActivity : AppCompatActivity() {

    private lateinit var roomId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participants)

        // 방 정보
        roomId = intent.getStringExtra("roomId")
        val firebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser = firebaseAuth.currentUser!!
        val uid = user.uid
        // 파이어베이스 방 Id
        FirebaseDatabase.getInstance().reference.child("/workOutRooms/$roomId").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                // 방장 이름 값
                //TODO: 방 DB 구조 다시 생각
                val teamHead = p0.child("teamHead").value
                findViewById<TextView>(R.id.teamHeadName).text = teamHead.toString()
                val teamPhotoUrl = p0.child("/members/$uid/photoUrl").value
                Glide.with(this@ParticipantsActivity)
                    .load(teamPhotoUrl)
                    .into(teamHeadProfile)
                Log.d("p0","p0 : ${teamPhotoUrl}")
            }
        })
    }
}
