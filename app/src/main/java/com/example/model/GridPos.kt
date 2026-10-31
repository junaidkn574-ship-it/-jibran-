package com.example.model

data class GridPos(val r: Int, val c: Int) {
    fun isAdjacent(other: GridPos): Boolean {
        val dr = kotlin.math.abs(r - other.r)
        val dc = kotlin.math.abs(c - other.c)
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1)
    }
}
