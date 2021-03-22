package com.kristers.tictactoe

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Player vs Controller activity
 * @param m: Game board 2d array (0 -  are empty fields /  1 - are Controller fields / -1 - are Player fields)
 */


class PvC : AppCompatActivity() {

    var playerName: String? = null
    var isPlayerTurn: Boolean = false;

    lateinit var pName: TextView
    lateinit var cName: TextView
    lateinit var turn: TextView
    lateinit var winLine: ImageView

    lateinit var tLayout: TableLayout
    lateinit var cSymbol: String
    lateinit var pSymbol: String

    lateinit var context: Context

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
        setContentView(R.layout.activity_pv_c)

        playerName = intent.getStringExtra("pName")

        context = this

        pName = findViewById(R.id.playerName)
        cName = findViewById(R.id.cName)
        tLayout = findViewById(R.id.table)
        turn = findViewById(R.id.turn)
        winLine = findViewById(R.id.winLine)

        //Random chooses who will have the first move
        val rnds = (1..2).random()
        if (rnds == 1) {
            cSymbol = "O"
            pSymbol = "X"
            isPlayerTurn = false

        } else {
            cSymbol = "X"
            pSymbol = "O"
            isPlayerTurn = true
        }

        pName.text = "$playerName: $pSymbol"
        cName.text = "Contoller: $cSymbol"

        if (isPlayerTurn) {
            turn.text = "Your turn"
        } else {
            turn.text = "Controller`s turn"
        }

        for (i in 0 until tLayout.childCount) {
            val row: TableRow = tLayout.getChildAt(i) as TableRow
            for (j in 0 until row.childCount) {
                val tV = row.getChildAt(j) as TextView
                tV.text = ""
                tV.setOnClickListener {
                    if (isPlayerTurn) {
                        GlobalScope.launch {
                            if (tV.text.equals("") || !tV.text.equals(cSymbol)) {
                                tV.text = pSymbol
                                m[i][j] = -1
                                val check = checkWin(m, true)
                                if (check.first) {
                                    showWinLine(check.second)
                                    delay(1500)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        showDialog(2.toByte())
                                    }, 100);

                                    return@launch
                                }
                            }
                            isPlayerTurn = false
                            Handler(Looper.getMainLooper()).postDelayed({
                                turn.text = "Controller`s turn"
                            }, 100);
                            delay(1500)
                            controllerThinking()
                            if (!checkIfEmptySpots(m)) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    showDialog(1.toByte())
                                }, 100)
                            }
                        }
                    }
                }
            }
        }
        if (!isPlayerTurn) {
            controllerThinking()
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

    //Checks If Controller can win in 1 move, checks if player will win in 1 move if neither then checks if controller will win in 2 moves, else will return false
    fun checkingMoves(): Boolean {
        var tmpFieldArray = m
        val checkAllArrays = hashMapOf<Int, Any>()
        val savePositions = hashMapOf<Int, Any>()
        var n: Int = 0
        for (i in m.indices) {
            for (j in m[0].indices) {
                if (tmpFieldArray[i][j] == 0) {
                    tmpFieldArray[i][j] = 1
                    val check = checkWin(m, false)
                    if (check.first) {
                        showWinLine(check.second)
                        val row = tLayout.getChildAt(i) as TableRow
                        val tV = row.getChildAt(j) as TextView
                        tV.text = cSymbol
                        m[i][j] = 1
                        GlobalScope.launch {
                            delay(1500)
                            Handler(Looper.getMainLooper()).postDelayed({
                                showDialog(0.toByte())
                            }, 100);
                        }
                        return true
                    }
                    tmpFieldArray[i][j] = 0
                }
            }
        }
        for (i in m.indices) {
            for (j in m[0].indices) {
                if (tmpFieldArray[i][j] == 0) {
                    tmpFieldArray[i][j] = -1
                    if (checkWin(tmpFieldArray, true).first) {
                        val row = tLayout.getChildAt(i) as TableRow
                        val tV = row.getChildAt(j) as TextView
                        m[i][j] = 1
                        tV.text = cSymbol
                        return true
                    } else {
                        tmpFieldArray[i][j] = 1
                        val posArray = ArrayList<Pair<Int, Int>>()
                        for (o in tmpFieldArray.indices) {
                            for (l in tmpFieldArray[0].indices) {
                                posArray.add(Pair(i, j))
                            }
                        }
                        savePositions[n] = posArray
                        checkAllArrays[n] = tmpFieldArray
                        n++
                    }
                    tmpFieldArray[i][j] = 0
                }
            }
        }
        for (k in 0 until checkAllArrays.size) {
            var pos = 0
            tmpFieldArray = checkAllArrays[k] as Array<Array<Int>>
            for (i in tmpFieldArray.indices) {
                for (j in tmpFieldArray[0].indices) {
                    if (tmpFieldArray[i][j] == 0) {
                        tmpFieldArray[i][j] = 1
                        if (checkWin(tmpFieldArray, false).first) {
                            val array = savePositions[k] as ArrayList<Pair<Int, Int>>
                            val row = tLayout.getChildAt(array[pos].first) as TableRow
                            val tV = row.getChildAt(array[pos].second) as TextView
                            m[array[pos].first][array[pos].second] = 1
                            tV.text = cSymbol
                            return true
                        }
                        tmpFieldArray[i][j] = 0
                        pos++
                    }
                }
            }
        }
        return false
    }

    //if checkingMoves() returns false, then controller will put his move in random free space
    fun randomMove() {
        val i = (0..2).random()
        val j = (0..2).random()
        if (m[i][j] == 0) {
            val row = tLayout.getChildAt(i) as TableRow
            val tV = row.getChildAt(j) as TextView
            m[i][j] = 1
            tV.text = cSymbol
        } else {
            randomMove()
        }
    }

    @SuppressLint("SetTextI18n")
    fun controllerThinking() {
        if(checkIfEmptySpots(m)) {
            isPlayerTurn = false
            val thoughtMoves = checkingMoves()
            isPlayerTurn = false
            if (!thoughtMoves) {
                randomMove()
            }
            Handler(Looper.getMainLooper()).postDelayed({
                turn.text = "Your turn"
            }, 100);

            isPlayerTurn = true
        }
    }

    /**
     * Shows win/lose or draw dialog
     * @param k: 0 - Controller won/ 1 - its draw/ 2 - Player won
     */
    @SuppressLint("SetTextI18n")
    fun showDialog(k: Byte) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.win_dialog)
        val winText = dialog.findViewById<TextView>(R.id.winText)

        when (k) {
            0.toByte() -> winText.text = "Better Luck Next Time!\nController Wins"
            1.toByte() -> winText.text = "Better Luck Next Time!\nIt`s Draw"
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
    //
}