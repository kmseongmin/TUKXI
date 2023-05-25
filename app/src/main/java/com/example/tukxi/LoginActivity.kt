package com.example.tukxi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: Button
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()
        loginBtn = findViewById<Button>(R.id.btnLogin)
        registerBtn = findViewById<Button>(R.id.btnRegister)
        emailEt = findViewById<EditText>(R.id.et_email)
        passwordEt = findViewById<EditText>(R.id.et_password)


        loginBtn.setOnClickListener {
            val email = emailEt.text.toString()
            val password = passwordEt.text.toString()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        if (user != null && user.isEmailVerified) {
                            // 이메일 확인 완료된 사용자
                            // 로그인 성공 처리
                            Toast.makeText(this,"로그인 성공",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // 이메일 확인이 완료되지 않은 사용자
                            Toast.makeText(this, "이메일 인증을 완료해야 증그인할 수 있습니다.", Toast.LENGTH_SHORT)
                                .show()
                            // 로그인 막는 처리
                        }
                    } else {
                        // 로그인 실패 처리
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}