package com.hour.onegoal

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.hour.onegoal.Util.toast
import kotlinx.android.synthetic.main.activity_new_post.*
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.custom_dialog.*
import soup.neumorphism.NeumorphTextView
import java.io.ByteArrayOutputStream
import java.io.IOException

class RoomActivity : AppCompatActivity() {

    private lateinit var roomTitle : String
    private lateinit var roomId : String
    private val REQUEST_IMAGE_CAPTURE = 100
    private val OPEN_GALLERY = 1
    private var filePath: Uri? = null
    private var imageUri: Uri? = null
    lateinit var photo : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        roomTitle = intent.getStringExtra("title")
        roomId = intent.getStringExtra("roomId")
        findViewById<NeumorphTextView>(R.id.valid_room_title).text = roomTitle

        enterAccount_cardView.setOnClickListener {
            val intent = Intent(this,ParticipantsActivity::class.java)
            intent.putExtra("roomId",roomId)
            startActivity(intent)
        }

        submit_cardView.setOnClickListener {
            showDialog()
        }
    }

    // 사진
    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        val body = dialog.findViewById(R.id.dialog_missionTitle) as TextView
        body.text = title
        photo = dialog.findViewById(R.id.dialog_imageView) as ImageView
        photo.setOnClickListener {
            showPictureDialog()
        }
        val yesBtn = dialog.findViewById(R.id.okButton) as Button
        val noBtn = dialog.findViewById(R.id.cancelButton) as Button
        yesBtn.setOnClickListener {
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

}
