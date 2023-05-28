package com.example.tukxi

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser as FirebaseUser

class RegisterActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var password_Confirm: EditText
    private lateinit var name : EditText
    private lateinit var nickname: EditText
    private lateinit var accountNum : EditText
    private lateinit var phoneNum : EditText
    private lateinit var birth : EditText

    private lateinit var radiogroup_gender : RadioGroup
    private lateinit var btnMale : RadioButton
    private lateinit var btnFemale : RadioButton

    private lateinit var btnRegister: Button
    private lateinit var btnEmailCheck: Button
    private lateinit var btnNicknameCheck : Button
    private lateinit var btnBank : Button

    private lateinit var auth: FirebaseAuth

    val bankList = arrayOf("은행선택","국민은행", "신한은행","농협은행","기업은행")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //EditText 변수들
        email = findViewById(R.id.et_regi_email)
        password = findViewById(R.id.et_regi_pwd)
        password_Confirm = findViewById(R.id.et_regi_pwdConfirm)
        name = findViewById(R.id.et_AccountNum)
        nickname = findViewById(R.id.et_Nickname)
        accountNum = findViewById(R.id.et_AccountNum)
        birth = findViewById(R.id.et_Birth)
        phoneNum = findViewById(R.id.et_PhoneNum)

        //Button 변수들
        btnRegister = findViewById(R.id.btn_RegisterFinish)
        btnEmailCheck = findViewById(R.id.btn_checkEmail)
        btnMale = findViewById(R.id.btnGenderMale)
        btnFemale = findViewById(R.id.btnGenderFemale)
        btnNicknameCheck = findViewById(R.id.btn_NicknameCheck)
        radiogroup_gender = findViewById(R.id.rdg_gender)
        btnBank = findViewById(R.id.btn_Bank)

        //Firebase 변수
        auth = FirebaseAuth.getInstance()

        // 기타 변수
        var idCheck : Boolean = false
        var Gender : String = "None"
        var NicknameCheck : Boolean = false


        // 이메일 중복 검사 버튼
        btnEmailCheck.setOnClickListener {
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
                            Toast.makeText(this,"해당 이메일은 사용 가능합니다.",Toast.LENGTH_SHORT).show()
                            idCheck = true
                        }
                    } else {
                        // 이메일 중복 검사 실패
                        Toast.makeText(this, "이메일 중복 검사 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        //닉네임 중복 검사 버튼
        btnNicknameCheck.setOnClickListener{
            val nickname : String = nickname.text.toString()

            checkNickNameAvailability(nickname){isAvailable->
                if(isAvailable){
                    NicknameCheck = true
                    Toast.makeText(this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show()
                }
            }

        }
        // 은행 선택
        btnBank.setOnClickListener{
            val dlg = AlertDialog.Builder(this@RegisterActivity)
            dlg.setTitle("은행 선택")
            val bankList = bankList
            dlg.setIcon(R.drawable.taxi1_icon)
            dlg.setSingleChoiceItems(bankList, 0){dialog, which->
                btnBank.text = bankList[which]
            }
            dlg.setPositiveButton("확인"){dialog,which->
                Toast.makeText(this@RegisterActivity,"확인",Toast.LENGTH_SHORT).show()
            }
            dlg.show()
        }
        // 성별 라디오버튼
        radiogroup_gender.setOnCheckedChangeListener{group,checkedGender->
            when(checkedGender){
                R.id.btnGenderMale->{
                    Gender = "Male"
                }
                R.id.btnGenderFemale->{
                    Gender = "Female"
                }
            }
        }
        // 회원가입 버튼 -> 유저 정보 저장 및 해당 이메일로 인증 메일 전송
        btnRegister.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            val passwordConfirm = password_Confirm.text.toString()
            val name = name.text.toString()
            val nickname = nickname.text.toString()
            val accountNum = accountNum.text.toString()
            val birth = birth.text.toString()
            val phoneNum = phoneNum.text.toString()

                if(!idCheck){
                Toast.makeText(this,"중복된 이메일.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != passwordConfirm) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!NicknameCheck){
                Toast.makeText(this,"중복된 닉네임",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                saveUserData(email,password,name,nickname,accountNum,phoneNum,birth,Gender)
                createUserWithEmailAndPassword(email,password)
            }
        }
    }

            // 회원가입 기능 (FirebaseAuth에 유저 등록)
            private fun createUserWithEmailAndPassword(email: String, password: String) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // 회원가입 성공
                            val user: FirebaseUser? = auth.currentUser
                            user?.sendEmailVerification()
                                ?.addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT)
                                            .show()
                                        Toast.makeText(this, "입력한 이메일로 인증메일이 전송되었습니다. 확인해주세요",Toast.LENGTH_LONG).show()
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

    // 회원 정보 저장
    fun saveUserData(Email : String, Password: String, Name : String, Nickname : String, AccountNum : String, PhoneNum : String,Birth : String,Gender : String){
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "Email" to Email,
            "Password" to Password,
            "Name" to Name,
            "Nickname" to Nickname,
            "AccountNum" to AccountNum,
            "PhoneNum" to PhoneNum,
            "Birth" to Birth,
            "Gender" to Gender)
        db.collection("UserInformation")// "UserInformation" 데이터베이스에 저장
            .add(user)
    }
    // 닉네임 중복 검사
    fun checkNickNameAvailability(Nickname: String, callback:(Boolean)->Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("UserInformation")
            .whereEqualTo("Nickname", Nickname)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val isAvailable = querySnapshot.isEmpty
                callback(isAvailable)
            }
            .addOnFailureListener { e ->
                callback(false)
            }
    }
}