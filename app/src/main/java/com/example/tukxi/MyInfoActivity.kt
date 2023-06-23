package com.example.tukxi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyInfoActivity :AppCompatActivity() {
    private lateinit var email: TextView
    private lateinit var name: TextView
    private lateinit var nickname: TextView
    private lateinit var bank: TextView
    private lateinit var phoneNum: TextView
    private lateinit var birth: TextView
    private lateinit var gender: TextView

    private lateinit var btnChangeNick: Button
    private lateinit var btnChangeBank: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myinfo)
        email = findViewById(R.id.tv_id)
        nickname = findViewById(R.id.tv_nickname)
        bank = findViewById(R.id.tv_bankname)
        phoneNum = findViewById(R.id.tv_phonenum)
        birth = findViewById(R.id.tv_birth)
        gender = findViewById(R.id.tv_gender)
        name = findViewById(R.id.tv_myname)

        btnChangeNick = findViewById(R.id.btn_ChangeNick)
        btnChangeBank = findViewById(R.id.btn_ChangeBank)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val uid = firebaseAuth.currentUser?.email
        val usersCollection = firestore.collection("UserInformation")
        val query = usersCollection.whereEqualTo("Email", uid)

        // 사용자 정보 불러오기
        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val userDocument = querySnapshot.documents[0]
                email.text = userDocument.getString("Email")
                nickname.text = userDocument.getString("Nickname")
                bank.text = userDocument.getString("Bank") + userDocument.getString(("AccountNum"))
                phoneNum.text = userDocument.getString("PhoneNum")
                birth.text = userDocument.getString("Birth")
                gender.text = userDocument.getString("Gender")
                name.text = userDocument.getString("Name")
            }
        }

        btnChangeNick.setOnClickListener {
            showChangeNicknameDialog()
        }
    }

    private fun showChangeNicknameDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.RoundDialog)
        dialogBuilder.setTitle("닉네임 변경")
        dialogBuilder.setIcon(R.drawable.taxi1_icon)
        val inputNick = EditText(this)
        inputNick.hint = "변경할 닉네임 입력"
        dialogBuilder.setView(inputNick)

        dialogBuilder.setPositiveButton("확인") { dialog, _ ->
            val newNickname = inputNick.text.toString()
            checkAndChangeNickname(newNickname)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun checkAndChangeNickname(newNickname: String) {
        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            val userRef = firestore.collection("UserInformation").document(uid)

            // Check if new nickname already exists in Firestore
            userRef.get().addOnSuccessListener { documentSnapshot ->
                val nicknameExists = documentSnapshot.getString("Nickname") == newNickname
                if (nicknameExists) {
                    // Show error message or take appropriate action
                    Toast.makeText(this,"해당 닉네임이 이미 존재합니다.",Toast.LENGTH_SHORT).show()
                } else {
                    // Update the nickname in Firestore
                    userRef.update("Nickname", newNickname)
                        .addOnSuccessListener {
                            // Update successful
                            // Update UI or perform any other actions
                            Toast.makeText(this,"닉네임 변경 완료",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            // Update failed
                            // Show error message or take appropriate action
                            Toast.makeText(this,"닉네임 변경 실패",Toast.LENGTH_SHORT).show()
                        }
                }
            }

        }
    }
}