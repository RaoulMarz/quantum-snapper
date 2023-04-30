package com.particle_life.models

import com.badlogic.gdx.Gdx
import org.joml.Vector3d

import scala.util.Random
import java.util
import java.util.concurrent.atomic.AtomicBoolean

class Physics(
    val accelerator: DefaultAccelerator,
    val matrixGenerator: DefaultMatrixGenerator,
    val positionSetter: DefaultPositionSetter,
    val typeSetter: DefaultTypeSetter
):
  private val DEFAULT_MATRIX_SIZE = 7
  private val DEFAULT_PARTICLES_COUNT = 10000
  var settings: PhysicsSettings = new PhysicsSettings()

  var particles: Array[Particle] = new Array[Particle](DEFAULT_PARTICLES_COUNT)

  // buffers for sorting by containers:
  private var containers: Array[Int] = new Array[Int](DEFAULT_PARTICLES_COUNT)
  private var containerNeighborhood: Array[Array[Int]] = null
  private var particlesBuffer: Array[Particle] = new Array[Particle](DEFAULT_PARTICLES_COUNT)

  private var loadDistributor : LoadDistributor = new LoadDistributor()

  /**
    * This is used to stop the updating mid-particle.
    */
  private var updateThreadsShouldRun : AtomicBoolean = new AtomicBoolean(false);

  private var initParticlesArrayFlag : Boolean = false
  // container layout:
  private var nx = 0
  private var ny = 0
  private var errTypeProcCounter = 0
  private var containerSize =
    0.13 // todo: implement makeContainerNeighborhood() to make this independent of rmax

  var preferredNumberOfThreads = 12

  def initPhysics(): Unit =
    calcNxNy()
    makeContainerNeighborhood()

    generateMatrix()
    setParticleCount(10000) // uses current position setter to create particles

  private def calcNxNy(): Unit =
    //        nx = (int) Math.ceil(2 * range.range.x / containerSize);
    //        ny = (int) Math.ceil(2 * range.range.y / containerSize);
    // currently, "floor" is needed (because containerSize = rmax), so that rmax lies inside "simple" neighborhood
    nx = Math.floor(2 / containerSize).toInt
    ny = Math.floor(2 / containerSize).toInt

  private def makeContainerNeighborhood(): Unit =
    containerNeighborhood = Array[Array[Int]](
      Array[Int](-1, -1),
      Array[Int](0, -1),
      Array[Int](1, -1),
      Array[Int](-1, 0),
      Array[Int](0, 0),
      Array[Int](1, 0),
      Array[Int](-1, 1),
      Array[Int](0, 1),
      Array[Int](1, 1)
    )

  def update(): Unit =
    updateParticles()

  private def updateParticles(): Unit =
    updateThreadsShouldRun.set(true)
    makeContainers()
    loadDistributor.distributeLoadEvenly(particles.length, preferredNumberOfThreads, (i: Int) => {
      def foo(i : Int): Boolean = {
        if (!updateThreadsShouldRun.get) return false
        updateVelocity(i)
        true
      }

      foo(i)
    })
    loadDistributor.distributeLoadEvenly(particles.length, preferredNumberOfThreads, (i: Int) => {
      def foo(i : Int): Boolean = {
        if (!updateThreadsShouldRun.get) return false
        updatePosition(i)
        true
      }

      foo(i)
    })
    updateThreadsShouldRun.set(false)

  def forceUpdateStop(): Unit =
    updateThreadsShouldRun.set(false)

