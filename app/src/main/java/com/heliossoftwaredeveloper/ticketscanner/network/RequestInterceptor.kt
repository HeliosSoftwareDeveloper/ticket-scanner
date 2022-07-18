package com.heliossoftwaredeveloper.ticketscanner.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RequestInterceptor  @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder().apply {
            addHeader("x-api-key", "7d0Xg8krdBTaaCVpQr5iQdfDfajcr93DF")
            addHeader("authorization", "Basic Yhd9X=38D88!")
            addHeader("accept-language", "en'")
        }.build()

        return chain.proceed(requestBuilder)
    }
}