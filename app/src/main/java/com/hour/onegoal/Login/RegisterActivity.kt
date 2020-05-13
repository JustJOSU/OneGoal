package com.hour.onegoal.Login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.hour.onegoal.R
import com.hour.onegoal.Data.User
import com.hour.onegoal.login
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            performRegister()
            return@setOnClickListener
        }

        already_have_account_text_view.setOnClickListener {
            Log.d("RegisterActivity", "Try to show login activity")

            // launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun performRegister() {
        // trim() : 오른쪽 끝에 있는 공백을 없애는 역할.
        val email = email_edittext_register.text.toString().trim()
        val password = password_edittext_register.text.toString().trim()
        val birth = birthday_edittext_register.text.toString().trim()

        if(email.isEmpty()){
            email_edittext_register.error = "이메일을 입력해주세요."
            email_edittext_register.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_edittext_register.error = "이메일 형식이 올바르지 않습니다."
            email_edittext_register.requestFocus()
            return
        }

        if(password.isEmpty() || password.length < 6){
            password_edittext_register.error = "비밀번호는 6글자 이상이어야 합니다."
            password_edittext_register.requestFocus()
            return
        }
        if(birth.isEmpty() || birth.length < 8){
            birthday_edittext_register.error = "생년월일을 입력해주세요."
            birthday_edittext_register.requestFocus()
            return
        }

        Log.d("RegisterActivity", "Email is: " + email)
        Log.d("RegisterActivity", "Password: $password")
        progressbar.visibility = View.VISIBLE
        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                progressbar.visibility = View.GONE
                if (!it.isSuccessful) return@addOnCompleteListener
                saveUser()
                // else if successful
                Log.d("RegisterActivity", "Successfully created user with uid: ${it.result?.user?.uid}")
                val intent = Intent(applicationContext, LoginActivity::class.java)
                // TODO: .apply {
                //        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                //    }
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "이메일 주소가 중복되어있는지 확인해주세요. ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUser(){
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val selectedId = radioGroup.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(selectedId).text.toString()
        val birth = birthday_edittext_register.text.toString()
        val user = User(uid,birth,radioButton)
        ref.setValue(user)
            .addOnSuccessListener {

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
            .addOnFailureListener{

            }

    }


}
