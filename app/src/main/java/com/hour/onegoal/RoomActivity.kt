package com.hour.onegoal

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.hour.onegoal.Data.Mission
import com.hour.onegoal.Data.TodayMission
import com.hour.onegoal.Util.loadImage
import com.hour.onegoal.Util.toast
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.today_mission_dialog.*
import soup.neumorphism.NeumorphTextView
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class RoomActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var roomTitle : String
    private lateinit var roomId : String
    private val REQUEST_IMAGE_CAPTURE = 100
    private val OPEN_GALLERY = 1
    private var filePath: Uri? = null
    private var imageUri: Uri? = null
    lateinit var photo : ImageView
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        database = FirebaseDatabase.getInstance().reference
        roomTitle = intent.getStringExtra("title")
        roomId = intent.getStringExtra("roomId")

        findViewById<NeumorphTextView>(R.id.valid_room_title).text = roomTitle

        // 참여자 카드 클릭 시
        enterAccount_cardView.setOnClickListener {
            val intent = Intent(this,ParticipantsActivity::class.java)
            intent.putExtra("roomId",roomId)
            startActivity(intent)
        }

        // 제출 버튼 클릭시
        submit_cardView.setOnClickListener {
            showDialog()
        }
        // 미션 히스토리 버튼 클릭 시
        missionHistory_cardView.setOnClickListener {
            val intent = Intent(this,MissionHistoryActivity::class.java)
            intent.putExtra("roomId",roomId)
            startActivity(intent)
        }
        todayMissionTitle.setOnClickListener {
            todayMissionDialog()
        }
    }

    // 오늘의 미션 텍스트뷰 클릭 시 나오는 다이얼로그
    private fun todayMissionDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.today_mission_dialog)

        val yesBtn = dialog.findViewById(R.id.ok_Today_Button) as Button
        val noBtn = dialog.findViewById(R.id.no_TodayButton) as Button
        yesBtn.setOnClickListener {
            val title = dialog.dialog_Today_missionTitle.text
            val description = dialog.dialog_Today_missionDescription.text
            submitTodayMission(this, title, description)
            todayMissionTitle.text = title
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
    // 미션 업로드
    private fun writeNewTodayMission(todayMissionTitle:String,todayMissionDescription:String){


        val dateFormat: DateFormat = SimpleDateFormat("yyyyMMdd")
        val date = Date()
        val strDate: String = dateFormat.format(date).toString()
        val todayMissionId = database.child("workOutRooms/$roomId/TodayMission").push().key
        // 생성되는 방의 key값

        if (todayMissionId == null) {
            Log.w(TAG, "Couldn't get push key for posts")
            return
        }
        val todayMission = TodayMission(todayMissionId,todayMissionTitle,todayMissionDescription,strDate)

        val todayMissionValues = todayMission.toMap()

        val childUpdates = HashMap<String, Any>()

        //childUpdates["/workOutRooms/$roomId/mission/$missionId/$userId/$todayMissionId"] = missionValues
        childUpdates["/workOutRooms/$roomId/MissionHistory/$strDate"] = todayMissionValues
        childUpdates["/workOutRooms/$roomId/TodayMission/$strDate"] = todayMissionValues
        childUpdates["users/$userId/myroom/TodayMission"] = todayMissionValues
        database.updateChildren(childUpdates)

    }

    //
    private fun submitTodayMission(context: Context, title: Editable, description:Editable) {

        // [START single_value_read]
        val firebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser = firebaseAuth.currentUser!!
        val userId = user.uid

        database.child("users").child(userId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    writeNewTodayMission(todayMissionTitle = title.toString(), todayMissionDescription = description.toString())

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException())
                }
            })
        // [END single_value_read]
    }


    // 제출 버튼 클릭 시 나오는 다이얼로그
    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)

        photo = dialog.findViewById(R.id.dialog_imageView) as ImageView
        photo.setOnClickListener {
            showPictureDialog()
        }

        val yesBtn = dialog.findViewById(R.id.ok_Today_Button) as Button
        val noBtn = dialog.findViewById(R.id.no_TodayButton) as Button
        yesBtn.setOnClickListener {
            submitMission()
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

    // 카메라 or 갤러리 선택
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("카메라", "갤러리")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> takePictureIntent()
                1 -> openGallery()
            }
        }
        pictureDialog.show()
    }
    // 카메라
    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }

        }
    }

    // 갤러리
    private fun openGallery() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY)
    }

    // 갤러리에서 이미지 업로드
    private fun uploadImage(){

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
        // ByteArrayOutputStream 은 메모리, 즉 바이트 배열에 데이터를 입출력하는데 사용되는 스트림이다
        val baos = ByteArrayOutputStream()
        // Storage 인스턴스 얻어와서 child() 에는 유저 정보를 받아와서 현재 유저정보를 받고 pics 라는 폴더에 저장한다.
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("mission/${FirebaseAuth.getInstance().currentUser?.uid}")
        // bitmap 압축 방식
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        // ByteArray 메소드를 이용하면 저장된 모든 내용이 바이트 배열로 반환
        val image = baos.toByteArray()
        // 스토리지 레퍼런스에 배열로 반환한 image 변수를 put !
        val upload = storageRef.putBytes(image)

        // TODO: 프로그래스바
        upload.addOnCompleteListener { uploadTask ->
            if (uploadTask.isSuccessful) {
                // 객체를 다운로드하는 데 사용할 수 있는 URL
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        filePath = it
                        photo.setImageBitmap(bitmap)
                    }
                }
            } else {
                uploadTask.exception?.let {
                    toast(it.message!!)
                }
            }
        }

    }

    // 카메라에서 업로드
    private fun uploadImageAndSaveUri(bitmap: Bitmap) {

        // ByteArrayOutputStream 은 메모리, 즉 바이트 배열에 데이터를 입출력하는데 사용되는 스트림이다
        val baos = ByteArrayOutputStream()
        // Storage 인스턴스 얻어와서 child() 에는 유저 정보를 받아와서 현재 유저정보를 받고 pics 라는 폴더에 저장한다.
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("mission/${FirebaseAuth.getInstance().currentUser?.uid}")
        // bitmap 압축 방식
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        // ByteArray 메소드를 이용하면 저장된 모든 내용이 바이트 배열로 반환
        val image = baos.toByteArray()
        // 스토리지 레퍼런스에 배열로 반환한 image 변수를 put !
        val upload = storageRef.putBytes(image)
        val dialogImageView = findViewById<ImageView>(R.id.dialog_imageView)
        //TODO: 프로그래스바
        upload.addOnCompleteListener { uploadTask ->
            // 성공하면 프로그레스바 안보이도록
            if (uploadTask.isSuccessful) {
                // 객체를 다운로드하는 데 사용할 수 있는 URL
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        Toast.makeText(this,"$it", Toast.LENGTH_SHORT).show()
                        photo.setImageBitmap(bitmap)

                    }
                }
            } else {
                uploadTask.exception?.let {
                    toast(it.message!!)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_GALLERY && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            filePath = data.data
            try {
                uploadImage()
            }catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val imageBitmap = data!!.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }


    }
    // 제출 버튼 미션 업로드
    private fun writeNewMission(missionPhotoUrl:String,missionUser:String,missionUserPhotoUrl: String){

        val missionId = database.child("workOutRooms/$roomId/mission").push().key
        // 생성되는 방의 key값
        val dateFormat: DateFormat = SimpleDateFormat("yyyyMMdd")
        val date = Date()
        val strDate: String = dateFormat.format(date).toString()

        if (missionId == null) {
            Log.w(TAG, "Couldn't get push key for posts")
            return
        }

        val mission = Mission(missionId,missionWriteTime = strDate,
            missionPhotoUrl = missionPhotoUrl,missionUser = missionUser,
                missionUserPhotoUrl = missionUserPhotoUrl
            )

        val missionValues = mission.toMap()

        val childUpdates = HashMap<String, Any>()


        val userid_map = mapOf("1" to "$userId")


        //childUpdates["/workOutRooms/$roomId/mission/$missionId/$userId"] = missionValues
        childUpdates["/workOutRooms/$roomId/MissionHistory/$strDate/members"] = userid_map
        database.updateChildren(childUpdates)

    }
    //그리고 DB 구조 다시 ;;
    private fun submitMission() {

        // [START single_value_read]
        val firebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser = firebaseAuth.currentUser!!
        val userId = user.uid

        database.child("users").child(userId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.child("username").value
                    val userPhotoUrl = dataSnapshot.child("photoUrl").value.toString()
                     if (filePath == null && imageUri == null){
                         val dateFormat: DateFormat = SimpleDateFormat("yyyyMMdd")
                         val date = Date()
                         val strDate: String = dateFormat.format(date).toString()
                        writeNewMission(missionPhotoUrl = "",missionUser = username.toString(),missionUserPhotoUrl = userPhotoUrl)
                         val intent = Intent(this@RoomActivity, MissionHistoryActivity::class.java)
                         intent.putExtra("writeDate", strDate)
                         startActivity(intent)
                    } else {
                        if (filePath == null){
                            writeNewMission(missionPhotoUrl = imageUri.toString(),missionUser = username.toString(),missionUserPhotoUrl = userPhotoUrl)
                        } else{
                            writeNewMission(missionPhotoUrl = filePath.toString(),missionUser = username.toString(),missionUserPhotoUrl = userPhotoUrl)
                        }
                    }

                    // [END_EXCLUDE]
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException())
                }
            })
        // [END single_value_read]


    }

    override fun onStart() {
        super.onStart()
        FirebaseDatabase.getInstance().reference.child("/workOutRooms/$roomId/mission/todayMission").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                val todayMission = p0.value
                for (ds in p0.children) {
                    val title = ds.child("todaymissionTitle").getValue(String::class.java)
                    todayMissionTitle.text = title
                }
               // Log.d(TAG,"p0 : $todayMission")
                /**
                if(todayMission == null){
                    todayMissionTitle.text = "오늘의 미션을 입력해 주세요."
               }
                else{
                    todayMissionTitle.text =
                }**/
            }

        })
    }

    // [END write_fan_out]
    companion object{
        val TAG = RoomActivity::class.qualifiedName
    }
}
