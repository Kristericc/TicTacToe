package com.kristers.tictactoe

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.*
import androidx.core.view.isVisible
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Player vs Player activity
 * @param m: Game board 2d array (0 -  are empty fields /  1 - are Player Nr2 fields / -1 - are Player fields (Games owner))
 */

class PvP : AppCompatActivity() {
    var playerName: String? = null
    var playerName2: String? = null
    var isPlayerTurn: Boolean = false;

    lateinit var context: Context

    lateinit var pName: TextView
    lateinit var cName: TextView
    lateinit var turn: TextView
    lateinit var winLine: ImageView


    lateinit var tLayout: TableLayout
    lateinit var pSymbol2: String
    lateinit var pSymbol: String

    var m = Array(3) { Array(3) { 0 } }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()
        setView()
    }

    //Sets View and adds OnClickListener to Table Layout row fields
    @SuppressLint("SetTextI18n")
    fun setView() {
        setContentView(R.layout.activity_pv_p)

        context = this

        playerName = intent.getStringExtra("pName")
        playerName2 = intent.getStringExtra("playerName2")

        pName = findViewById(R.id.playerName)
        cName = findViewById(R.id.cName)
        winLine = findViewById(R.id.winLine)
        tLayout = findViewById(R.id.table)
        turn = findViewById(R.id.turn)

        //Random chooses who will have the first move
        val rnds = (1..2).random()
        if (rnds == 1) {
            pSymbol2 = "O"
            pSymbol = "X"
            isPlayerTurn = false

        } else {
            pSymbol2 = "X"
            pSymbol = "O"
            isPlayerTurn = true
        }

        pName.text = "$playerName: $pSymbol"
        cName.text = "$playerName2: $pSymbol2"

        if (isPlayerTurn) {
            turn.text = "$playerName`s turn"
        } else {
            turn.text = "$playerName2`s turn"
        }

        for (i in 0 until tLayout.childCount) {
            val row: TableRow = tLayout.getChildAt(i) as TableRow
            for (j in 0 until row.childCount) {
                val tV = row.getChildAt(j) as TextView
                tV.text = ""
                tV.setOnClickListener {
                    if (isPlayerTurn) {
                        if (tV.text.equals("") || !tV.text.equals(pSymbol2)) {
                            tV.text = pSymbol
                            m[i][j] = -1
                            val check = checkWin(m, true)
                            if (check.first) {
                                showWinLine(check.second)
                                GlobalScope.launch {
                                    delay(1500)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        showDialog(0.toByte())
                                    }, 100)
                                }
                            }
                        }
                        turn.text = "$playerName2`s turn"
                        isPlayerTurn = false
                    } else {
                        if (tV.text.equals("") || !tV.text.equals(pSymbol)) {
                            tV.text = pSymbol2
                            m[i][j] = 1
                            val check = checkWin(m, false)
                            if (check.first) {
                                showWinLine(check.second)
                                GlobalScope.launch {
                                    delay(1500)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        showDialog(1.toByte())
                                    }, 100)
                                }
                            }
                        }
                        turn.text = "$playerName`s turn"
                        isPlayerTurn = true
                    }
                    if (!checkIfEmptySpots(m)) {
                        GlobalScope.launch {
                            delay(1500)
                            Handler(Looper.getMainLooper()).postDelayed({
                                showDialog(2.toByte())
                            }, 100)
                        }
                    }
                }
            }
        }
    }

    /**
     *Adjusts win line according to row/column or diagonal
     * @param place:
     *              place.first = row(0)/column(1) or diagonal(2)
     *              place.second = which row/column or diagonal (0-2)
     */
    fun showWinLine(place: Pair<Int, Int>) {
        if (place.first == 1) {
            if (place.second == 0) {
                winLine.translationX = -300f
            } else if (place.second == 2) {
                winLine.translationX = +300f
            }
        } else if (place.first == 2) {
            winLine.rotation = 90f
            if (place.second == 0) {
                winLine.translationY = -300f
            } else if (place.second == 2) {
                winLine.translationY = +300f
            }
        } else {
            if (place.second == 1) {
                winLine.rotation = 45f
            } else if (place.second == 2) {
                winLine.rotation = -45f
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            winLine.isVisible = true
        }, 100);
    }

    fun resetGame() {
        m = Array(3) { Array(3) { 0 } }
        setView()
    }

    /**
     * Shows win/lose or draw dialog
     * @param k: 0 - Controller won/ 1 - its draw/ 2 - Player won
     */
    @SuppressLint("SetTextI18n")
    fun showDialog(k: Byte) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.win_dialog)
        val winText = dialog.findViewById<TextView>(R.id.winText)

        when (k) {
            0.toByte() -> winText.text = "Congratulations!\n$playerName wins!"
            1.toByte() -> winText.text = "Congratulations!\n$playerName2 wins!"
            else -> {
                winText.text = "Better Luck Next Time!\nIt`s Draw"
            }
        }

        val quitBtn = dialog.findViewById<Button>(R.id.quitBtn)
        quitBtn.setOnClickListener {
            dialog.dismiss()
            super.onBackPressed()
        }

        val resetBtn = dialog.findViewById<Button>(R.id.resetBtn)
        resetBtn.setOnClickListener {
            dialog.dismiss()
            resetGame()
        }
        dialog.show()
    }
}