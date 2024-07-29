package com.gamerboard.live.models.test

import com.gamerboard.live.service.screencapture.Copyable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

data class TestDataModel (
    val name : String,
    val packageName : String,
    val input: List<TestGameInput>,
    val groundTruth : File,
    val description : String,
    val videoFile : File,
) : Copyable<TestDataModel>{
    var index : Int? = 0
    override fun copyObj(): TestDataModel {
        return TestDataModel(
            name = name, input =  input, groundTruth =  groundTruth, description =  description, videoFile =  videoFile, packageName = packageName
        )
    }

    override fun toString(): String {
        return "Name: ${name},Description : ${description}, Input ${Json.encodeToString(input)}\ngroundTruth : ${groundTruth.path}, File Paht ${videoFile.path}"
    }
}