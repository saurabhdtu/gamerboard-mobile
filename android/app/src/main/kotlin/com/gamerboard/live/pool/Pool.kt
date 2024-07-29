package com.gamerboard.live.pool

abstract class Pool<T, P>(val size: Int, val factory: Factory<T>) {

    private val linkedList = HashMap<T, Boolean>(size)

    init {
        fill()
    }

    private fun fill() {
        repeat((0 until size).count()) {
            linkedList[factory.create()] = true
        }
    }

    /**
     * Get item from pool
     * @throws IndexOutOfBoundsException
     */

    fun get(parameter: P): T? {
        val findEmptySlot = linkedList.values.indexOfFirst { it }
        return poll(linkedList.keys.elementAtOrNull(findEmptySlot), parameter)?.also {
            linkedList[it] = false
        }
    }


    fun putBack(obj: T) {
        if (linkedList.containsKey(obj).not()) return
        linkedList[obj] = true
    }

    abstract fun poll(obj: T?, parameter: P): T?


    fun containsElement(item: T): Boolean {
        return linkedList.keys.contains(item)
    }

    fun elements(): List<T> {
        return linkedList.keys.toList()
    }

    fun clean() {
        linkedList.keys.forEach { cleanEach(it) }
        linkedList.clear()
    }

    abstract fun cleanEach(obj: T)
    interface Factory<T> {
        fun create(): T
        fun clean(data: T)
    }
}




