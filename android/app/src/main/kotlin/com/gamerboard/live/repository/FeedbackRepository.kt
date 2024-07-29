package com.gamerboard.live.repository

import com.apollographql.apollo3.api.Optional
import com.gamerboard.live.LowRatingQuery
import com.gamerboard.live.SubmitFeedbackMutation
import com.gamerboard.live.models.ApiResponse
import com.gamerboard.live.type.SubmitFeedbackInput
import com.gamerboard.live.utils.logException


/**
 * Created by saurabh.lahoti on 30/12/21
 */


class FeedbackRepository(private val apiClient: ApiClient) {

    suspend fun getReasons(): ApiResponse<List<String>> {
        try {
            val result = apiClient.query(LowRatingQuery()).execute()
            if (result.hasErrors())
                return ApiResponse(error = result.errors!!.first().message)
            return ApiResponse(response = result.data?.lowRatingReasons)
        } catch (ex: Exception) {
            logException(ex)
            return ApiResponse(error = ex.message)
        }
    }

    suspend fun submitFeedback(
        collectionEvent: String,
        rating: Int,
        comments: String?,
        ratingReason: String?
    ): ApiResponse<Int> {
        try {
            val result = apiClient.mutation(
                SubmitFeedbackMutation(
                    input = SubmitFeedbackInput(
                        collectionEvent,
                        Optional.presentIfNotNull(comments),
                        rating,
                        Optional.presentIfNotNull(ratingReason)
                    )
                )
            ).execute()
            if (result.hasErrors())
                return ApiResponse(error = result.errors!!.first().message)
            return ApiResponse(response = 100)
        } catch (ex: Exception) {
            logException(ex)
            return ApiResponse(error = ex.message)
        }
    }
}