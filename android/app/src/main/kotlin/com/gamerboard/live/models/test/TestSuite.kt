package com.gamerboard.live.models.test

data class TestSuite (
    val testData: ArrayList<TestDataModel> = arrayListOf(),
    val type : Type? = Type.VIDEO,
){
    enum class Type{
        IMAGE, VIDEO
    }
}