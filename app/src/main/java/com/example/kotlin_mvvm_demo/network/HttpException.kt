package com.example.kotlin_mvvm_demo.network

/**
 *
 * @author wenzhiming
 * @date 2024/07/12
 *
 */
class HttpException : Exception {
    val statusCode: Int
    val responseBody: String

    constructor(message: String, statusCode: Int, responseBody: String) : super(message) {
        this.statusCode = statusCode
        this.responseBody = responseBody
    }
}