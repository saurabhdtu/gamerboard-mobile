package com.gamerboard.live.service

/**
 * Created by saurabh.lahoti on 20/11/21
 */

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Pattern

/**
 * Created by saurabh.lahoti on 08/08/21
 */
class OCRReader(private val context: Context) {


    fun readImagesFromDownloads() {
        CoroutineScope(Dispatchers.IO).launch {
            val file = File("${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/ocr-test")
            if(!file.exists())
                file.mkdirs()
            for (f in file.listFiles()) {
                val image = InputImage.fromFilePath(context, Uri.fromFile(f))
                readOCR(f.name, image)
            }
        }


//            val path = "${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/ocr"
        /*val path = "${Environment.getExternalStorageDirectory()}/Gamerboard"
        val dir = File(path)
        if (!dir.exists())
            dir.mkdirs()
        val directory = File("${path}/images")
        if (!directory.exists())
            directory.mkdirs()
        try {
            if (directory.isDirectory) {
                for (file in directory.listFiles()!!) {
//                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                    val uri = Uri.fromFile(file)
                    try {
                        val image = InputImage.fromFilePath(context, uri)
                        val deferred = async { readOCR(path, image, file) }
                        deferred.await()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                *//*   *//*
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }*/
    }
}

private suspend fun readOCR(n: String, image: InputImage) {
    val ext = Pattern.compile("(?<=.)\\.[^.]+$")
    val name = ext.matcher(n).replaceAll("")
    withContext(Dispatchers.IO) {
        val recognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val startTime = System.currentTimeMillis()
        try {
            recognizer.process(image).addOnSuccessListener { result ->
                val endTime = System.currentTimeMillis()
                for (textBlock in result.textBlocks) {
                    val s =
                        "${textBlock.text} \n ===> left:${textBlock.boundingBox?.left}; right:${textBlock.boundingBox?.right}; " +
                                "top:${textBlock.boundingBox?.top}; bottom:${textBlock.boundingBox?.bottom} "
                    Log.i("OCR-text", "$name ***** $s")
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    /*//                    val cw = ContextWrapper(context.applicationContext)
//                    val directory = cw.getDir(path, Context.MODE_PRIVATE)
                val resolver = context.contentResolver
                val values = ContentValues()
// save to a folder
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "${name}-detection.txt")
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/txt")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + outputDirectory)
                }else{
                    values.put(MediaStore.MediaColumns.PA, Environment.DIRECTORY_DOWNLOADS + "/" + outputDirectory)
                }
                val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)
// You can use this outputStream to write whatever file you want:
                val outputStream = resolver.openOutputStream(uri!!)*/
}

/*private suspend fun readOCR(path: String, image: InputImage, file: File) {
    val ext = Pattern.compile("(?<=.)\\.[^.]+$")
    val name = ext.matcher(file.name).replaceAll("")
    withContext(Dispatchers.IO) {
        val recognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val startTime = System.currentTimeMillis()
        try {
            recognizer.process(image).addOnSuccessListener { result ->
                val dir = File("${path}/results")
                if (!dir.exists())
                    dir.mkdirs()
                val f = File(dir, "${name}-detection.txt")
                if (!f.exists())
                    f.createNewFile()
                val fos = FileOutputStream(f)
                val writer = fos.writer()
                val endTime = System.currentTimeMillis()

                for (textBlock in result.textBlocks) {
                    val s =
                        "${textBlock.text} \n ===> left:${textBlock.boundingBox?.left}; right:${textBlock.boundingBox?.right}; " +
                                "top:${textBlock.boundingBox?.top}; bottom:${textBlock.boundingBox?.bottom} " +
                                "\n totalTime=> ${endTime - startTime} ms \n\n\n "
                    Log.i("OCR-text", s)
                    writer.append(s)
                }
                writer.flush()
                writer.close()
                fos.close()
                CoroutineScope(Dispatchers.Main).launch {
                    UiUtils.showToast(context, "Finished for ${file.name}", null);
                }
            }.addOnFailureListener {
                it.printStackTrace()
            }


        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}*/

