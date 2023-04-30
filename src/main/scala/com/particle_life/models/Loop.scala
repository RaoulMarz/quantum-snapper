package com.particle_life.models

import com.particle_life.interfaces.LoopCallback

import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

class Loop :
  var maxDt: Double = 1.0 / 20.0 // min. 20 fps
  /**
    * If this is <code>true</code>, the callback won't be called in the loop.
    */
  var pause = false
  private val clock = new Clock(60)

  private var loopThread: Thread = null
  private val loopShouldRun = new AtomicBoolean(false)

  private val commandQueue : LinkedBlockingDeque[Runnable] = new LinkedBlockingDeque[Runnable]()
  private val once: AtomicReference[Runnable] = new AtomicReference[Runnable](null)

  def enqueue(cmd: Runnable): Unit =
    //todo: debug print if some GUI elements spam commands
    commandQueue.addLast(cmd)

  def doOnce(cmd: Runnable): Unit =
    once.set(cmd)

  private def loop(loop: LoopCallback): Unit =
    clock.tick()
    processCommandQueue()
    val onceCommand = once.getAndSet(null)
    if (onceCommand != null) onceCommand.run()
    if (!pause) loop.call(computeDt())

  private def processCommandQueue(): Unit =
    var cmd: Runnable = null
    var cmdRunning : Boolean = true;
    while (cmdRunning)
      cmd = commandQueue.pollFirst()
      cmdRunning = cmd != null
      if (cmdRunning)
        cmd.run()

  def start(loop: LoopCallback): Unit =
    if (loopThread != null) throw new IllegalStateException("Loop thread didn't finish properly (wasn't null).")
    loopShouldRun.set(true)
    loopThread = new Thread(() => {
      while (loopShouldRun.get) this.loop(loop)

    })
    loopThread.start()

/**
  * Tells the loop to stop and waits for the current iteration to finish.
  * A timeout of 0 means to wait forever.
  *
  * @param millis the time to wait for the loop to finish in milliseconds
  * @return whether the thread could be stopped
  */
  @throws[InterruptedException]
  def stop(millis: Long): Boolean =
    assert(loopThread != null, "Thread is null")
    if (!(loopThread.isAlive))
      throw new IllegalStateException("Thread is not running.")
    loopShouldRun.set(false)
    loopThread.join(millis) // A timeout of 0 means to wait forever.

    if (loopThread.isAlive)
      return false
    loopThread = null
    return true

  private def computeDt(): Double =
    val dt: Double = clock.getDtMillis / 1000.0
    return Math.min(maxDt, dt)

  def getActualDt(): Double = clock.getDtMillis / 1000.0

  def getAvgFramerate(): Double =
    return clock.getAvgFramerate