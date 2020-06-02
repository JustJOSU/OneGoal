package com.hour.onegoal

import android.content.Intent
import android.graphics.Typeface
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
import com.hour.onegoal.Util.toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val categoryList = arrayListOf<Category>(
        Category(R.drawable.run,"운동") ,
        Category(R.drawable.study, "공부")
    )
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference
    private var mAuthListener : FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // UserName 가져오는 코드
        val firebaseAuth = FirebaseAuth.getInstance()
        val mDatabase = FirebaseDatabase.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val mDb = mDatabase.reference
        val user: FirebaseUser = firebaseAuth.currentUser!!
        val uid = user.uid

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
            }
        // [START single_value_read]
        val userId = user.uid
        database.child("users").child(userId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(user.displayName == null || user.photoUrl == null)
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
        **/

            // 현재 유저 선택했을 경우
        profile_btn.setOnClickListener {
                val intent = Intent(applicationContext,
                    ProfileActivity::class.java)
                startActivity(intent)
            }

            // 카테고리 리스트
            val lm = GridLayoutManager(applicationContext,2)
            //LayoutManager는 RecyclerView의 각 item들을 배치하고,
            // item이 더이상 보이지 않을 때 재사용할 것인지 결정하는 역할을 한다
            main_category_recyclerView.layoutManager = lm
            main_category_recyclerView.setHasFixedSize(true)
            main_category_recyclerView.adapter = MainRvAdapter(applicationContext, categoryList){

            }
            main_category_recyclerView.viewTreeObserver.addOnGlobalLayoutListener { scrollToEnd() }

        // 투데이 미션 setText
        FirebaseDatabase.getInstance().getReference("/users/$uid").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.child("/myroom/todayMission").child("todaymissionTitle").value == null){
                    todayMission_title.text = "오늘의 미션은??"
                }else {
                    val title1 =
                        p0.child("/myroom/todayMission").child("todaymissionTitle").value.toString()
                    todayMission_title.text = title1
                }
                if (p0.child("/myroom").child("roomId").value == null){
                    main_today_mission_cardView.isClickable = false
                    toast("방 등록 또는 가입을 해주세요 ^^")
                }
                else{
                    val myroom = p0.child("/myroom").child("roomId").value.toString()
                    val title = p0.child("/myroom").child("title").value.toString()
                    // main_today_mission_cardView 클릭 시
                    main_today_mission_cardView.setOnClickListener {
                        val intent = Intent(this@MainActivity, RoomActivity::class.java)
                        intent.putExtra("roomId",myroom)
                        intent.putExtra("title",title)
                        startActivity(intent)
                    }
                }

            }

        })



        }
    private fun scrollToEnd() =
        (main_category_recyclerView.adapter!!.itemCount - 1).takeIf { it > 0 }?.let(main_category_recyclerView::smoothScrollToPosition)

}
