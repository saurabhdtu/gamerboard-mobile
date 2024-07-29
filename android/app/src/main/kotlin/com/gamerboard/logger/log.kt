package com.gamerboard.logger

import androidx.annotation.Keep
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class Log(
    val dt: String = DateUtils.formatTime(System.currentTimeMillis()),
    val cat: LogCategory,
    val uId:String?,
    val msg: String,
    val game:String?,
    val platformType: PlatformType
)