package com.gamerboard.live.gamestatemachine.games

import com.gamerboard.live.BuildConfig
import com.gamerboard.live.ModelParamQuery
import com.gamerboard.live.fragment.BucketFields
import com.gamerboard.live.fragment.LabelFields
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.type.SortOrder
import okhttp3.internal.immutableListOf

interface GameConstants {

    fun resultRankRating(): List<Int>

    fun resultRankKills(): List<Int>

    fun resultRank(): List<Int>

    fun homeScreenBucket(): List<Int>

    fun waitingScreenBucket(): List<Int>

    fun gameScreenBucket(): List<Int>

    fun loginScreenBucket(): List<Int>

    fun myProfileScreen(): List<Int>
    fun gameEndScreen(): List<Int>

    fun getSortOrderForLabel(tfResult: TFResult): Any

    /**
     * The confidence of the labels we receive from the model must be above the specified threshold
     * to qualify for valid detection. Below threshold values are recognised as false positives.
     * Checkout [TFProcessor.java]
     * */
    fun labelThreshold(): Map<Int, Double>

    fun unknownScreenBucket() = immutableListOf(-1)

    fun ratingLabel(): Int

    fun killLabel(): Int

    fun profileIdLabel(): Int

    fun gameModelURL(): String

    /**
     * The labels should be greater than the [LabelBufferSize] for a particular bucket, when we receive them above the set
     * limit only them we process them. This is present to avoid false labels which might accidentally be detected.
     * */

    fun labelBufferSize(): Map<List<Int>, Int>
//    = mapOf(
//        (homeScreenBucket()) to 3,
//        (loginScreenBucket()) to 2,
//        (resultRankRating()) to 1,
////        (performanceScreen()) to 1,
////        (rating()) to 1,
//        (resultRankKills()) to 1,
//        (waitingScreenBucket()) to 4,
//        (gameScreenBucket()) to 4,
//        (unknownScreenBucket()) to 1
//    )

    fun labelsForIndividualOCR(): List<Int>
    fun shouldPerformScaleAndStitching(label: Float): Boolean

    fun getGameInfoLabelFilter(label: Float): Boolean
    fun labelNameFromIndex(label: Int): String
    fun labelFromIndex(label: Int): LabelFields
    fun matchBucket(bucket: BucketFields, currLabel: List<Int>): Boolean

    fun buckets(): ModelParamQuery.Bucket?
}

class GameConstant(val modelParam: ModelParamQuery.ModelParam) : GameConstants {

    val resultRankRating by lazy {
        modelParam.bucket?.resultRankRating?.bucketFields?.labels?.map { it.labelFields.index }
            ?: emptyList()
    }
    private val gameScreen by lazy {
        modelParam.bucket?.gameScreenBucket?.bucketFields?.labels?.map { it.labelFields.index }
            ?: emptyList()
    }

    val resultRankKills by lazy {
        modelParam.bucket?.resultRankKills?.bucketFields?.labels?.map { it.labelFields.index }
            ?: emptyList()
    }
    private val resultRank by lazy {
        modelParam.bucket?.resultRank?.bucketFields?.labels?.map { it.labelFields.index }
            ?: emptyList()
    }
    val homeScreen by lazy {
        modelParam.bucket?.homeScreenBucket?.bucketFields?.labels?.map { it.labelFields.index }
            ?: emptyList()
    }
    private val waitingScreen by lazy {
        modelParam.bucket?.waitingScreenBucket?.bucketFields?.labels?.map { it.labelFields.index }
            ?: emptyList()
    }
    private val loginScreen by lazy {
        modelParam.bucket?.loginScreenBucket?.bucketFields?.labels?.map { it.labelFields.index }
            ?: emptyList()
    }
    private val myProfile by lazy {
        modelParam.bucket?.myProfileScreen?.bucketFields?.labels?.map { it.labelFields.index }
            ?: emptyList()
    }
    private val gameEndScreen by lazy {
        modelParam.bucket?.playAgain?.bucketFields?.labels?.map { it.labelFields.index }
            ?: emptyList()
    }

    private val buckets = modelParam.bucket


