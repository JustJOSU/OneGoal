package com.hour.onegoal.Login

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.hour.onegoal.*
import com.hour.onegoal.Data.User
import com.hour.onegoal.R
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.edit_text_name
import kotlinx.android.synthetic.main.activity_profile.logout
import kotlinx.android.synthetic.main.activity_profile.progressbar
import kotlinx.android.synthetic.main.activity_register.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"

    private lateinit var imageUri: Uri
    private val REQUEST_IMAGE_CAPTURE = 100
    private val OPEN_GALLERY = 1
    private var filePath: Uri? = null
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val mDatabase = FirebaseDatabase.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val mDb = mDatabase.reference
        val user: FirebaseUser = firebaseAuth.currentUser!!
        val userKey = user.uid
        mDb.child("users").child(userKey).addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val birthID =
                    p0.child("birth").getValue(String::class.java)!!
                val genderID =
                    p0.child("gender").getValue(String::class.java)
                currentUser?.let {
                    text_birth.setText(birthID)
                    text_gender.setText(genderID)
                    edit_text_name.setText(user.displayName)
                    text_email.text = user.email
                    Glide.with(this@ProfileActivity)
                        .load(user.photoUrl)
                        .into(profile_image_view)
                    if (user.isEmailVerified) {
                        text_not_verified.visibility = View.INVISIBLE
                    } else {
                        text_not_verified.visibility = View.VISIBLE
                    }
                }
            }

        })

        text_birth.visibility = View.INVISIBLE
        text_gender.visibility = View.INVISIBLE


        // 프로필 이미지 클릭
        profile_image_view.setOnClickListener {
            showPictureDialog()
        }
        // 저장버튼
        button_save.setOnClickListener {

            val photo = when {
                // 선택된 이미지를 사진에 사용한다.
                ::imageUri.isInitialized -> imageUri
                filePath != null -> filePath
                // 현재 사용자가 널이면
                currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                else -> currentUser.photoUrl
            }

            val name = edit_text_name.text.toString().trim()

            if (name.isEmpty()) {
                edit_text_name.error = "이름을 입력해주세요"
                edit_text_name.requestFocus()
                return@setOnClickListener
            }
            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photo)
                .build()

            progressbar.visibility = View.VISIBLE

            currentUser?.updateProfile(updates)
                ?.addOnCompleteListener { task ->
                    progressbar.visibility = View.INVISIBLE
                    if (task.isSuccessful) {
                        val uid = FirebaseAuth.getInstance().uid?: ""
                        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
                        val user = User(uid,text_birth.text.toString(),text_gender.text.toString(),username = currentUser.displayName!!)
                        ref.setValue(user)
                        application?.toast("프로필 업데이트 성공")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        application?.toast(task.exception?.message!!)
                    }
                }
        }

        // 로그아웃
        logout.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Are you sure?")
                setPositiveButton("Yes") { _, _ ->

                    FirebaseAuth.getInstance().signOut()
                    logout()

                }
                setNegativeButton("Cancel") { _, _ ->
                }
            }.create().show()
        }

        // 패스워드 변경
        text_password.setOnClickListener {
            val intent = Intent(this, UpdatePasswordActivity::class.java)
            startActivity(intent)
        }

        // 이메일 인증이 안되었을 때
        text_not_verified.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("이메일 인증하시겠습니까?")
                setPositiveButton("Yes") { _, _ ->
                    currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener {
                            if (it.isSuccessful){
                                FirebaseAuth.getInstance().signOut()
                                logout()
                                toast("현재 로그인 하신 이메일로 인증 메일을 보내드렸습니다. 인증 후 다시 로그인 하여주세요 ^^")
                            }else{
                                toast(it.exception?.message!!)
                            }

                        }
                }
                setNegativeButton("Cancel") { _, _ ->
                }
            }.create().show()

        }

        // 이메일 인증 페이지
        text_email.setOnClickListener {
            val intent = Intent(this,
                UpdateEmailActivity::class.java)
            startActivity(intent)
        }

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
            .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
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
                        profile_image_view.setImageBitmap(bitmap)
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
            .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
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
                        profile_image_view.setImageBitmap(bitmap)
                    }
                }
            } else {
                uploadTask.exception?.let {
                    toast(it.message!!)
                }
            }
        }

    }


}

