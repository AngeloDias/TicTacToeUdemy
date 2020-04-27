package br.com.training.android.tictactoeapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    private var myEmail: String? = null
    private var player1WinsCounts = 0
    private var player2WinsCounts = 0
    private var activePlayer = 1
    private var player1 = ArrayList<Int>()
    private var player2 = ArrayList<Int>()
    private val _users = "users"
    private val _request = "request"
    private var sessionID: String? = null
    private var playerSymbol: String? = null
    private val _playerOnline = "playerOnline"
    private var number = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle: Bundle = intent.extras!!
        myEmail = bundle.getString("email")

        incomingCall()
    }

    fun buClick(view:View){
        val buSelected = view as Button
        var cellId = 0

        when(buSelected.id){
            R.id.bu1 -> cellId = 1
            R.id.bu2 -> cellId = 2
            R.id.bu3 -> cellId = 3
            R.id.bu4 -> cellId = 4
            R.id.bu5 -> cellId = 5
            R.id.bu6 -> cellId = 6
            R.id.bu7 -> cellId = 7
            R.id.bu8 -> cellId = 8
            R.id.bu9 -> cellId = 9
        }

        myRef.child("playerOnline").child(sessionID!!).child(cellId.toString()).setValue(myEmail)
    }

    private fun playGame(cellId:Int, buSelected:Button){

        if( activePlayer == 1 ){
            buSelected.text = "X"

            buSelected.setBackgroundResource(R.color.blue)
            player1.add(cellId)

            activePlayer = 2

            autoPlay(cellId)

        }else{
            buSelected.text = "O"

            buSelected.setBackgroundResource(R.color.darkGreen)
            player2.add(cellId)

            activePlayer = 1

        }

        buSelected.isEnabled = false

        checkWinner()
    }

    private fun checkWinner() {
        var winer = -1

        // row 1
        if (player1.contains(1) && player1.contains(2) && player1.contains(3)) {
            winer = 1
        }
        if (player2.contains(1) && player2.contains(2) && player2.contains(3)) {
            winer = 2
        }

        // row 2
        if (player1.contains(4) && player1.contains(5) && player1.contains(6)) {
            winer = 1
        }
        if (player2.contains(4) && player2.contains(5) && player2.contains(6)) {
            winer = 2
        }

        // row 3
        if (player1.contains(7) && player1.contains(8) && player1.contains(9)) {
            winer = 1
        }
        if (player2.contains(7) && player2.contains(8) && player2.contains(9)) {
            winer = 2
        }

        // col 1
        if (player1.contains(1) && player1.contains(4) && player1.contains(7)) {
            winer = 1
        }
        if (player2.contains(1) && player2.contains(4) && player2.contains(7)) {
            winer = 2
        }

        // col 2
        if (player1.contains(2) && player1.contains(5) && player1.contains(8)) {
            winer = 1
        }
        if (player2.contains(2) && player2.contains(5) && player2.contains(8)) {
            winer = 2
        }

        // col 3
        if (player1.contains(3) && player1.contains(6) && player1.contains(9)) {
            winer = 1
        }
        if (player2.contains(3) && player2.contains(6) && player2.contains(9)) {
            winer = 2
        }

        if (winer == 1) {
            player1WinsCounts += 1
            Toast.makeText(this, "Player 1 win the game", Toast.LENGTH_LONG).show()
            restartGame()

        } else if (winer == 2) {
            player2WinsCounts += 1
            Toast.makeText(this, "Player 2 win the game", Toast.LENGTH_LONG).show()
            restartGame()
        }

    }

    private fun autoPlay(cellId: Int){

        val buSelected:Button? = when(cellId){
            1-> bu1
            2-> bu2
            3-> bu3
            4-> bu4
            5-> bu5
            6-> bu6
            7-> bu7
            8-> bu8
            9-> bu9
            else ->{ bu1}
         }

        if (buSelected != null) {
            playGame(cellId, buSelected)
        }
    }

    private fun restartGame(){

        activePlayer = 1
        player1.clear()
        player2.clear()

        for(cellId in 1..9){

            val buSelected:Button? = when(cellId){
                1-> bu1
                2-> bu2
                3-> bu3
                4-> bu4
                5-> bu5
                6-> bu6
                7-> bu7
                8-> bu8
                9-> bu9
                else ->{ bu1}
            }

            buSelected!!.text = ""
            buSelected.setBackgroundResource(R.color.whileBu)
            buSelected.isEnabled = true
        }

         Toast.makeText(this,"Player1: $player1WinsCounts, Player2: $player2WinsCounts", Toast.LENGTH_LONG).show()
    }

    fun btnRequestEvent(view: View) {
        val nick = editTextEmailPlay.text.toString().split("@")[0]

        if (nick.isNotEmpty()) {
            myRef.child(this._users).child(nick).child(this._request).push().setValue(myEmail)
            playerOnline(nick + myEmail.toString().split("@"[0]))

            playerSymbol = "X"
        } else {
            Toast.makeText(this, "Please, enter the user nick", Toast.LENGTH_SHORT).show()
        }

    }

    fun btnAcceptEvent(view: View) {
        val userEmail = editTextEmailPlay.text.toString().split("@")[0]

        myRef.child(this._users).child(userEmail).child(this._request).push().setValue(myEmail)
        playerOnline(userEmail + myEmail.toString().split("@"[0]))

        playerSymbol = "0"
    }

    private fun playerOnline(sessionID: String){
        this.sessionID = sessionID

        myRef.child(_playerOnline).removeValue()
        myRef.child(_playerOnline).child(sessionID).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                try {
                    player1.clear()
                    player2.clear()

                    val td = p0.value as HashMap<String, Any>

                    lateinit var value: String

                    for (key in td.keys) {
                        value = td[key] as String

                        if(value != myEmail) {
                            activePlayer = if(playerSymbol == "X") 1 else 2
                        } else {
                            activePlayer = if(playerSymbol == "X") 2 else 1
                        }

                        autoPlay(key.toInt())
                    }
                } catch(exc: Exception) {}
            }

        })
    }

    fun incomingCall() {
        val splitEmail = myEmail.toString().split("@")[0]

        myRef.child(this._users).child(splitEmail).child(this._request).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val td = p0.value as HashMap<String, Any>

                    lateinit var value: String

                    for (key in td.keys) {
                        value = td[key] as String

                        editTextEmailPlay.setText(value)

                        val notify = Notifications()

                        notify.notify(applicationContext, value + "want to play Tic-Tac-Toe", number)
                        number++
                        myRef.child(_users).child(splitEmail).child(_request)
                            .setValue(true)

                        break
                    }
                } catch(exc: Exception) {}
            }

        })

    }

}
