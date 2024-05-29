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
      this._start = System.currentTimeMillis()
      this._checkpoint = this._start
    }
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

  def registerTime(metric: String): Unit = {
    val now = System.currentTimeMillis()
    val duration = now - _checkpoint
    _checkpoint = now
    _times :+= (metric, duration)
  }

  def isStarted: Boolean = _started

  def isStopped: Boolean = _stopped

  def getTimes: Seq[(String, Long)] = {
    _times
  }

  def getTime(metric: String): Option[Long] = {
    _times.find(_._1 == metric).map(_._2)
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
