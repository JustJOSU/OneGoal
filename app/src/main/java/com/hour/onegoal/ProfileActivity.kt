package com.hour.onegoal

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"

    private lateinit var imageUri: Uri
    private val REQUEST_IMAGE_CAPTURE = 100

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        // UserName 가져오는 코드
        val firebaseAuth = FirebaseAuth.getInstance()
        val mDatabase = FirebaseDatabase.getInstance()
        val mDb = mDatabase.reference
        val user: FirebaseUser = firebaseAuth.currentUser!!
        val userKey = user.uid

        mDb.child("users").child(userKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userID =
                    dataSnapshot.child("username").getValue(String::class.java)!!
                val photoID =
                    dataSnapshot.child("profileImageUrl").getValue(String::class.java)

                currentUser?.let {user ->
                    edit_text_name.setText(userID)
                    Glide.with(this@ProfileActivity)
                        .load(photoID)
                        .into(profile_image_view)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
