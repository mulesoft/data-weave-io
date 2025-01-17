package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.module.http.HttpHeader
import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.mule.weave.v2.module.http.service.HttpClientRequest
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.http.service.TlsConfiguration

import scala.collection.JavaConverters._

object HttpClientLoggingUtil {

  def appendClientConfiguration(buffer: StringBuilder, clientConfiguration: HttpClientConfiguration): StringBuilder = {
    buffer.append(s"connectionTimeout: ${clientConfiguration.getConnectionTimeout} (ms), ")
    buffer.append(s"compressionHeader: ${clientConfiguration.isCompressionHeader}, ")
    buffer.append(s"decompress: ${clientConfiguration.isDecompress}")
    if (clientConfiguration.getTlsConfiguration != null) {
      buffer.append(", ")
      appendTlsConfiguration(buffer, clientConfiguration.getTlsConfiguration)
    }
    buffer
  }

  private def appendTlsConfiguration(buffer: StringBuilder, tlsConfiguration: TlsConfiguration): Unit = {
    if (tlsConfiguration != null) {
      buffer.append("tls: {")
      buffer.append(s"insecure: ${tlsConfiguration.isInsecure}")
      buffer.append("}")
    }
  }

  def appendRequest(buffer: StringBuilder, httpClientRequest: HttpClientRequest): StringBuilder = {
    appendUrl(buffer, httpClientRequest)
    val contentType = resolveContentType(httpClientRequest)
    if (contentType.nonEmpty) {
      buffer.append(s", Content-Type: $contentType")
    }
    buffer.append(s", with-body: ${httpClientRequest.getBody != null}, ")
    appendRequestConfiguration(buffer, httpClientRequest)
    buffer
  }

  private def appendUrl(buffer: StringBuilder, httpClientRequest: HttpClientRequest): Unit = {
    buffer.append(s"url: (${httpClientRequest.getMethod}) ${httpClientRequest.getUrl}")
    appendQueryParams(buffer, httpClientRequest)
  }

  private def appendQueryParams(buffer: StringBuilder, httpClientRequest: HttpClientRequest): Unit = {
    if (httpClientRequest.getQueryParams != null) {
      val queryParams = httpClientRequest.getQueryParams
      val queryParamsNames = queryParams.namesIgnoreCase
      if (!queryParamsNames.isEmpty) {
        buffer.append('?')
        val iterator = queryParamsNames.asScala.iterator
        while (iterator.hasNext) {
          val queryParamName = iterator.next()
          val valuesIterator = queryParams.allValuesIgnoreCase(queryParamName).iterator()
          while (valuesIterator.hasNext) {
            val value = valuesIterator.next()
            buffer.append(s"$queryParamName=$value")
            if (valuesIterator.hasNext) {
              buffer.append('&')
            }
          }
          if (iterator.hasNext) {
            buffer.append(',')
          }
        }
      }
    }
  }

  private def resolveContentType(httpClientRequest: HttpClientRequest): String = {
    val buffer = new StringBuilder()
    if (httpClientRequest.getHeaders != null) {
      val contentTypes = httpClientRequest.getHeaders.allValuesIgnoreCase(HttpHeader.CONTENT_TYPE_HEADER)
      if (!contentTypes.isEmpty) {
        val iterator = contentTypes.asScala.iterator
        while (iterator.hasNext) {
          buffer.append(iterator.next())
          if (iterator.hasNext) {
            buffer.append(',')
          }
        }
      }
    }
    buffer.toString()
  }

  private def appendRequestConfiguration(buffer: StringBuilder, httpClientRequest: HttpClientRequest): Unit = {
    buffer.append("config: {")
    buffer.append(s"followRedirects: ${httpClientRequest.isFollowRedirects}, ")
    buffer.append(s"requestTimeout: ${httpClientRequest.getRequestTimeout} (ms), ")
    buffer.append(s"readTimeout: ${httpClientRequest.getReadTimeout} (ms)")
    buffer.append("}")
  }

  def appendResponse(buffer: StringBuilder, httpResponse: HttpClientResponse): StringBuilder = {
    buffer.append(s"status: ${httpResponse.getStatus}")
    httpResponse.getStatusText.ifPresent(statusText => {
      buffer.append(s" ($statusText)")
    })
    if (httpResponse.getContentType.isPresent) {
      buffer.append(s", Content-Type: ${httpResponse.getContentType.get()}")
    }

    buffer.append(s", with-body: ${httpResponse.getBody.isPresent}")
    buffer
  }
}
