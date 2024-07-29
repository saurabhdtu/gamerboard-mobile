package com.gamerboard.live.service.screencapture.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.Keep
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gamerboard.live.*
import com.gamerboard.live.common.BroadcastFilters
import com.gamerboard.live.common.IntentKeys
import com.gamerboard.live.common.PrefsHelper
import com.gamerboard.live.common.RemoteConfigConstants
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.databinding.*
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.showToast
import com.gamerboard.live.gamestatemachine.stateMachine.*
import com.gamerboard.live.models.CustomGameResponse
import com.gamerboard.live.models.FeedBackFrom
import com.gamerboard.live.repository.ApiClient
import com.gamerboard.live.repository.GameRepository
import com.gamerboard.live.repository.ReTryGameSubmit
import com.gamerboard.live.repository.SessionManager
import com.gamerboard.live.service.screencapture.ScreenCaptureService
import com.gamerboard.live.service.screencapture.ui.adapters.AdapterLeaderboard
import com.gamerboard.live.service.screencapture.ui.adapters.LeaderboardPagination
import com.gamerboard.live.utils.*
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


/**
 * Created by saurabh.lahoti on 01/09/21
 */

@Suppress("PropertyName")
class ServiceUIHelper(
    val ctx: Context,
    var windowManager: WindowManager,
    val apiClient: ApiClient
) : KoinComponent {
    var currentProfileStatus = false
    private var lastStatus = 0
    var FLOATING_LOGO_SIZE = ctx.resources.getDimension(R.dimen.floating_logo_size).toInt()
    var GB_LOGO_PIN_LOCATION_Y = UiUtils.convertDpToPixel(60, ctx)
    var GB_LOGO_MARGIN = UiUtils.convertDpToPixel(6, ctx)
    var FLOATING_MENU_WIDTH = ctx.resources.getDimension(R.dimen.floating_menu_width).toInt()
    var FLOATING_MENU_HEIGHT = ctx.resources.getDimension(R.dimen.floating_menu_height).toInt()
    var prevToast = 0

    private val LOG_TAG = "ServiceUIHelper"

    private var MEASURE_6DP = UiUtils.convertDpToPixel(6, ctx)
    var paramsGBLogo: WindowManager.LayoutParams? = null
    var notVerifiedTagParams: WindowManager.LayoutParams? = null
    private var paramsRestart: WindowManager.LayoutParams? = null
    var layoutFloatingLogoBinding: LayoutFloatingLogoBinding? = null
    var layoutNotVerifiedTag: LayoutNotVerifiedTagBinding? = null
    var layoutMenuBinding: LayoutFloatingMenuBinding? = null
    var layoutMenuParams: WindowManager.LayoutParams? = null
    private var layoutFloatingProgressBinding: LayoutFloatingProgressBinding? = null
    private var layoutKillsFetchedBinding: LayoutKillsFetchedBinding? = null

    var onBoardingUI: UserGuideUI? = null
    var shouldIgnoreEventsBefore = true
    var onBoardingHomeScreenRunning = false
    var onBoardingInGameRunning = false

    /*var onBoardingPostGameRunning = false*/
    companion object {
        var isFullScreen = false
    }

    var layoutBlockScreenOverlayBinding: BlockScreenOverlayBinding? = null
    var layoutLeaderBoardLayoutBinding: LeaderBoardLayoutBinding? = null
    var layoutCustomAlertDialogBinding: LayoutCustomAlertDialogBinding? = null

    /*private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        fun onGlobalLayout() {
            isFullScreen = display.y == helperWnd?.height
            Log.d("status-bar", "GlobalLayoutListener: $isFullScreen")
        }
    }*/
    var canMoveGbLogoVertically = false
    var canTapOnGbLogo = false
    var canTapOnLeaderBoardIcon = false

    //On the first time not verified tag is shown when the oboarding unverified popup is shown and this flag
    //is used when user again return to home screen from login or any other screen so it can show the tag animation again
    private var notVerifiedAlreadyShownFirstTime = false
    private var notVerifiedAnimationRunning = false

    private var notVerifiedAnimationJobContext: CoroutineContext =
        Dispatchers.Main + SupervisorJob()

    var menuIsVisible = false

    private var serviceRestartLayout: View? = null
    private var display = Point()
    private var densityDPI = 0
    private val displayMetrics = DisplayMetrics()
    var orientation = ScreenOrientation.LANDSCAPE
        set(value) {
            field = value
            calculateDisplayDimens()
        }

    private val prefsHelper: PrefsHelper by inject()

    val isNewProfileVerificationUIEnabled: Boolean
        get() = prefsHelper.getBoolean(
            SharedPreferenceKeys.NEW_PROFILE_VERIFY_UI
        )

    init {
        isFullScreen = true
        checkOnBoarding()
    }

    fun checkOnBoarding() {
        val checkRunTutorial =
            GamerboardApp.instance.prefsHelper.getString(SharedPreferenceKeys.RUN_TUTORIAL)
        val checkRunTutorialInGame =
            GamerboardApp.instance.prefsHelper.getString(SharedPreferenceKeys.RUN_TUTORIAL_IN_GAME)
        /*val checkRunTutorialPostGame =
            GamerboardApp.instance.prefsHelper.getString(SharedPreferenceKeys.RUN_TUTORIAL_POST_GAME)*/
        onBoardingHomeScreenRunning = (checkRunTutorial == "${OnBoardingStep.PENDING}")
        onBoardingInGameRunning = (checkRunTutorialInGame == "${OnBoardingStep.PENDING}")
        /*onBoardingPostGameRunning = (checkRunTutorialPostGame == "${OnBoardingStep.PENDING}")*/
        shouldIgnoreEventsBefore = false
    }

    private fun setUpOnBoarding(screenDimension: Point) {
        onBoardingUI = onBoardingUI ?: UserGuideUI(ctx, windowManager, screenDimension, this)
        canMoveGbLogoVertically = false
        canTapOnGbLogo = false
        canTapOnLeaderBoardIcon = (!onBoardingInGameRunning)
        onBoardingUI?.setUpOnBoarding()
    }

    private fun setUpOnBoardingInGame(screenDimension: Point) {
        onBoardingUI = onBoardingUI ?: UserGuideUI(ctx, windowManager, screenDimension, this)
        canMoveGbLogoVertically = true
        canTapOnGbLogo = true
        canTapOnLeaderBoardIcon = false
        MachineConstants.machineLabelProcessor.readyToVerify = 1
        onBoardingUI?.setUpOnBoarding()
    }

    private fun setUpOnBoardingPostGame(screenDimension: Point) {
        onBoardingUI = onBoardingUI ?: UserGuideUI(ctx, windowManager, screenDimension, this)
        canMoveGbLogoVertically = true
        canTapOnGbLogo = true
        canTapOnLeaderBoardIcon = true
        MachineConstants.machineLabelProcessor.readyToVerify = 1
        onBoardingUI?.setUpOnBoarding()
    }

    fun cleanUpOnBoarding() {
        onBoardingUI?.clearAllOverlays()
        //onBoardingUI = null
        canMoveGbLogoVertically = true
        canTapOnGbLogo = true
        canTapOnLeaderBoardIcon = (!onBoardingInGameRunning)
        setListeners(
            listOf(
                R.id.layout_verify, R.id.iv_help, R.id.iv_leaderboard, R.id.iv_open, R.id.iv_stop
            )
        )
        blockScreen(false)
        MachineConstants.machineLabelProcessor.showLoaderCircularBuffer.clear()
        MachineConstants.machineLabelProcessor.readyToVerify = 1
    }

    fun clearResources() {
        try {
            log("Clearing view")
            layoutFloatingLogoBinding?.let { windowManager.removeView(it.root) }
            serviceRestartLayout?.let { windowManager.removeView(it) }
            layoutFloatingProgressBinding?.let { windowManager.removeView(it.root) }
            layoutNotVerifiedTag?.let { windowManager.removeView(it.root) }

            layoutFloatingLogoBinding = null
            layoutNotVerifiedTag = null
            serviceRestartLayout = null
            layoutFloatingProgressBinding = null
            Log.d("status-bar", "removed-global-layout listener")
            /*helperWnd?.let {
                it.viewTreeObserver?.removeOnGlobalLayoutListener { layoutListener }
                windowManager.removeView(it)
            }*/
            hideMenu()

            layoutBlockScreenOverlayBinding?.let { windowManager.removeView(it.root) }
            layoutBlockScreenOverlayBinding = null

            hideLeaderBoard("")
            onBoardingUI?.stopOnBoardingTutorial()
            onBoardingUI = onBoardingUI ?: UserGuideUI(ctx, windowManager, display, this)
            onBoardingUI?.clearAllOverlays()
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun inflateBubbleLayout() {
        try {
            log("Inflating floating layout")
            clearResources()
            onBoardingUI?.clearAllOverlays()
//        setupListenerToDetectStatusBar()
            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutFloatingLogoBinding = LayoutFloatingLogoBinding.inflate(layoutInflater)
            layoutNotVerifiedTag = LayoutNotVerifiedTagBinding.inflate(layoutInflater)
            /*floatingBubbleView =
                    layoutInflater.inflate(R.layout.layout_floating_view, null, false)*/
            //setRecordingStatus(0, null)
            //setVerifiedUserStatus()
            paramsGBLogo = UiUtils.getOverlayParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                canBePortrait = true
            )


            /*(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)*/
            paramsGBLogo?.gravity = Gravity.END or Gravity.TOP
            var isAnimationOn =
                GamerboardApp.instance.prefsHelper.getBoolean(SharedPreferenceKeys.SHOW_VERIFICATION_ONLY) && (GamerboardApp.instance.prefsHelper.getString(
                    SharedPreferenceKeys.CURRENT_GAME_NAME
                ) == "BGMI")

            if (isAnimationOn) {
                FLOATING_LOGO_SIZE = (FLOATING_LOGO_SIZE * (1.5)).roundToInt();
            }

            paramsGBLogo?.width = FLOATING_LOGO_SIZE
            paramsGBLogo?.x = GB_LOGO_MARGIN
            paramsGBLogo?.y = GB_LOGO_PIN_LOCATION_Y
            //-1 * (FLOATING_LAYOUT_HEIGHT / 2) + UiUtils.convertDpToPixel(22, ctx)
            //-1 * (display.y - (UiUtils.convertDpToPixel(22, ctx) + FLOATING_LAYOUT_HEIGHT)) / 2
            /*(-1 * display.y / 2)+ (UiUtils.convertDpToPixel(10, ctx) + FLOATING_LOGO_SIZE)*/

            inflateBlockScreen()

            notVerifiedTagParams = UiUtils.getOverlayParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                canBePortrait = true
            ).apply {
                width = 0
                x = GB_LOGO_MARGIN
                y = GB_LOGO_PIN_LOCATION_Y
                gravity = Gravity.END or Gravity.TOP
                layoutNotVerifiedTag!!.root.visibility = View.GONE
                windowManager.addView(layoutNotVerifiedTag!!.root, this)
            }
            windowManager.addView(layoutFloatingLogoBinding!!.root, paramsGBLogo)

            val bufferSize: Int = UiUtils.convertDpToPixel(20, ctx)
            layoutFloatingLogoBinding?.frameFloater?.setOnTouchListener(object :
                View.OnTouchListener {
                private var lastAction = 0
                private var initialX = 0
                private var initialY = 0
                private val topLimit = UiUtils.convertDpToPixel(38, ctx)
                private var initialTouchX = 0f
                private var initialTouchY = 0f
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {

                            //remember the initial position.
                            if (paramsGBLogo != null) {
                                initialX = paramsGBLogo!!.x
                                initialY = paramsGBLogo!!.y
                            }

                            //get the touch location
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            lastAction = event.action
                            return true
                        }

                        MotionEvent.ACTION_UP -> {
                            if (lastAction == MotionEvent.ACTION_DOWN || ((lastAction == MotionEvent.ACTION_MOVE) && (abs(
                                    event.rawX - initialTouchX
                                ) < MEASURE_6DP) && (abs(
                                    event.rawY - initialTouchY
                                ) < MEASURE_6DP))
                            ) {
                                // tapped on logo
                                if (canTapOnGbLogo) {
                                    showExpandedLayout()
                                } else {
                                    onBoardingUI?.clickedOverGbIcon()
                                }
                            }
                            lastAction = event.action
                            return true
                        }

                        MotionEvent.ACTION_MOVE -> {

                            if (!canMoveGbLogoVertically) return true

                            val y = initialY + (event.rawY - initialTouchY).toInt()
                            if (paramsGBLogo != null && y > 0 && (y + FLOATING_LOGO_SIZE) < (display.y - topLimit) && (y + FLOATING_LOGO_SIZE > topLimit * 2)) {
                                paramsGBLogo!!.y = y
                                notVerifiedTagParams?.y = y
                            }
                            layoutNotVerifiedTag?.let { layout ->
                                windowManager.updateViewLayout(layout.root, notVerifiedTagParams)

                            }
                            layoutFloatingLogoBinding?.let { layout ->
                                windowManager.updateViewLayout(layout.root, paramsGBLogo)
                                lastAction = event.action
                            }
                            return true
                        }
                    }
                    return false
                }
            })
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    private fun inflateBlockScreen() {
        try {
            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutBlockScreenOverlayBinding = BlockScreenOverlayBinding.inflate(layoutInflater)
            val params: WindowManager.LayoutParams = UiUtils.getOverlayParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                canBePortrait = true
            )
            /*params.width = display.x + 20
            params.height = display.y + 20*/

            UiUtils.setFullUiParams(layoutBlockScreenOverlayBinding?.root)
            layoutBlockScreenOverlayBinding?.root!!.visibility = View.GONE
            windowManager.addView(layoutBlockScreenOverlayBinding!!.root, params)
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    private fun popDownOverlays(nextCallback: ((input: String) -> Unit)? = null) {
        hideMenu()
        hideLeaderBoard("")
        blockScreen(shouldBlockTap = false)
        nextCallback?.apply {
            nextCallback.invoke("Next")
        }
    }

    fun blockScreen(
        shouldBlockTap: Boolean? = null,
        popDownOnTap: Boolean = false,
        nextCallback: ((input: String) -> Unit)? = null,
        clippedBackground: Int? = null
    ): Boolean {
        val wasVisible = layoutBlockScreenOverlayBinding?.root?.visibility == View.GONE

        if (shouldBlockTap == null) return wasVisible

        if (shouldBlockTap) {
            if (clippedBackground != null) {
                layoutBlockScreenOverlayBinding?.root?.setBackgroundResource(clippedBackground)
            } else layoutBlockScreenOverlayBinding?.root?.setBackgroundColor(ctx.getColor(R.color.bg_block_screen_color))

            layoutBlockScreenOverlayBinding?.root?.visibility = View.VISIBLE
            layoutBlockScreenOverlayBinding?.root?.setOnClickListener(View.OnClickListener {
                if (popDownOnTap) {
                    layoutBlockScreenOverlayBinding?.root?.visibility = View.GONE
                    onBoardingUI?.uiViewsManager?.removeUiView(
                        layoutBlockScreenOverlayBinding?.root, UiViewsManager.OverlayOn.GAME_APP
                    )
                    onBoardingUI?.onTapOutSideOverlay(nextCallback = nextCallback)
                    nextCallback?.invoke("")
                }
            })
            onBoardingUI?.uiViewsManager?.addUiView(
                layoutBlockScreenOverlayBinding?.root, UiViewsManager.OverlayOn.GAME_APP
            )
        } else {
            layoutBlockScreenOverlayBinding?.root?.visibility = View.GONE
            onBoardingUI?.uiViewsManager?.removeUiView(
                layoutBlockScreenOverlayBinding?.root, UiViewsManager.OverlayOn.GAME_APP
            )
        }
        return wasVisible
    }

    fun hideLeaderBoard(message: String) {
        layoutLeaderBoardLayoutBinding?.let { windowManager.removeView(it.root) }
        layoutLeaderBoardLayoutBinding = null
    }

    private fun hidePopupWithLeaderBoard(message: String) {
        popDownOverlays()
    }

    fun showLeaderBoard(
        x: Int,
        y: Int,
        alignOverlayFrom: UiUtils.AlignOverlayFrom? = UiUtils.AlignOverlayFrom.TopRight
    ) {
        try {
            blockScreen(
                shouldBlockTap = true,
                popDownOnTap = true,
                nextCallback = this::hidePopupWithLeaderBoard,
                clippedBackground = R.color.bg_block_screen_color_transparent
            )
            layoutLeaderBoardLayoutBinding?.let { windowManager.removeView(it.root) }
            layoutLeaderBoardLayoutBinding = null

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutLeaderBoardLayoutBinding = LeaderBoardLayoutBinding.inflate(layoutInflater)

            val params: WindowManager.LayoutParams = UiUtils.getOverlayParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
            )

            params.gravity = UiUtils.getAlignmentToGravity(alignOverlayFrom)
            params.x = x
            params.y = y
            params.width = ctx.resources.getDimension(R.dimen.floating_layout_width).toInt()
            params.height = ctx.resources.getDimension(R.dimen.floating_menu_height).toInt()


            val rvLeaderboard = layoutLeaderBoardLayoutBinding?.rvLeaderboard
            val tbLeaderBoard = layoutLeaderBoardLayoutBinding?.lbTabview
            val progressBar = layoutLeaderBoardLayoutBinding?.lbProgress
            val refreshLayout = layoutLeaderBoardLayoutBinding?.lbRefresh
            val lbPlaceHolderEmpty = layoutLeaderBoardLayoutBinding?.lbNoActiveTournaments


            val adapterLeaderboard = AdapterLeaderboard(ctx, apiClient)
            rvLeaderboard?.layoutManager = LinearLayoutManager(ctx)
            rvLeaderboard?.adapter = adapterLeaderboard
            layoutLeaderBoardLayoutBinding?.root?.setOnClickListener { _ -> }
            adapterLeaderboard.leaderboardPagination = LeaderboardPagination(
                ctx,
                rvLeaderboard!!,
                tbLeaderBoard!!,
                progressBar!!,
                apiClient,
                refreshLayout!!,
                lbPlaceHolderEmpty!!
            )
            adapterLeaderboard.leaderboardPagination.getActiveTournaments(apiClient)

            layoutMenuBinding?.menuLeaderboardHighlight?.background = null
            windowManager.addView(layoutLeaderBoardLayoutBinding!!.root, params)
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    fun showKillsFetchedOverlay(kills: String?) {
        try {
            layoutKillsFetchedBinding?.let { windowManager.removeView(it.root) }
            layoutKillsFetchedBinding = null
            loaderOnScreen(false, null)
            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutKillsFetchedBinding = LayoutKillsFetchedBinding.inflate(layoutInflater)

            val params: WindowManager.LayoutParams = UiUtils.getOverlayParams(
                (display.x * 0.75f).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT,
                canBePortrait = true
            )
            params.gravity = Gravity.END or Gravity.BOTTOM

            layoutKillsFetchedBinding!!.tvKills.text =
                if (kills == null) ctx.getString(R.string.kills_processed) else String.format(
                    ctx.getString(R.string.kills_captured), kills
                )

            layoutKillsFetchedBinding!!.tvDontSkip.text =
                SpannableString(ctx.getString(R.string.dont_skip)).apply {
                    this.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                ctx,
                                R.color.verified
                            )
                        ),
                        15,
                        ctx.getString(R.string.dont_skip).length,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                }
            layoutKillsFetchedBinding!!.ivClose.setOnClickListener {
                windowManager.removeView(layoutKillsFetchedBinding?.root)
                layoutKillsFetchedBinding = null
                autoDismissTimer.cancel()
            }
            windowManager.addView(layoutKillsFetchedBinding?.root, params)
            autoDismissTimer.start()
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    fun showExpandedLayout() {
        if (menuIsVisible) {
            hideExpandedLayout()
            return
        }
        blockScreen(
            shouldBlockTap = true,
            popDownOnTap = true,
            nextCallback = this::hidePopupWithLeaderBoard,
            clippedBackground = R.color.bg_block_screen_color_transparent
        )
        showMenu(
            x = GB_LOGO_MARGIN + FLOATING_LOGO_SIZE + GB_LOGO_MARGIN, y = 0, shouldCenter = false
        )
        setListeners(
            listOf(
                R.id.layout_verify, R.id.iv_help, R.id.iv_leaderboard, R.id.iv_open, R.id.iv_stop
            )
        )
    }

    private fun hideExpandedLayout() {
        if (!menuIsVisible) return
        hideMenu()
        popDownOverlays()
    }

    fun showMenu(x: Int, y: Int, shouldCenter: Boolean? = null, shouldUpdate: Boolean = true) {
        try {
            layoutMenuBinding?.let { windowManager.removeView(it.root) }

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutMenuBinding =
                layoutMenuBinding ?: LayoutFloatingMenuBinding.inflate(layoutInflater)

            layoutMenuParams = UiUtils.getOverlayParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                canBePortrait = true
            )

            layoutMenuParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            layoutMenuParams?.x = x
            layoutMenuParams?.y = y
            layoutMenuParams?.width =
                ctx.resources.getDimension(R.dimen.floating_menu_width).toInt()
            layoutMenuParams?.height =
                ctx.resources.getDimension(R.dimen.floating_menu_height).toInt()

            if (shouldCenter != null && shouldCenter) {
                layoutMenuParams?.gravity = Gravity.CENTER
                layoutMenuParams?.x = 0
                layoutMenuParams?.y = 0
            }

            windowManager.addView(layoutMenuBinding!!.root, layoutMenuParams)
            /*addUiView(layoutMenuBinding!!.root, OverlayOn.BGMI_APP)*/
            if (shouldUpdate) updateFloatingMenu()
            menuIsVisible = true
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    fun hideMenu() {
        layoutMenuBinding?.let { windowManager.removeView(it.root) }
        layoutMenuBinding = null
        menuIsVisible = false
    }


    private fun updateFloatingMenu() {
        /*val verified = (Machine.stateMachine.state is VerifiedUser)
        setVerifiedUserStatus(verified, fetchedBgmiId)
        setRecordingStatus(0, null)*/

        val verifiedProfile = (StateMachine.machine.state is VerifiedUser)
        layoutMenuBinding?.ivProfileStatusLarge?.apply {
            if (!verifiedProfile) {
                setImageResource(R.drawable.ic_unverified)
                layoutMenuBinding!!.tvVerify.text = resources.getText(R.string.profile_not_verified)
            } else {
                if (StateMachine.machine.state is VerifiedUser) {
                    layoutMenuBinding!!.tvVerify.text = resources.getText(R.string.profile_verified)
                    setImageResource(R.drawable.ic_verified)
                }
            }
        }
    }

    private fun onConfigurationChanged(newDimens: Point) {
        hideExpandedLayout()
        if (layoutFloatingLogoBinding != null && paramsGBLogo != null) {
            updateViewsOnConfigurationChange(
                layoutFloatingLogoBinding!!.root, newDimens, paramsGBLogo!!
            )
            GB_LOGO_PIN_LOCATION_Y = (paramsGBLogo!!.y)
        }
        if (layoutNotVerifiedTag != null && notVerifiedTagParams != null) {
            updateViewsOnConfigurationChange(
                layoutNotVerifiedTag!!.root,
                newDimens,
                notVerifiedTagParams!!
            )
        }

        adjustYPositionOfNotVerifiedTag()

        if (serviceRestartLayout != null && paramsRestart != null) {
            updateViewsOnConfigurationChange(serviceRestartLayout!!, newDimens, paramsRestart!!)
        }
    }

    private fun adjustYPositionOfNotVerifiedTag() {
        notVerifiedTagParams?.apply {
            layoutNotVerifiedTag?.root?.let {
                y = GB_LOGO_PIN_LOCATION_Y
                windowManager.updateViewLayout(layoutNotVerifiedTag?.root, this@apply)
            }
        }
    }

    private fun updateViewsOnConfigurationChange(
        v: View, newDimens: Point, params: WindowManager.LayoutParams
    ) {
        if (v.isAttachedToWindow) {
            val ratioW = newDimens.x / display.x.toFloat()
            val ratioH = newDimens.y / display.y.toFloat()
            params.x = (params.x * ratioW).toInt()
            params.y = (params.y * ratioH).toInt()
            windowManager.updateViewLayout(v, params)
            Log.e(LOG_TAG, "Old-D:$display New-D:$newDimens orientation:$orientation")
            Log.e(LOG_TAG, "paramsX:${params.x} paramsY:$${params.y}")
            display = newDimens
        }
        /*UiUtils.showToast(ctx, "Old-D:$display New-D:$newDimens orientation:$orientation", Toast.LENGTH_LONG)
        UiUtils.showToast(ctx, "paramsX:${params.x} paramsY:$${params.y}", Toast.LENGTH_LONG)*/
    }


    fun calculateDisplayDimens(): Point {
        val screenDimens = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            screenDimens.x = windowMetrics.bounds.width() /*- insets.left - insets.right*/
            screenDimens.y = windowMetrics.bounds.height() /*- insets.top - insets.bottom*/
        } else {
            windowManager.defaultDisplay.getRealSize(screenDimens)
            densityDPI = displayMetrics.densityDpi
        }
        val newDimens = Point()
        if (orientation == ScreenOrientation.LANDSCAPE) {
            newDimens.x = max(screenDimens.x, screenDimens.y)
            newDimens.y = min(screenDimens.x, screenDimens.y)
        } else {
            newDimens.x = min(screenDimens.x, screenDimens.y)
            newDimens.y = max(screenDimens.x, screenDimens.y)
        }
        onConfigurationChanged(newDimens)
        display = newDimens
        return display
    }

    private fun makeLeaderBoardVisible() {
        showLeaderBoard(
            x = GB_LOGO_MARGIN + FLOATING_MENU_WIDTH + (GB_LOGO_MARGIN + FLOATING_LOGO_SIZE + GB_LOGO_MARGIN),
            y = 0,
            alignOverlayFrom = UiUtils.AlignOverlayFrom.CenterVerticalRight
        )
    }


    private fun setListenerLeaderBoard() {
        layoutMenuBinding?.ivLeaderboard?.setOnClickListener {
            EventUtils.instance()
                .logAnalyticsEvent(Events.MINI_MENU_INTERACTED, mapOf("action" to "view_lb"))
            if (layoutLeaderBoardLayoutBinding?.flLeaderboard == null) makeLeaderBoardVisible() else hideLeaderBoard(
                ""
            )
            onBoardingUI?.clickedOverLeaderboardIcon()
        }
    }

    fun setListeners(activatedMenuIds: List<Int>) {

        if (activatedMenuIds.contains(R.id.iv_leaderboard) && canTapOnLeaderBoardIcon) {
            layoutMenuBinding?.ivLeaderboard?.alpha = 1f
            setListenerLeaderBoard()
        } else {
            layoutMenuBinding?.ivLeaderboard?.alpha = 0.4f
        }

        if (activatedMenuIds.contains(R.id.iv_help)) {
            layoutMenuBinding?.ivHelp?.alpha = 1f
            layoutMenuBinding?.ivHelp?.setOnClickListener {
                EventUtils.instance().logAnalyticsEvent(
                    Events.MINI_MENU_INTERACTED, mapOf("action" to "open_discord")
                )
                showCustomAlertDialog(
                    shouldCenter = true,
                    x = 0,
                    y = 0,
                    head = "Gamerboard",
                    message = ctx.getString(R.string.you_will_be_redirected),
                    okCallback = this::openDiscordServer
                )
            }
        } else {
            layoutMenuBinding?.ivHelp?.alpha = 0.4f
        }

        if (activatedMenuIds.contains(R.id.iv_open)) {
            layoutMenuBinding?.ivOpen?.alpha = 1f
            layoutMenuBinding?.ivOpen?.setOnClickListener {
                EventUtils.instance().logAnalyticsEvent(
                    Events.MINI_MENU_INTERACTED,
                    mapOf("action" to if (MainActivity.gbOnForeground) "open_bgmi" else "open_gb")
                )
                showCustomAlertDialog(
                    shouldCenter = true,
                    x = 0,
                    y = 0,
                    head = "Gamerboard",
                    message = if (MainActivity.gbOnForeground) String.format(
                        ctx.getString(R.string.open_bgmi_alert),
                        MachineConstants.currentGame.gameName
                    ) else ctx.getString(
                        R.string.open_gamer_board_alert
                    ),
                    okCallback = if (MainActivity.gbOnForeground) this::launchGame else this::launchGamerboard
                )
            }
        } else {
            layoutMenuBinding?.ivOpen?.alpha = 0.4f
        }

        if (activatedMenuIds.contains(R.id.iv_stop)) {
            layoutMenuBinding?.ivStop?.alpha = 1f
            layoutMenuBinding?.ivStop?.setOnClickListener {

                EventUtils.instance().logAnalyticsEvent(
                    Events.MINI_MENU_INTERACTED, mapOf("action" to "stop_service")
                )

                showCustomAlertDialog(
                    shouldCenter = true,
                    x = 0,
                    y = 0,
                    head = "Gamerboard",
                    message = ctx.getString(R.string.do_you_wish_to_close_app),
                    okCallback = this::stopGamerBoard
                )
            }

        } else {
            layoutMenuBinding?.ivStop?.alpha = 0.4f
        }

        if (activatedMenuIds.contains(R.id.layout_verify)) {
            layoutMenuBinding?.layoutVerify?.alpha = 1f
            layoutMenuBinding?.layoutVerify?.setOnClickListener {
                EventUtils.instance().logAnalyticsEvent(
                    Events.MINI_MENU_INTERACTED, mapOf("action" to "click_verify")
                )
            }
        } else {
            layoutMenuBinding?.layoutVerify?.alpha = 0.4f
        }
    }

    fun openDiscordServer() {
        UiUtils.showToast(ctx, "Launching discord server...", null)
        try {
            val intent = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    FirebaseRemoteConfig.getInstance().getString(RemoteConfigConstants.GB_DISCORD),
                )
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(
                Intent.createChooser(intent, "Gamerboard").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: java.lang.Exception) {
            logException(e)
        }
    }

    private fun launchGamerboard() {

        CoroutineScope(Dispatchers.IO).launch {
            log {
                it.setMessage("User switched to Gamerboard App")
                it.addContext("reason", GameRepository.GameFailureReason.SWITCHED_TO_GB)
                it.setCategory(LogCategory.ENGINE)
            }
        }

        val launchIntent: Intent? =
            ctx.packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID)
        FeedbackUtils.requestFeedback(
            ctx, FeedBackFrom.BACK_TO_GB_FROM_GAME, BroadcastFilters.NATIVE_TO_FLUTTER
        )
        ctx.startActivity(launchIntent)
    }

    private fun launchGame() {

        CoroutineScope(Dispatchers.IO).launch {

            log {
                it.setMessage("User Switched to BGMI app")
                it.addContext("reason", GameRepository.GameFailureReason.SWITCHED_TO_BGMI)
                it.setCategory(LogCategory.ENGINE)
            }
        }

        val launchIntent: Intent? =
            ctx.packageManager.getLaunchIntentForPackage(MachineConstants.currentGame.packageName)
        ctx.startActivity(launchIntent)
    }

    private fun stopGamerBoard() {

        // if no game is about to finish i.e on the result screens
        if (StateMachine.machine.state !is State.FetchResult) CoroutineScope(Dispatchers.IO).launch {
            SessionManager.clearSession()
            log {
                it.setMessage("Service stopped via menu!")
                it.addContext("reason", GameRepository.GameFailureReason.CLOSED_SERVICE)
                it.setCategory(LogCategory.CME)
            }
        }

        // this will finish the game if exists.
        StateMachine.machine.transition(Event.ServiceStopped("Service stopped: Menu action stopGamerBoard Service stopped manually!"))
        StateMachine.machine.transition(
            Event.GameCompleted(
                "Service stopped: Menu action stopGamerBoard Service stopped manually!",
                executeInBackground = true
            )
        )

        hideExpandedLayout()
        clearResources()
        onBoardingUI?.clearAllOverlays()
        (ctx.applicationContext).stopService(
            Intent(
                (ctx.applicationContext), ScreenCaptureService::class.java
            )
        )
    }

    fun preProfileVerification(
        fetchedBgmiId: String?, fetchedCharacterId: String?, bitmap: Bitmap? = null
    ) {
        if (fetchedBgmiId.isNullOrBlank()) return
        if (fetchedCharacterId.isNullOrBlank()) return
        actionOnScreen(
            screenOfType = ScreenOfType.PRE_PROFILE_VERIFICATION, map = mapOf(
                "fetchedNumericId" to fetchedBgmiId, "fetchedCharacterId" to fetchedCharacterId
            ),
            bitmap = bitmap
        )

        /*onBoardingUI?.actionOnScreenUserGuide(
                ScreenOfType.PRE_PROFILE_VERIFICATION,
                mapOf(
                    "fetchedNumericId" to fetchedBgmiId,
                    "fetchedCharacterId" to fetchedCharacterId
                )
            )*/
    }

    fun setVerifiedUserStatus(
        verifiedProfile: Boolean, fetchedBgmiId: String?, fetchedCharacterId: String?
    ) {
        currentProfileStatus = verifiedProfile
        log {
            it.setMessage("Set Profile verified indicator")
            it.addContext("is_profile_verified", verifiedProfile)
        }
        Log.e(ServiceUIHelper::class.java.simpleName, "SetVerifiedStatus21")
        layoutFloatingLogoBinding?.ivLogo?.apply {
            if (!verifiedProfile) {
                setBackgroundResource(R.drawable.bg_stroke_circle_unverified)
                layoutFloatingLogoBinding!!.vLogoBg.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.unverified_bg))

            } else {
                if (fetchedBgmiId.isNullOrBlank()) return
                if (fetchedCharacterId.isNullOrBlank()) return

                actionOnScreen(
                    ScreenOfType.PROFILE_VERIFIED, mapOf(
                        "fetchedNumericId" to fetchedBgmiId,
                        "fetchedCharacterId" to fetchedCharacterId
                    )
                )
                /*onBoardingUI?.actionOnScreenUserGuide(
                    ScreenOfType.PROFILE_VERIFIED,
                    mapOf(
                        "fetchedNumericId" to fetchedBgmiId,
                        "fetchedCharacterId" to fetchedCharacterId
                    )
                )*/

                if (StateMachine.machine.state is VerifiedUser) {
                    setBackgroundResource(R.drawable.bg_stroke_circle_verified)
                    layoutFloatingLogoBinding!!.vLogoBg.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.verified_bg))
                }

                stopNotVerifiedAnimation()
            }

            updateIconStatus(verifiedProfile)

        }
        layoutMenuBinding?.ivProfileStatusLarge?.apply {
            if (!verifiedProfile) {
                Log.e("ServiceUIHelper", "Verified NOt")
                setImageResource(R.drawable.ic_unverified)
                layoutMenuBinding!!.tvVerify.text =
                    context.resources.getString(R.string.profile_not_verified)
            } else {
                if (StateMachine.machine.state is VerifiedUser) {
                    Log.e("ServiceUIHelper", "Verified")
                    layoutMenuBinding!!.tvVerify.text =
                        context.resources.getString(R.string.profile_verified)
                    setImageResource(R.drawable.ic_verified)
                }
            }
        }
    }

    fun updateIconStatus(verifiedProfile: Boolean) {
        layoutFloatingLogoBinding?.ivLogo?.apply {
            imageTintList = if (!verifiedProfile) {
                setBackgroundResource(R.drawable.bg_stroke_circle_unverified)
                layoutFloatingLogoBinding!!.vLogoBg.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.unverified_bg))
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.inactive))
            } else {
                setBackgroundResource(R.drawable.bg_stroke_circle_verified)
                layoutFloatingLogoBinding!!.vLogoBg.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.verified_bg))
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.verified))
            }

        }
        if (verifiedProfile) {
            stopNotVerifiedAnimation()
        }
    }

    fun setServiceBlockLayout() {
        log("Service Stop ")

        layoutFloatingLogoBinding?.ivLogo?.apply {

            try {
                setBackgroundResource(R.drawable.bg_stroke_circle_service_stop)
                layoutFloatingLogoBinding!!.vLogoBg.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.unverified_bg))
                setUpOnBoarding(Point())
                onBoardingUI?.actionOnScreenUserGuide(ScreenOfType.SERVICE_STOP, mapOf())
                setImageResource(R.drawable.ic_gb_logo)
                imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context, R.color.bg_dark_2
                    )
                )
            } catch (e: Error) {

            }

        }
    }

    fun setRecordingStatus(i: Int, currentAppPkg: String?) {
        if (lastStatus != i) {
            log("Set floating bubble indicator $i")
            layoutFloatingLogoBinding?.ivLogo?.apply {
                imageTintList = if (i == 0) {
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context, R.color.txt_color_inactive
                        )
                    )
                } else {
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.verified))
                }
            }
            lastStatus = i
