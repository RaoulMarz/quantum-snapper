package com.particle_life.models

import java.util

class Clock(n: Int):
  private var inTime: Long = -1
  private var lastTimes: Array[Double] = new Array[Double](n)
  private var currentTimeIndex = -1
  private var dt: Double = 0.0

  util.Arrays.fill(lastTimes, 0)

  /** Average passed time over the last <code>n</code> times {@link # tick ( )}
    * or {@link # in ( )} / {@link # out ( )} was called.
    */
  private var avgDt: Double = 0
  private var dtVariance: Double = 0

  /** @param n
    *   over how many values should the average be calculated? (default is 20)
    */
  /*
  def this(n: Int) {
    this()
    lastTimes = new Array[Double](n)
    Arrays.fill(lastTimes, 0)
  }
   */

  /** Shortcut for out() and in(). Use this if you simply want to measure how
    * much time passes between two tick() calls.
    */
  def tick(): Unit =
    if (inTime != -1) out()
    in()

  /** Use in() and out() if you want to time certain intervals instead of your
    * whole loop.
    */
  def in(): Unit =
    inTime = System.nanoTime

  def out(): Unit =
    if (inTime == -1)
      // must call in() before out()
      throw new RuntimeException(
        "Clock.out() was called even though Clock.in() was never called before"
      )
    dt = (System.nanoTime - inTime) / 1000000.0
    val n = lastTimes.length // for convenience

    // step to next index in array
    currentTimeIndex += 1
    if (currentTimeIndex >= n) currentTimeIndex = 0
    // put value in array
    lastTimes(currentTimeIndex) = dt
    // calc average and variance of array
    if (n < 2)
      avgDt = lastTimes(0)
      dtVariance = 0
    else
      var sum: Double = 0f
      var squareSum: Double = 0f
      for (t <- lastTimes)
        sum += t
        squareSum += t * t
      avgDt = sum / n
      dtVariance = (squareSum - n * avgDt * avgDt) / (n - 1)

  /** @return
    *   average passed time in milliseconds.
    */
  def getAvgDtMillis: Double = avgDt

  def getAvgFramerate: Double =
    if (avgDt == 0) return 0.0
    1000.0 / avgDt

  def getStandardDeviation: Double = Math.sqrt(dtVariance)

  def getDtMillis: Double = dt

  def getFramerate: Double =
    if (dt == 0) return 0.0
    1000.0 / lastTimes(currentTimeIndex)
