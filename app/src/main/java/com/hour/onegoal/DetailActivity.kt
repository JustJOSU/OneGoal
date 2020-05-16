package com.hour.onegoal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hour.onegoal.Data.WorkoutRoom
import com.hour.onegoal.Util.loadImage
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val roomId = intent.getStringExtra("roomId")
        val roomTitle = intent.getStringExtra("title")
        val roomDescription = intent.getStringExtra("description")
        val roomPhotoUrl = intent.getStringExtra("photoUrl")
        val roomSummary = intent.getStringExtra("summary")
        FirebaseDatabase.getInstance().getReference("/workOutRooms")
            .addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val detailPhoto = findViewById<ImageView>(R.id.detailPhoto)
                    val detailTitle = findViewById<TextView>(R.id.detailTitle)
                    val detailDescription = findViewById<TextView>(R.id.detailDescription)
                    val detailSummary = findViewById<TextView>(R.id.detailSummary)

                    toast("This is summary : $roomSummary")
                    detailTitle.text = roomTitle
                    detailDescription.text = roomDescription
                    detailSummary.text = roomSummary
                    detailPhoto.loadImage(roomPhotoUrl)


                }

            })
    }
}
