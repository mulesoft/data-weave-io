package org.mule.weave.v2.module.http.netty.cookie

import io.netty.handler.codec.http.cookie.Cookie
import org.asynchttpclient.cookie.CookieStore
import org.asynchttpclient.uri.Uri

import java.util
import java.util.Collections
import java.util.function.Predicate

class EmptyCookieStore extends CookieStore {

  private val emptyCookies = Collections.emptyList[Cookie]

  override def add(uri: Uri, cookie: Cookie): Unit = {
    // Nothing to do
  }

  override def get(uri: Uri): util.List[Cookie] = {
    emptyCookies
  }

  override def getAll: util.List[Cookie] = {
    emptyCookies
  }

  override def remove(predicate: Predicate[Cookie]): Boolean = {
    // Nothing to do
    false
  }

  override def clear(): Boolean = {
    // Nothing to do
    false
  }

  override def evictExpired(): Unit = {
    // Nothing to do
  }

  override def incrementAndGet(): Int = {
    // Nothing to do
    0
  }

  override def decrementAndGet(): Int = {
    // Nothing to do
    0
  }

  override def count(): Int = 0
}

object EmptyCookieStore {
  def apply(): EmptyCookieStore = new EmptyCookieStore()
}