/**
  * Shutdown the internal thread pool.
  * Blocks until all tasks have completed execution.
  *
  * @param timeoutMilliseconds how long to wait for update threads to finish their execution (in milliseconds)
  * @return {@code true} if all tasks terminated and {@code false} if the timeout elapsed before termination
  */
  @throws[InterruptedException]
  def shutdown(timeoutMilliseconds: Long): Boolean =
    return loadDistributor.shutdown(timeoutMilliseconds)

  def setPositions(): Unit =
    util.Arrays.stream(particles).forEach(this.setPosition)

  private def generateMatrix(): Unit =
    val prevSize =
      if (settings.matrix != null) settings.matrix.size()
      else DEFAULT_MATRIX_SIZE
    settings.matrix = matrixGenerator.makeMatrix(prevSize)
    assert(
      settings.matrix.size() eq prevSize,
      "Matrix size should only change via setMatrixSize()"
    )

  def setMatrixSize(newSize: Int): Unit =
    val prevMatrix = settings.matrix
    val prevSize = prevMatrix.size()
    if (newSize == prevSize) return () // keep previous matrix

    settings.matrix = matrixGenerator.makeMatrix(newSize)
    assert(settings.matrix.size() eq newSize)
    // copy as much as possible from previous matrix
    val commonSize = Math.min(prevSize, newSize)
    for (i <- 0 until commonSize)
      for (j <- 0 until commonSize)
        settings.matrix.set(i, j, prevMatrix.get(i, j))
    if (newSize < prevSize)
      // need to change types of particles that are not in the new matrix
      for (smp <- particles)
        if (smp.`type` >= newSize) setType(smp)

  /** Use this to avoid the container pattern showing (i.e. if particles are
    * treated differently depending on their position in the array).
    */
  private def shuffleParticles(): Unit =
    Random.shuffle(particles) // Arrays.asList(particles)

  /** Creates a new particle and <ol> <li>sets its type using the default type
    * setter</li> <li>sets its position using the active position setter</li>
    * </ol> (in that order) and returns it.
    */
  private def generateParticle(): Particle =
    val p = new Particle()
    setType(p)
    setPosition(p)
    p

  protected def setPosition(pp: Particle): Unit =
    if (pp == null)
      return ()
    positionSetter.set(pp.position, pp.`type`, settings.matrix.size())
    Range.wrap(pp.position)
    pp.velocity.x = 0
    pp.velocity.y = 0
    pp.velocity.z = 0

  protected def setType(tp: Particle): Unit =
    if (tp == null)
      return ()
    tp.`type` = typeSetter.getType(
      new Vector3d(tp.position),
      new Vector3d(tp.velocity),
      tp.`type`,
      settings.matrix.size()
    )

  private def makeContainers(): Unit =
    // ensure that nx and ny are still OK
    containerSize =
      settings.rmax // todo: in the future, containerSize should be independent of rmax

    calcNxNy() // todo: only change if containerSize (or range) changed

    // todo: (future) containerNeighborhood depends on rmax and containerSize.
    // if (rmax changed or containerSize changed) {
    //     makeContainerNeighborhood();
    // }
    // init arrays
    if (containers == null || (containers.length ne nx * ny))
      containers = new Array[Int](nx * ny)
    util.Arrays.fill(containers, 0)
    if (particlesBuffer == null || (particlesBuffer.length ne particles.length))
      particlesBuffer = new Array[Particle](particles.length)
    // calculate container capacity
    for (p <- particles)
      if (p != null)
        val ci = getContainerIndex(p.position)
        containers(ci) += 1
    // capacity -> index
    var offset = 0
    var i = 0
    while (i < containers.length)
      val cap = containers(i)
      containers(i) = offset
      offset += cap
      i += 1
    // fill particles into containers
    for (p <- particles)
      if (p != null)
        val ci = getContainerIndex(p.position)
        val i = containers(ci)
        particlesBuffer(i) = p
        containers(ci) += 1 // for next access
    // swap buffers
    val h = particles
    particles = particlesBuffer
    particlesBuffer = h

  private def getContainerIndex(position: Vector3d) =
    var cx = ((position.x + 1) / containerSize).asInstanceOf[Int]
    var cy = ((position.y + 1) / containerSize).asInstanceOf[Int]
    // for solid borders
    if (cx == nx) cx = nx - 1
    if (cy == ny) cy = ny - 1
    cx + cy * nx

  private def wrapContainerX(cx: Int) = if (cx < 0) cx + nx
  else if (cx >= nx) cx - nx
  else cx

  private def wrapContainerY(cy: Int) = if (cy < 0) cy + ny
  else if (cy >= ny) cy - ny
  else cy

  private def computeFrictionFactor(halfLife: Double, dt: Double): Double =
    if (halfLife == 0) return 0.0 // avoid division by zero
    if (halfLife == Double.PositiveInfinity) return 1.0
    Math.pow(0.5, dt / halfLife)

  private def updateVelocity(i: Int): Unit =
    val upv = particles(i)
    if (upv == null)
      return ()
    // apply friction before adding new velocity
    upv.velocity.mul(
      computeFrictionFactor(settings.velocityHalfLife, settings.dt)
    )
    val cx0 = Math.floor((upv.position.x + 1) / containerSize).toInt
    val cy0 = Math.floor((upv.position.y + 1) / containerSize).toInt
    var continueLoop: Boolean = true
    for (containerNeighbor <- containerNeighborhood)
      continueLoop = true
      var cx = wrapContainerX(cx0 + containerNeighbor(0))
      var cy = wrapContainerY(cy0 + containerNeighbor(1))
      if (settings.wrap)
        cx = wrapContainerX(cx)
        cy = wrapContainerX(cy)
      else if (cx < 0 || cx >= nx || cy < 0 || cy >= ny) continueLoop = false
      if (continueLoop)
        val ci = cx + cy * nx
        val start =
          if (ci == 0) 0
          else containers(ci - 1)
        val stop = containers(ci)
        for (j <- start until stop)
          if (i == j) continueLoop = false
          val q = particles(j)
          val relativePosition = connection(upv.position, q.position)
          val distanceSquared = relativePosition.lengthSquared
          // only check particles that are closer than or at rmax
          if (
            distanceSquared != 0 && distanceSquared <= settings.rmax * settings.rmax
          )
            relativePosition.div(settings.rmax)
            val deltaV = accelerator.accelerate(
              settings.matrix.get(upv.`type`, q.`type`),
              relativePosition
            )
            // apply force as acceleration
            upv.velocity.add(
              deltaV.mul(settings.rmax * settings.force * settings.dt)
            )

  private def updatePosition(i: Int): Unit =
    val p = particles(i)
    if (p == null)
      return ()
    // pos += vel * dt
    p.velocity.mulAdd(settings.dt, p.position, p.position)
    ensurePosition(p.position)

  def connection(pos1: Vector3d, pos2: Vector3d): Vector3d =
    val delta = new Vector3d(pos2).sub(pos1)
    if (settings.wrap)
      // wrapping the connection gives us the shortest possible distance
      Range.wrap(delta)
    delta

  def distance(pos1: Nothing, pos2: Nothing): Double =
    connection(pos1, pos2).length

  /** Changes the coordinates of the given vector to ensures that they are in
    * the correct range. <ul> <li> If <code>settings.wrap == false</code>, the
    * coordinates are simply clamped to [-1.0, 1.0]. </li> <li> If
    * <code>settings.wrap == true</code>, the coordinates are made to be inside
    * [-1.0, 1.0) by adding or subtracting multiples of 2. </li> </ul> This
    * method is called by {@link # update ( )} after changing the particles'
    * positions. It is just exposed for convenience. That is, if you change the
    * coordinates of the particles yourself, you can use this to make sure that
    * the coordinates are in the correct range before {@link # update ( )} is
    * called.
    *
    * @param position
    */
  def ensurePosition(position: Vector3d): Unit =
    if (settings.wrap) Range.wrap(position)
    else Range.clamp(position)

  def setTypes(): Unit =
    util.Arrays.stream(particles).forEach((p) => setType(p))

  def setParticleCount(n: Int): Unit =
    if (particles == null)
      particles = new Array[Particle](n)
      initParticlesArrayFlag = false
    if (!initParticlesArrayFlag)
      initParticlesArrayFlag = true
      for (i <- 0 until n)
        particles(i) = generateParticle()
    else if (n != particles.length)
      // strategy: if the array size changed, try to keep most of the particles
      val newParticles = new Array[Particle](n)
      if (n < particles.length)
        // randomly shuffle particles first
        // (otherwise, the container layout becomes visible)
        shuffleParticles()
        // copy previous array as far as possible
        for (i <- 0 until n)
          newParticles(i) = particles(i)
      else // array becomes longer
        // copy old array and add particles to the end
        var i = 0
        while (i < particles.length)
          newParticles(i) = particles(i)

          i += 1
        for (i <- particles.length until n)
          newParticles(i) = generateParticle()
      particles = newParticles

  def getTypeCount: Array[Int] =
    val typeCount = new Array[Int](settings.matrix.size())
    try
      util.Arrays.fill(typeCount, 0)
      val particlesCount = particles.length;
      var idx = 0
      for (getTCParticle <- particles)
        if (getTCParticle != null)
          typeCount(getTCParticle.`type`) += 1
        else
          if (errTypeProcCounter <= 400)
            Gdx.app.log(
              "Paint-Fluidity -- [Physics]",
              s"getTypeCount(), matrix size=${settings.matrix.size()}, particlesCount=${particlesCount}, idx=${idx}"
            )
          errTypeProcCounter += 1
        idx += 1
    catch
      case e: ArrayIndexOutOfBoundsException => {
        println(s"[Physics] getTypeCount(), exception=${e.getMessage}, matrix size=${settings.matrix.size()}")
      }
      case e: NullPointerException => {
        println(s"[Physics] getTypeCount(), exception=${e.getMessage}, matrix size=${settings.matrix.size()}")
      }
    typeCount

  def setTypeCountEqual(): Unit =
    val nTypes = settings.matrix.size()
    if (nTypes < 2) return
    val idealTypeCount = new Array[Int](nTypes)
    val count = Math.ceil(particles.length / nTypes.toDouble).toInt
    util.Arrays.fill(idealTypeCount, 0, nTypes - 1, count)
    idealTypeCount(nTypes - 1) = particles.length - (nTypes - 1) * count
    setTypeCount(idealTypeCount)

  private def setTypeCount(typeCount: Array[Int]): Unit =
    val nTypes = settings.matrix.size()
    if (nTypes < 2) return ()

    if (typeCount.length != nTypes)
      throw new IllegalArgumentException(String.format("Got array of length %d, but current matrix size is %d. Maybe you should change the matrix size before doing this.", typeCount.length, nTypes))
    // randomly shuffle particles first
    // (otherwise, the container layout becomes visible)
    shuffleParticles()
    val newCount = util.Arrays.stream(typeCount).sum
    if (newCount != particles.length)
      val newParticles = new Array[Particle](newCount)
      val actualTypeCount = new Array[Int](nTypes)
      util.Arrays.fill(actualTypeCount, 0)
      // sort all unusable particles to the end
      var i = 0
      var j = particles.length - 1
      while (i < j)
        val `type` = particles(i).`type`
        if (actualTypeCount(`type`) < typeCount(`type`))
          // need more of this type -> leave it in front
          actualTypeCount(`type`) += 1
          i += 1
        else
          // have enough of this type -> swap to back
          ArrayUtils.swap(particles, i, j)
          j -= 1
      // now i points at the end (exclusive) of the reusable particles
      // copy as much as possible
      val copyLength = Math.min(newCount, particles.length)
      var k = 0
      while (k < copyLength)
        newParticles(k) = particles(k)
        k += 1
      // if necessary, fill up rest with new particles
      while (k < newCount)
        newParticles(k) = new Particle()
        k += 1
      // change types of all particles that couldn't be reused
      while (i < newCount)
        // find type that has too few particles
        val `type` = ArrayUtils.findFirstIndexWithLess(actualTypeCount, typeCount) // need more of this type

        val tccp = newParticles(i)
        tccp.`type` = `type`
        setPosition(tccp) // possible that position setter is based on type

        actualTypeCount(`type`) += 1
        i += 1
      particles = newParticles
    else
      val actualTypeCount = getTypeCount
      for (stcp <- particles)
        if (actualTypeCount(stcp.`type`) > typeCount(stcp.`type`))
          // need fewer of this type
          // find type that has too few particles
          val `type` = ArrayUtils.findFirstIndexWithLess(actualTypeCount, typeCount) // need more of this type

          // change type
          actualTypeCount(stcp.`type`) -= 1
          stcp.`type` = `type`
          actualTypeCount(`type`) += 1
