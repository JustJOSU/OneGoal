package com.hour.onegoal

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
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

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
        // UserName 가져오는 코드
        currentUser?.let { user ->
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

        profile_image_view.setOnClickListener {
            showPictureDialog()
        }
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
                        application?.toast("프로필 업데이트 성공")
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        application?.toast(task.exception?.message!!)
                    }
                }
        }

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

