package com.hour.onegoal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hour.onegoal.Data.User
import com.hour.onegoal.Data.WorkoutRoom
import com.hour.onegoal.Login.ProfileActivity
import com.hour.onegoal.Util.loadImage
import com.hour.onegoal.Util.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_work_out_room.*
import org.w3c.dom.Text


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




        //findViewById<ImageView>(R.id.teamUserProfile).loadImage(teamHeadPhotoUrl)
        findViewById<TextView>(R.id.roomTitle).text = roomTitle
        findViewById<ImageView>(R.id.roomPhoto).loadImage(roomPhotoUrl)
        findViewById<TextView>(R.id.roomDescription).text = roomDescription
        findViewById<TextView>(R.id.roomSummary).text = roomSummary
        findViewById<TextView>(R.id.roomTeamHead).text = roomTeamHead

       /** deleteButton.setOnClickListener {
            delete(roomId)
        }**/
       // 방장 프로필
       val teamHeadPhotoUrl = FirebaseDatabase.getInstance().getReference("workOutRooms/${roomId}")
        teamHeadPhotoUrl.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val checkProfile = p0.child("teamHeadPhotoUrl").value.toString()
                findViewById<CircleImageView>(R.id.roomTeamHeadProfileImage).loadImage(checkProfile)

            }

        })


        roomEnter_btn.setOnClickListener {
            val members_ref = FirebaseDatabase.getInstance().getReference("workOutRooms/${roomId}/members")
            members_ref.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val checkNumber = p0.childrenCount.toString().toInt()
                    if (checkNumber == 8){
                        roomEnter_btn.isClickable = false
                        Log.d("checkNumber","check Number : $checkNumber")
                        toast("인원이 꽉 찼습니다. 다른 방을 이용하여 주세요 ^^")
                    }
                    else{
                        enterRoom()
                    }
                }

            })

        }
        val d = findViewById<TextView>(R.id.roomDescription)
        d.movementMethod = ScrollingMovementMethod()
    }

    // 방입장
    private fun enterRoom() {
        // [START single_value_read]
        val firebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser = firebaseAuth.currentUser!!

        database.child("users/$userId").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user_info = p0.getValue(User::class.java)

                if(p0.child("myroom").exists()){
                    roomEnter_btn.isClickable = false
                    toast("방은 계정 당 하나만 입장하실 수 있습니다.")
                    return
                }

                if(user_info?.username != null){
                    val user = User(userId,
                        user_info.username!!,user_info.gender,user_info.birth,user_info.photoUrl)
                    val userValues = user.toMap()

                    val room = WorkoutRoom(roomId, roomTeamHead, roomTitle, roomSummary, roomDescription, roomPhotoUrl)
                    val roomValues = room.toMap()

                    val childUpdates = HashMap<String, Any>()
                    childUpdates["users/$userId/myroom"] = roomValues
                    childUpdates["workOutRooms/$roomId/members/$userId"] = userValues

                    database.updateChildren(childUpdates)

                    val intent = Intent(this@WorkOutRoomActivity, RoomActivity::class.java)
                    intent.putExtra("title",roomTitle)
                    intent.putExtra("roomId",roomId)
                    startActivity(intent)
                }
                else {
                    val intent = Intent(this@WorkOutRoomActivity, ProfileActivity::class.java).apply {
                        toast("프로필을 완성시켜야만 방을 입장할 수 있습니다.")
                    }
                    startActivity(intent)
                }
            }
        })
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
        val roomTable1 : DatabaseReference = FirebaseDatabase.getInstance().getReference("/users/$userId/myroom")
        // 2020-05-24 21:26 조성재 -변경사항 기록-
        // user-workOutRooms에서도 지우려면 경로를 ("/user-workOutRooms/$userId/$roomId")와 같이해줘야됨
        // 이유는 NewPostActivity의 308번 라인 참고

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
