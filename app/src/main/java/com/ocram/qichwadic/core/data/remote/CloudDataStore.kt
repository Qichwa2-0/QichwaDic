package com.ocram.qichwadic.core.data.remote

import retrofit2.Response

abstract class CloudDataStore {

    fun <T> processResponse(response: Response<T>): ApiResponse<out T> {
        if(response.isSuccessful) {
            response.body()?.let {
                return ApiResponse.Success(response.body()!!)
            }
        }
        return ApiResponse.Error(response.code())
    }
}