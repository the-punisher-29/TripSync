package com.sk.tripsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
//
//
//class LoginActivity : AppCompatActivity() {
//    private var auth: FirebaseAuth? = null
//    private var googleSignInClient: GoogleSignInClient? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        // Initialize Firebase Auth
//        auth = FirebaseAuth.getInstance()
//
//        // Configure Google Sign-In
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id)) // Ensure this matches your client ID
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        val signInButton = findViewById<Button>(R.id.btn_google_sign_in)
//        signInButton?.setOnClickListener { signIn() }
//            ?: Log.e("MainActivity", "Sign-in button not found")
//    }
//
//    private fun signIn() {
//        val signInIntent = googleSignInClient!!.signInIntent
//        startActivityForResult(signInIntent, MainActivity.Companion.RC_SIGN_IN)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent
//        if (requestCode == MainActivity.Companion.RC_SIGN_IN) {
//            GoogleSignIn.getSignedInAccountFromIntent(data)
//                .addOnCompleteListener { task: Task<GoogleSignInAccount> ->
//                    try {
//                        val account =
//                            task.getResult(ApiException::class.java)
//                        firebaseAuthWithGoogle(account)
//                    } catch (e: ApiException) {
//                        // Google Sign-In failed
//                        Log.w("LoginActivity", "Google sign in failed", e)
//                    }
//                }
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
//        auth!!.signInWithCredential(GoogleAuthProvider.getCredential(acct.idToken, null))
//            .addOnCompleteListener(
//                this
//            ) { task: Task<AuthResult?> ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("MainActivity", "signInWithCredential:success")
//                    // Update UI with user info
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(
//                        "MainActivity",
//                        "signInWithCredential:failure",
//                        task.exception
//                    )
//                    // Update UI accordingly
//                }
//            }
//    }
//
//    companion object {
//        private const val RC_SIGN_IN = 9001
//    }
//}