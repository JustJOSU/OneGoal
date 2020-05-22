package com.hour.onegoal

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.*
import com.hour.onegoal.Util.loadImage
import com.hour.onegoal.Util.toast
import kotlinx.android.synthetic.main.activity_work_out_room.*

class WorkOutRoomActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val REQUEST_IMAGE_CAPTURE = 100
    private val OPEN_GALLERY = 1
    private var filePath: Uri? = null
    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_out_room)

        database = FirebaseDatabase.getInstance().reference

        val roomId = intent.getStringExtra("roomId")
        val roomTitle = intent.getStringExtra("title")
        val roomDescription = intent.getStringExtra("description")
        val roomPhotoUrl = intent.getStringExtra("photoUrl")
        val roomSummary = intent.getStringExtra("summary")
        val roomTeamHead = intent.getStringExtra("teamHead")
        FirebaseDatabase.getInstance().getReference("/workOutRooms")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val workRoomPhoto = findViewById<ImageView>(R.id.roomPhoto)
                    val workRoomTitle = findViewById<TextView>(R.id.roomTitle)
                    val workRoomDescription = findViewById<TextView>(R.id.roomDescription)
                    val workRoomSummary = findViewById<TextView>(R.id.roomSummary)
                    val workRoomTeamHead = findViewById<TextView>(R.id.roomTeamHead)

                    toast("This is summary : $roomSummary")
                    workRoomTitle.text = roomTitle
                    workRoomDescription.text = roomDescription
                    workRoomSummary.text = roomSummary
                    workRoomPhoto.loadImage(roomPhotoUrl)
                    workRoomTeamHead.text = " : $roomTeamHead"
                }

            })
        //TODO 방 삭제 똑바로
        deleteButton.setOnClickListener {
            delete(roomId)
        }

    }

    private fun delete(roomId: String) {

        val roomTable: DatabaseReference = FirebaseDatabase.getInstance().getReference("/workOutRooms")
        roomTable.removeValue()
        val roomTable1 : DatabaseReference = FirebaseDatabase.getInstance().getReference("/user-workOutRooms")
        roomTable1.removeValue()
        val intent = Intent(this,WorkoutActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    // TAG WorkOutRoomActivity
    companion object{
        val TAG = WorkOutRoomActivity::class.qualifiedName
    }

}
