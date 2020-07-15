package com.ocram.qichwadic.core.data

sealed class ApiResponse<T> {
    data class Success<T>(val data: T): ApiResponse<T>()
    data class Error(val errorCode: Int): ApiResponse<Nothing>()

}