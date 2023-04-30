package com.particle_life.prime

import com.particle_life.models.{
  LoadDistributor,
  Particle,
  Physics,
  PhysicsSettings
}
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class PhysicsSnapshot:
  private val logger = LogManager.getLogger
  private val PREFERRED_NUMBER_OF_THREADS = 12

  var positions: Array[Double] = null
  var velocities: Array[Double] = null
  var types: Array[Int] = null

  var settings: PhysicsSettings = null
  var particleCount: Int = 0
  var typeCount: Array[Int] = null

  /** unix timestamp from when this snapshot was taken (milliseconds)
    */
  var snapshotTime: Long = 0L

  def take(p: Physics, loadDistributor: LoadDistributor): Unit =
    write(p.particles, loadDistributor)
    settings = p.settings.deepCopy()
    particleCount = p.particles.length
    typeCount = p.getTypeCount
    snapshotTime = System.currentTimeMillis

  private def write(
    particles: Array[Particle],
    loadDistributor: LoadDistributor
  ): Unit =
    val n: Int = particles.length
    if (types == null || types.length != n)
      positions = new Array[Double](n * 3)
      velocities = new Array[Double](n * 3)
      types = new Array[Int](n)
    val result: String = String.format(
      "PhysicsSnapshot void write(), n=%1$s, types.length=%2$s",
      n,
      types.length
    )
    logger.info(result)
    loadDistributor.distributeLoadEvenly(
    n,
    PREFERRED_NUMBER_OF_THREADS,
    (i) => {
      val p: Particle = particles(i)
      val i3: Int = 3 * i
      if (p != null) {
        positions(i3) = p.position.x
        positions(i3 + 1) = p.position.y
        positions(i3 + 2) = p.position.z
        velocities(i3) = p.velocity.x
        velocities(i3 + 1) = p.velocity.y
        velocities(i3 + 2) = p.velocity.z
        types(i) = p.`type`
      }
      true
    }
  )
