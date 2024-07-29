package com.gamerboard.live.gamestatemachine.bgmi

class UserHandlerTest {

    /* @Before
     fun setup() {
         debugMachine = DEBUGGER.DIRECT_HANDLE
         UserHandler.resetUser()
         MachineLabelProcessor.currentBuffer.clear()
         Machine.stateMachine.transition(GameEvent.OnResetState)
     }

     private fun loginScreen(fromStart: Boolean = true) {
         if (fromStart)
             Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
         MachineInputHandler.handleInput(LOGIN_RAW.build(), MachineLabelProcessor.getBucket(
             MachineLabelUtils.getFromLabels(LOGIN_RAW_JSON.obj().labels)))
         Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
     }

     private fun homeScreen(fromStart: Boolean = true) {
         MachineInputHandler.handleInput(START.build(), MachineLabelProcessor.getBucket(
             MachineLabelUtils.getFromLabels(START_JSON.obj().labels)))
         Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
     }

     private fun waitingScreen(fromStart: Boolean = true) {
         if (fromStart)
             homeScreen(fromStart)
         MachineInputHandler.handleInput(USER1_WAITING_RAW.build(), MachineLabelProcessor.getBucket(
             MachineLabelUtils.getFromLabels(USER1_WAITING_RAW_JSON.obj().labels)))
         Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
     }


     private fun gameScreen(fromStart: Boolean = true) {
         if (fromStart) {
             waitingScreen(fromStart)
             Truth.assertThat(CurrentGame.game.startTimeStamp).isNotEqualTo("Un-Known")
         }
         MachineInputHandler.handleInput(CLASSIC_ALL_IN_GAME.build(), MachineLabelProcessor.getBucket(
             MachineLabelUtils.getFromLabels(CLASSIC_ALL_IN_GAME_JSON.obj().labels)))
         Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.GAME_STARTED)
     }

     private fun resultRankKillsGameInfoScreen(fromStart: Boolean = true, screen: MachineResult.Builder= resultScreenResultBuilder(), json:String= USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON) {
         if (fromStart) {
             gameScreen(fromStart)
             Truth.assertThat(CurrentGame.game.startTimeStamp).isNotEqualTo("Un-Known")
         }
         MachineInputHandler.handleInput(screen.build(), MachineLabelProcessor.getBucket(
             MachineLabelUtils.getFromLabels(json.obj().labels)))
         Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
     }


     @Test
     fun testOverrideGameResultPlayersDefeated(){
         gameScreen(true)

         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyush[.]finishes 2").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "ratyush[.]finishes 21").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "atyush[.]finishes 21").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "ratyush[.]finishes 21").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "yush[.]finishes 2I").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "atyush[.]finishes 2").obj())

         homeScreen(false)
         Truth.assertThat(UserHandler.user.games).hasSize(1)
         Truth.assertThat(UserHandler.user.games[0].kills).isEqualTo("21")


         gameScreen(true)

         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyush[.]finishes ").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyush[.]finishes").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyush[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyush[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyush[.]finishes  ").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "  pratyush[.]finishes 1l").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = " pratyush[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyush[.]finishes  ").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "Pratyush[.]finishes ").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "Pratyush[.]finishes 1 ").obj())

         homeScreen(false)
         Truth.assertThat(UserHandler.user.games).hasSize(2)
         Truth.assertThat(UserHandler.user.games[1].kills).isEqualTo("1")

     }



     @Test
     fun testOverrideGameResultRank(){
         gameScreen(true)

         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(rank = "7/99").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(rank = "  7/99").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(rank = "7 /99").obj())

         Truth.assertThat(UserHandler.user.games).hasSize(1)
         Truth.assertThat(UserHandler.user.games[0].rank).isEqualTo("7")
     }


     @Test
     fun testOverrideGameResultTeamRank(){
         gameScreen(true)

         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(rank = "7/99").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(rank = "  7/99").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(rank = "7 /99").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(rank = "7 / 99").obj())

         Truth.assertThat(UserHandler.user.games).hasSize(1)
         Truth.assertThat(UserHandler.user.games[0].rank).isEqualTo("7")
     }

     @Test
     fun testSendForAutoMlOverCharacter(){
         gameScreen(true)
         UserHandler.runQuery = false
         //UserHandler.originalBGBICharacterID = "pratyushtiwa"

         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())
         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())
         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())

         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes B").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes B").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes B").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes B").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes B").obj())


         Truth.assertThat(UserHandler.user.games).hasSize(1)
         Truth.assertThat(UserHandler.user.games[0].kills).isEqualTo("B")

         Truth.assertThat(UserHandler.runQuery).isFalse()

         homeScreen(false)

         Truth.assertThat(UserHandler.runQuery).isTrue()
         Truth.assertThat(UserHandler.user.games[0].metaInfoJson).isNotNull()
         Truth.assertThat(UserHandler.user.games[0].metaInfoJson).isNotEqualTo(CurrentGame.UNKNOWN)
     }

     @Test
     fun testNotSendForAutoMl(){
         gameScreen(true)
         UserHandler.runQuery = false
         //UserHandler.originalBGBICharacterID = "pratyushtiwa"

         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())
         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())
         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())
         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())
         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())

         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())

         Truth.assertThat(UserHandler.user.games).hasSize(1)
         Truth.assertThat(UserHandler.user.games[0].kills).isEqualTo("1")

         Truth.assertThat(UserHandler.runQuery).isFalse()

         homeScreen(false)

         Truth.assertThat(UserHandler.runQuery).isTrue()
         Truth.assertThat(UserHandler.user.games[0].metaInfoJson).isEqualTo(CurrentGame.UNKNOWN)
     }

     @Test
     fun testSendForAutoMlIfNoTier(){
         gameScreen(true)
         UserHandler.runQuery = false
         //UserHandler.originalBGBICharacterID = "pratyushtiwa"

         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder(rating = "Gold").obj())
         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder(rating = "Gold").obj())
         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder(rating = "Gold").obj())

         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())

         Truth.assertThat(UserHandler.user.games).hasSize(1)
         Truth.assertThat(UserHandler.user.games[0].kills).isEqualTo("1")

         Truth.assertThat(UserHandler.runQuery).isFalse()

         homeScreen(false)

         Truth.assertThat(UserHandler.runQuery).isTrue()
         Truth.assertThat(UserHandler.user.games[0].metaInfoJson).isNotNull()
         Truth.assertThat(UserHandler.user.games[0].metaInfoJson).isNotEqualTo(CurrentGame.UNKNOWN)
         MachineLabelUtils.testLogGreen("${UserHandler.user.games[0].metaInfoJson}")
     }

     @Test
     fun testSendForAutoMlIfNoTierLessThan2(){
         gameScreen(true)
         UserHandler.runQuery = false
         //UserHandler.originalBGBICharacterID = "pratyushtiwa"

         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder(rating = "Gold I").obj())


         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(kills = "pratyushtiwa[.]finishes 1").obj())

         Truth.assertThat(UserHandler.user.games).hasSize(1)
         Truth.assertThat(UserHandler.user.games[0].kills).isEqualTo("1")

         Truth.assertThat(UserHandler.runQuery).isFalse()

         homeScreen(false)

         Truth.assertThat(UserHandler.runQuery).isTrue()
         Truth.assertThat(UserHandler.user.games[0].metaInfoJson).isNotNull()
         Truth.assertThat(UserHandler.user.games[0].metaInfoJson).isNotEqualTo(CurrentGame.UNKNOWN)
         MachineLabelUtils.testLogGreen("${UserHandler.user.games[0].metaInfoJson}")
     }

     @Test
     fun testRankCacheSize(){
         gameScreen(true)
         //UserHandler.originalBGBICharacterID = "pratyushtiwa"

         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())
         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())
         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())
         MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder().obj())

         Truth.assertThat(MachineInputValidator.rankCacheRating).hasSize(3)

         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(rank = "74/99").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(rank = "74/99").obj())
         MachineLabelProcessor.processInputBuffer(resultRankKillsScreenJsonBuilder(rank = "74/99").obj())


         Truth.assertThat(MachineInputValidator.rankCacheKills).hasSize(2)

         Truth.assertThat(UserHandler.user.games).hasSize(1)
         Truth.assertThat(UserHandler.user.games[0].kills).isEqualTo("1")

         homeScreen(false)

         Truth.assertThat(UserHandler.runQuery).isTrue()
         Truth.assertThat(UserHandler.user.games[0].metaInfoJson).isNotNull()
         Truth.assertThat(UserHandler.user.games[0].metaInfoJson).isNotEqualTo(CurrentGame.UNKNOWN)
         MachineLabelUtils.testLogGreen("${UserHandler.user.games[0].metaInfoJson}")
     }
 */
}