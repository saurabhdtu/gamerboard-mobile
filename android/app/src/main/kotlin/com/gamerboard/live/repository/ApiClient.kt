package com.gamerboard.live.repository

import android.content.Context
import android.util.Log
import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.interceptor.ApolloInterceptor
import com.apollographql.apollo3.interceptor.ApolloInterceptorChain
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.utils.EventUtils
import com.gamerboard.live.utils.Events
import com.gamerboard.logger.Logger
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by saurabh.lahoti on 01/05/22
 */
val ApiClientModule = module {
    single {
        ApiClient(androidContext())
    }
}

class ApiClient(ctx: Context) {
    val prefsHelper = (ctx as GamerboardApp).prefsHelper

    private val apolloClient = ApolloClient.Builder()
        .serverUrl(BuildConfig.API_ENDPOINT)
        .addInterceptor(object : ApolloInterceptor {
            override fun <D : Operation.Data> intercept(
                request: ApolloRequest<D>,
                chain: ApolloInterceptorChain
            ): Flow<ApolloResponse<D>> {
                FirebaseCrashlytics.getInstance()
                    .log("apollo-operation: ${request.operation.name()}")
                Log.d(
                    "apollo", "operation: ${request.operation.name()}\n" +
                            "doc: ${request.operation.document()}\n" +
                            "header: ${request.httpHeaders.toString()}"
                )
                return chain.proceed(request)
            }

        })
        .addHttpHeader("release", BuildConfig.VERSION_CODE.toString())
        .addHttpInterceptor(object : HttpInterceptor {
            override suspend fun intercept(
                request: HttpRequest,
                chain: HttpInterceptorChain
            ): HttpResponse {
                val token = prefsHelper.getString(SharedPreferenceKeys.AUTH_TOKEN)
                if (token == null)
                    EventUtils.instance().logAnalyticsEvent(
                        Events.NULL_TOKEN,
                        mapOf("params" to (request.body?.toString() ?: ""))
                    )
                return chain.proceed(
                    request.newBuilder().addHeader(
                        "Authorization",
                        "Bearer $token"
                    ).addHeader(
                        "trace",
                        (if (Logger.loggingFlags.optBoolean("api_response")) 1 else 0).toString()
                    )
                        .addHeader(
                            "device",
                            prefsHelper.getString(SharedPreferenceKeys.UUID).toString()
                        ).build()
                )
            }

        })
        .build()

    fun <D : Query.Data> query(query: Query<D>): ApolloCall<D> {
        return apolloClient.query(query)
    }

    fun <D : Mutation.Data> mutation(mutation: Mutation<D>): ApolloCall<D> {
        return apolloClient.mutation(mutation)
    }
}