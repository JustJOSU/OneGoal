    package com.hour.onegoal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Tag
import com.hour.onegoal.Data.WorkoutRoom
import kotlinx.android.synthetic.main.activity_new_post.*

    class NewPostActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        database = FirebaseDatabase.getInstance().reference

        fabSubmitPost.setOnClickListener { submitRoom()}
    }

        private fun submitRoom() {
            //TODO: photourl 넘기는 거랑 현재 user 의 name 도 넘겨줘야하는 것 고민
            val title = fieldTitle.text.toString()
            val summary = fieldSummary.text.toString()
            val discription = fieldDiscription.text.toString()
            // Title is required
            if (title.isEmpty()) {
                fieldTitle.error = "제목을 입력해주세요"
                fieldTitle.requestFocus()
                return
            }
            if (summary.isEmpty()) {
                fieldSummary.error = "요약을 입력해주세요"
                fieldSummary.requestFocus()
                return
            }
            if (discription.isEmpty()) {
                fieldDiscription.error = "상세설명을 입력해주세요"
                fieldDiscription.requestFocus()
                return
            }

        }

        private fun writeNewWorkOutRoom(userId:String, username:String, title:String,
                                 summary:String, discription:String, photoUrl:String){
            val key = database.child("rooms").push().key
            if(key == null){
                Log.w(TAG, "Couldn't get push key for rooms")
                return
            }
            val workOutroom = WorkoutRoom(userId)
        }

        companion object{
            val TAG = NewPostActivity::class.qualifiedName
        }
    }
