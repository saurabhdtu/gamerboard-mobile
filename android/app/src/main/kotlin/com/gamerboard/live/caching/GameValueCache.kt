package com.gamerboard.live.caching

import android.util.Log
import android.util.SparseArray

class GameValueCache {

    companion object{
        private val TAG = GameValueCache::class.java.simpleName
    }
    private val cache = HashMap<String, String>()


    fun getValue(key : String) : String?{
        return cache.getOrDefault(key, null)
    }

    fun putValue(key : String, value : String){
       if(cache.contains(key).not()){
           cache[key] = value
       }
    }

    fun clear(){
        cache.clear()
    }
}