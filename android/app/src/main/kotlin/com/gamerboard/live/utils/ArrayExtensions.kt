package com.gamerboard.live.utils

import android.util.SparseArray

object ArrayExtensions {

    fun <T> SparseArray<T>.putIfAbsent(key: Int, value: T) {
        if (this[key] == null)
            this[key] = value
    }

    fun <T> SparseArray<T>.containsKey(key: Int): Boolean {
        var contains = false
        for (i in 0 until this.size())
            if (this.keyAt(i) == key) {
                contains = true
                break
            }
        return contains
    }

    fun <T> SparseArray<T>.keys(): MutableSet<Int> {
        val set = linkedSetOf<Int>()
        for (i in 0 until this.size())
            set.add(this.keyAt(i))
        return set
    }

    fun <T> SparseArray<T>.entries(): List<Pair<Int, T>> {
        val set = arrayListOf<Pair<Int, T>>()
        for (i in 0 until this.size())
            set.add(Pair(this.keyAt(i), this[this.keyAt(i)]))
        return set
    }
}