package com.hour.onegoal

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.hour.onegoal.Data.EnterRoom
import com.hour.onegoal.Data.User
import com.hour.onegoal.Data.WorkoutRoom
import com.hour.onegoal.Login.ProfileActivity
import com.hour.onegoal.Util.loadImage
import com.hour.onegoal.Util.toast
import kotlinx.android.synthetic.main.activity_work_out_room.*

class WorkOutRoomActivity : AppCompatActivity() {
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var database: DatabaseReference
    private val REQUEST_IMAGE_CAPTURE = 100
    private val OPEN_GALLERY = 1
    private var filePath: Uri? = null
    private lateinit var imageUri: Uri
    private lateinit var superRoomPhotoUri: String

    private lateinit var roomId :String
    private lateinit var roomTitle :String
    private lateinit var roomDescription :String
    private lateinit var roomPhotoUrl :String
    private lateinit var roomSummary :String
    private lateinit var roomTeamHead :String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_out_room)

        database = FirebaseDatabase.getInstance().reference
        superRoomPhotoUri = intent.getStringExtra("photoUrl")

        roomId = intent.getStringExtra("roomId")
        roomTitle = intent.getStringExtra("title")
        roomDescription = intent.getStringExtra("description")
        roomPhotoUrl = intent.getStringExtra("photoUrl")
        roomSummary = intent.getStringExtra("summary")
        roomTeamHead = intent.getStringExtra("teamHead")


        findViewById<TextView>(R.id.roomTitle).text = roomTitle
        findViewById<ImageView>(R.id.roomPhoto).loadImage(roomPhotoUrl)
        findViewById<TextView>(R.id.roomDescription).text = roomDescription
        findViewById<TextView>(R.id.roomSummary).text = roomSummary
        findViewById<TextView>(R.id.roomTeamHead).text = roomTeamHead

        //TODO 방 삭제 똑바로
        deleteButton.setOnClickListener {
            delete(roomId)
        }

        roomEnter_btn.setOnClickListener {
            enterRoom1()
        }

    }

    // 방입장 TODO : 잠옴 ㅠㅠ
    private fun enterRoom1() {

        // [START single_value_read]
        val firebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser = firebaseAuth.currentUser!!
        val userId = user.uid
        database.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user_info = p0.child("users/$userId").children
                val room_info = p0.child("workOutRooms/$roomId").value
                Log.d(TAG, "User Info : $user_info")
                for(h in user_info){
                    Log.d(TAG,"h : ${h.key}")
                }
            }

        })
        """
        database.child("users").child(userId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.child("username").value
                    val photoUrl = dataSnapshot.child("photoUrl").value.toString()
                    if(username != null){
                        Log.d(TAG,"enterRoom1 func : $roomId")
                        enterRoom(userId,roomId,username.toString(),photoUrl)
                        val enterId = database.child("users/$userId").push().key
                        
                        val intent = Intent(this@WorkOutRoomActivity, RoomActivity::class.java)
                        startActivity(intent)
                    }else {
                        val intent = Intent(this@WorkOutRoomActivity, ProfileActivity::class.java).apply {
                            toast("프로필을 완성시켜야만 방을 입장할 수 있습니다.")
                        }
                        startActivity(intent)
                    }
                    finish()
                    // [END_EXCLUDE]
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException())
                }
            })
        // [END single_value_read]
        """


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
    }

    // 방입장 TODO: 잠옴 ;;
    private fun enterRoom(userId: String, roomId: String,username:String,photoUrl:String){
        Log.d(TAG,"Enter ID : $roomId")
        val enterId = database.child("workOutRooms/$roomId").push().key
        // 생성되는 방의 key값

        if (enterId == null) {
            Log.w(NewPostActivity.TAG, "Couldn't get push key for posts")
            return
        }

        val enterRoom = EnterRoom(enterId, roomId, userId, username, photoUrl)
        // 2020-05-24 21:26 조성재 -변경사항 기록-
        // WorkoutRoom(uid, teamHead, title, summary, description,photoUrl) -> WorkoutRoom(roomId, teamHead, title, summary, description,photoUrl)로 변경

        val enterRoomValues = enterRoom.toMap()

        val childUpdates = HashMap<String, Any>()
        // 일반 방
        childUpdates["/workOutRooms/$roomId/$enterId"] = enterRoomValues
        // 유저가 만든 방 구분
        childUpdates["/user-workOutRooms/$userId/$roomId/$enterId"] = enterRoomValues
        database.updateChildren(childUpdates)
    }

    // TAG WorkOutRoomActivity
    companion object{
        val TAG = WorkOutRoomActivity::class.qualifiedName
    }

}
