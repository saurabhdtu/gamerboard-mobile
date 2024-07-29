package com.gamerboard.logger

enum class PlatformType(val description: String) { F("flutter"), A("android") }

enum class LogCategory(val description: String) {
    E("error"),
    D("debug"),
    SM("state_machine"),
    OCR("ocr_output"),
    RANK_KILL("rank_and_kill"),
    M("match_data"),
    CM("complete_match"),
    ICM("incomplete_match"),
    CME("complete_match_with_error"),
    ENGINE("state_machine_engine"),
    AUTOML_IMAGE("base64_image_for_automl"),
    AUTO_ML("auto_ml_log"),
    API_CALL("api_calls"),
    SC("screen_capture"),
    MACHINE_EVENTS("state_machine_events")
}

const val logFileSize = 8
/**
D = Debug
E = Error/exception
SM = state machine
VC = vision call
OCR = ocr output
M = match data
CM = complete match
ICM = Incomplete match
CME = Complete match with error
SESSION = contains the session capture data for un synced games
ENGINE = State machine engine
AUTOML_IMAGE = contains the base64 Image for automl
AUTOML = auto ml logs
VCE = vision call error
MACHINE_EVENTS = Logs all the machine events
 */
