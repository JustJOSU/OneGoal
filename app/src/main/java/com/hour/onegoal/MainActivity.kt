package com.hour.onegoal

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.hour.onegoal.Data.Category
import com.hour.onegoal.Login.ProfileActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val categoryList = arrayListOf<Category>(
        Category(R.drawable.workout,"운동") ,
        Category(R.drawable.study, "공부"),
        Category(R.drawable.music, "음악" )
    )
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // UserName 가져오는 코드
        val firebaseAuth = FirebaseAuth.getInstance()
        val mDatabase = FirebaseDatabase.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val mDb = mDatabase.reference
        val user: FirebaseUser = firebaseAuth.currentUser!!
        val userKey = user.uid
        /**
        currentUser?.let {
            if (it.displayName == null && it.photoUrl == null){
                user_textView.visibility = View.VISIBLE
                current_user_imageView.visibility = View.VISIBLE
            }
            else{
                    user_textView.text = user.displayName
                    Glide.with(this@MainActivity)
                    .load(user.photoUrl)
                    .into(current_user_imageView)
                }
            }**/
        // [START single_value_read]
        val userId = user.uid
        database.child("users").child(userId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(user.displayName == null && user.photoUrl == null)
                    {
                        user_textView.visibility = View.VISIBLE
                        current_user_imageView.visibility = View.VISIBLE
                    }
                    else{
                        user_textView.text = user.displayName
                        Glide.with(this@MainActivity)
                            .load(user.photoUrl)
                            .into(current_user_imageView)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        // [END single_value_read]


            // 현재 유저 선택했을 경우
            main_id_set.setOnClickListener {
                val intent = Intent(applicationContext,
                    ProfileActivity::class.java)
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
            main_category_recyclerView.viewTreeObserver.addOnGlobalLayoutListener { scrollToEnd() }

        }
    private fun scrollToEnd() =
        (main_category_recyclerView.adapter!!.itemCount - 1).takeIf { it > 0 }?.let(main_category_recyclerView::smoothScrollToPosition)


}
