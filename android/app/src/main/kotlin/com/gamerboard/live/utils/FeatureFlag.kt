package com.gamerboard.live.utils

enum class FeatureFlag {
  NEW_LOGGING, OLD
}

enum class FeatureKillAlgoFlag(val key : String){
  BASELINE("baseline") , PER_FRAME("per_frame"), PER_GAME("per_game")
}