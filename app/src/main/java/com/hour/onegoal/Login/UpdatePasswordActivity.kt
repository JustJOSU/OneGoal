package com.hour.onegoal.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.hour.onegoal.R
import com.hour.onegoal.Util.toast
import kotlinx.android.synthetic.main.activity_update_password.*

class UpdatePasswordActivity : AppCompatActivity() {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)
        layoutPassword.visibility = View.VISIBLE
        layoutUpdatePassword.visibility = View.GONE

        button_authenticate.setOnClickListener {

            val password = edit_text_password.text.toString().trim()

            if (password.isEmpty()) {
                edit_text_password.error = "Password required"
                edit_text_password.requestFocus()
                return@setOnClickListener
            }


            currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                progressbar.visibility = View.VISIBLE
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        progressbar.visibility = View.GONE
                        when {
                            task.isSuccessful -> {
                                layoutPassword.visibility = View.GONE
                                layoutUpdatePassword.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException -> {
                                edit_text_password.error = "Invalid Password"
                                edit_text_password.requestFocus()
                            }
                            else -> toast(task.exception?.message!!)
                        }
                    }
            }

        }

        button_update.setOnClickListener {

            val password = edit_text_new_password.text.toString().trim()

            if(password.isEmpty() || password.length < 6){
                edit_text_new_password.error = "atleast 6 char password required"
                edit_text_new_password.requestFocus()
                return@setOnClickListener
            }

            if(password != edit_text_new_password_confirm.text.toString().trim()){
                edit_text_new_password_confirm.error = "password did not match"
                edit_text_new_password_confirm.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let{ user ->
                progressbar.visibility = View.VISIBLE
                user.updatePassword(password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            val intent = Intent(this,
                                ProfileActivity::class.java).apply{
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                        }else{
                             toast(task.exception?.message!!)
                        }
                    }
            }
        }

    }
}
