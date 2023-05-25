/*package com.example.tukxi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.tukxi.databinding.ActivityMainBinding.inflate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private var mFirebaseAuth: FirebaseAuthException? = null
    private var mDatabaseRef: DatabaseReference? = null
    private var etId: EditText? = null
    private  var etPwd:EditText? = null
    private var btnRegister: Button? = null
    private  var btnLogin:android.widget.Button? = null
    private  var btnFindIdPw:android.widget.Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = inflate(layoutInflater)
        setContentView(binding.root)

        etId = findViewById(R.id.et_email)
        etPwd = findViewById(R.id.et_pw)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        btnFindIdPw = findViewById(R.id.btnFindIdPw)
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().reference

        btnLogin.setOnClickListener(View.OnClickListener {
            val strId = etId.getText().toString()
            val strPw = etPwd.getText().toString()
            mFirebaseAuth.signInWithEmailAndPassword(strId, strPw)
                .addOnCompleteListener(this@LoginActivity,
                    OnCompleteListener<Any?> { task ->
                        if (task.isSuccessful) {
                            //로그인 성공
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    })
        })

    }
}