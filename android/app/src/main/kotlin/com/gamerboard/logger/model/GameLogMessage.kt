package com.gamerboard.logger.model

import android.os.Build
import android.util.Log
import androidx.annotation.Keep
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.PrefsHelper
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.gamestatemachine.stateMachine.GameResultDetails
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.gamestatemachine.stateMachine.VisionStateMachine
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.utils.SensorHelper
import com.gamerboard.live.utils.mb
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.LogHelper
import com.gamerboard.logger.PlatformType
import com.gamerboard.logging.serializer.LogMessage
import com.gamerboard.logging.utils.now
import com.google.gson.Gson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Keep
@Serializable
class GameLogMessage(
   
) : LogMessage, KoinComponent {

    @SerialName("gId")
    var gameId: String? = null
    @SerialName("sId")
    var sessionId: String? = GamerboardApp.sessionId
    @SerialName("uId")
    var username: String? = ""
    @SerialName("aV")
    var appVersion: String = BuildConfig.VERSION_NAME
    @SerialName("aVC")
    var appVersionCode: Long = BuildConfig.VERSION_CODE.toLong()
    @SerialName("sV")
    var sdkVersion: String = Build.VERSION.SDK_INT.toString()

    @SerialName("m")
    var message : String? = ""
    @SerialName("g")
    var game: String = ""
    @SerialName("c")
    var category: String = LogCategory.D.description
    @SerialName("p")
    var platform: String = PlatformType.A.description
    @SerialName("dt")
    var context: ArrayList<HashMap<String, String>> = arrayListOf()
    @SerialName("t")
    var timestamp: String = convertDate(now())
    @SerialName("sm")
    var stateMachine: HashMap<String, String> = hashMapOf()
    @SerialName("oI")
    var ocrInfo: OcrInfoMessage? = null
            
    @SerialName("dI")
    val deviceInfo: DeviceInfo = DeviceInfo(
        deviceId = GamerboardApp.instance.deviceId,
        manufacturer = Build.MANUFACTURER,
        brand = Build.BRAND,
        model = Build.MODEL,
        cpuTemp = SensorHelper.cpuTemp,
        gpuTemp = SensorHelper.gpuTemp
    )


    @Serializable
    data class DeviceInfo(
        @SerialName("dI")
        private var deviceId: String,
        @SerialName("fm")
        var freeMemory: String? = "",
        @SerialName("ct")
        private var cpuTemp: Float,
        @SerialName("gt")
        private var gpuTemp: Float,
        @SerialName("mf")
        private var manufacturer: String,
        @SerialName("b")
        private var brand: String,
        @SerialName("md")
        private var model: String,
    )

    init {

    }


    init {
        if (MachineConstants.isGameInitialized())
            game = MachineConstants.currentGame.gameName
    }

    class Builder(private val identifier : String? = "") {
        private val logHelper: LogHelper by KoinJavaComponent.inject(LogHelper::class.java)
        val prefsHelper: PrefsHelper by KoinJavaComponent.inject(PrefsHelper::class.java)

        private val logMessage: GameLogMessage = GameLogMessage()

        init {
            logMessage.deviceInfo.freeMemory = logHelper.getMemoryInfo().availMem.mb().toString()
            logMessage.username = prefsHelper.getString(SharedPreferenceKeys.USER_ID) ?: ""
            logMessage.gameId = if(identifier?.trim().isNullOrBlank()) null else identifier
        }

        fun setMessage(message: String): Builder {
            logMessage.message = message
            return this
        }

        fun setCategory(module: LogCategory): Builder {
            logMessage.category = module.description
            return this
        }

        fun setPlatform(platform: PlatformType): Builder {
            logMessage.platform = platform.description
            return this
        }

        fun addContext(key: String, value: Any?): Builder {
            if (value == null) {
                addContext(key, "")
                return this
            }
            logMessage.context.add(
                hashMapOf(
                    Pair("key", key),
                    Pair("value", Gson().toJson(value))
                )
            )
            return this
        }

        fun addContext(key: String, value: Game?): Builder {
            logMessage.context.add(
                hashMapOf(
                    Pair("key", key),
                    Pair("value", Json.encodeToString(value))
                )
            )
            return this
        }

        fun addContext(key: String, value: GameResultDetails?): Builder {
            logMessage.context.add(
                hashMapOf(
                    Pair("key", key),
                    Pair("value", Json.encodeToString(value))
                )
            )
            return this
        }

        private fun hasSerializableAnnotation(obj: Any): Boolean {
            return obj::class.java.annotations.any { it.annotationClass == Serializable::class }
                ?: false
        }

        fun addContext(key: String, value: String?): Builder {
            logMessage.context.add(
                hashMapOf(
                    Pair("key", key),
                    Pair("value", value ?: "")
                )
            )
            return this
        }

        fun addContext(key: String, value: Int?): Builder {
            logMessage.context.add(
                hashMapOf(
                    Pair("key", key),
                    Pair("value", value?.toString() ?: "")
                )
            )
            return this
        }

        fun addContext(key: String, value: Boolean?): Builder {
            addContext(key, value?.toString())
            return this
        }

        fun addContext(key: String, value: Float?): Builder {
            addContext(key, value?.toString())
            return this
        }

        fun addContext(key: String, value: Long?): Builder {
            addContext(key, value?.toString())
            return this
        }

        fun addContext(key: String, value: Int): Builder {
            addContext(key, value.toString())
            return this
        }

        fun addContext(key: String, value: Double): Builder {
            addContext(key, value.toString())
            return this
        }

        fun setOcrInfo(ocrInfo: OcrInfoMessage): Builder {
            this.logMessage.ocrInfo = ocrInfo
            return this
        }

        fun setBucketName(bucketName: String): Builder {
            this.logMessage.stateMachine["bucket"] = bucketName
            return this
        }

        fun build(): GameLogMessage {
            logMessage.stateMachine["csm"] = StateMachine.machine.state.javaClass.simpleName
            logMessage.stateMachine["vsm"] =
                VisionStateMachine.visionImageSaver.state.javaClass.simpleName

            logMessage.appVersion = BuildConfig.VERSION_NAME
            logMessage.appVersionCode= BuildConfig.VERSION_CODE.toLong()
            logMessage.sdkVersion = Build.VERSION.SDK_INT.toString()
            logMessage.sessionId = GamerboardApp.sessionId
            logMessage.timestamp = convertDate(now())
            return logMessage
        }
    }

    companion object {
        private val TAG = GameLogMessage::class.java.simpleName
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(): String {
        try {
            return ProtoBuf.encodeToHexString(this)
        } catch (ex: Exception) {
            Log.e("GameLogMessage", "Exception ${ex.message}")
        }
        return ""
    }

}
val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH).apply {
    this.setTimeZone(TimeZone.getTimeZone("UTC")) // Set the desired timezone (e.g., UTC)

}

fun convertDate(dateTime: Date): String {
   return isoFormat.format(dateTime)
}