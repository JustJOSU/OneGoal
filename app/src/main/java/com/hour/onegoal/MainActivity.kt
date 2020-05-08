package com.hour.onegoal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hour.onegoal.Data.Category
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object{
        val INTENT_PARCELABLE = "OBJECT_INTENT"
    }
    val categoryList = arrayListOf<Category>(
        Category(R.drawable.workout,"운동") ,
        Category(R.drawable.study, "공부"),
        Category(R.drawable.music, "음악" )
    )
    private val currentUser = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UserName 가져오는 코드
        val firebaseAuth = FirebaseAuth.getInstance()
        val mDatabase = FirebaseDatabase.getInstance()
        val mDb = mDatabase.reference
        val user: FirebaseUser = firebaseAuth.currentUser!!
        val userKey = user.uid

        mDb.child("users").child(userKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userID =
                    dataSnapshot.child("username").getValue(String::class.java)
                val photoID =
                    dataSnapshot.child("profileImageUrl").getValue(String::class.java)
                currentUser?.let {user ->
                    user_textView.text = userID
                    Glide.with(this@MainActivity)
                        .load(photoID)
                        .into(current_user_imageView)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        // 현재 유저 선택했을 경우
        main_id_set.setOnClickListener {
            val intent = Intent(applicationContext,ProfileActivity::class.java)
            startActivity(intent)
        }

        // 카테고리 리스트
        val lm = GridLayoutManager(applicationContext,1)
        //LayoutManager는 RecyclerView의 각 item들을 배치하고,
        // item이 더이상 보이지 않을 때 재사용할 것인지 결정하는 역할을 한다
        main_category_recyclerView.layoutManager = lm
        main_category_recyclerView.setHasFixedSize(true)
        main_category_recyclerView.adapter = MainRvAdapter(applicationContext, categoryList){

        }
        //TODO: 리사이클러뷰 끝까지 내려가질 않음
        main_category_recyclerView.viewTreeObserver.addOnGlobalLayoutListener { scrollToEnd() }

    }
    private fun scrollToEnd() =
        (main_category_recyclerView.adapter!!.itemCount - 1).takeIf { it > 0 }?.let(main_category_recyclerView::smoothScrollToPosition)

}
