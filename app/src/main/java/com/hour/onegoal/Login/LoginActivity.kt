package com.hour.onegoal.Login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.hour.onegoal.MainActivity
import com.hour.onegoal.R
import com.hour.onegoal.Util.login
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // TODO: progressbar show
        login_button.setOnClickListener{
            // trim() : 오른쪽 끝에 있는 공백을 없애는 역할.
            val email = email_edittext_login.text.toString().trim()
            val password = password_edittext_login.text.toString().trim()
            if(email.isEmpty()){
                email_edittext_login.error = "이메일을 입력해주십시오."
                email_edittext_login.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                email_edittext_login.error = "이메일 형식이 올바르지 않습니다."
                email_edittext_login.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty() || password.length < 6){
                password_edittext_login.error = "비밀번호를 확인 해 주십시오."
                password_edittext_login.requestFocus()
                return@setOnClickListener
            }

            signIn(email,password)
        }


        register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun signIn(email: String, password: String){
        Log.d(TAG, "signIn:$email")
        progressbar.visibility = View.VISIBLE
        if(!validateForm()){
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
            progressbar.visibility = View.GONE
            if(task.isSuccessful){
                progressbar.visibility = View.GONE
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "아이디와 비밀번호를 확인하여주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = email_edittext_login.text.toString()
        if (TextUtils.isEmpty(email)){
            email_edittext_login.error = "Required"
            valid = false
        } else {
            email_edittext_login.error = null
        }

        val password = password_edittext_login.text.toString()
        if(TextUtils.isEmpty(password)){
            password_edittext_login.error = "Required"
            valid = false
        } else {
            password_edittext_login.error = null
        }

        return valid
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser?.let {
            login()
        }
    }

    override fun onClick(v: View?) {
        val i = v?.id
        when (i) {
            R.id.login_button -> signIn(email_edittext_login.text.toString(), password_edittext_login.text.toString())
        }
    }


    companion object{
        private const val TAG = "EmailPassword"
    }

}
