package com.particle_life.models

class PhysicsSettings:

  /** Allows particles to move and interact across the world's borders (-1.0,
    * +1.0).
    */
  var wrap = true

  /** no interaction between particles that are further apart than rmax
    */
  var rmax = 0.04

  /** The time in seconds after which half the velocity of a particle should be
    * lost due to friction. The actual friction factor <code>f</code> that the
    * velocity is multiplied with in every time step is calculated on the basis
    * of this value according to the following formula: <code>f = Math.pow(0.5,
    * dt / frictionHalfLife)</code>
    */
  var velocityHalfLife : Double = 0.043f

  /** Scaled force by an arbitrary factor.
    */
  var force : Double = 1.0f

  /** Time that is assumed to have passed between each simulation step, in
    * seconds.
    */
  var dt : Double = 0.02f
  var matrix = new DefaultMatrix(6)

  def deepCopy(): PhysicsSettings =
    val p = new PhysicsSettings()
    p.wrap = wrap
    p.rmax = rmax
    p.velocityHalfLife = velocityHalfLife
    p.force = force
    p.dt = dt
    p.matrix = matrix.deepCopy()
    p

