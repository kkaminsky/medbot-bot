package com.kkaminsky.medbotbot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder


@Service
class RestTemplateServiceImpl(
        private val restTemplate: RestTemplate
) : RestTemplateService {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    /**
     * Метод для добавления параметров запроса (то, что после ?) и переменных пути
     */
    fun addRequestParamsAndUrlVariables(requestParams: Map<String, String?>, urlVariables: Map<String, String?>,
                                        uri: String): String {

        val builder = UriComponentsBuilder.fromHttpUrl(uri)

        val urlWithPathVariables = builder.buildAndExpand(urlVariables).toUri()

        val newBuilder = UriComponentsBuilder.fromUri(urlWithPathVariables)

        for (param in requestParams) {
            newBuilder.queryParam(param.key, param.value)
        }

        return newBuilder.toUriString()
    }

    override fun <TypeIn, TypeOut : Any> doPostRequest(urlVariables: Map<String, String?>, requestParams: Map<String, String?>,
                                                       payload: TypeIn, responseType: Class<TypeOut>, uri: String): TypeOut {

        val preparedUrl = addRequestParamsAndUrlVariables(requestParams = requestParams, urlVariables = urlVariables,
                uri = uri)


        logger.info("Отправлен POST запрос по урл $preparedUrl c телом $payload")


        val response = restTemplate.postForObject(preparedUrl, payload, responseType, urlVariables)
        return response
                ?: throw Exception("Произошла ошбибка при выполенении POST запроса по URL $preparedUrl c телом $payload! Невозможно получить ответ!")
    }

    override fun <TypeOut : Any> doGetRequest(urlVariables: Map<String, String?>, requestParams: Map<String, String?>,
                                              responseType: Class<TypeOut>, uri: String): TypeOut {

        val preparedUrl = addRequestParamsAndUrlVariables(requestParams = requestParams, urlVariables = urlVariables,
                uri = uri)

        logger.info("Отправлен GET запрос по урл $preparedUrl")

        val response = restTemplate.getForObject(preparedUrl, responseType)
        return response
                ?: throw Exception("Произошла ошбибка при выполенении GET запроса по URL $preparedUrl! Невозможно получить ответ!")
    }

    override fun <TypeIn : Any> doPutRequest(urlVariables: Map<String, String?>, requestParams: Map<String, String?>,
                                             payload: TypeIn, uri: String) {

        val preparedUrl = addRequestParamsAndUrlVariables(requestParams = requestParams, urlVariables = urlVariables,
                uri = uri)

        logger.info("Отправлен PUT запрос по урл $preparedUrl")

        restTemplate.put(preparedUrl, payload)

    }

}