package com.kristers.tictactoe

fun checkIfEmptySpots(m: Array<Array<Int>>):Boolean{
    var hasEmptySpace = false
    for(i in m.indices) {
        for (j in m[0].indices) {
            if(m[i][j]==0){
                hasEmptySpace = true
            }
        }
    }
    if(hasEmptySpace){
        return true
    }
    return false
}