package org.mule.weave.io.http.mock.functions

import org.mule.weave.io.http.mock.MockServer
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._

object MockServerRegistry {

  private val cache = new ConcurrentHashMap[String, MockServer]()

  def get(id: String): Option[MockServer] = {
    Option(cache.get(id))
  }

  def register(id: String, server: MockServer): Unit = {
    cache.computeIfAbsent(id, _=> server)
  }

  def getAll: Iterator[MockServer] = {
    cache.values().iterator().asScala
  }
}
