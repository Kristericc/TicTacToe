package com.kristers.tictactoe

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.*

/**
 * Main screen activity
 */

class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    var playerName: String? = null

    lateinit var pName: TextView
    lateinit var pvP: Button
    lateinit var pvC: Button
    lateinit var iOpenBtn: ImageView

    @SuppressLint("CommitPrefEdits", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        playerName = sharedPreferences.getString("playerName", null)

        setContentView(R.layout.activity_main)

        makeStatusBarTransparent()

        if (playerName == null) {
            setPlayersName()
        } else {
            pName = findViewById<TextView>(R.id.pName)
            pName.text = "Hello, $playerName"
        }

        pvP = findViewById(R.id.pvpBtn)
        pvC = findViewById(R.id.pvcBtn)
        iOpenBtn = findViewById(R.id.infoOpenBtn)

        //On click PvC activity is called. Passes player name
        pvC.setOnClickListener {
            val intent = Intent(this@MainActivity, PvC::class.java).apply {
                putExtra("pName", playerName)
            }
            startActivity(intent)
        }

        //On click PvC activity is called. Asks for second Players name. Passes both names
        pvP.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.player_name_input)

            val nameText = dialog.findViewById<TextView>(R.id.enterNameText)
            nameText.text = "Enter 2nd Players name"

            val saveBtn = dialog.findViewById<Button>(R.id.saveBtn)
            saveBtn.text = "Continue"
            saveBtn.setOnClickListener {
                val textView = dialog.findViewById<EditText>(R.id.editTextTextPersonName)
                if (textView.text.length > 0) {
                    val intent = Intent(this@MainActivity, PvP::class.java).apply {
                        putExtra("pName", playerName)
                        putExtra("playerName2", textView.text.toString())
                    }
                    startActivity(intent)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Valid Player name is required", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }

        //Info dialog opens up. Can change name from there
        iOpenBtn.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.info_dialog)

            val closeBtn = dialog.findViewById<Button>(R.id.closeBtn)
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

            val changeNameBtn = dialog.findViewById<Button>(R.id.changeName)
            changeNameBtn.setOnClickListener {
                dialog.dismiss()
                setPlayersName()
            }
            dialog.show()
        }
    }

    //Sets player name
    @SuppressLint("SetTextI18n")
    fun setPlayersName() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.player_name_input)

        val saveBtn = dialog.findViewById<Button>(R.id.saveBtn)
        saveBtn.setOnClickListener {
            val textView = dialog.findViewById<EditText>(R.id.editTextTextPersonName)
            if (textView.text.length > 0) {
                playerName = textView.text.toString()
                pName = findViewById<TextView>(R.id.pName)
                pName.text = "Hello, $playerName"
                editor.putString("playerName", playerName)
                editor.commit()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Valid Player name is required", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
}