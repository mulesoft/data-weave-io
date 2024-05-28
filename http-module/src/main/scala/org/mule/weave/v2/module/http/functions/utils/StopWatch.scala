package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.module.http.functions.utils.StopWatch.TOTAL

class StopWatch {
  private var _stopped = false
  private var _start: Long = _
  private var _checkpoint: Long = _
  private var _times: Seq[(String, Number)] = Seq()

  def start(): Unit = {
    this._start = System.nanoTime()
    this._checkpoint = this._start
  }

  def stop(): Unit = {
    if (!_stopped) {
      _stopped = true
      val now = System.nanoTime()
      val duration: Number = (now - _start).toFloat / 1000000.0
      _times :+= (TOTAL, duration)
    }
  }

  def registerTime(metricName: String): Unit = {
    val now = System.nanoTime()
    val duration: Number = (now - _checkpoint).toFloat / 1000000.0
    _checkpoint = now
    _times :+= (metricName, duration)
  }

  def getTimes: Seq[(String, Number)] = {
    _times
  }
}

object StopWatch {
  val TOTAL = "total"

  def apply(): StopWatch = new StopWatch()
}
