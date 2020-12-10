package com.kkaminsky.medbotbot.service

interface RestTemplateService {

    fun <TypeIn, TypeOut : Any> doPostRequest(
            urlVariables: Map<String,String?> = mapOf(),
            requestParams: Map<String, String?> = mapOf(),
            payload: TypeIn,
            responseType: Class<TypeOut>,
            uri: String
    ): TypeOut

    fun <TypeOut : Any> doGetRequest(
            urlVariables: Map<String,String?> = mapOf(),
            requestParams: Map<String, String?> = mapOf(),
            responseType: Class<TypeOut>,
            uri: String): TypeOut


    fun  <TypeIn : Any> doPutRequest(
            urlVariables: Map<String,String?> = mapOf(),
            requestParams: Map<String, String?> = mapOf(),
            payload: TypeIn,
            uri: String
    )
}