    package com.hour.onegoal

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.hour.onegoal.Data.User
import com.hour.onegoal.Data.WorkoutRoom
import com.hour.onegoal.Login.ProfileActivity
import kotlinx.android.synthetic.main.activity_new_post.*
import java.io.ByteArrayOutputStream
import java.io.IOException

    class NewPostActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
        private val REQUEST_IMAGE_CAPTURE = 100
        private val OPEN_GALLERY = 1
        private var filePath: Uri? = null
        private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        database = FirebaseDatabase.getInstance().reference

        fabSubmitRoom.setOnClickListener { submitRoom()}

        fieldPhoto.setOnClickListener { showPictureDialog() }

        fieldSummary.filters = arrayOf(InputFilter.LengthFilter(100))

        fieldSummary.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                countWord.text = "0 / 100"
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var userinput = fieldSummary.text.toString()
                countWord.text = userinput.length.toString() + " / 100"
            }

            override fun afterTextChanged(s: Editable?) {
                var userinput = fieldSummary.text.toString()
                countWord.text = userinput.length.toString() + " / 100"
            }

        })
    }

        private fun submitRoom() {
            //photourl 넘기는 거랑 현재 user 의 name 도 넘겨줘야하는 것 고민
            val title = fieldTitle.text.toString()
            val summary = fieldSummary.text.toString()
            val description = fieldDescription.text.toString()

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


            // [START single_value_read]
            val firebaseAuth = FirebaseAuth.getInstance()
            val user: FirebaseUser = firebaseAuth.currentUser!!
            val userId = user.uid

            database.child("users").child(userId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val username = dataSnapshot.child("username").value.toString()
                        val teamHead = dataSnapshot.child("username").value

                        if(teamHead == null){
                            val intent = Intent(this@NewPostActivity, ProfileActivity::class.java).apply {
                                toast("프로필을 완성시켜야만 방을 만들 수가 있습니다!!")
                            }
                            startActivity(intent)
                            finish()
                        } else if (filePath == null && imageUri == null){
                            writeNewPost(userId, username, title, summary, description, photoUrl = "")
                        } else {
                            if (filePath == null){
                                writeNewPost(userId, username, title, summary, description,photoUrl = imageUri.toString())
                            } else{
                                writeNewPost(userId, username, title, summary, description,photoUrl = filePath.toString())
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
