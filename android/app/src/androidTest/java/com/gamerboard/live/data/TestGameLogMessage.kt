package com.gamerboard.live.data

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
import com.gamerboard.logger.model.OcrInfoMessage
import com.gamerboard.logger.model.convertDate
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
class TestGameLogMessage(
   
) : LogMessage, KoinComponent {

    @SerialName("gId")
    var gameId: String? = null
    @SerialName("sId")
    var sessionId: String? = "test"
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
        deviceId = "test_device",
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

    companion object {
        private val TAG = TestGameLogMessage::class.java.simpleName
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

