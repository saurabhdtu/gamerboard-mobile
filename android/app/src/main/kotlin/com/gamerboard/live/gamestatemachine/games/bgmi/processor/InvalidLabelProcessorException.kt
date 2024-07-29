package com.gamerboard.live.gamestatemachine.games.bgmi.processor

import java.lang.Exception

class InvalidLabelProcessorException(override val message : String) : Exception(message) {
}