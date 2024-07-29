package com.gamerboard.live.service.screencapture

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.MainActivity
import com.gamerboard.live.common.IntentRequestCode
import com.gamerboard.live.common.VideoTestConstants
import com.gamerboard.live.fragment.GameResponse
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants.UNKNOWN
import com.gamerboard.live.gamestatemachine.showToast
import com.gamerboard.live.models.db.GameInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import kotlin.math.max

//var current_test_path: String = "/sdcard/Android/data/com.gamerboard.live.test/files/Download/test/video/"
//var vdoName = 1


@Serializable
data class VideoTestObj(
    val videoTestName: String,
    val videoTestFilePath: String,
    val groundTruthFilePath: String,
    val outputDataFilePath: String,
)

open class AutoDebuggingHelper : KoinComponent {

    private val context: Context by inject()
    protected val resultFilePath =
        "${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/tests_output/failed_tests.txt"

    @Serializable
    data class ExpectedData(
        val id: Int = 0,
        var userId: String?,
        var valid: Boolean?,
        var rank: String?,
        var gameInfo: String?,
        var kills: String?,
        var teamRank: String?,
        var initialTier: String?,
        var finalTier: String?,
        var endTimestamp: String?,
        var startTimeStamp: String?,
        var gameId: String?,
        var synced: Int? = 0,
        var metaInfoJson: String?,
        var serverGameId: Int? = null,
        var serverUserId: Int? = null,
        var squadScoring: String? = null
    ) {
        @Serializable
        data class squadScore(
            var username: String?,
            var kills: Int?
        )

        private val context: Context by inject<Context>(Context::class.java)
        fun compare(toObj: ExpectedData): String? {
            if (rank != toObj.rank)
                return ("Rank did not match, expected $rank, found ${toObj.rank}")

            if (kills != toObj.kills)
                return ("Kills did not match, expected $kills, found ${toObj.kills}")
            /*  if (initialTier?.lowercase() != toObj.initialTier?.lowercase())
                  return ("InitialTier did not match, expected $initialTier, found ${toObj.initialTier}")*/
            // assert this only if the Auto ML is false
            //Note : This commented out due to the known issue where roman number II is read as I and III as II
            /*if ((metaInfoJson==null || metaInfoJson == UNKNOWN)  && finalTier?.lowercase() != toObj.finalTier?.lowercase())
                return ("FinalTier did not match, expected $finalTier, found ${toObj.finalTier}")*/
            if (gameInfo == null && toObj.gameInfo != UNKNOWN)
                return ("Game info was found to be null, expected $gameInfo, found ${toObj.gameInfo}")

            /*if (metaInfoJson != toObj.metaInfoJson)
                return ("meta info json did not match, expected $metaInfoJson, found ${toObj.metaInfoJson}")*/

            if (gameInfo == null)
                return null
            if (toObj.gameInfo == null)
                return null

            val gameInfo: GameInfo = Json.decodeFromString(gameInfo!!)
            val toGameInfo: GameInfo = Json.decodeFromString(toObj.gameInfo!!)

            if (gameInfo.type.lowercase() != toGameInfo.type.lowercase())
                return ("Game Info type did not match, expected ${gameInfo.type}, found ${toGameInfo.type}")
            if (gameInfo.group.lowercase() != toGameInfo.group.lowercase())
                return ("Game Info group did not match, expected ${gameInfo.group}, found ${toGameInfo.group}")
            if (gameInfo.mode.lowercase() != toGameInfo.mode.lowercase())
                return ("Game Info mode did not match, expected ${gameInfo.mode}, found ${toGameInfo.mode}")
            if (gameInfo.view.lowercase() != toGameInfo.view.lowercase())
                return ("Game Info view did not match, expected ${gameInfo.view}, found ${toGameInfo.view}")
//            return null

            if (squadScoring == null)
                return null
            if (toObj.squadScoring == null)
                return null

            val squadScores = Json.decodeFromString<List<squadScore>>(squadScoring!!)
            val expectedSquadScores = Json.decodeFromString<List<squadScore>>(toObj.squadScoring!!)


            if (squadScores.size == expectedSquadScores.size && squadScores.size > 1) {
                for (i in expectedSquadScores.indices) {
                    val findUsername = squadScores.firstOrNull {
                        LabelUtils.editDistance(
                            it.username!!,
                            expectedSquadScores[i].username!!
                        ).also { distance ->
                            Log.i("Compare", "Edit distance ${it.username} =-== $distance")
                        } < max(3, it.username!!.length / 2)
                    }
                        ?: return ("Squad team member ${squadScores[i].username} is not present, expected ${expectedSquadScores[i].username}, not found")

                    if (findUsername.kills != expectedSquadScores[i].kills)
                        return ("Kills of username ${expectedSquadScores[i].username} did not match, expected ${expectedSquadScores[i].kills}, found ${findUsername.kills}")

                    if (LabelUtils.editDistance(
                            findUsername.username!!,
                            expectedSquadScores[i].username!!
                        ) > max(3, expectedSquadScores[i].username!!.length / 2)
                    )
                        return ("Username of  ${expectedSquadScores[i].username} did not match, expected ${expectedSquadScores[i].username}, found ${findUsername.username}")
                }
                if (teamRank != toObj.teamRank)
                    return ("TeamRank did not match, expected $teamRank, found ${toObj.teamRank}")
            }

            return null

        }
    } // game ID

