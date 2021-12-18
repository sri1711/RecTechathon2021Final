package com.asyncdevs.devforum

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class register_activity : AppCompatActivity() {
    private var gender: String?= null
    private var role: String? = null
    private lateinit var auth: FirebaseAuth
    private var rootNode: FirebaseDatabase? = null
    private var referenceDatabase: DatabaseReference? = null
    private var userID: String? = null
    private var SignInType : String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val name = findViewById<EditText>(R.id.etName)
        val mailID = findViewById<EditText>(R.id.et_Mail)
        val pass = findViewById<EditText>(R.id.et_pass)
        val cpass = findViewById<EditText>(R.id.et_cpass)
        val dept = findViewById<EditText>(R.id.deptName)


        SignInType = "" //intent.getStringExtra("SignIn")

        if(SignInType.equals("Google")){
            setUpViewForGoogleSignIn()
        }



        auth = Firebase.auth

        val genderLayout = findViewById<TextInputLayout>(R.id.GenderLayout)

        val genderType = arrayOf("Male", "Female", "Rather Not say")

        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            genderType
        )

        val editTextFilledExposedDropdown =
            findViewById<AutoCompleteTextView>(R.id.filled_exposed_dropdown)
        editTextFilledExposedDropdown.setAdapter(adapter)

        val designation = arrayOf("Student","Admin")
        val designationAdapter = ArrayAdapter(this,R.layout.dropdown_menu_popup_item,designation)
        val designationTextFilledExposedDropdown = findViewById<AutoCompleteTextView>(R.id.designation_exposed_dropdown)
        designationTextFilledExposedDropdown.setAdapter(designationAdapter)

        designationTextFilledExposedDropdown.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                role = s.toString()
            }
        })

        name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.length!! <5){
                    name.error = "Name should have more than 5 characters"
                }
            }

        })

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]{2,4}+".toRegex()
        mailID.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(!(s.toString().trim().matches(emailPattern) && s?.length!!>0)){
                    mailID.error = "Mail ID is invalid"
                }
            }

        })
        val passPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~\$^+=<>]).{8,20}\$".toRegex()


        findViewById<Button>(R.id.btn_signIn).setOnClickListener {
            val intent = Intent(this,login_activity::class.java)
            startActivity(intent)
        }


        findViewById<Button>(R.id.btn_signup).setOnClickListener {
            if((designationTextFilledExposedDropdown.text.isEmpty() || editTextFilledExposedDropdown.text.isEmpty() || name.text.isEmpty() || mailID.text.isEmpty() || pass.text.isEmpty() || cpass.text.isEmpty())  && !SignInType.equals("Google")){
                Toast.makeText(this,"Please fill the details", Toast.LENGTH_SHORT).show()
            }
            else if(name.error!=null){
                Toast.makeText(this,"Please enter the valid name", Toast.LENGTH_SHORT).show()
            }
            else if(mailID.error!=null){
                Toast.makeText(this,"Please enter valid Email", Toast.LENGTH_SHORT).show()
            }
            else if(!(pass.text.toString().matches(passPattern)) && !SignInType.equals("Google")){
                Toast.makeText(this,"Password is weak :(", Toast.LENGTH_SHORT).show()
            }
            else if(cpass.text.toString() != pass.text.toString()){
                Toast.makeText(this,"Password doesn't match with confirm password", Toast.LENGTH_SHORT).show()
            }
            else if(dept.text.toString()==null){
                Toast.makeText(this,"Please enter the valid Department name", Toast.LENGTH_SHORT).show()
            }
            else{
                if(!SignInType.equals("Google")){
                    Log.i("SRI1711", name.text.toString()+":"+mailID.text.toString()+":" + pass.text.toString() + ":" + role!! + ":"+ gender!! +":"+dept!!.text.toString())
                    performRegister(name.text.toString(),mailID.text.toString(),pass.text.toString(),role!!,gender!!,dept.text.toString())
                }
                else{
                    addDetailsToDb(
                        name.text.toString(),
                        auth.currentUser!!.uid.toString(),
                        mailID.text.toString(),
                        dept.text.toString()
                    )
//                    val intent = Intent(this,dashboardActivity::class.java)
//                    startActivity(intent)
                    finish()
                }
            }
        }

    }

    private fun addDetailsToDb(name: String, userID: String, email: String, dept: String){
        rootNode = FirebaseDatabase.getInstance()

        referenceDatabase = rootNode!!.reference.child("users/${userID}")
        referenceDatabase!!.child("Name").setValue(name)
        referenceDatabase!!.child("Gender").setValue(gender)
        referenceDatabase!!.child("Designation").setValue(role)
        referenceDatabase!!.child("Email").setValue(email)
        referenceDatabase!!.child("Dept").setValue(dept)
    }

    private fun performRegister(name: String,email: String, password: String,role: String,gender: String,dept: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    sendEmailVerification(user!!)
                    userID = task.result?.user?.uid.toString()
                    val email = user!!.email
                    addDetailsToDb(name,userID!!,email!!,dept)
                    val intent = Intent(this,login_activity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendEmailVerification(user: FirebaseUser){
        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Verification mail sent to ${user!!.email}", Toast.LENGTH_SHORT).show()
                    Log.d(ContentValues.TAG, "Email sent.")
                }
                else {
                    Log.e(ContentValues.TAG, "sendEmailVerification", task.exception);
                    Toast.makeText(this,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show();
                }
            }
    }

    private fun setUpViewForGoogleSignIn() {
        val name = findViewById<EditText>(R.id.etName)
        val mailID = findViewById<EditText>(R.id.et_Mail)
        val pass = findViewById<TextInputLayout>(R.id.Pass_Textinput)
        val cpass = findViewById<TextInputLayout>(R.id.cPass_Textinput)

        val designationTextFilledExposedDropdown = findViewById<AutoCompleteTextView>(R.id.designation_exposed_dropdown)

        val user = Firebase.auth.currentUser

        val useName = user?.displayName
        val userMail = user?.email

        name.setText("$useName")
        mailID.setText("$userMail")
        pass.visibility = View.GONE
        cpass.visibility = View.GONE

    }
}