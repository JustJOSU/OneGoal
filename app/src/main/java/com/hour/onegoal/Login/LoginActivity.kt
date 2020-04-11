package com.hour.onegoal.Login

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.hour.onegoal.MainActivity
import com.hour.onegoal.R
import com.hour.onegoal.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // buttons
        login_button.setOnClickListener(this)


        register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun signIn(email: String, password: String){
        Log.d(TAG, "signIn:$email")
        if(!validateForm()){
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
            if(task.isSuccessful){
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT).show()
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
        val currentUser = auth.currentUser
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
