package com.hour.onegoal

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.hour.onegoal.Util.loadImage
import com.hour.onegoal.Util.toast
import kotlinx.android.synthetic.main.activity_work_out_room.*

class WorkOutRoomActivity : AppCompatActivity() {
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var database: DatabaseReference
    private val REQUEST_IMAGE_CAPTURE = 100
    private val OPEN_GALLERY = 1
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_out_room)

        database = FirebaseDatabase.getInstance().reference

        val roomId = intent.getStringExtra("roomId")
        val roomTitle = intent.getStringExtra("title")
        val roomDescription = intent.getStringExtra("description")
        val roomPhotoUrl: String? = intent.getStringExtra("photoUrl")
        val roomSummary = intent.getStringExtra("summary")
        val roomTeamHead = intent.getStringExtra("teamHead")

        Log.d(TAG,"roomId : $roomId")
        Log.d(TAG,"roomTitle : $roomTitle")
        Log.d(TAG,"roomDescription : $roomDescription")
        Log.d(TAG,"roomPhotoUri : $roomPhotoUrl")
        Log.d(TAG,"roomSummary : $roomSummary")
        Log.d(TAG,"roomTeamHead : $roomTeamHead")

        //TODO 방 삭제 똑바로
        deleteButton.setOnClickListener {
            delete(roomId)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun delete(roomId: String) {
        """
        if(superRoomPhotoUri != null){
            val storage_uri = FirebaseStorage.getInstance().getReferenceFromUrl(superRoomPhotoUri)
            storage_uri.delete().addOnSuccessListener {
                //TODO: 성공했을 시
            }.addOnFailureListener{
                //TODO: 실패했을 시
            }
        }
        """

        val roomTable: DatabaseReference = FirebaseDatabase.getInstance().getReference("/workOutRooms/$roomId")
        // 2020-05-24 21:26 조성재 -변경사항 기록-
        // 지우려는 방의 경로를 ("/workOutRooms") -> ("/workOutRooms/$roomId") 변경

        roomTable.removeValue()
        val roomTable1 : DatabaseReference = FirebaseDatabase.getInstance().getReference("/user-workOutRooms/$userId/$roomId")
        // 2020-05-24 21:26 조성재 -변경사항 기록-
        // user-workOutRooms에서도 지우려면 경로를 ("/user-workOutRooms/$userId/$roomId")와 같이해줘야됨
        // 이유는 NewPostActivity의 308번 라인 참고

        roomTable1.removeValue()

        val intent = Intent(this,WorkoutActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    // TAG WorkOutRoomActivity
    companion object{
        val TAG = WorkOutRoomActivity::class.qualifiedName
    }

}
