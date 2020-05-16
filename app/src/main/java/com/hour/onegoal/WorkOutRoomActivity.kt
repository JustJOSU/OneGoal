package com.hour.onegoal

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.*
import com.hour.onegoal.Data.WorkoutRoom
import com.hour.onegoal.Util.loadImage

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
    }




    // TAG WorkOutRoomActivity
    companion object{
        val TAG = WorkOutRoomActivity::class.qualifiedName
    }
    // [START write_fan_out]

    // 방 수정
    private fun writeNewPost(userId: String, teamHead: String, title: String,
                             summary:String, description:String,photoUrl:String) {

        val key = database.child("workOutRooms").push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for posts")
            return
        }

        val workOutRoom = WorkoutRoom(userId, teamHead, title, summary, description,photoUrl)
        val workOutRoomValues = workOutRoom.toMap()

        val childUpdates = HashMap<String, Any>()
        // 일반 방
        childUpdates["/workOutRooms/$key"] = workOutRoomValues
        // 유저가 만든 방 구분
        childUpdates["/user-workOutRooms/$userId/$key"] = workOutRoomValues

        database.updateChildren(childUpdates)
    }
    // [END write_fan_out]
}
