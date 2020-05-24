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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_out_room)

        database = FirebaseDatabase.getInstance().reference
        superRoomPhotoUri = intent.getStringExtra("photoUrl")

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

        database.child("users").child(userId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.child("username").value.toString()
                    val roomId = dataSnapshot.child("roomId").value.toString()
                    val photoUrl = dataSnapshot.child("photoUrl").value.toString()
                    if(username == null){
                        val intent = Intent(this@WorkOutRoomActivity, ProfileActivity::class.java).apply {
                            toast("프로필을 완성시켜야만 방을 입장할 수 있습니다.")
                        }
                        startActivity(intent)
                    }else {
                        enterRoom(userId,roomId = roomId, username = username,photoUrl = photoUrl)
                        val intent = Intent(this@WorkOutRoomActivity, RoomActivity::class.java)
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
