package com.example.tukxi

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser as FirebaseUser

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var password_Confirm: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnCheck: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        email = findViewById(R.id.et_Email)
        password = findViewById(R.id.et_Pwd)
        password_Confirm = findViewById(R.id.et_Pwd2)
        btnRegister = findViewById(R.id.btnRegisterFinish)
        btnCheck = findViewById(R.id.btn_Check_Email)
        auth = FirebaseAuth.getInstance()
        var idCheck = 0

        btnCheck.setOnClickListener {
            val email: String = email.text.toString()
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods ?: emptyList<String>()
                        if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                            // 이미 동일한 이메일로 가입된 계정이 존재함
                            Toast.makeText(this, "해당 이메일은 이미 가입된 계정이 있습니다.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            // 이메일 중복 검사 통과
                            idCheck = 1
                        }
                    } else {
                        // 이메일 중복 검사 실패
                        Toast.makeText(this, "이메일 중복 검사 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnRegister.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            val passwordConfirm = password_Confirm.text.toString()

            if(idCheck != 1){
                Toast.makeText(this,"중복된 이메일.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != passwordConfirm) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                createUserWithEmailAndPassword(email,password)
            }
        }
    }
            private fun createUserWithEmailAndPassword(email: String, password: String) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // 회원가입 성공
                            val user: FirebaseUser? = auth.currentUser
                            user?.sendEmailVerification()
                                ?.addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        Toast.makeText(this, "이메일 인증 완료.", Toast.LENGTH_SHORT)
                                            .show()
                                        finish()
                                    } else
                                        Toast.makeText(this, "이메일 인증 실패.", Toast.LENGTH_SHORT)
                                            .show()
                                }
                            Log.d("SignupActivity", "회원가입 성공: ${user?.email}")
                            // 추가 작업이 필요한 경우 여기에 작성
                        } else {
                            // 회원가입 실패
                            val exception = task.exception
                            if (exception is FirebaseAuthUserCollisionException) {
                                // 이미 가입된 이메일
                                Toast.makeText(this, "해당 이메일은 이미 가입된 계정이 있습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                // 기타 회원가입 실패 사유
                                Toast.makeText(
                                    this,
                                    "회원가입 실패: ${exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            }
        }