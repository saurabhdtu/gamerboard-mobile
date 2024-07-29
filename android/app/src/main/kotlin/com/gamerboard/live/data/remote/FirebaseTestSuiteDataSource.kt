package com.gamerboard.live.data.remote

import android.content.Context
import android.os.Environment
import android.util.Log
import com.gamerboard.live.models.test.TestDataModel
import com.gamerboard.live.models.test.TestGameInput
import com.gamerboard.live.models.test.TestSuite
import com.gamerboard.logger.ILogger
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.lang.Exception
import kotlin.math.roundToInt

/**
 * Downloads test suite from firebase and store it locally.
 * It may take a while to download all files in temporary folder
 */
class FirebaseTestSuiteDataSource : ITestSuiteDataSource, KoinComponent {
    private val context: Context by inject()
    private val downloadLink: String = "gs://gamerboard-dev.appspot.com"
    private val downloadFolderPath: String =
        "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}"
    private val logger: ILogger by inject(ILogger::class.java)
    companion object{
        private val TAG = FirebaseTestSuiteDataSource::class.java.simpleName
    }
    override suspend fun getVideoSuite(packageName: String): TestSuite {
        val testDataModels = arrayListOf<TestDataModel>()
        val downloadFolder = File(downloadFolderPath, packageName)
        if (readLocalFiles(downloadFolder, packageName, testDataModels)) return TestSuite(
            testDataModels
        )
        try {
            val reference =
                Firebase.storage(downloadLink).getReference("video_test_suite/${packageName}")
            val maxDownloadSizeBytes: Long = 256 * 1024
            val filesResult = reference.listAll().await()
            Log.i(TAG, "Downloading ${filesResult.prefixes} folder")
            filesResult.prefixes.forEach { parentFolder ->
                parentFolder.listAll().await().prefixes.forEach {
                    Log.i(TAG, "Downloading ${it.name} folder")
                    Log.i(TAG, "Downloading ${it.listAll().await()} folder")
                    val name = "${parentFolder.name}_${it.name}"
                    val parentFolder = File(downloadFolder, "${parentFolder.name}/${it.name}")
                    if (!parentFolder.exists()) {
                        parentFolder.mkdirs()
                    }
                    Log.e(TAG, " it.listAll().await().prefixes ${ it.listAll().await().items}")
                    it.listAll().await().items.forEach { child ->
                        val file = File(parentFolder, child.name)
                        child.getFile(file).addOnProgressListener { task ->
                            val percentage =
                                ((task.bytesTransferred.toFloat() / task.totalByteCount.toFloat()) * 100).roundToInt()
                            Log.i(
                                FirebaseTestSuiteDataSource::class.java.simpleName,
                                "${percentage}% downloaded."
                            )
                        }
                    }

                }
            }

            readLocalFiles(downloadFolder, packageName, testDataModels)
            return TestSuite(
                testDataModels
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return TestSuite(testDataModels)
    }

    private fun readLocalFiles(
        downloadFolder: File,
        packageName: String,
        testDataModels: ArrayList<TestDataModel>,
    ): Boolean {
        if (downloadFolder.exists()) {
            val listFiles = downloadFolder.listFiles()
            if (listFiles.isNotEmpty()) {
                listFiles?.forEach { parent ->
                    parent.listFiles()?.forEach {
                        val videoFile = File(it, "video.mp4")
                        val groundTruthFile = File(it, "ground_truth.json")
                        val descriptionFile = File(it, "desc.txt")
                        val inputFile = File(it, "input.json")

                        if(!videoFile.exists() || !groundTruthFile.exists() || !descriptionFile.exists() || !inputFile.exists() ) return@forEach

                        Log.i("TestSuiteDownload", "${parent.name}_${it.name}")
                        createFromFiles(
                            "${parent.name}_${it.name}",
                            packageName = packageName,
                            descriptionFile = descriptionFile,
                            inputFile = inputFile,
                            testDataModels = testDataModels,
                            groundTruthFile = groundTruthFile,
                            videoFile = videoFile
                        )
                    }
                }
                return true
            }

        }
        return false
    }

    private fun createFromFiles(
        name: String,
        packageName: String,
        descriptionFile: File,
        inputFile: File,
        testDataModels: ArrayList<TestDataModel>,
        groundTruthFile: File,
        videoFile: File,
    ) {
        val description = descriptionFile.readText(Charsets.UTF_8)
        val input: List<TestGameInput> =
            Json.decodeFromString(inputFile.readText(Charsets.UTF_8).trim())

        testDataModels.add(
            TestDataModel(
                name = name,
                input = input,
                groundTruth = groundTruthFile,
                description = description.trim(),
                videoFile = videoFile,
                packageName = packageName
            )
        )
    }

    /**
     * TODO: Need to structure the files on firebase storage. Need to create a format.
     */
    override suspend fun getImageSuite(): TestSuite {
        val testDataModels = arrayListOf<TestDataModel>()
        try {
            val reference = Firebase.storage(downloadLink).getReference("image_test_suite")
            val maxDownloadSizeBytes: Long = 256 * 1024
            val filesResult = reference.listAll().await()
            println("Downloading ${filesResult.prefixes} folder")
            filesResult.prefixes.forEach { parentFolder ->
                parentFolder.listAll().await().prefixes.forEach {
                    println("Downloading ${it.name} folder")
                    val name = "${parentFolder.name}_${it.name}"
                    val descriptionRef = it.child("desc.txt").getBytes(maxDownloadSizeBytes).await()

                    val imageFile = File.createTempFile(name, ".png")
                    it.child("image.png").getFile(imageFile).await()

                    val groundTruthFile = File.createTempFile("${name}_ground_truth", ".json")

                    it.child("groundTruth.json").getFile(groundTruthFile).await()
                    val inputRef = it.child("input.json").getBytes(maxDownloadSizeBytes).await()
                    val description = descriptionRef.toString(Charsets.UTF_8)
                    val input: List<TestGameInput> =
                        Json.decodeFromString(inputRef.toString(Charsets.UTF_8).trim())

                    testDataModels.add(
                        TestDataModel(
                            name = it.name,
                            input = input,
                            groundTruth = groundTruthFile,
                            description = description.trim(),
                            videoFile = imageFile,
                            packageName = ""
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return TestSuite(type = TestSuite.Type.IMAGE, testData = testDataModels)
    }


    private suspend fun getFiles(
        path: String,
        onEachFile: (name: String, parentRef: StorageReference, fileRef: StorageReference) -> Unit,
        onEndOfFiles: () -> Unit,
    ) {
        try {
            val reference = Firebase.storage(downloadLink).getReference(path)
            val filesResult = reference.listAll().await()
            logger.log("Downloading ${filesResult.prefixes} folder")
            filesResult.prefixes.forEach { parentFolder ->
                parentFolder.listAll().await().prefixes.forEach {
                    logger.log("Downloading ${it.name} folder")
                    val name = "${parentFolder.name}_${it.name}"
                    onEachFile(name, parentFolder, it)
                }
            }
            onEndOfFiles()
        } catch (ex: Exception) {
            logger.log("Error Occured ${ex.stackTraceToString()}")
        }
    }
}