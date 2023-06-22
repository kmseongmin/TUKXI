package com.example.tukxi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: Button

    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText

    private lateinit var findPwdTv : TextView

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findPwdTv = findViewById(R.id.findPassword)
        // 버튼
        loginBtn = findViewById(R.id.btnLogin)
        registerBtn = findViewById(R.id.btnRegister)

        // EditText
        emailEt = findViewById(R.id.et_email)
        passwordEt = findViewById(R.id.et_password)
        // Firebase
        firebaseAuth = FirebaseAuth.getInstance()

        //Login 버튼 눌렀을 때.
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
                            val intent = Intent(this, MainRoomActivity::class.java)

                            startActivity(intent)
                        } else {
                            // 이메일 확인이 완료되지 않은 사용자
                            Toast.makeText(this, "이메일 인증이 완료되어야 로그인할 수 있습니다.", Toast.LENGTH_SHORT)
                                .show()
                            // 로그인 막는 처리
                        }
                    } else {
                        // 로그인 실패 처리
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        //회원가입 버튼 눌렀을 때
        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        //비밀번호 찾기 버튼 눌렀을 때
        findPwdTv.setOnClickListener {
            val builder = AlertDialog.Builder(this,R.style.RoundDialog)
            builder.setTitle("비밀번호 찾기")
            builder.setIcon(R.drawable.taxi1_icon)


            val inputEmail = EditText(this)
            inputEmail.hint = "이메일 입력"
            builder.setView(inputEmail)

            builder.setPositiveButton("확인") { dialog, _ ->
                val email = inputEmail.text.toString().trim()
                if (email.isNotEmpty()) {
                    firebaseAuth.fetchSignInMethodsForEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val signInMethods = task.result?.signInMethods
                                if (signInMethods.isNullOrEmpty()) {
                                    Toast.makeText(this, "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    firebaseAuth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener { resetTask ->
                                            if (resetTask.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "입력하신 이메일로 비밀번호 재설정 이메일을 보냈습니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "비밀번호 재설정 이메일을 보내는데 실패했습니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            } else {
                                Toast.makeText(this, "이메일 확인 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "이메일을 입력하여 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }


    }
}