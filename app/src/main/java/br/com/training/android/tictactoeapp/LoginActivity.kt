package br.com.training.android.tictactoeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
    }

    fun onLoginEvent(view: View) {
        loginToFirebase(editTextEmail.text.toString(), editTextPassword.text.toString())
    }

    fun loginToFirebase(email: String, pass: String) {
        mAuth!!.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {task ->
            if(task.isSuccessful) {
                Toast.makeText(applicationContext, "Successful login", Toast.LENGTH_LONG).show()

                val currentUser = mAuth!!.currentUser

                Log.d("TicTacToeDebuggingUser", "Current user is null? ${currentUser == null}")

                if (currentUser != null) {
                    val splitEmail = currentUser.email.toString().split("@")[0]

                    myRef.child("players/users").child(splitEmail).setValue(currentUser.uid)
                }

                loadMain()
            } else {
                Toast.makeText(applicationContext, "Login failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadMain()
    }

    private fun loadMain() {
        val currentUser = mAuth!!.currentUser

        if(currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)

            startActivity(intent)
        }
    }

}