    open fun callForVideoTestOnFile(ctx: Context?, videoTests: ArrayList<VideoTestObj>) {
        if (videoTests.isEmpty())
            return

        val currentVideoTest = videoTests.first()
        videoTests.remove(currentVideoTest)

        val file = File(currentVideoTest.videoTestFilePath)
        if (!file.exists()) {
            showToast("File not exist", force = true)
            return
        }

        val uri = FileProvider.getUriForFile(ctx!!, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            putExtra(Intent.EXTRA_RETURN_RESULT, true)

            MainActivity.videoTests = videoTests
            MainActivity.currentVideoTest = currentVideoTest

            putExtra(VideoTestConstants.CURRENT_VIDEO_TEST, Json.encodeToString(currentVideoTest))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addCategory("android.intent.category.DEFAULT")
            setDataAndType(uri, "video/mp4")
        }

        try {
            (ctx as MainActivity).startActivityForResult(
                intent,
                IntentRequestCode.TEST_VDO_COMPLETION
            )
        } catch (e: ActivityNotFoundException) {
            showToast("No APP found to open this file", force = true)
        } catch (e: java.lang.Exception) {
            showToast("File opened incorrectly", force = true)
        }
    }

    @SuppressLint("SdCardPath")
    open fun compareResult(
        groundTruthFilePath: String,
        outputFilePath: String,
        currentVideoTest: VideoTestObj
    ): Boolean {
        val groundTruthFile = File(groundTruthFilePath)
        val outputFile = File(outputFilePath)

        if (!groundTruthFile.exists()) {
            logFailedTest(
                "File to compare results was not found, please provide the file to run tests",
                currentVideoTest
            )
            return false
        }
        Log.d("testing", "File groundTruth: ${groundTruthFile.name} exists!")

        if (!outputFile.exists()) {
            logFailedTest(
                "File containing results was not found, please make sure output is saved to run tests",
                currentVideoTest
            )
            return false
        }

        if (groundTruthFile.readText().isEmpty() && outputFile.readText().isEmpty()) {
            logFailedTest("Both Files were empty!", currentVideoTest)
            return false
        }

        if (outputFile.readText().isEmpty()) {
            logFailedTest("Output Files were empty!", currentVideoTest)
            return false
        }

        if (groundTruthFile.readText().isEmpty()) {
            logFailedTest("Ground Truth Files were empty!", currentVideoTest)
            return false
        }

        val trueDataGames =
            Json.decodeFromString<List<ExpectedData>>(groundTruthFile.readText().trim())
        val outPutDataGames =
            Json.decodeFromString<List<ExpectedData>>(outputFile.readText().trim())
        var allPassed = true

        if (trueDataGames.size == outPutDataGames.size)
            for (i in outPutDataGames.indices) {
                val failed = trueDataGames[i].compare(outPutDataGames[i])
                failed?.let { failureMessage ->
                    logFailedTest(
                        failureMessage,
                        trueDataGames[i],
                        outPutDataGames[i],
                        currentVideoTest
                    )
                }
                allPassed = allPassed && failed == null
            }
        else {
            logFailedTest(
                "Ground truth data and output data have different no of games, test failed! \n\nTrue data: ${trueDataGames.joinToString { it.toString() + "\n" }} \n\nOutput Data: ${outPutDataGames.joinToString { it.toString() + "\n" }}",
                currentVideoTest
            )
            allPassed = false
        }
        if (allPassed)
            logFailedTest("Test passed!", currentVideoTest)
        else
            logFailedTest("Test failed!", currentVideoTest)

        shareResultFile(allPassed, File(resultFilePath))

        return allPassed
    }

    protected open fun logFailedTest(message: String, currentVideoTest: VideoTestObj) {
        val file = File(resultFilePath)

        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            file.writeText("")
        }

        file.appendText("\n-----------------\nTest summary: $message\n-----------------\n")
        file.appendText("Test desc: ${currentVideoTest.videoTestName}\n\n")
        file.appendText("Video test: ${currentVideoTest.videoTestFilePath}\n\n")
    }


    protected open fun logFailedTest(
        failureMessage: String,
        trueData: ExpectedData,
        outputData: ExpectedData,
        currentVideoTest: VideoTestObj,
    ) {
        val file = File(resultFilePath)

        logFailedTest(failureMessage, currentVideoTest)

        file.appendText("True Data: $trueData\n\n")
        file.appendText("Output Data $outputData\n\n")
    }

    protected open fun shareResultFile(allPassed: Boolean, file: File) {

        // Get URI and MIME type of file
        val uri: Uri =
            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        val mime = context.contentResolver.getType(uri)
        // Open file with user selected app
        val intent = Intent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            action = Intent.ACTION_VIEW
            setDataAndType(uri, mime)
        }
        context.startActivity(
            Intent.createChooser(intent, "Tests output file!")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        )
    }
}
