package com.gamerboard.live.game

import com.gamerboard.live.gamestatemachine.games.GameHelper
import org.junit.Assert
import org.junit.Test

class TestGameHelper {

    @Test
    fun testMethodClassNameValidation_validatedSuccessfully(){
        val isExist = GameHelper.validateMethodAndClass("MLKitOCR", "queryOcr")
        Assert.assertTrue(isExist)
    }
}