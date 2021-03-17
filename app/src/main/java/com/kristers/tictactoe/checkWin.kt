package com.kristers.tictactoe

fun checkWin(m: Array<Array<Int>>, isPlayersTurn: Boolean): Pair<Boolean, Pair<Int, Int>>{
    var isMatch: Boolean = true
    for (i in m.indices){
        for(j in m[0].indices){
            if(isPlayersTurn) {
                if (m[i][j] == 0 || m[i][j] == 1) {
                    isMatch = false
                }
            }
            else{
                if(m[i][j]==0 || m[i][j]==-1){
                    isMatch = false
                }
            }
        }
        if(isMatch){
            println("rows")
            return Pair(true, Pair(2, i))
        }
        isMatch = true
    }
    for (j in m.indices) {
        for (i in m[0].indices) {
            if(isPlayersTurn) {
                if (m[i][j] == 0 || m[i][j] == 1) {
                    isMatch = false
                }
            }
            else {
                if (m[i][j] == 0 || m[i][j] == -1){ isMatch = false}
            }
        }
        if (isMatch) {
            println("column")

            return Pair(true, Pair(1, j))
        }
        isMatch = true
    }
    for (i in m.indices) {
        if(isPlayersTurn) {
            if (m[i][i] == 0 || m[i][i] == 1) {
                isMatch = false
            }
        }else{ if(m[i][i]==0 || m[i][i]==-1){ isMatch = false}}
    }
    if (isMatch) {
        println("diog1")

        return Pair(true, Pair(3, 2))
    }
    isMatch = true
    var n : Int = 0
    for (i in m.size-1 downTo 0) {
        if(isPlayersTurn){ if(m[i][n]==0 || m[i][n]==1){ isMatch = false}}
        else{ if(m[i][n]==0 || m[i][n]==-1){ isMatch = false}}
        n++
    }
    if (isMatch) {
        println("diog2")

        return Pair(true, Pair(3, 1))
    }
    return Pair(false, Pair(-1, -1))
}