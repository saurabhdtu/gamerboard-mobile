package com.gamerboard.live.test.game

import android.util.Log
import com.gamerboard.live.models.test.TestDataModel
import com.gamerboard.live.service.screencapture.AutoDebuggingHelper
import com.gamerboard.live.service.screencapture.VideoTestObj
import com.gamerboard.live.slack.SlackClient
import com.gamerboard.live.slack.data.Attachment
import com.gamerboard.live.slack.data.SlackRequestBody
import com.gamerboard.live.utils.createIfNotExists
import com.gamerboard.live.utils.removeAndCreate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class TestAutoDebuggingHelper(private val total: Int, private val lastPassedCount: Int = 0, private val lastFailedCount: Int = 0) : AutoDebuggingHelper(),
        KoinComponent {

    private val slackClient: SlackClient by inject()

    private var testDataModel: TestDataModel? = null

    private var _passedCount = lastPassedCount
    val passedCount get() =  _passedCount
    private var _failedCount = lastFailedCount
    val failedCount get() =  _failedCount
    var currentTest = 0

    companion object {
        private val TAG = TestAutoDebuggingHelper::class.java.simpleName
    }

    init {

    }

    fun setTestModel(testDataModel: TestDataModel) {
        //Delete existing test data file
        val resultFile = File(resultFilePath)

        resultFile.createIfNotExists()
        resultFile.appendText(" \n\n${currentTest + 1}/${total} Details : \n")
        resultFile.appendText("Test Folder Name : ${testDataModel.description}\n")

        this.testDataModel?.let { model ->
            try {
                //    if (model.groundTruth.exists()) model.groundTruth.delete()
                // if (model.videoFile.exists()) model.videoFile.delete()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        this.testDataModel = testDataModel

        currentTest++
    }

    override fun compareResult(
            groundTruthFilePath: String, outputFilePath: String, currentVideoTest: VideoTestObj,
    ): Boolean {
        val passed = super.compareResult(groundTruthFilePath, outputFilePath, currentVideoTest)
        Log.i(TAG, "comparison result ${currentVideoTest.videoTestName} : Passed ${passed}")
        if (passed) {
            _passedCount++
        } else {
            _failedCount++
        }
        return passed
    }

    override fun shareResultFile(allPassed: Boolean, file: File) {
        //Do nothing

    }


    fun uploadResultFile() {

        sendResultToSlack(File(resultFilePath))
    }

    fun log(msg: String) {
        val file = File(resultFilePath)
        file.createIfNotExists()
        file.appendText(msg)
    }

    private fun sendResultToSlack(resultFile: File) {
        val totalTestRun = _failedCount + _passedCount
        val testDate = SimpleDateFormat("dd-MMM-yyyy, hh:mm a").format(Calendar.getInstance().time)
        val description =
                "[ ${testDate}] : Test Summary,  ${_failedCount}/${totalTestRun} Failed,\n ${_passedCount}/${totalTestRun} Passed\n"
       Log.i(TAG, description)
        resultFile.appendText(description)
        println(resultFile.readText())

        _passedCount = 0
        _failedCount = 0
        val outputData = resultFile.readText(Charsets.UTF_8)
        deleteResultFile()

        slackClient.post(
                SlackRequestBody(
                        text = description, attachments = listOf(
                        Attachment(
                                fallback = description,
                                color = "#36a64f",
                                title = description,
                                filename = "TestSummary_${testDate}.txt",
                                type = "text",
                                text = outputData
                        )
                )
                )
        )


    }

    fun logError(errorMessage: String) {
        val file = File(resultFilePath)
        file.createIfNotExists()
        file.appendText("================ Test Failed ===================\n")
        file.appendText("Test Name : ${testDataModel?.name}\n")
        file.appendText("Test Description : ${testDataModel?.description}\n")
        file.appendText("Inputs : ${testDataModel?.input?.map { "${it.id} : ${it.username}" }}\n")
        file.appendText(errorMessage)
        file.appendText("================ Test Result End ===================\n")

        shareResultFile(false, file)
    }

    private fun deleteResultFile() {
        val file = File(resultFilePath)
        if (file.exists()) {
            file.renameTo(File(resultFilePath.replace("failed_tests", "failed_tests_${Date()}")))
        }
        file.removeAndCreate()
    }
}