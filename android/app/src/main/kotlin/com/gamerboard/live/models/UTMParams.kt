package com.gamerboard.live.models

import kotlinx.serialization.Serializable

/**
 * Created by saurabh.lahoti on 09/01/22
 */
@Serializable
data class UTMParams(
    val utmCampaign: String?,
    val utmSource: String?,
    val utmMedium: String?
)