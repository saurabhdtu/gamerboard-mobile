package com.gamerboard.live.repository

import com.gamerboard.live.ModelParamQuery
import com.gamerboard.live.common.PrefsHelper
import com.gamerboard.live.common.toSupportedGame
import com.gamerboard.live.gamestatemachine.games.GameConstant
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.utils.logException
import com.gamerboard.logger.gson
import com.google.gson.Gson

class ModelParameterManager(val prefsHelper: PrefsHelper) {

    companion object {
        private const val KEY_MODEL_PARAMS = "model_parameters"
    }

    fun save(modelParam: ModelParamQuery.ModelParam) {
        prefsHelper.putString(KEY_MODEL_PARAMS, gson.toJson(modelParam))
    }

    fun load() {
        prefsHelper.getString(KEY_MODEL_PARAMS)?.let {
             gson.fromJson(it, ModelParamQuery.ModelParam::class.java)
        }?.also { modelParam ->
            MachineConstants.gameConstants = GameConstant(modelParam = modelParam)
        }
    }

    suspend fun getModelParam(
        apiClient: ApiClient?,
        packageName: String
    ) : Boolean{
        try {
            val query = ModelParamQuery(eSport = packageName.toSupportedGame().eSport)

            val modelData = apiClient?.query(query)?.execute()

            modelData?.data?.modelParam?.let { param ->
                save(modelParam = param)
                MachineConstants.gameConstants = GameConstant(modelParam = param)
                return true
            }
        }catch (e:Exception){
            logException(e)
        }
        return false
    }

}