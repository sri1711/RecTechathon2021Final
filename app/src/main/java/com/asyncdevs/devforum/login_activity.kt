package com.asyncdevs.devforum


import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.asyncdevs.devforum.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class login_activity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    var googleSignInClient : GoogleSignInClient? = null
    var RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val mbtn_signIn = findViewById<com.google.android.material.button.MaterialButton>(R.id.mbtn_signIn)

        auth = Firebase.auth

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mbtn_signIn.setOnClickListener {
            val email = findViewById<EditText>(R.id.etUserId)
            val password = findViewById<EditText>(R.id.etPassword)
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]{2,4}+".toRegex()
            if (email.text.toString().isEmpty()){
                email.setError("This field can not be blank")
            }
            else if(!email.text.toString().trim().matches(emailPattern)){
                email.setError("Enter a proper mail Id")
            }
            else{
                if (password.text.toString().isEmpty()){
                    password.setError("This field can not be blank")
                }
                else{
                    //mProgress!!.show()
                    validateLogin(email.text.toString(), password.text.toString())
                }
            }

        }

        findViewById<Button>(R.id.btForgotPassword).setOnClickListener {
            // TODO: 18-Dec-21
//            val intent = Intent(this, forgotPasswordActivity::class.java)
//            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_signup).setOnClickListener {
            // TODO: 18-Dec-21
//            val intent = Intent(this, RegisterScreen::class.java)
//            startActivity(intent)
        }

        findViewById<Button>(R.id.mbtn_GsignIn).setOnClickListener {
            signIn()
        }


    }

    private fun signIn() {
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }

    }

    private fun validateLogin(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    // mProgress!!.dismiss()
                    user = auth.currentUser!!
                    if(user.isEmailVerified){
                        // TODO: 18-Dec-21
//                        val intent = Intent(this,dashboardActivity::class.java)
//                        intent.flags  = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this,"Verify your email sent to "+ user.email, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    //mProgress!!.dismiss()
                    Toast.makeText(
                        baseContext, "Authentication failed \n ${task.exception.toString().substring(task.exception.toString().indexOf(":")+2,task.exception.toString().length)}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    LoginSetup(user!!)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
    }

    private fun LoginSetup(user : FirebaseUser){
        val ref = FirebaseDatabase.getInstance()
            .getReference("users/${user.uid}")
        ref?.addListenerForSingleValueEvent(object  : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    // TODO: 18-Dec-21
//                    val intent = Intent(this@LoginScreen,dashboardActivity::class.java)
//                    intent.putExtra("SignIn","Google")
//                    intent.flags  = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    startActivity(intent)
                }
                else{
                    // TODO: 18-Dec-21
//                    val intent = Intent(this@LoginScreen,RegisterScreen::class.java)
//                    intent.putExtra("SignIn","Google")
//                    intent.flags  = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    startActivity(intent)
                }
            }
        })
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if(currentUser != null){
            // TODO: 18-Dec-21
//            val intent = Intent(this,dashboardActivity::class.java)
            intent.flags  = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}