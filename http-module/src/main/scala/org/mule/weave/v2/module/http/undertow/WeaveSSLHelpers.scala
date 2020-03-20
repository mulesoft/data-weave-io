package org.mule.weave.v2.module.http.undertow

import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

import org.mule.weave.v2.io.SeekableStream
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.types.BinaryType
import org.mule.weave.v2.model.types.NullType
import org.mule.weave.v2.model.types.ObjectType
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.model.values.NullValue
import org.mule.weave.v2.model.values.ObjectValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.util.ObjectValueUtils

object WeaveSSLHelpers {

  def getClientSSLContext(sslOptions: Option[Value[_]], allowSelfSignedCertificates: Boolean = false)(implicit ctx: EvaluationContext): SSLContext = {
    val options: ObjectSeq = try {
      val sslOptionsObject: Value[_] = sslOptions.getOrElse(ObjectValue(Seq()))
      ObjectType.coerce(sslOptionsObject).evaluate
    } catch {
      case e: Throwable => throw new Exception("sslOptions must be an object", e)
    }

    val cert = ObjectValueUtils.select(options, "cert").getOrElse(NullValue)
    val password = ObjectValueUtils.select(options, "password").getOrElse(NullValue)
    val keyStoreTypeValue = ObjectValueUtils.select(options, "keyStoreTypeValue").getOrElse(NullValue)
    val sslProtocolValue = ObjectValueUtils.select(options, "sslProtocol").getOrElse(NullValue)

    val is: SeekableStream =
      try {
        if (NullType.accepts(cert))
          null
        else
          BinaryType.coerce(cert).evaluate
      } catch {
        case e: Throwable =>
          throw new Exception("sslOptions.cert must be a String or Binary or Null", e)
      }

    val pass: Array[Char] = try {
      if (NullType.accepts(password))
        null
      else
        StringType.coerce(password).evaluate.toCharArray
    } catch {
      case e: Throwable =>
        throw new Exception("sslOptions.password must be a String or Binary or Null", e)
    }

    val keyStoreType: String = try {
      if (NullType.accepts(keyStoreTypeValue))
        "JKS"
      else
        StringType.coerce(keyStoreTypeValue).evaluate
    } catch {
      case e: Throwable =>
        throw new Exception("sslOptions.keyStoreType must be \"JKS\" or \"PKCS12\"", e)
    }

    val sslProtocol: String = try {
      if (NullType.accepts(sslProtocolValue))
        "TLS"
      else
        StringType.coerce(sslProtocolValue).evaluate
    } catch {
      case e: Throwable =>
        throw new Exception("sslOptions.sslProtocol cannot be coerced to String", e)
    }

    val keyManagers: Array[KeyManager] = if (is != null || pass != null || keyStoreType != "JKS") {
      val loadedKeystore = KeyStore.getInstance(keyStoreType)
      loadedKeystore.load(is, pass)

      val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)

      keyManagerFactory.init(loadedKeystore, pass)

      keyManagerFactory.getKeyManagers
    } else {
      null
    }

    val trustManagers: Array[TrustManager] =
      if (allowSelfSignedCertificates) {
        Array[TrustManager](new X509TrustManager() {
          def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {
            println(authType)
          }

          def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {
            println(authType)
          }

          def getAcceptedIssuers: Array[X509Certificate] = Array[X509Certificate]()
        })
      } else {
        null
      }

    val context = SSLContext.getInstance(sslProtocol)

    context.init(keyManagers, trustManagers, new SecureRandom())
    context
  }

}
