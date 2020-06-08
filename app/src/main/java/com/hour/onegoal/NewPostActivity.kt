package com.hour.onegoal

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.hour.onegoal.Data.User
import com.hour.onegoal.Data.WorkoutRoom
import com.hour.onegoal.Login.ProfileActivity
import com.hour.onegoal.Util.toast
import kotlinx.android.synthetic.main.activity_new_post.*
import java.io.ByteArrayOutputStream
import java.io.IOException


class NewPostActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
        private val REQUEST_IMAGE_CAPTURE = 100
        private val OPEN_GALLERY = 1
        private var filePath: Uri? = null
        private var imageUri: Uri? = null
    private lateinit var getUserId :String
    private lateinit var getBirthId :String
    private lateinit var getGenderId :String
    private lateinit var getUsernameId :String
    private lateinit var getPhotoUrlId :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        database = FirebaseDatabase.getInstance().reference

        fabSubmitRoom.setOnClickListener { submitRoom()}

        fieldPhoto.setOnClickListener { showPictureDialog() }

        fieldSummary.filters = arrayOf(InputFilter.LengthFilter(50))

        fieldSummary.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                countWord.text = "0 / 50"
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var userinput = fieldSummary.text.toString()
                countWord.text = userinput.length.toString() + " / 50"
            }

            override fun afterTextChanged(s: Editable?) {
                var userinput = fieldSummary.text.toString()
                countWord.text = userinput.length.toString() + " / 50"

                if(userinput.length == 50 ){
                    fieldDescription.requestFocus()
                }
            }

        })

        fieldSummary.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (event.keyCode === KeyEvent.KEYCODE_ENTER) {
                fieldDescription.requestFocus()
                true
            } else {
                false
            }
        })

        fieldDescription.filters = arrayOf(InputFilter.LengthFilter(300))

        fieldDescription.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                countWord_1.text = "0 / 300"
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var userinput = fieldDescription.text.toString()
                countWord_1.text = userinput.length.toString() + " / 300"
            }

            override fun afterTextChanged(s: Editable?) {
                var userinput = fieldDescription.text.toString()
                countWord_1.text = userinput.length.toString() + " / 300"

            }

        })

        fieldDescription.movementMethod= ScrollingMovementMethod()

         getUserId = intent.getStringExtra("userId")
         getBirthId = intent.getStringExtra("birth")
         getGenderId = intent.getStringExtra("gender")
         getUsernameId = intent.getStringExtra("username")
         getPhotoUrlId = intent.getStringExtra("photoUrl")

    }

        private fun submitRoom() {
            //photourl 넘기는 거랑 현재 user 의 name 도 넘겨줘야하는 것 고민
            val title = fieldTitle.text.toString()
            val summary = fieldSummary.text.toString()
            val description = fieldDescription.text.toString()
            val numberCount = fieldNumber.text.toString()
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
            if (description.isEmpty()) {
                fieldDescription.error = "상세설명을 입력해주세요"
                fieldDescription.requestFocus()
                return
            }
            //TODO: 인원 수 조건 문 달기
            if(numberCount.isEmpty()){
                fieldNumber.error = "인원 수를 입력해주세요"
                fieldNumber.requestFocus()
                return
            }

            // [START single_value_read]
            val firebaseAuth = FirebaseAuth.getInstance()
            val user: FirebaseUser = firebaseAuth.currentUser!!
            val userId = user.uid

            database.child("users").child(userId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val username = dataSnapshot.child("username").value
                        val teamHeadPhotoUrl = dataSnapshot.child("photoUrl").value.toString()
                        if(username == null){
                            val intent = Intent(this@NewPostActivity, ProfileActivity::class.java).apply {
                                toast("프로필을 완성시켜야만 방을 만들 수가 있습니다!!")
                            }
                            startActivity(intent)
                            finish()
                        } else if (filePath == null && imageUri == null){
                            val roomId = writeNewPost(userId, username.toString(), title, summary, description, roomPhotoUrl = "",
                                numberCount = numberCount,
                                teamHeadPhotoUrl = teamHeadPhotoUrl
                            )
                            val intent = Intent(this@NewPostActivity, RoomActivity::class.java)
                            intent.putExtra("roomId",roomId)
                            intent.putExtra("title",title)
                            Log.d("teamHeadPhotoUrl", "teamHeadPhotoUrl : ${intent.putExtra("teamHeadPhotoUrl",teamHeadPhotoUrl)}")
                            startActivity(intent)
                        } else {
                            //TODO: 뭘 넣어야할것같은데;;
                            if (filePath == null){
                                val roomId = writeNewPost(userId, username.toString(), title, summary, description,
                                    roomPhotoUrl = imageUri.toString(),numberCount = numberCount,teamHeadPhotoUrl = teamHeadPhotoUrl)
                                val intent = Intent(this@NewPostActivity, RoomActivity::class.java)
                                intent.putExtra("roomId",roomId)
                                intent.putExtra("title",title)
                                Log.d("teamHeadPhotoUrl", "teamHeadPhotoUrl : ${intent.putExtra("teamHeadPhotoUrl",teamHeadPhotoUrl)}")
                                startActivity(intent)
                            } else{
                                val roomId = writeNewPost(userId, username.toString(), title, summary, description,
                                    roomPhotoUrl = filePath.toString(),numberCount = numberCount,teamHeadPhotoUrl = teamHeadPhotoUrl)
                                val intent = Intent(this@NewPostActivity, RoomActivity::class.java)
                                intent.putExtra("roomId",roomId)
                                intent.putExtra("title",title)
                                Log.d("teamHeadPhotoUrl", "teamHeadPhotoUrl : ${intent.putExtra("teamHeadPhotoUrl",teamHeadPhotoUrl)}")
                                startActivity(intent)
                            }
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

        companion object{
            val TAG = NewPostActivity::class.qualifiedName
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
            intent.setType("image/*")
            startActivityForResult(intent, OPEN_GALLERY)
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

        // 갤러리에서 이미지 업로드
        private fun uploadImage(){
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            // ByteArrayOutputStream 은 메모리, 즉 바이트 배열에 데이터를 입출력하는데 사용되는 스트림이다
            val baos = ByteArrayOutputStream()
            // Storage 인스턴스 얻어와서 child() 에는 유저 정보를 받아와서 현재 유저정보를 받고 pics 라는 폴더에 저장한다.
            val storageRef = FirebaseStorage.getInstance()
                .reference
                .child("workRoom/${FirebaseAuth.getInstance().currentUser?.uid}")
            // bitmap 압축 방식
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            // ByteArray 메소드를 이용하면 저장된 모든 내용이 바이트 배열로 반환
            val image = baos.toByteArray()
            // 스토리지 레퍼런스에 배열로 반환한 image 변수를 put !
            val upload = storageRef.putBytes(image)
            // 불러오는 동안 프로그레스바 로 보이도록 !
            progressbar_pic.visibility = View.VISIBLE
            upload.addOnCompleteListener { uploadTask ->
                // 성공하면 프로그레스바 안보이도록
                progressbar_pic.visibility = View.INVISIBLE

                if (uploadTask.isSuccessful) {
                    // 객체를 다운로드하는 데 사용할 수 있는 URL
                    storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                        urlTask.result?.let {
                            filePath = it
                            fieldPhoto.setImageBitmap(bitmap)
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
                .child("workRoom/${FirebaseAuth.getInstance().currentUser?.uid}")
            // bitmap 압축 방식
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            // ByteArray 메소드를 이용하면 저장된 모든 내용이 바이트 배열로 반환
            val image = baos.toByteArray()
            // 스토리지 레퍼런스에 배열로 반환한 image 변수를 put !
            val upload = storageRef.putBytes(image)

            // 불러오는 동안 프로그레스바 로 보이도록 !
            progressbar_pic.visibility = View.VISIBLE
            upload.addOnCompleteListener { uploadTask ->
                // 성공하면 프로그레스바 안보이도록
                progressbar_pic.visibility = View.INVISIBLE

                if (uploadTask.isSuccessful) {
                    // 객체를 다운로드하는 데 사용할 수 있는 URL
                    storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                        urlTask.result?.let {
                            imageUri = it
                            Toast.makeText(this,"$it",Toast.LENGTH_SHORT).show()
                            fieldPhoto.setImageBitmap(bitmap)
                        }
                    }
                } else {
                    uploadTask.exception?.let {
                        toast(it.message!!)
                    }
                }
            }

        }
        // [START write_fan_out]

        // 방 업로드
        private fun writeNewPost(userId: String, teamHead: String, title: String,
                                 summary:String, description:String,roomPhotoUrl:String,numberCount:String,teamHeadPhotoUrl:String): String {

            val roomId = database.child("workOutRooms").push().key
            // 생성되는 방의 key값

            if (roomId == null) {
                Log.w(TAG, "Couldn't get push key for posts")
            }
            val user = User(getUserId, getBirthId,getGenderId,getUsernameId,getPhotoUrlId)
            Log.d(TAG,"user key : ${user.uid}")
            Log.d(TAG,"user id : ${user.username}")
            Log.d(TAG,"user gend : ${user.gender}")
            Log.d(TAG,"user birth : ${user.birth}")
            Log.d(TAG,"user photo : ${user.photoUrl}")
            Log.d(TAG,"user key : ${user}")

            val userValues = user.toMap()

            val workOutRoom = WorkoutRoom(roomId, teamHead, title, summary, description,roomPhotoUrl,numberCount,teamHeadPhotoUrl)
            // 2020-05-24 21:26 조성재 -변경사항 기록-
            // WorkoutRoom(uid, teamHead, title, summary, description,photoUrl) -> WorkoutRoom(roomId, teamHead, title, summary, description,photoUrl)로 변경
            val workOutRoomValues = workOutRoom.toMap()

            val childUpdates = HashMap<String, Any>()
            // 일반 방
            childUpdates["workOutRooms/$roomId"] = workOutRoomValues
            childUpdates["users/$userId/myroom"] = workOutRoomValues
            database.updateChildren(childUpdates)
            childUpdates.clear()
            // 방 만들면 내 userId 밑에 myroom 이라는걸 만듦 2020 - 06 -02

            childUpdates["workOutRooms/$roomId/members/$userId"] = userValues

            Log.d(TAG,"childUpdated : $childUpdates")

            database.updateChildren(childUpdates)

            return roomId.toString()
        }
        // [END write_fan_out]

    }
