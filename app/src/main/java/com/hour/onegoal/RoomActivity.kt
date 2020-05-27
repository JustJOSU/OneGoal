package com.hour.onegoal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_room.*
import soup.neumorphism.NeumorphTextView

class RoomActivity : AppCompatActivity() {

    private lateinit var roomTitle : String
    private lateinit var roomId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        roomTitle = intent.getStringExtra("title")
        roomId = intent.getStringExtra("roomId")
        findViewById<NeumorphTextView>(R.id.valid_room_title).text = roomTitle

        enterAccount_cardView.setOnClickListener {
            val intent = Intent(this,ParticipantsActivity::class.java)
            intent.putExtra("roomId",roomId)
            startActivity(intent)
        }
    }
}
