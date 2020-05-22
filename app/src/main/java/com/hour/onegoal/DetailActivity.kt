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
import com.hour.onegoal.Util.toast

class DetailActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val REQUEST_IMAGE_CAPTURE = 100
    private val OPEN_GALLERY = 1
    private var filePath: Uri? = null
    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        database = FirebaseDatabase.getInstance().reference

        val roomId = intent.getStringExtra("roomId")
        val roomTitle = intent.getStringExtra("title")
        val roomDescription = intent.getStringExtra("description")
        val roomPhotoUrl = intent.getStringExtra("photoUrl")
        val roomSummary = intent.getStringExtra("summary")
        val roomTeamHead = intent.getStringExtra("teamHead")
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
                    val detailTeamHead = findViewById<TextView>(R.id.detailTeamHead)

                    toast("This is summary : $roomSummary")
                    detailTitle.text = roomTitle
                    detailDescription.text = roomDescription
                    detailSummary.text = roomSummary
                    detailPhoto.loadImage(roomPhotoUrl)
                    detailTeamHead.text= roomTeamHead
                }

            })
    }




    // TAG DetailActivity
    companion object{
        val TAG = DetailActivity::class.qualifiedName
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
