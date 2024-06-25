package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.mule.weave.v2.module.http.service.HttpClientRequest
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.http.service.TlsConfiguration

import scala.collection.JavaConverters._

object HttpClientLoggingUtil {

  def appendClientConfiguration(buffer: StringBuilder, clientConfiguration: HttpClientConfiguration): StringBuilder = {
    buffer.append(s"connectionTimeout: ${clientConfiguration.getConnectionTimeout} (ms), ")
    buffer.append(s"compressionHeader: ${clientConfiguration.isCompressionHeader}, ")
    buffer.append(s"decompress: ${clientConfiguration.isDecompress}, ")
    appendTlsConfiguration(buffer, clientConfiguration.getTlsConfiguration)
    buffer
  }

  private def appendTlsConfiguration(buffer: StringBuilder, tlsConfiguration: TlsConfiguration): Unit = {
    buffer.append("tls: {")
    buffer.append(s"insecure: ${tlsConfiguration.isInsecure}")
    buffer.append("}")
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
    if (!httpClientRequest.getQueryParams.isEmpty) {
      buffer.append('?')
      val iterator = httpClientRequest.getQueryParams.asScala.iterator
      while (iterator.hasNext) {
        val queryParam = iterator.next()
        val valuesIterator = queryParam._2.iterator()
        while (valuesIterator.hasNext) {
          val value = valuesIterator.next()
          buffer.append(s"${queryParam._1}=$value")
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

  private def resolveContentType(httpClientRequest: HttpClientRequest): String = {
    val buffer = new StringBuilder()
    val maybeContentType = Option(httpClientRequest.getHeaders.get("Content-Type"))
    if (maybeContentType.isDefined) {
      val contentTypes = maybeContentType.get
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