//            onBoardingUI?.uiViewsManager?.updateUiForService(i == 1, currentAppPkg)
        }
    }

    fun highlightMenuItem(itemIds: List<Int>) {
        for (item in itemIds) {
            layoutMenuBinding?.menuLeaderboardHighlight!!.setBackgroundResource(R.drawable.bg_solid_circle_yellow)
        }
    }


    private fun guideTheUserToVerifyIfNotVerified(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            //delay(2000)
            if (StateMachine.machine.state !is VerifiedUser) {
                //do not allow to tap un till pop down overlay
                canTapOnGbLogo = false

                onBoardingUI =
                    onBoardingUI ?: UserGuideUI(ctx, windowManager, display, this@ServiceUIHelper)
                onBoardingUI?.guideTheUserToVerifyProfile("")


                //Start not verified tag
                startNotVerifiedTagAnimation()
                //onBoardingUI = null
            }
        }
    }

    fun startNotVerifiedTagAnimation() {
        if (isNewProfileVerificationUIEnabled.not()) return

        if (notVerifiedAnimationRunning) return

        notVerifiedAlreadyShownFirstTime = true

        notVerifiedAnimationJobContext = Dispatchers.Main + SupervisorJob()
        CoroutineScope(notVerifiedAnimationJobContext).launch {
            notVerifiedAnimationRunning = true
            while (StateMachine.machine.state !is VerifiedUser) {
                delay(5000)
                showNotVerifiedBubble()
                delay(10000)
                hideNotVerifiedBubble()
            }
        }
    }

    fun stopNotVerifiedAnimation() {
        if (isNewProfileVerificationUIEnabled.not()) return

        notVerifiedAnimationRunning = false
        notVerifiedAnimationJobContext.cancel()
        hideNotVerifiedBubble()
    }

    private fun hideNotVerifiedBubble() {

        if (isNewProfileVerificationUIEnabled.not()) return

        layoutNotVerifiedTag?.frameLayout?.apply {

            val animator = ValueAnimator.ofInt(width, 0)
            animator.interpolator = DecelerateInterpolator()

            animator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                val params = layoutParams
                params.width = animatedValue
                layoutParams = params
            }

            animator.doOnEnd {
                layoutNotVerifiedTag?.root?.visibility = View.GONE
                notVerifiedTagParams?.apply {
                    width = resources.getDimension(R.dimen.floating_logo_size).roundToInt()
                    y = GB_LOGO_PIN_LOCATION_Y
                    if (layoutNotVerifiedTag?.root != null) {
                        windowManager.updateViewLayout(layoutNotVerifiedTag?.root, this)
                    }
                }
            }
            animator.duration = 500 // Set the duration of the animation in milliseconds
            animator.start()

        }
    }

    private fun showNotVerifiedBubble() {
        if (isNewProfileVerificationUIEnabled.not()) return
        if (StateMachine.machine.state is VerifiedUser) return

        layoutNotVerifiedTag?.frameLayout?.apply {
            visibility = View.GONE

            val sizeInPixels = UiUtils.convertDpToPixel(130, ctx)

            notVerifiedTagParams?.apply {
                width = sizeInPixels
                y = GB_LOGO_PIN_LOCATION_Y
                if (layoutNotVerifiedTag?.root != null) {
                    windowManager.updateViewLayout(layoutNotVerifiedTag?.root, this)
                }
            }
            val animator = ValueAnimator.ofInt(0, sizeInPixels - FLOATING_LOGO_SIZE / 2)
            animator.interpolator = AccelerateInterpolator()
            animator.doOnStart {
                visibility = View.VISIBLE
                layoutNotVerifiedTag?.root?.setBackgroundColor(resources.getColor(android.R.color.transparent))
                layoutNotVerifiedTag?.root?.visibility = View.VISIBLE
            }
            animator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                val params = layoutParams
                params.width = animatedValue
                layoutParams = params
            }
            animator.duration = 500 // Set the duration of the animation in milliseconds
            animator.start()
        }
    }


    private fun populateAndShowScoresAndSkillChangeForUser(
        message: String, customGameResponse: CustomGameResponse
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            //delay(2000)
            onBoardingUI =
                onBoardingUI ?: UserGuideUI(ctx, windowManager, display, this@ServiceUIHelper)
            onBoardingUI?.showUserHisScoresAndSkillChange(
                "", customGameResponse = customGameResponse
            )
            //onBoardingUI = null
        }
    }

    private fun populateAndShowScoresAndSkillChangeForMultiplayerGame(customGameResponse: CustomGameResponse) {
        CoroutineScope(Dispatchers.Main).launch {
            onBoardingUI =
                onBoardingUI ?: UserGuideUI(ctx, windowManager, display, this@ServiceUIHelper)
            onBoardingUI?.showMultiplayerGameScoresAndSkillChange(customGameResponse)
        }
    }

    private fun showUserNameUpdateAlert(
        originalUsername: String?, updatedUsername: String?, callback: CallBack<Boolean>
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            onBoardingUI =
                onBoardingUI ?: UserGuideUI(ctx, windowManager, display, this@ServiceUIHelper)
            originalUsername?.let {
                if (updatedUsername != null) {
                    onBoardingUI?.showAlertOnUserNameChange(it, updatedUsername, callback)
                }
            }
        }
    }

    private fun showUserIdMismatch(
        originalUserId: String, fetchedUserId: String,
        callback: CallBack<Boolean>
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            onBoardingUI =
                onBoardingUI ?: UserGuideUI(ctx, windowManager, display, this@ServiceUIHelper)
            onBoardingUI?.showProfileIdMismatch(originalUserId, fetchedUserId, callback)
        }
    }

    private fun populateAndShowGameNotOverYet(customGameResponse: CustomGameResponse) {
        CoroutineScope(Dispatchers.Main).launch {
            onBoardingUI =
                onBoardingUI ?: UserGuideUI(ctx, windowManager, display, this@ServiceUIHelper)
            onBoardingUI?.showUserThatGameIsNotOverYet(customGameResponse)
        }
    }


    fun loaderOnScreen(
        show: Boolean,
        message: String?,
        screen: MachineConstants.ScreenName = MachineConstants.ScreenName.OTHER
    ) {
        try {
            if (screen == MachineConstants.ScreenName.PERFORMANCE)
                layoutKillsFetchedBinding?.let {
                    windowManager.removeView(it.root)
                    layoutKillsFetchedBinding = null
                }
            if (show && message?.isNotEmpty() == true && layoutKillsFetchedBinding == null) {
                val shouldUpdate = layoutFloatingProgressBinding != null
                val paramsProgress: WindowManager.LayoutParams = UiUtils.getOverlayParams(
                    ctx.resources.getDimension(R.dimen._400sdp).toInt(),
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    canBePortrait = true
                )
                paramsProgress.gravity = Gravity.END or Gravity.BOTTOM
//                    paramsProgress.x = display.x - 10
                val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
                if (layoutFloatingProgressBinding == null) layoutFloatingProgressBinding =
                    LayoutFloatingProgressBinding.inflate(layoutInflater)
                layoutFloatingProgressBinding!!.tvLoaderMessage.text = message
                log(" ${if (shouldUpdate) "Update" else "create"} loader")

                if (shouldUpdate) windowManager.updateViewLayout(
                    layoutFloatingProgressBinding?.root, paramsProgress
                )
                else windowManager.addView(layoutFloatingProgressBinding?.root, paramsProgress)

                loaderTimer.start()
            } else {
                if (layoutFloatingProgressBinding != null && layoutFloatingProgressBinding?.root != null) windowManager.removeView(
                    layoutFloatingProgressBinding?.root
                )
                layoutFloatingProgressBinding = null
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private val loaderTimer = object : CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            CoroutineScope(Dispatchers.Main).launch {
                loaderOnScreen(false, null)
            }
        }
    }

    private val autoDismissTimer = object : CountDownTimer(5000, 1000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            CoroutineScope(Dispatchers.Main).launch {
                layoutKillsFetchedBinding?.root?.let {
                    windowManager.removeView(it)
                    layoutKillsFetchedBinding = null
                }
            }
        }
    }

    fun inflateRestartServiceLayout(screenCaptureService: ScreenCaptureService) {
        try {
            log("Inflating service restart layout")
            clearResources()
            onBoardingUI?.clearAllOverlays()
            val youtubeUrl = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigConstants.GB_RELAUNCH_VIDEO_ID);
            val html =
                """<iframe width="100%" height="100%" src="https://www.youtube.com/embed/$youtubeUrl" title="Watch the video" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>"""
            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            serviceRestartLayout =
                layoutInflater.inflate(R.layout.layout_service_restart, null, false)

            val btnRestart = serviceRestartLayout!!.findViewById(R.id.btn_restart) as View
            val btnClose = serviceRestartLayout!!.findViewById(R.id.btn_close) as View

            val wv = serviceRestartLayout!!.findViewById(R.id.webView) as WebView

            paramsRestart = UiUtils.getOverlayParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                canBePortrait = true
            )

            Log.e(LOG_TAG, display.toString())
            paramsRestart?.gravity = Gravity.CENTER
            windowManager.addView(serviceRestartLayout, paramsRestart)

            wv.getSettings().setJavaScriptEnabled(true);
            wv.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");

            wv.webChromeClient = WebChromeClient()
            val webSettings = wv.settings
            wv.settings.loadWithOverviewMode = true
            webSettings.javaScriptEnabled = true;
            btnClose.setOnClickListener {
                serviceRestartLayout?.let { windowManager.removeView(it.rootView) }

                serviceRestartLayout = null
            }

            btnRestart.setOnClickListener {
                ctx.startActivity(Intent(ctx, MainActivity::class.java).apply {
                    (ctx.applicationContext as GamerboardApp).prefsHelper.putBoolean(
                        IntentKeys.LAUNCH_GAME, true
                    )
                    serviceRestartLayout?.let { windowManager.removeView(it.rootView) }
                    serviceRestartLayout = null
                    putExtra(IntentKeys.APP_RESTARTED, true)
                    putExtra(IntentKeys.LAUNCH_GAME, true)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                Log.d(SessionManager.tag, "serViceUIHelper.stop()")
                screenCaptureService.stop(forceClearSession = false)
            }
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    fun retrySubmitGame(
        shouldCenter: Boolean?,
        x: Int,
        y: Int,
        head: String,
        message: String,
        retryCallBack: ReTryGameSubmit?
    ) {
        try {
            layoutCustomAlertDialogBinding?.let { windowManager.removeView(it.root) }
            layoutCustomAlertDialogBinding = null

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutCustomAlertDialogBinding =
                LayoutCustomAlertDialogBinding.inflate(layoutInflater)

            val params: WindowManager.LayoutParams = UiUtils.getOverlayParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
            )

            params.gravity = Gravity.CENTER
            params.x = x
            params.y = y

            if (shouldCenter != null && shouldCenter) {
                params.gravity = Gravity.CENTER
                params.x = 0
                params.y = 0
            }

            /*layoutCustomAlertDialogBinding?.layoutCustomAlertDialogBackground?.setOnClickListener {
                layoutCustomAlertDialogBinding?.let { windowManager.removeView(it.root) }
                layoutCustomAlertDialogBinding = null
            }*/

            layoutCustomAlertDialogBinding?.lvNeedHelp?.visibility = View.VISIBLE;

            layoutCustomAlertDialogBinding?.tvJoinDiscord?.setOnClickListener {
                openDiscordServer()
            }
            layoutCustomAlertDialogBinding?.layoutCustomAlertDialogContainer?.setOnClickListener {

            }

            layoutCustomAlertDialogBinding?.btnCustomAlertDialogOk?.text = "Cancel"
            layoutCustomAlertDialogBinding?.btnCustomAlertDialogOk?.setOnClickListener {
                EventUtils.instance()
                    .logAnalyticsEvent(Events.ALERT_BOX_INTERACTED, mapOf("action" to "yes"))
                retryCallBack?.cancel()
                layoutCustomAlertDialogBinding?.let { windowManager.removeView(it.root) }
                layoutCustomAlertDialogBinding = null
            }

            layoutCustomAlertDialogBinding?.btnCustomAlertDialogCancel?.text = "Retry"
            layoutCustomAlertDialogBinding?.btnCustomAlertDialogCancel?.setOnClickListener {
                EventUtils.instance()
                    .logAnalyticsEvent(Events.ALERT_BOX_INTERACTED, mapOf("action" to "no"))
                retryCallBack?.retry()
                layoutCustomAlertDialogBinding?.let { windowManager.removeView(it.root) }
                layoutCustomAlertDialogBinding = null
            }

            layoutCustomAlertDialogBinding?.tvCustomAlertDialogHead?.text = Html.fromHtml(head)
            layoutCustomAlertDialogBinding?.tvCustomAlertDialogMessage?.text =
                Html.fromHtml(message)
            windowManager.addView(layoutCustomAlertDialogBinding!!.root, params)
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    private fun showCustomAlertDialog(
        shouldCenter: Boolean?,
        x: Int,
        y: Int,
        head: String,
        message: String,
        okCallback: (() -> Unit)? = null
    ) {
        try {
            layoutCustomAlertDialogBinding?.let { windowManager.removeView(it.root) }
            layoutCustomAlertDialogBinding = null

            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
            layoutCustomAlertDialogBinding =
                LayoutCustomAlertDialogBinding.inflate(layoutInflater)

            val params: WindowManager.LayoutParams = UiUtils.getOverlayParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
            )

            params.gravity = Gravity.CENTER
            params.x = x
            params.y = y

            if (shouldCenter != null && shouldCenter) {
                params.gravity = Gravity.CENTER
                params.x = 0
                params.y = 0
            }

            layoutCustomAlertDialogBinding?.layoutCustomAlertDialogBackground?.setOnClickListener {
                layoutCustomAlertDialogBinding?.let { windowManager.removeView(it.root) }
                layoutCustomAlertDialogBinding = null
            }

            layoutCustomAlertDialogBinding?.layoutCustomAlertDialogContainer?.setOnClickListener {

            }

            layoutCustomAlertDialogBinding?.btnCustomAlertDialogOk?.setOnClickListener {
                EventUtils.instance()
                    .logAnalyticsEvent(Events.ALERT_BOX_INTERACTED, mapOf("action" to "yes"))
                okCallback?.invoke()
                layoutCustomAlertDialogBinding?.let { windowManager.removeView(it.root) }
                layoutCustomAlertDialogBinding = null
            }

            layoutCustomAlertDialogBinding?.btnCustomAlertDialogCancel?.setOnClickListener {
                EventUtils.instance()
                    .logAnalyticsEvent(Events.ALERT_BOX_INTERACTED, mapOf("action" to "no"))
                layoutCustomAlertDialogBinding?.let { windowManager.removeView(it.root) }
                layoutCustomAlertDialogBinding = null
            }

            layoutCustomAlertDialogBinding?.tvCustomAlertDialogHead?.text = Html.fromHtml(head)
            layoutCustomAlertDialogBinding?.tvCustomAlertDialogMessage?.text =
                Html.fromHtml(message)
            windowManager.addView(layoutCustomAlertDialogBinding!!.root, params)
        } catch (ex: Exception) {
            logException(ex)
        }
    }


    fun gameFailed(failureReason: GameRepository.GameFailureReason?, message: String? = null) {
        Toast.makeText(ctx, "Game failed", Toast.LENGTH_SHORT).show()
        onBoardingUI?.gameFailed(failureReason, message)
        /*notVerifiedAnimationJobContext.cancel()
        hideNotVerifiedBubble()*/
    }


    var startedOnBording = false
    fun actionOnScreen(
        screenOfType: ScreenOfType,
        map: Map<String, String?>? = null,
        callback: CallBack<Boolean>? = null,
        bitmap: Bitmap? = null
    ) {
        if (shouldIgnoreEventsBefore) return
        checkOnBoarding()

        when (screenOfType) {
            ScreenOfType.HOME -> {
                val checkUserVerification =
                    GamerboardApp.instance.prefsHelper.getBoolean(SharedPreferenceKeys.SHOW_VERIFICATION_ONLY) && (GamerboardApp.instance.prefsHelper.getString(
                        SharedPreferenceKeys.CURRENT_GAME_NAME
                    ) == SupportedGames.BGMI.gameName)

                if (checkUserVerification) {
                    cleanUpOnBoarding()
                    guideTheUserToVerifyIfNotVerified("")
                } else {
                    if (onBoardingHomeScreenRunning && !startedOnBording) {
                        setUpOnBoarding(display)
                        onBoardingUI?.actionOnScreenUserGuide(ScreenOfType.HOME, mapOf())
                    } else {
                        cleanUpOnBoarding()
                        guideTheUserToVerifyIfNotVerified("")
                    }
                }

                // Start animation
                startNotVerifiedTagAnimation()

            }

            ScreenOfType.PROFILE_VERIFIED -> {
                if (onBoardingHomeScreenRunning && !startedOnBording) {
                    setUpOnBoarding(display)
                    val checkUserVerification =
                        GamerboardApp.instance.prefsHelper.getBoolean(SharedPreferenceKeys.SHOW_VERIFICATION_ONLY) && (GamerboardApp.instance.prefsHelper.getString(
                            SharedPreferenceKeys.CURRENT_GAME_NAME
                        ) == SupportedGames.BGMI.gameName)

                    if (checkUserVerification) {
                        onBoardingUI?.startUserVerifyNewOnboarding();
                    } else {
                        onBoardingUI?.actionOnScreenUserGuide(ScreenOfType.HOME, mapOf())
                    }
                } else {
                    if (!startedOnBording) cleanUpOnBoarding()
                    onBoardingUI?.actionOnScreenUserGuide(
                        ScreenOfType.PROFILE_VERIFIED, map
                    )
                }
            }

            ScreenOfType.PRE_PROFILE_VERIFICATION -> {
                if (onBoardingHomeScreenRunning && !startedOnBording) {
                    setUpOnBoarding(display)
                    val checkUserVerification =
                        GamerboardApp.instance.prefsHelper.getBoolean(SharedPreferenceKeys.SHOW_VERIFICATION_ONLY) && (GamerboardApp.instance.prefsHelper.getString(
                            SharedPreferenceKeys.CURRENT_GAME_NAME
                        ) == SupportedGames.BGMI.gameName)

                    if (checkUserVerification) {
                        onBoardingUI?.startUserVerifyNewOnboarding();
                    } else {
                        onBoardingUI?.actionOnScreenUserGuide(ScreenOfType.HOME, mapOf())
                    }
                } else {
                    if (!startedOnBording) cleanUpOnBoarding()
                    onBoardingUI?.actionOnScreenUserGuide(
                        ScreenOfType.PRE_PROFILE_VERIFICATION, map, bitmap
                    )
                }
            }

            ScreenOfType.IN_GAME -> {
                if (onBoardingInGameRunning) {
                    setUpOnBoardingInGame(display)
                    onBoardingUI?.actionOnScreenUserGuide(screenOfType, mapOf())
                } else {
                    cleanUpOnBoarding()
                }
            }

            ScreenOfType.ESPORT_LOGIN -> {
                startNotVerifiedTagAnimation()
                onBoardingUI?.actionOnScreenUserGuide(screenOfType, map)
            }

            ScreenOfType.FIRST_GAME_END -> {
                // first time game end
            }

            ScreenOfType.GAME_END -> {

                if (BuildConfig.IS_TEST) return

                val customGameResponse: CustomGameResponse? =
                    LabelUtils.decodeServerGameFromMap(map)
                customGameResponse?.let {
                    /*if (onBoardingPostGameRunning) {
                        setUpOnBoardingPostGame(screenDimension)
                        onBoardingUI?.actionOnScreenUserGuide(screenOfType, map)
                    } else {*/
                    cleanUpOnBoarding()
                    populateAndShowScoresAndSkillChangeForUser("", customGameResponse)
                    /*}*/
                }
            }

            ScreenOfType.MULTIPLAYER_GAME_END_WITHOUT_KILLS -> {
                if (BuildConfig.IS_TEST)
                    return
                LabelUtils.decodeServerGameFromMap(map)?.let {
                    cleanUpOnBoarding()
                    populateAndShowGameNotOverYet(it)
                }
            }

            ScreenOfType.MULTIPLAYER_GAME_END_WITH_KILLS -> {
                if (BuildConfig.IS_TEST)
                    return
                LabelUtils.decodeServerGameFromMap(map)?.let {
                    cleanUpOnBoarding()
                    populateAndShowScoresAndSkillChangeForMultiplayerGame(it)
                }
            }

            ScreenOfType.USERNAME_UPDATE_ALERT -> {
                if (BuildConfig.IS_TEST) return
                map?.let {
                    cleanUpOnBoarding()
                    callback?.let { cl ->
                        showUserNameUpdateAlert(
                            it["originalUserName"], it["fetchedUserName"], cl
                        )
                    }
                }
            }

            ScreenOfType.USER_ID_MISMATCH -> {
                map?.let {
                    cleanUpOnBoarding()
                    callback?.let { cl ->
                        showUserIdMismatch(
                            it["originalUserId"]!!, it["fetchedUserId"]!!, cl
                        )
                    }
                }
            }

            else -> {

            }
        }
    }


    fun updateGameProfile(
        gameUserName: String,
        gameProfileId: String,
        response: (String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            GameRepository.updateGameProfile(gameUserName, apiClient, gameId = gameProfileId) {
                CoroutineScope(Dispatchers.Main).launch {
                    response(it)
                }
            }
        }
    }

    fun showGameIdVerificationError(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(3 * 1000)
            val machine = StateMachine.machine
            if (machine.state !is VerifiedUser && prevToast != 99) {
                prevToast = 99
                showToast(message, force = true)
            }
        }
    }

}

@Keep
enum class ScreenOrientation {
    PORTRAIT, LANDSCAPE
}


interface CallBack<T> {
    fun onDone(t: T)
}