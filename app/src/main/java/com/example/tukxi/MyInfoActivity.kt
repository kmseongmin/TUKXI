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
    private lateinit var accountNum : TextView

    private lateinit var btnChangeNick: Button
    private lateinit var btnChangeAccountNum: Button
    private lateinit var btnChangeBank : Button

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
        accountNum = findViewById(R.id.tv_AccountNum)

        btnChangeNick = findViewById(R.id.btn_ChangeNick)
        btnChangeAccountNum = findViewById(R.id.btn_ChangeAccountNum)
        btnChangeBank = findViewById(R.id.btn_ChangeBank)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val userEmail = firebaseAuth.currentUser?.email
        val usersCollection = firestore.collection("UserInformation")
        val query = usersCollection.whereEqualTo("Email", userEmail)

        // 사용자 정보 불러오기
        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val userDocument = querySnapshot.documents[0]
                email.text = userDocument.getString("Email")
                nickname.text = userDocument.getString("Nickname")
                bank.text = userDocument.getString("Bank")
                accountNum.text = userDocument.getString("AccountNum")
                phoneNum.text = userDocument.getString("PhoneNum")
                birth.text = userDocument.getString("Birth")
                gender.text = userDocument.getString("Gender")
                name.text = userDocument.getString("Name")
            }
        }
        btnChangeNick.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this, R.style.RoundDialog)
            dialogBuilder.setTitle("닉네임 변경")
            dialogBuilder.setIcon(R.drawable.taxi1_icon)
            val inputNick = EditText(this)
            inputNick.hint = "변경할 닉네임 입력"
            dialogBuilder.setView(inputNick)
            dialogBuilder.setPositiveButton("확인") { dialog, _ ->
                val newNickname = inputNick.text.toString()
                query.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val dupQuery = usersCollection.whereEqualTo("Nickname", newNickname)
                                .get()
                                .addOnSuccessListener { dupDoc ->
                                    if (dupDoc.isEmpty) {
                                        val documentRef = usersCollection.document(document.id)
                                        nickname.text=newNickname
                                        documentRef.update("Nickname", newNickname)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    this,
                                                    "닉네임 변경 성공",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    this,
                                                    "닉네임 변경 실패",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    } else {
                                        Toast.makeText(this, "중복된 닉네임이 존재합니다.", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                        }
                        dialog.dismiss()
                    }
            }
            dialogBuilder.show()
        }

        btnChangeAccountNum.setOnClickListener {
            val selectedBankIndex = intArrayOf(0)
            val dialogBuilder = AlertDialog.Builder(this, R.style.RoundDialog)
            dialogBuilder.setTitle("계좌번호 변경")
            dialogBuilder.setIcon(R.drawable.taxi1_icon)
            val accountNumberEditText = EditText(this)
            accountNumberEditText.hint = "계좌번호 입력"
            dialogBuilder.setView(accountNumberEditText)

            dialogBuilder.setPositiveButton("확인") { dialog, _ ->
                val newAccountNum = accountNumberEditText.text.toString()
                query.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val dupQuery = usersCollection.whereEqualTo("AccountNum", newAccountNum)
                                .get()
                                .addOnSuccessListener { dupDoc ->
                                    if (dupDoc.isEmpty) {
                                        val documentRef = usersCollection.document(document.id)
                                        accountNum.text=newAccountNum
                                        documentRef.update("AccountNum", newAccountNum)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    this,
                                                    "계좌번호 변경 성공",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    this,
                                                    "계좌번호 변경 실패",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    } else {
                                        Toast.makeText(this, "중복된 계좌번호가 존재합니다.", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                        }
                        dialog.dismiss()
                    }
            }
            dialogBuilder.setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            dialogBuilder.show()
        }
        btnChangeBank.setOnClickListener{
            val dialogBuilder = AlertDialog.Builder(this,R.style.RoundDialog)
            dialogBuilder.setTitle("은행 선택")
            dialogBuilder.setIcon(R.drawable.taxi1_icon)

            val banks = arrayOf("은행선택","국민은행", "신한은행","농협은행","기업은행")
            dialogBuilder.setItems(banks){dialog,which->
                bank.text = banks[which]
                query.get().addOnSuccessListener { docs->
                    if(!docs.isEmpty){
                        val doc = docs.documents[0]
                        doc.reference.update("Bank",banks[which])
                            .addOnSuccessListener {
                                Toast.makeText(this,"은행 변경 성공",Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener{
                                Toast.makeText(this,"변경 실패",Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                    .addOnFailureListener{
                        Toast.makeText(this,"오류 발생",Toast.LENGTH_SHORT).show()
                    }
            }
            dialogBuilder.setNegativeButton("취소"){dialog,_->
                dialog.dismiss()
            }
            dialogBuilder.show()
        }


    }
}