package com.gamerboard.live.service.screencapture.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.os.CountDownTimer
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.MainActivity
import com.gamerboard.live.R
import com.gamerboard.live.common.BroadcastFilters
import com.gamerboard.live.common.GAME_PACKAGES
import com.gamerboard.live.common.PrefsHelper
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.databinding.*
import com.gamerboard.live.di.poolModule
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.showToast
import com.gamerboard.live.gamestatemachine.stateMachine.*
import com.gamerboard.live.models.CustomGameResponse
import com.gamerboard.live.models.FeedBackFrom
import com.gamerboard.live.repository.GameRepository
import com.gamerboard.live.repository.getFinalTier
import com.gamerboard.live.repository.getKills
import com.gamerboard.live.service.screencapture.ui.adapters.AdapterJoinedTournaments
import com.gamerboard.live.service.screencapture.ui.adapters.AdapterReasons
import com.gamerboard.live.service.screencapture.ui.adapters.AdapterTeamMembers
import com.gamerboard.live.service.screencapture.ui.adapters.AdapterTeamMembersPending
import com.gamerboard.live.utils.*
import com.gamerboard.live.utils.UiUtils.getAlignmentToGravity
import com.gamerboard.live.utils.UiUtils.getOverlayParams
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.LogHelper
import com.gamerboard.logger.log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class UserGuideUI(
    val ctx: Context,
    private val windowManager: WindowManager,
    private val screenDimension: Point,
    private val serviceUIHelper: ServiceUIHelper
) : KoinComponent {

    private val logHelper: LogHelper by inject()
    private val prefsHelper: PrefsHelper by inject()
    private var layoutOnboardingPopupSmall: LayoutOnboardingPopupSmallBinding? = null
    private var layoutNotVerifiedPopupNotVerifiedBinding: LayoutOnboardingPopupNotVerifiedBinding? =
        null
    private var layoutOnboardingPopupWithMenu: LayoutOnboardingPopupWithMenuBinding? = null
    private var layoutUnverifiedOverlay: LayoutUnverifiedOverlayBinding? = null
    private var layoutOnboardingMenuPopupVerificationNewUiV2Binding: LayoutOnboardingMenuPopupVerificationNewUiV2Binding? =
        null
    private var layoutVerifyGameId: LayoutVerifyGameIdBinding? = null
    private var layoutOnboardingReadyToPlay: LayoutOnboardingReadyToPlayBinding? = null

    /*private var layoutOnboardingPlacementMatchComplete: LayoutOnboardingPlacementMatchCompleteBinding? =
        null*/
    private var layoutPostGameScoresSkillLevel: LayoutPostMatchScoreWithSkillLevelBinding? = null
    private var layoutUserNameUpdateBinding: LayoutUserNameUpdateBinding? = null
    private var layoutUserIdMismatchBinding: LayoutUserIdMismatchBinding? = null
    private var layoutPostMatchScoreForSquadBinding: LayoutPostMatchScoreForSquadBinding? = null
    private var layoutSquadMatchScoreSubmittedBinding: LayoutSquadMatchScroreSubmittedBinding? =
        null


    private var beforeGBLogoX: Int = 0
    private var alignedGBLogoY: Int = 0
    private var alignedMenuTopY: Int = 0
    private var beforeMenu: Int = 0
    private var bgmiMenuHeight: Int = 0

    // click listeners
    private var step1ToStep2OnClickGBLogo = false
    private var listeningForUserVerification = GamerboardApp.instance.prefsHelper.getString(
        SharedPreferenceKeys.RUN_TUTORIAL
    ) == "${OnBoardingStep.COMPLETED}"
    private var step9UserClicksOnLeaderBoardIcon = false
    private var tapsShouldShowHideMenuWithoutBlockingScreen = false

    // returning user listeners
    private var listeningForReturningUserVerification = false

    val uiViewsManager: UiViewsManager = UiViewsManager(serviceUIHelper, windowManager)

    fun setUpOnBoarding() {
        refreshDimensions()
        step1ToStep2OnClickGBLogo = false
        listeningForUserVerification = false || listeningForUserVerification
        step9UserClicksOnLeaderBoardIcon = false
        tapsShouldShowHideMenuWithoutBlockingScreen = false
    }

    private fun refreshDimensions() {
        /*showToast("Layout: $beforeGBLogoX  $alignedGBLogoY $beforeMenu $alignedMenuTopY")*/
        beforeGBLogoX = serviceUIHelper.FLOATING_LOGO_SIZE + 2 * serviceUIHelper.GB_LOGO_MARGIN
        alignedGBLogoY = (serviceUIHelper.paramsGBLogo?.y ?: serviceUIHelper.GB_LOGO_PIN_LOCATION_Y)
        beforeMenu =
            beforeGBLogoX + serviceUIHelper.FLOATING_MENU_WIDTH + serviceUIHelper.GB_LOGO_MARGIN
        //alignedMenuTopY = if (serviceUIHelper.layoutMenuBinding != null) (getLocationOnScreen(serviceUIHelper.layoutMenuBinding?.root as View).y) else alignedGBLogoY
        bgmiMenuHeight = serviceUIHelper.FLOATING_MENU_HEIGHT
        alignedMenuTopY = ((screenDimension.y) - bgmiMenuHeight) / 2
    }


    // OnBoarding Intro
    private fun startOnBoardingTutorial() {
        showToast("Starting OnBoarding!")
        uiViewsManager.addUiView(
            serviceUIHelper.layoutBlockScreenOverlayBinding!!.root,
            UiViewsManager.OverlayOn.GAME_APP
        )

        CoroutineScope(Dispatchers.Main).launch {
            //delay(2000)
            refreshDimensions()
            serviceUIHelper.blockScreen(
                shouldBlockTap = true, popDownOnTap = false, nextCallback = null
            )
            EventUtils.instance()
                .logAnalyticsEvent(Events.OVERLAY_SHOWN, mapOf("type" to "tutorial_welcome"))

            showOnboardingPopupSmall(
                headText = ctx.getString(R.string.welcome_gamerboard),
                contentText = String.format(
                    ctx.getString(R.string.welcome_gamerboard_content),
                    MachineConstants.currentGame.gameName
                ),
                x = 0,
                y = 0,
                alignOverlayFrom = UiUtils.AlignOverlayFrom.Center,
                nextCallback = {
                    EventUtils.instance().logAnalyticsEvent(
                        Events.OVERLAY_DISMISSED, mapOf(
                            "type" to "tutorial_welcome", "action_performed" to "next"
                        )
                    )
                    step1IntroduceGbLogo(it)
                },
                nextBtnText = "NEXT",
                nextButtonTextColor = R.color.txt_color_white
            )
        }
    }

    private fun stopServiceDialog() {
        showToast("Stop Service!")
//        uiViewsManager.addUiView(
//            serviceUIHelper.layoutBlockScreenOverlayBinding!!.root,
//            UiViewsManager.OverlayOn.BGMI_APP
//        )

        CoroutineScope(Dispatchers.Main).launch {
            //delay(2000)
            refreshDimensions()
            serviceUIHelper.blockScreen(
                shouldBlockTap = false, popDownOnTap = false, nextCallback = null
            )
            showOnboardingPopupSmall(
                contentText = ctx.getString(R.string.stop_gamerboard_service),
                x = beforeGBLogoX,
                y = alignedGBLogoY,
                btnClickable = true,
                alignOverlayFrom = UiUtils.AlignOverlayFrom.TopRight,
                nextCallback = {
//                    ServiceManager()
                    serviceUIHelper.canTapOnGbLogo = true
                    ctx.sendBroadcast(Intent(BroadcastFilters.SERVICE_COM).apply {
                        putExtra("action", "register_projection")
                    })
                    /*                    uiViewsManager.clearAll()
                                        clearAllOverlays()

                                        serviceUIHelper.clearResources()

                                        ctx.startActivity(Intent(ctx, MainActivity::class.java).apply {
                                            (ctx.applicationContext as GamerboardApp).prefsHelper.putBoolean(
                                                IntentKeys.RESTART_CLICKED,
                                                true
                                            )
                                            putExtra(IntentKeys.RESTART_CLICKED, true)
                                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        })*/

                },
                nextBtnText = ctx.getString(R.string.turn_on),
                nextButtonColor = R.color.color_accent,
                nextButtonTextColor = R.color.txt_color_white
            )
        }
    }


    private fun step1IntroduceGbLogo(message: String) {
        refreshDimensions()
        EventUtils.instance()
            .logAnalyticsEvent(Events.OVERLAY_SHOWN, mapOf("type" to "tutorial_gamerboard_icon"))

        showOnboardingPopupSmall(
            headText = ctx.getString(R.string.gamerboard_icon),
            showArrow = true,
            contentText = ctx.getString(R.string.gamerboard_icon_content),
            x = beforeGBLogoX,
            y = alignedGBLogoY,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.TopRight
        )
        step1ToStep2OnClickGBLogo = true
    }

    private fun step2IntroduceGbMenu(message: String) {
        EventUtils.instance().logAnalyticsEvent(
            Events.OVERLAY_SHOWN, mapOf(
                "type" to "tutorial_gamerboard_mini_menu"
            )
        )
        serviceUIHelper.showMenu(x = beforeGBLogoX, y = 0, shouldUpdate = false)
        serviceUIHelper.setListeners(
            listOf(
                R.id.layout_verify, R.id.iv_help, R.id.iv_open, R.id.iv_stop
            )
        )
        refreshDimensions()
        showOnboardingPopupWithMenu(
            headText = ctx.getString(R.string.gamerboard_mini_menu),
            showArrow = false,
            contentText = ctx.getString(R.string.gamerboard_mini_menu_content),
            x = beforeMenu,
            y = 0,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.CenterVerticalRight,
            nextCallback = {
                EventUtils.instance().logAnalyticsEvent(
                    Events.OVERLAY_DISMISSED, mapOf(
                        "type" to "tutorial_gamerboard_mini_menu", "action" to "next"
                    )
                )
                step3ShowVerificationSteps(it)
            },
            nextBtnText = "Next",
            nextButtonTextColor = R.color.txt_color_white
        )
    }

    private fun step3ShowVerificationSteps(message: String) {
        refreshDimensions()
        serviceUIHelper.blockScreen(
            shouldBlockTap = true,
            popDownOnTap = false,
            //clippedBackground = R.drawable.custom_block_screen_clipped
        )
        EventUtils.instance().logAnalyticsEvent(
            Events.OVERLAY_SHOWN, mapOf(
                "type" to "tutorial_verify_session"
            )
        )
        showOnboardingPopupWithVerification(
            headText = ctx.getString(R.string.gamerboard_verification),
            showArrow = false,
            contentText = ctx.getString(R.string.gamerboard_verification_content),
            x = beforeMenu,
            y = 0,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.CenterVerticalRight,
            nextCallback = {
                EventUtils.instance().logAnalyticsEvent(
                    Events.OVERLAY_DISMISSED, mapOf(
                        "type" to "tutorial_verify_session", "action" to "okay"
                    )
                )
                step4LetTheUserVerify(it)
            },
            nextBtnText = "OKAY",
            nextButtonTextColor = R.color.txt_color_white
        )
    }


    fun startUserVerifyNewOnboarding() {
        serviceUIHelper.startedOnBording = true
        CoroutineScope(Dispatchers.IO).launch {
            listeningForUserVerification = true
            delay(1000)
            MachineConstants.machineLabelProcessor.showLoaderCircularBuffer.clear()
            MachineConstants.machineLabelProcessor.readyToVerify = 1
        }
    }

    private fun step4LetTheUserVerify(message: String) {
        refreshDimensions()
        //serviceUIHelper.blockScreen(shouldBlockTap = false)
        serviceUIHelper.showMenu(x = beforeGBLogoX, y = 0)
        serviceUIHelper.setListeners(
            listOf(
                R.id.layout_verify, R.id.iv_help, R.id.iv_open, R.id.iv_stop
            )
        )
        refreshDimensions()
        serviceUIHelper.blockScreen(shouldBlockTap = true, popDownOnTap = true, nextCallback = {
            EventUtils.instance().logAnalyticsEvent(
                Events.OVERLAY_DISMISSED, mapOf(
                    "type" to "tutorial_user_to_verify", "action" to "touch_outside"
                )
            )
            popDownUnverifiedPopup(it)
        })
        EventUtils.instance().logAnalyticsEvent(
            Events.OVERLAY_SHOWN, mapOf(
                "type" to "tutorial_user_to_verify"
            )
        )
        showOnboardingPopupWithUnverified(
            headText = ctx.getString(R.string.gamerboard_unverified),
            contentText = ctx.getString(MachineConstants.currentGame.verifyGameProfile),
            x = beforeMenu,
            y = alignedMenuTopY,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.TopRight
        )


        // allow closing/opening menu
        // serviceUIHelper.canTapOnGbLogo = true

        CoroutineScope(Dispatchers.IO).launch {
            listeningForUserVerification = true
            delay(1000)
            MachineConstants.machineLabelProcessor.showLoaderCircularBuffer.clear()
            MachineConstants.machineLabelProcessor.readyToVerify = 1
        }
    }

    private fun popDownUnverifiedPopupForReturningUser(message: String) {

        //only after pop down we allow to tap over it
        serviceUIHelper.canTapOnGbLogo = true

        popDownUnverifiedPopup(message)
        serviceUIHelper.hideMenu()
        // allow closing/opening menu
        //serviceUIHelper.canTapOnGbLogo = true
    }

    private fun popDownUnverifiedPopup(message: String) {
        removeViewFromWindow(layoutUnverifiedOverlay)
        removeViewFromWindow(layoutOnboardingMenuPopupVerificationNewUiV2Binding)

        layoutUnverifiedOverlay = null
        layoutOnboardingMenuPopupVerificationNewUiV2Binding = null

        //hide menu and
        serviceUIHelper.hideMenu()

        // allow closing/opening menu
        serviceUIHelper.canTapOnGbLogo = false

        // till he reaches the verification step
    }


    private fun step5UserVerifiedWith(
        fetchedUserId: String,
        fetchedCharacterId: String,
        bitmap: Bitmap?
    ) {
        removeViewFromWindow(layoutUnverifiedOverlay)

        // do not allow closing/opening menu
        serviceUIHelper.canTapOnGbLogo = false

        // for the user to escape if he wishes to stop at verification
        //tapsShouldShowHideMenuWithoutBlockingScreen = true

        // if exists
        serviceUIHelper.hideMenu()
        serviceUIHelper.hideLeaderBoard("")

        serviceUIHelper.blockScreen(
            shouldBlockTap = true,
            popDownOnTap = false,
            clippedBackground = MachineConstants.currentGame.overlayProfileVerification
        )
        showVerifyGameIdBox(
            x = 0,
            y = 0,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.Center,
            fetchedUserId = fetchedUserId,
            bitmap = bitmap,
            fetchedCharacterId = fetchedCharacterId
        )

    }

    private fun step6UserConfirmedBgmiId(
        confirmedBgmiId: String, fetchedUserId: String, fetchedCharacterId: String
    ) {
        serviceUIHelper.hideMenu()
        serviceUIHelper.hideLeaderBoard("")
        serviceUIHelper.canTapOnGbLogo = false
        serviceUIHelper.canTapOnLeaderBoardIcon = false

        // mark the user verified
        StateMachine.machine.transition(
            Event.SetOriginalGameProfile(
                confirmedBgmiId,
                fetchedCharacterId
            )
        )
        StateMachine.machine.transition(
            Event.VerifyUser(
                gameCharId = fetchedCharacterId,
                gameProfileId = confirmedBgmiId
            )
        )

        // for the user to escape if he wishes to stop at verification
        //tapsShouldShowHideMenuWithoutBlockingScreen = false
        EventUtils.instance().logAnalyticsEvent(
            Events.OVERLAY_SHOWN, mapOf(
                "type" to "tutorial_profile_verified"
            )
        )

        /*showToast("Confirmed :$confirmedBgmiId")*/
        refreshDimensions()
        serviceUIHelper.blockScreen(shouldBlockTap = true, popDownOnTap = false)
        showOnboardingPopupSmall(
            headText = ctx.getString(R.string.gamerboard_session_verified),
            showArrow = true,
            contentText = ctx.getString(R.string.gamerboard_session_verified_content),
            x = beforeGBLogoX,
            y = alignedGBLogoY,
            nextCallback = {
                EventUtils.instance().logAnalyticsEvent(
                    Events.OVERLAY_DISMISSED, mapOf(
                        "type" to "tutorial_profile_verified"
                    )
                )
                step7UserReadyToPlay(it)
            },
            alignOverlayFrom = UiUtils.AlignOverlayFrom.TopRight,
            nextBtnText = "NEXT",
            nextButtonTextColor = R.color.txt_color_white
        )
    }


    private fun step7UserReadyToPlay(string: String) {
        refreshDimensions()
        serviceUIHelper.blockScreen(shouldBlockTap = true, popDownOnTap = false, nextCallback = {
            EventUtils.instance().logAnalyticsEvent(
                Events.OVERLAY_DISMISSED, mapOf(
                    "type" to "tutorial_ready_to_play", "action" to "touch_outside"
                )
            )
            popDownReadyToPlayOnTapOutside(it)
        })

        EventUtils.instance().logAnalyticsEvent(
            Events.OVERLAY_SHOWN, mapOf(
                "type" to "tutorial_ready_to_play"
            )
        )
        showReadyToPlay(
            headText = ctx.getString(R.string.ready_to_play),
            contentText = ctx.getString(MachineConstants.currentGame.verifyStartText),
            x = 0,
            y = 0,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.Center
        )
    }

    private fun popDownReadyToPlayOnTapOutside(string: String) {
        //Mark Complete
        GamerboardApp.instance.prefsHelper.putString(
            SharedPreferenceKeys.RUN_TUTORIAL, "${OnBoardingStep.COMPLETED}"
        )

        // this will update the current running flags
        serviceUIHelper.checkOnBoarding()

        uiViewsManager.clearAll()
        clearAllOverlays()

        serviceUIHelper.hideMenu()
        removeViewFromWindow(layoutOnboardingReadyToPlay)
    }

    private fun step8OnboardingReadyToPlayComplete(string: String) {
        //Mark Complete
        GamerboardApp.instance.prefsHelper.putString(
            SharedPreferenceKeys.RUN_TUTORIAL, "${OnBoardingStep.COMPLETED}"
        )

        // this will update the current running flags
        serviceUIHelper.checkOnBoarding()

        uiViewsManager.clearAll()
        clearAllOverlays()

        serviceUIHelper.blockScreen(shouldBlockTap = false)
        serviceUIHelper.hideMenu()
        serviceUIHelper.canTapOnGbLogo = true
        serviceUIHelper.canMoveGbLogoVertically = true
    }

    // OnBoarding In-Game
    private fun step9GuideAboutLeaderBoard(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            //delay(50)
            refreshDimensions()
            serviceUIHelper.canTapOnGbLogo = false
            uiViewsManager.clearAll()
            clearAllOverlays()

            serviceUIHelper.showMenu(x = beforeGBLogoX, y = 0)
            refreshDimensions()
            serviceUIHelper.highlightMenuItem(listOf(R.id.iv_leaderboard))
            showOnboardingPopupSmall(headText = ctx.getString(R.string.current_leaderboard),
                showArrow = false,
                contentText = ctx.getString(R.string.current_leaderboard_content),
                x = beforeMenu,
                y = 0,
                alignOverlayFrom = UiUtils.AlignOverlayFrom.CenterVerticalRight,
                nextBtnText = "OK",
                nextCallback = {
                    EventUtils.instance().logAnalyticsEvent(
                        Events.OVERLAY_DISMISSED, mapOf(
                            "type" to "tutorial_leaderboard", "action" to "ok"
                        )
                    )
                    userPressedOkFromLeaderBoardTutorial(it)
                })
            serviceUIHelper.blockScreen(shouldBlockTap = false)
            step9UserClicksOnLeaderBoardIcon = true
            serviceUIHelper.canTapOnLeaderBoardIcon = true
            serviceUIHelper.setListeners(
                listOf(
                    R.id.layout_verify,
                    R.id.iv_help,
                    R.id.iv_leaderboard,
                    R.id.iv_open,
                    R.id.iv_stop
                )
            )
            EventUtils.instance().logAnalyticsEvent(
                Events.OVERLAY_SHOWN, mapOf(
                    "type" to "tutorial_leaderboard",
                )
            )
            // Mark complete
            GamerboardApp.instance.prefsHelper.putString(
                SharedPreferenceKeys.RUN_TUTORIAL_IN_GAME, "${OnBoardingStep.COMPLETED}"
            )
        }
    }


    private fun userPressedOkFromLeaderBoardTutorial(message: String) {
        if (step9UserClicksOnLeaderBoardIcon) {

            while (step9UserClicksOnLeaderBoardIcon) step9UserClicksOnLeaderBoardIcon = false
            removeViewFromWindow(layoutOnboardingPopupSmall)

            // Hide the menu also, not needed
            serviceUIHelper.hideMenu()
            serviceUIHelper.canTapOnGbLogo = true
            serviceUIHelper.canTapOnLeaderBoardIcon = true
        }
    }

    private fun step9CompletedOpenLeaderBoard(message: String) {
        refreshDimensions()
        serviceUIHelper.showLeaderBoard(
            x = beforeMenu, y = 0, alignOverlayFrom = UiUtils.AlignOverlayFrom.CenterVerticalRight
        )
        serviceUIHelper.canTapOnGbLogo = true
        serviceUIHelper.canMoveGbLogoVertically = true
        serviceUIHelper.canTapOnLeaderBoardIcon = true
        serviceUIHelper.setListeners(
            listOf(
                R.id.layout_verify, R.id.iv_help, R.id.iv_leaderboard, R.id.iv_open, R.id.iv_stop
            )
        )

        //Mark Complete
        GamerboardApp.instance.prefsHelper.putString(
            SharedPreferenceKeys.RUN_TUTORIAL_IN_GAME, "${OnBoardingStep.COMPLETED}"
        )
        uiViewsManager.clearAll()
        clearAllOverlays()
    }

    /*private fun step10GuideAboutPostGameScores(
        message: String,
        game: Game
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            //delay(2000)
            serviceUIHelper.blockScreen(
                shouldBlockTap = true,
                popDownOnTap = false,
                clippedBackground = R.color.bg_block_screen_color_dark
            )

            // hide menu for more space
            serviceUIHelper.hideMenu()
            serviceUIHelper.hideLeaderBoard("")
            serviceUIHelper.canTapOnGbLogo = false

            showPlacementComplete(
                x = 0,
                y = 0,
                alignOverlayFrom = UiUtils.AlignOverlayFrom.Center,
                game = game
            )

            //Mark Complete
            // No need to mark it complete here, we will do this from GameRepository
            /*GamerboardApp.instance.prefsHelper.putString(
                    SharedPreferenceKeys.RUN_TUTORIAL_POST_GAME,
                    "${OnBoardingStep.COMPLETED}"
            )*/
        }
    }*/

    private fun step11OnBoardingPopUpClosed(message: String) {
        uiViewsManager.clearAll()
        clearAllOverlays()

        stopOnBoardingTutorial()
    }

    fun stopOnBoardingTutorial() {
        clearAllOverlays()
        serviceUIHelper.cleanUpOnBoarding()
    }


    // Returning user
    fun guideTheUserToVerifyProfile(message: String) {
        val onlyVerification = serviceUIHelper.isNewProfileVerificationUIEnabled
        refreshDimensions()
        //serviceUIHelper.blockScreen(shouldBlockTap = false)
        if (onlyVerification) {
            serviceUIHelper.hideMenu()
            serviceUIHelper.FLOATING_LOGO_SIZE = 18
        } else {
            serviceUIHelper.showMenu(x = beforeGBLogoX, y = 0)
            serviceUIHelper.setListeners(
                listOf(
                    R.id.layout_verify, R.id.iv_help, R.id.iv_open, R.id.iv_stop
                )
            )
        }

        refreshDimensions()
        serviceUIHelper.blockScreen(shouldBlockTap = true, popDownOnTap = true, nextCallback = {
            popDownUnverifiedPopupForReturningUser(it)
        })
        EventUtils.instance().logAnalyticsEvent(
            Events.OVERLAY_SHOWN, mapOf(
                "type" to "verify_your_session",
            )
        )
        showOnboardingPopupWithUnverified(
            headText = ctx.getString(R.string.gamerboard_unverified),
            contentText = ctx.getString(MachineConstants.currentGame.verifyGameProfile),
            x = if (onlyVerification) beforeGBLogoX + 56 else beforeMenu,
            y = alignedMenuTopY,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.TopRight
        )
        listeningForReturningUserVerification = true
    }

    private fun profileIsVerifiedNowHideUnverifiedOverlay(message: String) {
        removeViewFromWindow(layoutUnverifiedOverlay)
        serviceUIHelper.hideMenu()
        serviceUIHelper.blockScreen(shouldBlockTap = false)
    }

    fun showUserHisScoresAndSkillChange(message: String, customGameResponse: CustomGameResponse) {
        refreshDimensions()
        serviceUIHelper.blockScreen(
            shouldBlockTap = true,
            popDownOnTap = false,
            clippedBackground = R.color.bg_block_screen_color_dark
        )

        // hide menu for more space
        serviceUIHelper.hideMenu()
        serviceUIHelper.hideLeaderBoard("")
        serviceUIHelper.canTapOnGbLogo = false

        showPostGameScoresSkillChange(
            x = 0,
            y = 0,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.Center,
            customGameResponse = customGameResponse
        )
    }

    fun showMultiplayerGameScoresAndSkillChange(customGameResponse: CustomGameResponse) {
        refreshDimensions()
        serviceUIHelper.blockScreen(
            shouldBlockTap = true,
            popDownOnTap = false,
            clippedBackground = R.color.bg_block_screen_color_dark
        )

        // hide menu for more space
        serviceUIHelper.hideMenu()
        serviceUIHelper.hideLeaderBoard("")
        serviceUIHelper.canTapOnGbLogo = false

        showPostGameScoresSkillChangesForMultiplayerGame(
            x = 0,
            y = 0,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.Center,
            customGameResponse = customGameResponse
        )
    }

    private fun requestFeedback() {
        FeedbackUtils.requestFeedback(ctx, FeedBackFrom.GAME_COMPLETION)
    }

    private fun showPostGameScoresSkillChangesForMultiplayerGame(
        x: Int,
        y: Int,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight,
        displayOverGbApp: Boolean = false,
        customGameResponse: CustomGameResponse
    ) {
        try {
            removeViewFromWindow(layoutPostMatchScoreForSquadBinding)

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutPostMatchScoreForSquadBinding =
                LayoutPostMatchScoreForSquadBinding.inflate(layoutInflater)

            // params
            val params: WindowManager.LayoutParams = getOverlayParams(
                width = (0.8 * screenDimension.x).toInt(),
                height = (0.8 * screenDimension.y).toInt()
            )

            layoutPostMatchScoreForSquadBinding?.tvJoinDiscord?.setOnClickListener {
                serviceUIHelper.openDiscordServer()
            }
            layoutPostMatchScoreForSquadBinding?.layoutTotalScore?.tvTotalScore?.text =
                customGameResponse.serverGame.game.score.toInt().toString()

            layoutPostMatchScoreForSquadBinding?.layoutTotalScore?.tvTeamRank?.text =
                customGameResponse.serverGame.game.rank.toString()
            if (customGameResponse.serverGame.tournaments.isNotEmpty()) {
                val list =
                    customGameResponse.serverGame.tournaments.filter { it?.isAdded == true }
                        .toList()
                if (list.isNotEmpty()) {
                    list.first()?.squadScores?.let { scores ->
                        layoutPostMatchScoreForSquadBinding?.layoutPlayers?.rvPostMatchSquadList?.apply {
                            layoutManager = LinearLayoutManager(ctx)
                            adapter = AdapterTeamMembers(
                                scores, customGameResponse.scoring.scoring.killPoints
                            )
                        }
                        layoutPostMatchScoreForSquadBinding?.rvReasons?.apply {
                            layoutManager = LinearLayoutManager(ctx)
                            adapter = AdapterReasons(
                                customGameResponse.serverGame.tournaments, scaleDown = true
                            )
                            visibility = View.VISIBLE
                        }
                        //btn close
                        layoutPostMatchScoreForSquadBinding?.ivClose?.setOnClickListener {
                            // now he can click after closing the pop up
                            serviceUIHelper.canTapOnGbLogo = true
                            step11OnBoardingPopUpClosed("Close!")
                            removeViewFromWindow(layoutPostMatchScoreForSquadBinding)
                            requestFeedback()
                        }
                        layoutPostMatchScoreForSquadBinding?.btnDone?.setOnClickListener {
                            // now he can click after closing the pop up
                            serviceUIHelper.canTapOnGbLogo = true
                            step11OnBoardingPopUpClosed("Close!")
                            removeViewFromWindow(layoutPostMatchScoreForSquadBinding)
                            requestFeedback()
                        }

                        // align from
                        params.gravity = getAlignmentToGravity(alignOverlayFrom)
                        params.x = x
                        params.y = y
                        UiUtils.setFullUiParams(layoutPostMatchScoreForSquadBinding?.root)
                        EventUtils.instance()
                            .logAnalyticsEvent(Events.OVERLAY_SHOWN, mapOf("type" to "game_score"))

                        //add view
                        windowManager.addView(layoutPostMatchScoreForSquadBinding!!.root, params)
                        uiViewsManager.addUiView(
                            layoutPostMatchScoreForSquadBinding!!.root,
                            if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.GAME_APP
                        )
                    }
                }

            }
        } catch (ex: Exception) {
            logException(ex)
        }
    }


    private fun showPostGameThatItsNotCompletedYet(
        x: Int,
        y: Int,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight,
        displayOverGbApp: Boolean = false,
        customGameResponse: CustomGameResponse
    ) {
        try {
            removeViewFromWindow(layoutSquadMatchScoreSubmittedBinding)
            logMessage(customGameResponse.serverGame.toString())
            var currentGame =
                customGameResponse.serverGame.tournaments.first { it?.isAdded == false }
            if (currentGame == null) currentGame =
                customGameResponse.serverGame.tournaments.first()
            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutSquadMatchScoreSubmittedBinding =
                LayoutSquadMatchScroreSubmittedBinding.inflate(layoutInflater)

            // params
            val params: WindowManager.LayoutParams = getOverlayParams(
                width = WindowManager.LayoutParams.WRAP_CONTENT,
                height = WindowManager.LayoutParams.WRAP_CONTENT
            )

            //btn close
            layoutSquadMatchScoreSubmittedBinding?.btnDone?.setOnClickListener {
                // now he can click after closing the pop up
                serviceUIHelper.canTapOnGbLogo = true
                step11OnBoardingPopUpClosed("Close!")
                removeViewFromWindow(layoutSquadMatchScoreSubmittedBinding)
                requestFeedback()
            }
            if (currentGame?.submissionState != null) {
                layoutSquadMatchScoreSubmittedBinding?.rvPlayers?.apply {
                    layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    adapter = currentGame.submissionState?.let {
                        AdapterTeamMembersPending(
                            it
                        )
                    }
                }
            } else {
                layoutSquadMatchScoreSubmittedBinding?.rvPlayers?.visibility = View.GONE
            }

            layoutSquadMatchScoreSubmittedBinding?.tvWaiting?.text = currentGame?.exclusionReason


            layoutSquadMatchScoreSubmittedBinding?.ivClose?.setOnClickListener {
                // now he can click after closing the pop up
                serviceUIHelper.canTapOnGbLogo = true
                step11OnBoardingPopUpClosed("Close!")
                removeViewFromWindow(layoutSquadMatchScoreSubmittedBinding)
                requestFeedback()
            }

            // align from
            params.gravity = getAlignmentToGravity(alignOverlayFrom)
            params.x = x
            params.y = y
            UiUtils.setFullUiParams(layoutSquadMatchScoreSubmittedBinding?.root)
            EventUtils.instance()
                .logAnalyticsEvent(Events.OVERLAY_SHOWN, mapOf("type" to "game_score"))

            //add view
            windowManager.addView(layoutSquadMatchScoreSubmittedBinding!!.root, params)
            uiViewsManager.addUiView(
                layoutSquadMatchScoreSubmittedBinding!!.root,
                if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.GAME_APP
            )
        } catch (e: Exception) {
            logMessage(Gson().toJson(customGameResponse.serverGame.tournaments))
            logException(e)
        }
    }

    fun showUserThatGameIsNotOverYet(customGameResponse: CustomGameResponse) {
        refreshDimensions()
        serviceUIHelper.blockScreen(
            shouldBlockTap = true,
            popDownOnTap = false,
            clippedBackground = R.color.bg_block_screen_color_dark
        )

        // hide menu for more space
        serviceUIHelper.hideMenu()
        serviceUIHelper.hideLeaderBoard("")
        serviceUIHelper.canTapOnGbLogo = false

        showPostGameThatItsNotCompletedYet(
            x = 0,
            y = 0,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.Center,
            customGameResponse = customGameResponse
        )
    }

    private fun showAlertDialogForUnverified(message: String) {
        EventUtils.instance().logAnalyticsEvent(
            Events.OVERLAY_SHOWN, mapOf(
                "type" to "user_unverified"
            )
        )
        showOnboardingPopupSmall(
            headText = "Gamerboard",
            showArrow = false,
            contentText = ctx.getString(R.string.you_have_been_unverified),
            x = 0,
            y = 0,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.Center,
            nextCallback = this::popDownBGMILoginOverlayOnPressOk,
            nextBtnText = "  OK  ",
            nextButtonTextColor = R.color.txt_color_white,
            background = R.color.bg_dark_2,
            displayOverGbApp = false
        )
    }

    //enum class GameFailureReason {MISSING_RANK, MISSING_KILLS, MISSING_TIER, MISSING_GAME_INFO, MISSING_GROUP, MISSING_MAP}

    fun gameFailed(failureReason: GameRepository.GameFailureReason?, message: String?) {
        when (failureReason) {
            GameRepository.GameFailureReason.GAME_NOT_COMPLETED -> {

                log {
                    it.setMessage("Game not completed!")
                    it.addContext("reason", failureReason)
                    it.setCategory(LogCategory.ICM)
                }
                logHelper.completeLogging()
            }

            GameRepository.GameFailureReason.STARTED_WITHOUT_VERIFYING -> {

                log {
                    it.setMessage("User started the game without verifying profile.")
                    it.addContext("reason", failureReason)
                    it.setCategory(LogCategory.ICM)
                }
                logHelper.completeLogging()
            }

            else -> {
                log {
                    it.setMessage(GameRepository.GameFailureReason.OTHER.getMessage())
                    it.addContext("reason", GameRepository.GameFailureReason.OTHER)
                    it.setCategory(LogCategory.ICM)
                }
                logHelper.completeLogging()
            }

        }
        EventUtils.instance().logAnalyticsEvent(
            Events.OVERLAY_SHOWN, mapOf(
                "type" to "game_failed"
            )
        )


        if (BuildConfig.IS_TEST) return

        if (serviceUIHelper.isNewProfileVerificationUIEnabled.not() && failureReason == GameRepository.GameFailureReason.ENDED_WITHOUT_VERIFYING) {
            return
        }

        if (serviceUIHelper.isNewProfileVerificationUIEnabled &&
            (failureReason == GameRepository.GameFailureReason.STARTED_WITHOUT_VERIFYING ||
                    failureReason == GameRepository.GameFailureReason.ENDED_WITHOUT_VERIFYING)
        ) {
            var newMessage = failureReason.getMessage()
            if (failureReason == GameRepository.GameFailureReason.STARTED_WITHOUT_VERIFYING) {
                newMessage =
                    GameRepository.GameFailureReason.STARTED_WITHOUT_VERIFYING_VARIANT.getMessage()
            }
            showNotVerifiedPopup(
                contentText = message ?: newMessage,
                x = 0,
                y = 0,
                alignOverlayFrom = UiUtils.AlignOverlayFrom.Center,
                onDismiss = this::onPopupNotVerifiedDismissed
            )
        } else {
            showOnboardingPopupSmall(
                headText = "Gamerboard",
                showArrow = false,
                contentText = message ?: failureReason?.getMessage(),
                x = 0,
                y = 0,
                alignOverlayFrom = UiUtils.AlignOverlayFrom.Center,
                nextCallback = this::popDownMatchFailedPreddOk,
                nextBtnText = "  OK  ",
                nextButtonTextColor = R.color.txt_color_white,
                background = R.color.bg_dark_2,
                displayOverGbApp = false
            )
        }
    }

    private fun popDownBGMILoginOverlayOnPressOk(message: String) {
        removeViewFromWindow(layoutOnboardingPopupSmall)
    }

    private fun popDownMatchFailedPreddOk(message: String) {
        removeViewFromWindow(layoutOnboardingPopupSmall)
        // requestFeedback()
    }

    private fun onPopupNotVerifiedDismissed() {
        removeViewFromWindow(layoutNotVerifiedPopupNotVerifiedBinding)
        // requestFeedback()
    }

    fun actionOnScreenUserGuide(
        firstScreenOfType: ScreenOfType,
        metadata: Map<String, String?>? = null,
        bitmap: Bitmap? = null
    ) {
        when (firstScreenOfType) {
            ScreenOfType.HOME -> {
                serviceUIHelper.startedOnBording = true
                startOnBoardingTutorial()
            }

            ScreenOfType.SERVICE_STOP -> {
//                serviceUIHelper.startedOnBording = true
                stopServiceDialog()
            }

            ScreenOfType.PRE_PROFILE_VERIFICATION -> {
                if (metadata == null) return
                if (listeningForUserVerification) {
                    serviceUIHelper.startedOnBording = true
                    listeningForUserVerification = false
                    val fetchedGameId = metadata["fetchedNumericId"]!!
                    val fetchedCharacterId = metadata["fetchedCharacterId"]!!
                    step5UserVerifiedWith(fetchedGameId, fetchedCharacterId, bitmap)
                }
            }

            ScreenOfType.PROFILE_VERIFIED -> {
                if (StateMachine.machine.state !is VerifiedUser) return
                val onBoarding =
                    (StateMachine.machine.state as UserDetails).unVerifiedUserDetails.onBoarding
                if (listeningForReturningUserVerification && !onBoarding) {
                    listeningForReturningUserVerification = false
                    profileIsVerifiedNowHideUnverifiedOverlay("")
                }
            }

            ScreenOfType.IN_GAME -> {
                val checkRunTutorialInGame =
                    GamerboardApp.instance.prefsHelper.getString(SharedPreferenceKeys.RUN_TUTORIAL_IN_GAME)
                if (checkRunTutorialInGame == "${OnBoardingStep.PENDING}") {
                    step9GuideAboutLeaderBoard("")
                }
            }

            ScreenOfType.ESPORT_LOGIN -> {
                clearAllOverlays()
                showAlertDialogForUnverified("")
            }

            ScreenOfType.FIRST_GAME_END -> {
                // first game end screen
            }

            else -> {

            }
        }
    }

    fun clickedOverGbIcon() {
        if (step1ToStep2OnClickGBLogo) {
            EventUtils.instance().logAnalyticsEvent(
                Events.OVERLAY_DISMISSED, mapOf(
                    "type" to "gamerboard_icon", "action" to "tutorial_gb_icon_clicked"
                )
            )
            while (step1ToStep2OnClickGBLogo) step1ToStep2OnClickGBLogo = false

            removeViewFromWindow(layoutOnboardingPopupSmall)
            step2IntroduceGbMenu("$step1ToStep2OnClickGBLogo")
        }

        if (tapsShouldShowHideMenuWithoutBlockingScreen) {

            if (serviceUIHelper.menuIsVisible) {
                serviceUIHelper.hideMenu()
            } else {
                serviceUIHelper.showMenu(x = beforeGBLogoX, y = 0)
                serviceUIHelper.setListeners(
                    listOf(
                        R.id.layout_verify, R.id.iv_help, R.id.iv_open, R.id.iv_stop
                    )
                )
            }
        }

    }

    fun clickedOverLeaderboardIcon() {
        if (step9UserClicksOnLeaderBoardIcon) {

            while (step9UserClicksOnLeaderBoardIcon) step9UserClicksOnLeaderBoardIcon = false

            removeViewFromWindow(layoutOnboardingPopupSmall)
            step9CompletedOpenLeaderBoard("$step9UserClicksOnLeaderBoardIcon")
        }
    }


    fun onTapOutSideOverlay(nextCallback: ((input: String) -> Unit)? = null) {
        nextCallback?.invoke("")
    }

    private fun removeViewFromWindow(anyView: ViewBinding?) {
        try {
            anyView?.let { if (anyView.root.parent != null) windowManager.removeView(anyView.root) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showNotVerifiedPopup(
        contentText: String? = null,
        x: Int,
        y: Int,
        displayOverGbApp: Boolean = false,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight,
        onDismiss: () -> Unit = {}
    ) {
        try {
            removeViewFromWindow(layoutNotVerifiedPopupNotVerifiedBinding)
            layoutNotVerifiedPopupNotVerifiedBinding =
                LayoutOnboardingPopupNotVerifiedBinding.inflate(
                    LayoutInflater.from(ctx)
                ).also { binding ->
                    val params: WindowManager.LayoutParams = getOverlayParams(
                        width = WindowManager.LayoutParams.WRAP_CONTENT,
                        height = WindowManager.LayoutParams.WRAP_CONTENT,
                        canBePortrait = true
                    )

                    // align from
                    params.gravity = getAlignmentToGravity(alignOverlayFrom)
                    params.x = x
                    params.y = y
                    UiUtils.setFullUiParams(binding.root)

                    binding.tvDescriptionText.text = Html.fromHtml(contentText)

                    binding.root.setBackgroundResource(
                        R.color.bg_dark_2,
                    )

                    binding.btnClose.setOnClickListener {
                        onDismiss()
                    }
                    binding.btnContainer.setOnClickListener {
                        onDismiss()
                    }
                    //add view
                    windowManager.addView(binding.root, params)
                    uiViewsManager.addUiView(
                        binding.root,
                        if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.GAME_APP
                    )
                }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    // show ui onboarding
    private fun showOnboardingPopupSmall(
        headText: String? = null,
        showArrow: Boolean? = null,
        contentText: String? = null,
        x: Int,
        y: Int,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight,
        nextCallback: ((input: String) -> Unit)? = null,
        nextBtnText: String? = null,
        nextButtonTextColor: Int? = R.color.txt_color_white,
        nextButtonColor: Int? = null,
        background: Int? = null,
        displayOverGbApp: Boolean = false,
        btnClickable: Boolean = false
    ) {
        try {
            removeViewFromWindow(layoutOnboardingPopupSmall)

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutOnboardingPopupSmall = LayoutOnboardingPopupSmallBinding.inflate(layoutInflater)

            layoutOnboardingPopupSmall?.lvNeedHelp?.visibility = View.VISIBLE

            layoutOnboardingPopupSmall?.tvJoinDiscord?.setOnClickListener {
                serviceUIHelper.openDiscordServer()
            }
            // head
            if (headText != null) layoutOnboardingPopupSmall?.tvHead?.text =
                Html.fromHtml(headText) else layoutOnboardingPopupSmall?.tvHead?.visibility =
                View.GONE

            // arrow
            if (showArrow != null) layoutOnboardingPopupSmall?.icArrow?.visibility =
                (if (showArrow) View.VISIBLE else View.GONE) else layoutOnboardingPopupSmall?.icArrow?.visibility =
                View.GONE

            // content
            if (contentText != null) layoutOnboardingPopupSmall?.tvContentText?.text =
                Html.fromHtml(contentText) else layoutOnboardingPopupSmall?.tvContentText?.visibility =
                View.GONE

            // params
            val params: WindowManager.LayoutParams = getOverlayParams(
                width = WindowManager.LayoutParams.WRAP_CONTENT,
                height = WindowManager.LayoutParams.WRAP_CONTENT,
                canBePortrait = true
            )

            // align from
            params.gravity = getAlignmentToGravity(alignOverlayFrom)
            params.x = x
            params.y = y
            UiUtils.setFullUiParams(layoutOnboardingPopupSmall?.root)

            // next button callback
            if (nextCallback == null) layoutOnboardingPopupSmall?.nextBtnContainer?.visibility =
                View.GONE
            nextCallback?.let { callback ->
                layoutOnboardingPopupSmall?.nextBtnContainer?.visibility = View.VISIBLE

                if (btnClickable) {
                    layoutOnboardingPopupSmall?.nextBtnContainer?.let { cont ->
                        cont.setOnClickListener {
                            removeViewFromWindow(layoutOnboardingPopupSmall)
                            callback.invoke("")
                        }
                    }
                } else {
                    layoutOnboardingPopupSmall?.tvNextText?.let { tv ->
                        tv.setOnClickListener {
                            removeViewFromWindow(layoutOnboardingPopupSmall)
                            callback.invoke("")
                        }
                    }
                }

                layoutOnboardingPopupSmall?.tvNextText?.let { tv ->

                    nextButtonColor?.let {
                        layoutOnboardingPopupSmall?.nextBtnContainer?.setBackgroundColor(
                            ctx.resources.getColor(
                                nextButtonColor
                            )
                        )
                        layoutOnboardingPopupSmall?.tvNextText?.setBackgroundColor(
                            ctx.resources.getColor(
                                nextButtonColor
                            )
                        )

                    }
                    tv.text = Html.fromHtml(nextBtnText)
                    nextButtonTextColor?.let {
                        tv.setTextColor(ctx.resources.getColor(nextButtonTextColor))
                    }
                }
            }

            // background resource
            layoutOnboardingPopupSmall!!.root.setBackgroundResource(
                background ?: R.drawable.bg_curved_tutorial_popup_overlay
            )

            //add view
            windowManager.addView(layoutOnboardingPopupSmall!!.root, params)
            uiViewsManager.addUiView(
                layoutOnboardingPopupSmall!!.root,
                if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.GAME_APP
            )
        } catch (ex: Exception) {
            logException(ex)
        }
    }


    private fun showOnboardingPopupWithMenu(
        headText: String? = null, showArrow: Boolean? = null,
        contentText: String? = null, x: Int, y: Int,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight,
        nextCallback: ((input: String) -> Unit)? = null,
        nextBtnText: String? = null,
        nextButtonTextColor: Int? = null,
        background: Int? = null,
        displayOverGbApp: Boolean = false,
    ) {
        try {
            removeViewFromWindow(layoutOnboardingPopupWithMenu)

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutOnboardingPopupWithMenu =
                LayoutOnboardingPopupWithMenuBinding.inflate(layoutInflater)

            // head
            if (headText != null) layoutOnboardingPopupWithMenu?.tvHead?.text =
                Html.fromHtml(headText) else layoutOnboardingPopupWithMenu?.tvHead?.visibility =
                View.GONE

            // content text
            if (contentText != null) layoutOnboardingPopupWithMenu?.tvContentText?.text =
                Html.fromHtml(contentText) else layoutOnboardingPopupWithMenu?.tvContentText?.visibility =
                View.GONE

            // params
            val params: WindowManager.LayoutParams = getOverlayParams(
                width = WindowManager.LayoutParams.WRAP_CONTENT,
                height = ctx.resources.getDimension(R.dimen.floating_menu_height).toInt(),
                canBePortrait = true
            )

            // align from
            params.gravity = getAlignmentToGravity(alignOverlayFrom)
            params.x = x
            params.y = y
            UiUtils.setFullUiParams(layoutOnboardingPopupWithMenu?.root)

            // next button callback
            if (nextCallback == null) layoutOnboardingPopupWithMenu?.tvNextText?.visibility =
                View.GONE
            nextCallback?.let { callback ->


                layoutOnboardingPopupWithMenu?.tvNextText?.visibility = View.VISIBLE
                layoutOnboardingPopupWithMenu?.tvNextText?.let { tv ->
                    tv.setOnClickListener(View.OnClickListener {
                        //removeViewFromWindow(layoutOnboardingPopupWithMenu)
                        callback.invoke("")
                    })
                    tv.text = Html.fromHtml(nextBtnText)
                    nextButtonTextColor?.let {
                        tv.setTextColor(ctx.resources.getColor(nextButtonTextColor))
                    }
                }
            }

            // background resource
            layoutOnboardingPopupWithMenu!!.root.setBackgroundResource(
                background ?: R.drawable.bg_curved_tutorial_popup_overlay
            )

            //add view
            windowManager.addView(layoutOnboardingPopupWithMenu!!.root, params)
            uiViewsManager.addUiView(
                layoutOnboardingPopupWithMenu!!.root,
                if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.GAME_APP
            )
        } catch (ex: Exception) {
            logException(ex)
        }
    }


    private fun showOnboardingPopupWithVerification(
        headText: String? = null,
        showArrow: Boolean? = null,
        contentText: String? = null,
        x: Int,
        y: Int,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight,
        nextCallback: ((input: String) -> Unit)? = null,
        nextBtnText: String? = null,
        nextButtonTextColor: Int? = null,
        background: Int? = null,
        displayOverGbApp: Boolean = false
    ) {
        //removeViewFromWindow(layoutOnboardingPopupWithMenu)
        try {
            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutOnboardingPopupWithMenu =
                layoutOnboardingPopupWithMenu ?: LayoutOnboardingPopupWithMenuBinding.inflate(
                    layoutInflater
                )

            // head
            if (headText != null) layoutOnboardingPopupWithMenu?.tvHead?.text =
                Html.fromHtml(headText) else layoutOnboardingPopupWithMenu?.tvHead?.visibility =
                View.GONE

            // replace in existing view
            var c: View =
                layoutOnboardingPopupWithMenu?.root?.findViewById<TextView>(R.id.tv_content_text) as View
            val parent = c.parent as ViewGroup
            val index = parent.indexOfChild(c)
            parent.removeView(c)
            c = layoutInflater.inflate(
                R.layout.layout_onboarding_menu_popup_verification, parent, false
            )
            parent.addView(c, index)
            c.findViewById<ImageView>(R.id.imageView2)
                .setImageResource(MachineConstants.currentGame.verifyProfileImage)
            // content text
            c.findViewById<TextView>(R.id.tv_content_text_2).text =
                Html.fromHtml(ctx.getString(R.string.gamerboard_unverified_content))
            c.findViewById<TextView>(R.id.tv_content_text_1).text =
                Html.fromHtml(ctx.getString(MachineConstants.currentGame.pleaseVerifyGameProfile))

            // params
            val params: WindowManager.LayoutParams = getOverlayParams(
                width = WindowManager.LayoutParams.WRAP_CONTENT,
                height = ctx.resources.getDimension(R.dimen.floating_menu_height).toInt(),
                canBePortrait = true
            )

            // align from
            params.gravity = getAlignmentToGravity(alignOverlayFrom)
            params.x = x
            params.y = y
            UiUtils.setFullUiParams(layoutOnboardingPopupWithMenu?.root)


            // set alpha
            //layoutOnboardingPopupWithMenu?.menuGuideInclude?.ivVerifyStatusArrow?.alpha = 0.4f
            layoutOnboardingPopupWithMenu?.menuGuideInclude?.ivHelpArrow?.alpha = 0.4f
            layoutOnboardingPopupWithMenu?.menuGuideInclude?.ivLeaderboardArrow?.alpha = 0.4f
            layoutOnboardingPopupWithMenu?.menuGuideInclude?.icOpenArraw?.alpha = 0.4f
            layoutOnboardingPopupWithMenu?.menuGuideInclude?.icStopGbArrow?.alpha = 0.4f


            //layoutOnboardingPopupWithMenu?.menuGuideInclude?.tvVerifyStatusTxt?.alpha = 0.4f
            layoutOnboardingPopupWithMenu?.menuGuideInclude?.tvHelpTxt?.alpha = 0.4f
            layoutOnboardingPopupWithMenu?.menuGuideInclude?.tvLeaderboardTxt?.alpha = 0.4f
            layoutOnboardingPopupWithMenu?.menuGuideInclude?.tvOpenTxt?.alpha = 0.4f
            layoutOnboardingPopupWithMenu?.menuGuideInclude?.tvStopGb?.alpha = 0.4f


            // next button callback
            if (nextCallback == null) layoutOnboardingPopupWithMenu?.tvNextText?.visibility =
                View.GONE
            nextCallback?.let { callback ->
                layoutOnboardingPopupWithMenu?.tvNextText?.visibility = View.VISIBLE
                layoutOnboardingPopupWithMenu?.tvNextText?.let { tv ->
                    tv.setOnClickListener(View.OnClickListener {
                        removeViewFromWindow(layoutOnboardingPopupWithMenu)
                        callback.invoke("")
                    })
                    tv.text = Html.fromHtml(nextBtnText)
                    nextButtonTextColor?.let {
                        tv.setTextColor(ctx.resources.getColor(nextButtonTextColor))
                    }
                }
            }

            // background resource
            layoutOnboardingPopupWithMenu!!.root.setBackgroundResource(
                background ?: R.drawable.bg_curved_tutorial_popup_overlay
            )

            //add view
            windowManager.updateViewLayout(layoutOnboardingPopupWithMenu!!.root, params)
            uiViewsManager.addUiView(
                layoutOnboardingPopupWithMenu!!.root,
                if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.GAME_APP
            )
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    private fun showOnboardingPopupWithUnverified(
        headText: String? = null,
        contentText: String? = null,
        x: Int,
        y: Int,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight,
        displayOverGbApp: Boolean = false
    ) {
        try {
            val onlyVerification = serviceUIHelper.isNewProfileVerificationUIEnabled
            removeViewFromWindow(layoutOnboardingMenuPopupVerificationNewUiV2Binding)

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            val params: WindowManager.LayoutParams = getOverlayParams(
                width = WindowManager.LayoutParams.WRAP_CONTENT,
                height = WindowManager.LayoutParams.WRAP_CONTENT,
                canBePortrait = true
            )

            // align from
            params.gravity = getAlignmentToGravity(alignOverlayFrom)
            params.x = x
            params.y = y

            if (onlyVerification) {
                onlyVerification(layoutInflater, params, displayOverGbApp)
            } else {
                layoutUnverifiedOverlay = LayoutUnverifiedOverlayBinding.inflate(layoutInflater)

                // head
                if (headText != null) layoutUnverifiedOverlay?.tvHead?.text =
                    Html.fromHtml(headText) else layoutUnverifiedOverlay?.tvHead?.visibility =
                    View.GONE

                // content
                if (contentText != null)
                    layoutUnverifiedOverlay?.tvUnverifiedContent?.text =
                        Html.fromHtml(contentText) else
                    layoutUnverifiedOverlay?.tvUnverifiedContent?.visibility = View.GONE
                layoutUnverifiedOverlay?.ivUnverified?.setImageResource(MachineConstants.currentGame.verifyProfileImage)


                UiUtils.setFullUiParams(layoutUnverifiedOverlay?.root)

                //add view
                windowManager.addView(layoutUnverifiedOverlay!!.root, params)
                uiViewsManager.addUiView(
                    layoutUnverifiedOverlay!!.root,
                    if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.GAME_APP
                )
            }


//             params
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    private fun onlyVerification(
        layoutInflater: LayoutInflater,
        params: WindowManager.LayoutParams,
        displayOverGbApp: Boolean
    ) {

        layoutOnboardingMenuPopupVerificationNewUiV2Binding =
            LayoutOnboardingMenuPopupVerificationNewUiV2Binding.inflate(layoutInflater)

        val tvContentText1: TextView? =
            layoutOnboardingMenuPopupVerificationNewUiV2Binding?.tvContentText1

        val tvContentText2: TextView? =
            layoutOnboardingMenuPopupVerificationNewUiV2Binding?.tvContentText2
        val btnClose: TextView? = layoutOnboardingMenuPopupVerificationNewUiV2Binding?.btnClose

        val view = layoutOnboardingMenuPopupVerificationNewUiV2Binding?.root

        UiUtils.setFullUiParams(view)
        tvContentText2?.text =
            Html.fromHtml(ctx.getString(R.string.gamerboard_new_unverified_title))

        tvContentText1?.text =
            Html.fromHtml(ctx.getString(R.string.gamerboard_new_unverified_content))

        btnClose?.setOnClickListener {
            removeViewFromWindow(layoutOnboardingMenuPopupVerificationNewUiV2Binding)
            serviceUIHelper.blockScreen(false)
        }
        //add view
        windowManager.addView(view, params)
        uiViewsManager.addUiView(
            view,
            if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.GAME_APP
        )
    }

    var canClickConfirm = true
    var enteredGameID = ""

    private fun setBtnActive() {
        layoutVerifyGameId?.btnConfirmBgmiId?.setBackgroundResource(R.color.btn_bg_solid)
        layoutVerifyGameId?.btnConfirmBgmiId?.setTextColor(ctx.getColor(R.color.txt_color_white))
    }

    private fun setBtnInActive() {
        layoutVerifyGameId?.btnConfirmBgmiId?.setBackgroundResource(R.drawable.bg_rect_stroke)
        layoutVerifyGameId?.btnConfirmBgmiId?.setTextColor(ctx.getColor(R.color.btn_bg_stroke))
    }

    private fun preCheckEditDistID(enteredBgmiIdID: String, fetchedUserId: String): Boolean {
        return LabelUtils.editDistance(enteredBgmiIdID, fetchedUserId) <= 2
    }

    @SuppressLint("SetTextI18n")
    private fun showVerifyGameIdBox(
        x: Int,
        y: Int,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight,
        displayOverGbApp: Boolean = false,
        fetchedUserId: String,
        fetchedCharacterId: String,
        bitmap: Bitmap? = null
    ) {
        try {
            removeViewFromWindow(layoutVerifyGameId)
            CoroutineScope(Dispatchers.IO).launch {
                delay(1000)
                MachineConstants.machineLabelProcessor.readyToVerify = 0
            }
            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutVerifyGameId = LayoutVerifyGameIdBinding.inflate(layoutInflater)


            // params
            val params: WindowManager.LayoutParams = getOverlayParams(
                width = WindowManager.LayoutParams.WRAP_CONTENT,
                height = WindowManager.LayoutParams.WRAP_CONTENT,
                canFocus = true
            )


            //set texts
            layoutVerifyGameId?.tvHead?.text =
                Html.fromHtml(
                    String.format(
                        ctx.resources.getString(R.string.confirm_your_id_number),
                        MachineConstants.currentGame.gameName
                    )
                )
            layoutVerifyGameId?.tvContent1?.text =
                Html.fromHtml(ctx.resources.getString(MachineConstants.currentGame.findProfileId))
            layoutVerifyGameId?.tvContent2?.text =
                Html.fromHtml(
                    String.format(
                        ctx.resources.getString(R.string.i_have_checked_my_id),
                        MachineConstants.currentGame.gameName
                    )
                )
            layoutVerifyGameId?.ivTutorialVerification?.setImageResource(MachineConstants.currentGame.verifyGameIdImage)


            //check box
            layoutVerifyGameId?.chkReadBgmiIdCnd?.setOnCheckedChangeListener { _, checked ->
                if (checked) setBtnActive()
                else setBtnInActive()
            }

            // align from
            params.gravity = getAlignmentToGravity(alignOverlayFrom)
            params.x = x
            params.y = y
            UiUtils.setFullUiParams(layoutVerifyGameId?.root)

            //fetched id

            // Do not prefill id
            val enabledPrefill = prefsHelper.getBoolean(SharedPreferenceKeys.GAME_ID_VERIFICATION)
            layoutVerifyGameId?.edBgmiId?.isEnabled = enabledPrefill.not()
            if (enabledPrefill) {
                layoutVerifyGameId?.tvHead?.text =
                    String.format(
                        ctx.getString(R.string.make_sure_your_game_id),
                        MachineConstants.currentGame.gameName
                    )
                layoutVerifyGameId?.edBgmiId?.setText(fetchedUserId)
                layoutVerifyGameId?.icArrow?.visibility = View.GONE
                layoutVerifyGameId?.layoutCheckbox?.visibility = View.GONE
                layoutVerifyGameId?.edBgmiId?.gravity = Gravity.CENTER
                layoutVerifyGameId?.tvContent1?.text = ctx.getString(R.string.please_check)
                if (bitmap?.isRecycled == false) {
                    bitmap?.let {
                        layoutVerifyGameId?.ivTutorialVerification?.setImageBitmap(it)
                    }
                }
                layoutVerifyGameId?.layoutRetry?.visibility = View.VISIBLE
                layoutVerifyGameId?.btnConfirmBgmiId?.isEnabled = false
                layoutVerifyGameId?.btnConfirmBgmiId?.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.semi_transparent_gray
                    )
                )
                val ctd = object : CountDownTimer(5000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val sec = millisUntilFinished / 1000
                        if (sec >= 1) {
                            layoutVerifyGameId?.btnConfirmBgmiId?.text =
                                ("${ctx.getString(R.string.confirm)} in $sec")
                        } else {
                            layoutVerifyGameId?.btnConfirmBgmiId?.setTextColor(
                                ContextCompat.getColor(
                                    ctx,
                                    R.color.white
                                )
                            )
                            layoutVerifyGameId?.btnConfirmBgmiId?.setBackgroundResource(R.drawable.bg_button)
                            layoutVerifyGameId?.btnConfirmBgmiId?.text =
                                ctx.getString(R.string.confirm)
                            layoutVerifyGameId?.btnConfirmBgmiId?.isEnabled = true
                        }
                    }

                    override fun onFinish() {

                    }

                }.start()

                layoutVerifyGameId?.btnConfirmBgmiId?.text =
                    "${ctx.getString(R.string.confirm)} in 4"

                layoutVerifyGameId?.btnRetry?.setOnClickListener {
                    UiUtils.showToast(ctx, ctx.getString(R.string.retrying), null)
                    serviceUIHelper.blockScreen(false)
                    listeningForUserVerification = true
                    removeViewFromWindow(layoutVerifyGameId)
                    bitmap?.recycle()
                    ctd.cancel()
                    layoutVerifyGameId = null
                }
            } else {
                layoutVerifyGameId?.icArrow?.setImageResource(R.drawable.ic_edit_grey)
                layoutVerifyGameId?.edBgmiId?.setText("")
                layoutVerifyGameId?.icArrow?.setOnClickListener {
                    layoutVerifyGameId?.edBgmiId?.requestFocus()
                    val imm: InputMethodManager? =
                        ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.showSoftInput(
                        layoutVerifyGameId?.edBgmiId,
                        InputMethodManager.SHOW_IMPLICIT
                    )
                }
            }

            layoutVerifyGameId?.edBgmiId?.showSoftInputOnFocus = true

            //btn confirm
            layoutVerifyGameId?.btnConfirmBgmiId?.setOnClickListener {
                layoutVerifyGameId?.tvError?.visibility = View.GONE

                if (enabledPrefill.not() && !layoutVerifyGameId?.chkReadBgmiIdCnd?.isChecked!!) return@setOnClickListener
                if (!canClickConfirm) return@setOnClickListener

                canClickConfirm = false
                setBtnInActive()
                layoutVerifyGameId?.btnConfirmBgmiId?.text = "Please wait..."

                layoutVerifyGameId?.edBgmiId?.text?.let { id ->
                    enteredGameID = "$id"
                    if (!preCheckEditDistID(enteredGameID, fetchedUserId)) {
                        EventUtils.instance().logAnalyticsEvent(
                            Events.GAME_PROFILE_ID_ERROR, mapOf("message" to "Game Id didn't match")
                        )
                        if (enabledPrefill)
                            layoutVerifyGameId?.edBgmiId?.error =
                                if (id.isEmpty()) "Game Id can not be empty!" else
                                    "Your Game Id didn't match!"
                        setBtnActive()
                        layoutVerifyGameId?.btnConfirmBgmiId?.text = "Confirm"
                        layoutVerifyGameId?.chkReadBgmiIdCnd?.performClick()
                        canClickConfirm = true
                        return@let
                    }
                    serviceUIHelper.updateGameProfile(fetchedCharacterId, enteredGameID) {
                        serviceUIHelper.blockScreen(false)
                        if (it == null) {
                            try {
                                EventUtils.instance()
                                    .logAnalyticsEvent(Events.GAME_PROFILE_ID_VERIFIED, mapOf())

                                // mark complete
                                GamerboardApp.instance.prefsHelper.putString(
                                    SharedPreferenceKeys.RUN_TUTORIAL, "${OnBoardingStep.COMPLETED}"
                                )

                                // this will update the current running flags
                                serviceUIHelper.checkOnBoarding()
                                bitmap?.recycle()
                                removeViewFromWindow(layoutVerifyGameId)
                                step6UserConfirmedBgmiId(
                                    confirmedBgmiId = enteredGameID,
                                    fetchedUserId = fetchedUserId,
                                    fetchedCharacterId = fetchedCharacterId
                                )
                            } catch (e: Exception) {
                                EventUtils.instance().logAnalyticsEvent(
                                    Events.GAME_PROFILE_ID_ERROR,
                                    mapOf("message" to "Unknown error")
                                )

                                setBtnActive()

                                layoutVerifyGameId?.btnConfirmBgmiId?.text = "Retry"
                                serviceUIHelper.canTapOnGbLogo = true
                                serviceUIHelper.canTapOnLeaderBoardIcon = false
                                e.printStackTrace()
                            }

                            canClickConfirm = true
                        } else {
                            EventUtils.instance().logAnalyticsEvent(
                                Events.GAME_PROFILE_ID_ERROR, mapOf("message" to it)
                            )

                            layoutVerifyGameId?.tvError?.text = it
                            layoutVerifyGameId?.tvError?.visibility = View.VISIBLE
                            setBtnActive()
                            layoutVerifyGameId?.btnConfirmBgmiId?.text = "Retry"
                            canClickConfirm = true

                            serviceUIHelper.canTapOnGbLogo = true
                            serviceUIHelper.canTapOnLeaderBoardIcon = false
                        }
                    }

                }
            }

            //add view
            windowManager.addView(layoutVerifyGameId!!.root, params)
            uiViewsManager.addUiView(
                layoutVerifyGameId!!.root,
                if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.GAME_APP
            )
            EventUtils.instance().logAnalyticsEvent(Events.GAME_PROFILE_ID_POPUP, mapOf())
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    private fun showReadyToPlay(
        headText: String? = null,
        contentText: String? = null,
        x: Int,
        y: Int,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight,
        displayOverGbApp: Boolean = false
    ) {
        try {
            removeViewFromWindow(layoutOnboardingReadyToPlay)

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutOnboardingReadyToPlay = LayoutOnboardingReadyToPlayBinding.inflate(layoutInflater)

            // head
            if (headText != null) layoutOnboardingReadyToPlay?.tvHead?.text =
                Html.fromHtml(headText) else layoutOnboardingReadyToPlay?.tvHead?.visibility =
                View.GONE

            // content
            if (contentText != null) layoutOnboardingReadyToPlay?.tvContentText?.text =
                Html.fromHtml(contentText) else layoutOnboardingReadyToPlay?.tvContentText?.visibility =
                View.GONE


            // params
            val params: WindowManager.LayoutParams = getOverlayParams(
                width = WindowManager.LayoutParams.WRAP_CONTENT,
                height = WindowManager.LayoutParams.WRAP_CONTENT
            )
            layoutOnboardingReadyToPlay?.imageView2?.setImageResource(MachineConstants.currentGame.verifyStartImage)
            //btn done
            layoutOnboardingReadyToPlay?.btnDoneOnboarding?.setOnClickListener {
                EventUtils.instance().logAnalyticsEvent(
                    Events.OVERLAY_DISMISSED, mapOf(
                        "type" to "tutorial_ready_to_play", "action" to "done"
                    )
                )
                step8OnboardingReadyToPlayComplete("Done!")
                removeViewFromWindow(layoutOnboardingReadyToPlay)
            }


            // align from
            params.gravity = getAlignmentToGravity(alignOverlayFrom)
            params.x = x
            params.y = y
            UiUtils.setFullUiParams(layoutOnboardingReadyToPlay?.root)

            //add view
            windowManager.addView(layoutOnboardingReadyToPlay!!.root, params)
            uiViewsManager.addUiView(
                layoutOnboardingReadyToPlay!!.root,
                if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.GAME_APP
            )
        } catch (ex: Exception) {
            logException(ex)
        }
    }


// show ui ingame


// show ui game end
    /*private fun showPlacementComplete(
        x: Int,
        y: Int,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight,
        displayOverGbApp: Boolean = false,
        game: Game
    ) {
        removeViewFromWindow(layoutOnboardingPlacementMatchComplete)

        val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
        layoutOnboardingPlacementMatchComplete =
            LayoutOnboardingPlacementMatchCompleteBinding.inflate(layoutInflater)

        //set texts
        layoutOnboardingPlacementMatchComplete?.inclPlacementComplete?.tvPlacementComplete?.text =
            Html.fromHtml(ctx.resources.getString(R.string.placement_match_completed))
        layoutOnboardingPlacementMatchComplete?.inclPlacementComplete?.tvPlacementCompleteContentBottom?.text =
            Html.fromHtml(ctx.resources.getString(R.string.you_will_be_competing_in_gamerboard))
        layoutOnboardingPlacementMatchComplete?.inclPlacementComplete?.tvPlacementCompleteContent1?.text =
            Html.fromHtml(ctx.resources.getString(R.string.open_the_full_gamerboard_app))


        // params
        val params: WindowManager.LayoutParams = getOverlayParams(
            width = WindowManager.LayoutParams.WRAP_CONTENT,
            height = WindowManager.LayoutParams.WRAP_CONTENT
        )

        val level = if (game.finalTier != UNKNOWN) game.finalTier?.split(" ")?.get(0) ?: "" else ""
        layoutOnboardingPlacementMatchComplete?.inclPlacementComplete?.tvPlacementLevel?.text =
            level

        // rating icon
        try {
            val ratingsUrl = getTierUrl("bgmi", game.finalTier!!.split('_').first())
            Glide.with(ctx).load(ratingsUrl)
                .into(layoutOnboardingPlacementMatchComplete?.inclPlacementComplete?.ivRatingIconTutorial!!)
        } catch (e: java.lang.Exception) {
            logException(e)
        }

        //btn close
        layoutOnboardingPlacementMatchComplete?.postMatchSummary?.btnOpenGb?.setOnClickListener {
            EventUtils.instance()
                .logAnalyticsEvent(
                    Events.OVERLAY_DISMISSED, mapOf(
                        "type" to "tutorial_complete",
                        "action" to "open_gb"
                    )
                )
            // now he can click after closing the pop up
            serviceUIHelper.canTapOnGbLogo = true
            serviceUIHelper.launchGamerboard()


            step11PlacementPopUpClosed("Close!")
            removeViewFromWindow(layoutOnboardingPlacementMatchComplete)
        }

        // align from
        params.gravity = getAlignmentToGravity(alignOverlayFrom)
        params.x = x
        params.y = y
        UiUtils.setFullUiParams(layoutOnboardingPlacementMatchComplete?.root)
        EventUtils.instance()
            .logAnalyticsEvent(
                Events.OVERLAY_SHOWN, mapOf(
                    "type" to "tutorial_complete",
                )
            )
        //add view
        windowManager.addView(layoutOnboardingPlacementMatchComplete!!.root, params)
        uiViewsManager.addUiView(
            layoutOnboardingPlacementMatchComplete!!.root,
            if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.BGMI_APP
        )

    }*/


    private fun showPostGameScoresSkillChange(
        x: Int,
        y: Int,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight,
        displayOverGbApp: Boolean = false,
        customGameResponse: CustomGameResponse
    ) {
        try {
            removeViewFromWindow(layoutPostGameScoresSkillLevel)

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutPostGameScoresSkillLevel =
                LayoutPostMatchScoreWithSkillLevelBinding.inflate(layoutInflater)

            //set texts

            // params
            val params: WindowManager.LayoutParams = getOverlayParams(
                width = WindowManager.LayoutParams.WRAP_CONTENT,
                height = WindowManager.LayoutParams.WRAP_CONTENT
            )

            //set adapter

            if (customGameResponse.serverGame.tournaments.isEmpty()) {
                layoutPostGameScoresSkillLevel?.inclPostMatchSkillChange?.rvPostMatchIncludeLeaderboard?.visibility =
                    View.GONE
                layoutPostGameScoresSkillLevel?.inclPostMatchSkillChange?.emptyView?.visibility =
                    View.VISIBLE
            } else {
                layoutPostGameScoresSkillLevel?.inclPostMatchSkillChange?.emptyView?.visibility =
                    View.GONE
                layoutPostGameScoresSkillLevel?.inclPostMatchSkillChange?.rvPostMatchIncludeLeaderboard?.visibility =
                    View.VISIBLE

                val adapterJoinedTournaments =
                    AdapterJoinedTournaments(customGameResponse.serverGame.tournaments)
                layoutPostGameScoresSkillLevel?.inclPostMatchSkillChange?.rvPostMatchIncludeLeaderboard?.layoutManager =
                    LinearLayoutManager(ctx)
                layoutPostGameScoresSkillLevel?.inclPostMatchSkillChange?.rvPostMatchIncludeLeaderboard?.adapter =
                    adapterJoinedTournaments
            }
            // points
            val serverGame = customGameResponse.serverGame.game
            val scoring = customGameResponse.scoring.scoring

            val rank = serverGame.rank
            val rankPoints = (scoring.rankPoints.find { it.rank == serverGame.rank }?.points) ?: 0

            val kills = serverGame.getKills()
            val killPoints = kills * scoring.killPoints

            val level = serverGame.getFinalTier().replace("_", " ")

            val totalPoints = serverGame.score.toInt()

            layoutPostGameScoresSkillLevel?.postMatchSummary?.tvRankPoints?.text =
                Html.fromHtml("<b>+$rankPoints</b> pts")
            layoutPostGameScoresSkillLevel?.postMatchSummary?.tvKillsPoints?.text =
                Html.fromHtml("<b>+$killPoints</b> pts")
            layoutPostGameScoresSkillLevel?.postMatchSummary?.tvMatchPoints?.text =
                Html.fromHtml("<b>$totalPoints</b> pts")
            layoutPostGameScoresSkillLevel?.postMatchSummary?.tvGameKills?.text =
                Html.fromHtml("Match Kills: <b>$kills</b>")
            layoutPostGameScoresSkillLevel?.postMatchSummary?.tvGameRank?.text =
                Html.fromHtml("Match Rank: <b>$rank</b>")

            layoutPostGameScoresSkillLevel?.inclPostMatchSkillChange?.tvLevel?.text =
                Html.fromHtml("<b>$level</b>")

            // rating icon
            try {
                val ratingsUrl = getTierUrl("bgmi", serverGame.getFinalTier().split('_').first())
                Glide.with(ctx).load(ratingsUrl)
                    .into(layoutPostGameScoresSkillLevel?.inclPostMatchSkillChange?.ivRatingIcon!!)
            } catch (e: java.lang.Exception) {
                logException(e)
            }
            //btn close
            layoutPostGameScoresSkillLevel?.btnClose?.setOnClickListener {

                // now he can click after closing the pop up
                serviceUIHelper.canTapOnGbLogo = true
                step11OnBoardingPopUpClosed("Close!")
                removeViewFromWindow(layoutPostGameScoresSkillLevel)
                requestFeedback()
            }

            // align from
            params.gravity = getAlignmentToGravity(alignOverlayFrom)
            params.x = x
            params.y = y
            UiUtils.setFullUiParams(layoutPostGameScoresSkillLevel?.root)
            EventUtils.instance().logAnalyticsEvent(
                Events.OVERLAY_SHOWN, mapOf(
                    "type" to "game_score",
                )
            )
            //add view
            windowManager.addView(layoutPostGameScoresSkillLevel!!.root, params)
            uiViewsManager.addUiView(
                layoutPostGameScoresSkillLevel!!.root,
                if (displayOverGbApp) UiViewsManager.OverlayOn.GB_APP else UiViewsManager.OverlayOn.GAME_APP
            )
        } catch (ex: Exception) {
            logException(ex)
        }
    }


    fun clearAllOverlays() {
        try {

            removeViewFromWindow(layoutOnboardingPopupSmall)
            removeViewFromWindow(layoutOnboardingPopupWithMenu)
            removeViewFromWindow(layoutUnverifiedOverlay)
            removeViewFromWindow(layoutOnboardingMenuPopupVerificationNewUiV2Binding)

            removeViewFromWindow(layoutOnboardingReadyToPlay)
            removeViewFromWindow(layoutVerifyGameId)
            //removeViewFromWindow(layoutOnboardingPlacementMatchComplete)
            removeViewFromWindow(layoutPostGameScoresSkillLevel)

            layoutOnboardingPopupSmall = null
            layoutOnboardingPopupWithMenu = null
            layoutUnverifiedOverlay = null
            layoutOnboardingMenuPopupVerificationNewUiV2Binding = null
            layoutVerifyGameId = null
            layoutOnboardingReadyToPlay = null
            //layoutOnboardingPlacementMatchComplete = null
            layoutPostGameScoresSkillLevel = null

            serviceUIHelper.blockScreen(false)
            uiViewsManager.clearAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showAlertOnUserNameChange(
        originalUsername: String, updatedUsername: String, callback: CallBack<Boolean>
    ) {
        try {
            removeViewFromWindow(layoutUserNameUpdateBinding)

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutUserNameUpdateBinding = LayoutUserNameUpdateBinding.inflate(layoutInflater)

            //set texts

            // params
            val params: WindowManager.LayoutParams = getOverlayParams(
                width = WindowManager.LayoutParams.WRAP_CONTENT,
                height = WindowManager.LayoutParams.WRAP_CONTENT
            )

            //set adapter

            layoutUserNameUpdateBinding?.tvTitle?.text = String.format(
                ctx.getString(
                    R.string.username_change_detected,
                    MachineConstants.currentGame.gameName
                )
            )

            layoutUserNameUpdateBinding?.tvJoinDiscord?.setOnClickListener {
                serviceUIHelper.openDiscordServer()
            }
            layoutUserNameUpdateBinding?.btnNo?.setOnClickListener {

                // now he can click after closing the pop up
                removeViewFromWindow(layoutUserNameUpdateBinding)
                callback.onDone(false)
            }
            layoutUserNameUpdateBinding?.btnYes?.setOnClickListener {

                // now he can click after closing the pop up
                removeViewFromWindow(layoutUserNameUpdateBinding)
                callback.onDone(true)
            }
            layoutUserNameUpdateBinding?.tvAlert1?.text = Html.fromHtml(
                String.format(ctx.getString(R.string.username_change_title), updatedUsername),
                Html.FROM_HTML_MODE_LEGACY
            )
            // align from
            params.gravity = getAlignmentToGravity(UiUtils.AlignOverlayFrom.Center)
            params.x = 0
            params.y = 0
            UiUtils.setFullUiParams(layoutUserNameUpdateBinding?.root)
            EventUtils.instance().logAnalyticsEvent(
                Events.OVERLAY_SHOWN, mapOf(
                    "type" to "username_update_alert",
                )
            )
            //add view
            windowManager.addView(layoutUserNameUpdateBinding!!.root, params)
            uiViewsManager.addUiView(
                layoutUserNameUpdateBinding!!.root, UiViewsManager.OverlayOn.GAME_APP
            )
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    fun showProfileIdMismatch(
        originalUserId: String, fetchedUserId: String, callback: CallBack<Boolean>
    ) {
        try {
            removeViewFromWindow(layoutUserIdMismatchBinding)

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutUserIdMismatchBinding = LayoutUserIdMismatchBinding.inflate(layoutInflater)

            //set texts

            // params
            val params: WindowManager.LayoutParams = getOverlayParams(
                width = WindowManager.LayoutParams.MATCH_PARENT,
                height = WindowManager.LayoutParams.WRAP_CONTENT
            )

            //set adapter
            layoutUserIdMismatchBinding?.tvTitle?.text = String.format(
                ctx.getString(
                    R.string.user_id_mismatch,
                    MachineConstants.currentGame.gameName
                )
            )

            layoutUserIdMismatchBinding?.tvJoinDiscord?.setOnClickListener {
                serviceUIHelper.openDiscordServer()
            }
            layoutUserIdMismatchBinding?.btnOk?.setOnClickListener {

                // now he can click after closing the pop up
                removeViewFromWindow(layoutUserIdMismatchBinding)
                callback.onDone(true)
            }
            layoutUserIdMismatchBinding?.tvAlert1?.text = Html.fromHtml(
                String.format(
                    ctx.getString(R.string.user_id_mismatch_details),
                    MachineConstants.currentGame.gameName,
                    fetchedUserId, originalUserId
                ),
                Html.FROM_HTML_MODE_LEGACY
            )
            layoutUserIdMismatchBinding?.tvAlert2?.text =
                String.format(
                    ctx.getString(R.string.user_id_mismatch_support),
                    if (MachineConstants.currentGame == SupportedGames.FREEFIRE) ctx.getString(R.string.user_id_mismatch_support_ff) else "",
                )
            // align from
            params.gravity = getAlignmentToGravity(UiUtils.AlignOverlayFrom.Center)
            params.x = 0
            params.y = 0
            UiUtils.setFullUiParams(layoutUserIdMismatchBinding?.root)
            EventUtils.instance().logAnalyticsEvent(
                Events.OVERLAY_SHOWN, mapOf(
                    "type" to "userid_mismatch",
                )
            )
            //add view
            windowManager.addView(layoutUserIdMismatchBinding!!.root, params)
            uiViewsManager.addUiView(
                layoutUserIdMismatchBinding!!.root, UiViewsManager.OverlayOn.GAME_APP
            )
        } catch (ex: Exception) {
            logException(ex)
        }
    }
}

enum class OnBoardingStep { NOT_STARTED, COMPLETED, PENDING }
enum class ScreenOfType {
    HOME, PROFILE_VERIFIED, PRE_PROFILE_VERIFICATION, SERVICE_STOP,
    IN_GAME, GAME_END, FIRST_GAME_END, ESPORT_LOGIN, USERNAME_UPDATE_ALERT,
    USER_ID_MISMATCH,
    MULTIPLAYER_GAME_END_WITH_KILLS, MULTIPLAYER_GAME_END_WITHOUT_KILLS
}

class UiViewsManager(
    private val serviceUIHelper: ServiceUIHelper, private val windowManager: WindowManager
) {
    // show hide overlays

    private var uiViews: ArrayList<Pair<View?, OverlayOn>> = arrayListOf()

    init {


    }

    fun clearAll() {
        uiViews.forEach { view -> removeUiView(view = view.first, OverlayOn.GAME_APP) }
        uiViews.forEach { view -> removeUiView(view = view.first, OverlayOn.GB_APP) }
        uiViews.forEach { view -> removeUiView(view = view.first, OverlayOn.NONE) }
        uiViews.clear()
    }

    fun addUiView(view: View?, overlayOn: OverlayOn) {
        CoroutineScope(Dispatchers.Main).launch {
            //delay(1000)
            if (uiViews.find { it.first == view && it.second == overlayOn } == null) {
                uiViews.add(Pair(view, overlayOn))
            }
        }
    }

    fun removeUiView(view: View?, overlayOn: OverlayOn) {
        //uiViews.removeIf { it.first == view && it.second == overlayOn }
    }

    private fun hideOverlays(hideOn: OverlayOn) {
        for (view in uiViews) if (view.second == hideOn || (hideOn == OverlayOn.NONE)) view.first?.visibility =
            View.GONE
    }

    private fun showOverlays(showOn: OverlayOn) {
        for (view in uiViews) if (view.second == showOn) {
            view.first?.visibility = View.VISIBLE
            UiUtils.setFullUiParams(view.first)
        }
    }

    fun updateUiForService(appActive: Boolean, currentAppPkg: String?) {
        val onForeground = MainActivity.gbOnForeground
        showOverlays(
            if ((currentAppPkg == BuildConfig.APPLICATION_ID && !onForeground) || GAME_PACKAGES.contains(
                    currentAppPkg
                ) || appActive
            ) OverlayOn.GAME_APP
            else if ((currentAppPkg == BuildConfig.APPLICATION_ID && onForeground)) OverlayOn.GB_APP
            else OverlayOn.NONE
        )

        hideOverlays(
            if ((currentAppPkg == BuildConfig.APPLICATION_ID && !onForeground) || GAME_PACKAGES.contains(
                    currentAppPkg
                ) || appActive
            ) OverlayOn.GB_APP
            else if ((currentAppPkg == BuildConfig.APPLICATION_ID && onForeground)) OverlayOn.GAME_APP
            else OverlayOn.NONE
        )
    }

    enum class OverlayOn { GAME_APP, GB_APP, NONE }
}