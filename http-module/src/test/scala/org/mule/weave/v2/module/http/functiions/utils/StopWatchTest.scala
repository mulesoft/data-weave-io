package org.mule.weave.v2.module.http.functiions.utils

import org.mule.weave.v2.module.http.functions.utils.StopWatch
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class StopWatchTest extends AnyFreeSpec with Matchers {

  "StopWatch starts turned on" in {
    val stopWatch = StopWatch(on = true)

    stopWatch.isStarted shouldBe true
    stopWatch.isStopped shouldBe false

    // Wait some time
    Thread.sleep(1000)

    stopWatch.stop()

    stopWatch.isStarted shouldBe true
    stopWatch.isStopped shouldBe true

    // Assert times
    val times = stopWatch.getTimes
    times.size shouldBe 1
    val maybeTotalTime = times.find(_._1 == StopWatch.TOTAL)
    maybeTotalTime.isDefined shouldBe true
    val total = maybeTotalTime.get._2
    total shouldBe stopWatch.getTotal
    (stopWatch.getTotal >= 0) shouldBe true
  }

  "StopWatch starts turned off" in {
    val stopWatch = StopWatch()

    stopWatch.isStarted shouldBe false
    stopWatch.isStopped shouldBe false

    stopWatch.start()

    // Wait some time
    Thread.sleep(1000)

    stopWatch.stop()

    stopWatch.isStarted shouldBe true
    stopWatch.isStopped shouldBe true

    // Assert times
    val times = stopWatch.getTimes
    times.size shouldBe 1
    val maybeTotalTime = times.find(_._1 == StopWatch.TOTAL)
    maybeTotalTime.isDefined shouldBe true
    val total = maybeTotalTime.get._2
    total shouldBe stopWatch.getTotal
    (stopWatch.getTotal >= 0) shouldBe true
  }

  "StopWatch should register times successfully" in {
    val stopWatch = StopWatch()

    stopWatch.isStarted shouldBe false
    stopWatch.isStopped shouldBe false

    stopWatch.start()

    // Wait some time
    Thread.sleep(1000)
    stopWatch.registerTime("partial-1")

    // Wait some time
    Thread.sleep(1500)
    stopWatch.registerTime("partial-2")

    // Total force stop
    stopWatch.getTotal

    stopWatch.isStarted shouldBe true
    stopWatch.isStopped shouldBe true

    // Assert times
    val times = stopWatch.getTimes
    times.size shouldBe 3

    val maybePartial1 = stopWatch.getTime("partial-1")
    maybePartial1.isDefined shouldBe true

    val maybePartial2 = stopWatch.getTime("partial-2")
    maybePartial2.isDefined shouldBe true

    val maybePartial3 = stopWatch.getTime("unknown")
    maybePartial3.isDefined shouldBe false

    (maybePartial2.get > maybePartial1.get) shouldBe true

    val maybeTotalTime = times.find(_._1 == StopWatch.TOTAL)
    maybeTotalTime.isDefined shouldBe true
    val total = maybeTotalTime.get._2
    total shouldBe stopWatch.getTotal
    (stopWatch.getTotal >= 0) shouldBe true
  }
}
