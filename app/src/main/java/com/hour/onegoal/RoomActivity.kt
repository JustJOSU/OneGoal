package com.hour.onegoal

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
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

        submit_cardView.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        val body = dialog.findViewById(R.id.dialog_missionTitle) as TextView
        body.text = title
        val photo = dialog.findViewById(R.id.dialog_imageView) as ImageView
        Glide.with(this)
            .load(photo)
            .into(photo)
        val yesBtn = dialog.findViewById(R.id.okButton) as Button
        val noBtn = dialog.findViewById(R.id.cancelButton) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }
}
