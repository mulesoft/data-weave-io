package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.module.http.functions.utils.StopWatch.TOTAL

class StopWatch(on: Boolean) {
  private var _started = false
  private var _stopped = false
  private var _start: Long = _
  private var _checkpoint: Long = _
  private var _total: Long = _
  private var _times: Seq[(String, Long)] = Seq()

  if (on) {
    start()
  }

  def start(): Unit = {
    if (!_started) {
      _started = true
      startWatch()
    }
  }

  private def startWatch(): Unit = {
    this._start = System.currentTimeMillis()
    this._checkpoint = this._start
  }

  def stop(): Unit = {
    if (!_stopped) {
      _stopped = true
      val now = System.currentTimeMillis()
      val duration = now - _start
      _total = duration
      _times :+= (TOTAL, duration)
    }
  }

  def registerTime(metricName: String): Unit = {
    val now = System.currentTimeMillis()
    val duration = now - _checkpoint
    _checkpoint = now
    _times :+= (metricName, duration)
  }

  def getTimes: Seq[(String, Long)] = {
    _times
  }

  def getTotal: Long = {
    stop()
    _total
  }
}

object StopWatch {
  val TOTAL = "total"

  def apply(on: Boolean = false): StopWatch = new StopWatch(on)
}