    private val bucketBufferSize by lazy {
        mutableMapOf<List<Int>, Int>().apply {
            modelParam.bucket?.gameScreenBucket?.bucketFields?.let {
                this[gameScreenBucket()] = it.bufferSize
            }
            modelParam.bucket?.resultRank?.bucketFields?.let { this[resultRank()] = it.bufferSize }
            modelParam.bucket?.resultRankKills?.bucketFields?.let {
                this[resultRankKills()] = it.bufferSize
            }
            modelParam.bucket?.resultRankRating?.bucketFields?.let {
                this[resultRankRating()] = it.bufferSize
            }
            modelParam.bucket?.homeScreenBucket?.bucketFields?.let {
                this[homeScreenBucket()] = it.bufferSize
            }
            modelParam.bucket?.loginScreenBucket?.bucketFields?.let {
                this[loginScreenBucket()] = it.bufferSize
            }
            modelParam.bucket?.waitingScreenBucket?.bucketFields?.let {
                this[waitingScreenBucket()] = it.bufferSize
            }
            modelParam.bucket?.myProfileScreen?.bucketFields?.let {
                this[myProfileScreen()] = it.bufferSize
            }
            modelParam.bucket?.playAgain?.bucketFields?.let {
                this[gameEndScreen()] = it.bufferSize
            }
        }
    }


    override fun resultRankRating() = resultRankRating

    override fun resultRankKills() = resultRankKills

    override fun resultRank() = resultRank

    override fun homeScreenBucket() = homeScreen

    override fun waitingScreenBucket() = waitingScreen
    override fun gameScreenBucket() = gameScreen

    override fun loginScreenBucket() = loginScreen

    override fun myProfileScreen() = myProfile
    override fun gameEndScreen(): List<Int> = gameEndScreen

    override fun getSortOrderForLabel(tfResult: TFResult): SortOrder {
        modelParam.labels.forEach {
            if (it.labelFields.index == tfResult.label.toInt()) {
                return it.labelFields.sortOrder
            }
        }
        return SortOrder.SKIP
    }

    private val thresholds by lazy { modelParam.labels.associate { (it.labelFields.index to it.labelFields.threshold) } }

    override fun labelThreshold(): Map<Int, Double> = thresholds

    override fun ratingLabel(): Int {
        val labelIndex = -1
        modelParam.labels.forEach {
            if (it.labelFields.name == "CLASSIC_RATING") {
                return it.labelFields.index
            }
        }
        return labelIndex
    }

    override fun killLabel(): Int {
        val labelIndex = -1
        modelParam.labels.forEach {
            if (it.labelFields.name == "CLASSIC_ALL_KILLS") {
                return it.labelFields.index
            }
        }
        return labelIndex
    }

    override fun profileIdLabel(): Int {
        val labelIndex = -1
        modelParam.labels.forEach {
            if (it.labelFields.name == "PROFILE_ID") {
                return it.labelFields.index
            }
        }
        return labelIndex
    }

    private val individualOCR by lazy {
        modelParam.labels.filter { it.labelFields.individualOCR }.map { it.labelFields.index }
    }

    override fun labelsForIndividualOCR() = individualOCR

    override fun shouldPerformScaleAndStitching(label: Float): Boolean =
        modelParam.labels[label.toInt()].labelFields.shouldPerformScaleAndStitching!!


    override fun getGameInfoLabelFilter(label: Float): Boolean =
        modelParam.labels[label.toInt()].labelFields.name == "GAME_INFO"


    override fun labelNameFromIndex(label: Int): String = modelParam.labels[label].labelFields.name

    override fun labelFromIndex(label: Int): LabelFields = modelParam.labels[label].labelFields

    override fun matchBucket(bucket: BucketFields, currLabel: List<Int>): Boolean {

        val mandatory = mutableListOf<Int>()
        val nonMandatory = mutableListOf<Int>()
        bucket.labels.forEach {
            if (it.labelFields.mandatory == true)
                mandatory.add(it.labelFields.index)
            nonMandatory.add(it.labelFields.index)
        }
        val first = nonMandatory.containsAll(currLabel)
        val second = currLabel.containsAll(mandatory)
        return first && second
    }

    override fun labelBufferSize() = bucketBufferSize

    override fun gameModelURL(): String = "${BuildConfig.GS_BUCKET_URL}/${modelParam.model_url}"

    override fun buckets(): ModelParamQuery.Bucket? = buckets
}

