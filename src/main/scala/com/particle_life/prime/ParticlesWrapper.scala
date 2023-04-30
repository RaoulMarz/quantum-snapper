package com.particle_life.prime

import com.badlogic.gdx.Gdx
import com.particle_life.models.{AcceleratorProvider, DefaultAccelerator, DefaultMatrixGenerator, DefaultPositionSetter, DefaultTypeSetter, DrawBuffer, LoadDistributor, Loop, MatrixGeneratorProvider, Physics, PhysicsSettings, PositionSetterProvider, TypeSetterProvider}
import com.particle_life.selection.{ExportSettings, ImportSettings, SelectionManager}
import org.joml.Matrix4d
import org.joml.Vector2d
import org.joml.Vector3d

import java.util.concurrent.atomic.AtomicBoolean

class ParticlesWrapper:
  private var settings: PhysicsSettings = null
  private var particleCount = 0
  private var preferredNumberOfThreads = 0
  private var drawCounter : Int = 0
  private var cursorParticleCount = 0

  // particle rendering: constants
  private val zoomStepFactor = 1.2
  private val particleSize = 4.0f // particle size on screen (in pixels)

  private val keepParticleSizeIndependentOfZoom = false
  private val shiftSmoothness = 0.3
  private val zoomSmoothness = 0.3
  private val autoDt = true
  private val fallbackDt = 0.02

  private var physicsSnapshot: PhysicsSnapshot = null
  private val physicsSnapshotLoadDistributor = new LoadDistributor() // speed up taking snapshots with parallelization
  private var particlesDrawData: Option[DrawBuffer] = None

  var newSnapshotAvailable = new AtomicBoolean(false)

  private val accelerators = new SelectionManager[DefaultAccelerator]()
  private val matrixGenerators = new SelectionManager[DefaultMatrixGenerator]()
  private val positionSetters = new SelectionManager[DefaultPositionSetter]()
  private val typeSetters = new SelectionManager[DefaultTypeSetter]()
  private var physics: Physics = null
  private var loop: Loop = null
  private val exportSettings = new ExportSettings()
  private val importSettings = new ImportSettings()

  def setup(): Unit =
    // todo: throw error if any return 0 elements
    //shaders.addAll(new Nothing().create)
    //palettes.addAll(new Nothing().create)

    accelerators.addAll(new AcceleratorProvider().create)
    positionSetters.addAll(new PositionSetterProvider().create)
    matrixGenerators.addAll(new MatrixGeneratorProvider().create)
    typeSetters.addAll(new TypeSetterProvider().create)
    accelerators.setActive(2)
    positionSetters.setActive(5)
    createPhysics()
    // set default selection for palette
    //val preferredPaletteName = "RainbowSmooth12.map"
    //if (palettes.hasName(preferredPaletteName)) palettes.setActive(palettes.getIndexByName(preferredPaletteName))


  private def createPhysics(): Unit =
    physics = new Physics(
      accelerators.getActive.`object`,
      matrixGenerators.getActive.`object`,
      positionSetters.getActive.`object`,
      typeSetters.getActive.`object`
    )
    physics.initPhysics()
    physicsSnapshot = new PhysicsSnapshot()
    physicsSnapshot.take(physics, physicsSnapshotLoadDistributor)
    newSnapshotAvailable.set(true)
    particlesDrawData = Some(new DrawBuffer())
    loop = new Loop()
    loop.start((dt) => {
      physics.settings.dt =
        if (autoDt) dt
        else fallbackDt
      physics.update()
    })

  private def snapshotTaker(): Unit =
    physicsSnapshot.take(physics, physicsSnapshotLoadDistributor)
    newSnapshotAvailable.set(true)

  def getSnapshotValues: Option[DrawBuffer] = particlesDrawData

  def draw(dt: Double): Unit =
    if (newSnapshotAvailable.get)
      // get local copy of snapshot
      //renderer.bufferParticleData(physicsSnapshot.positions, physicsSnapshot.velocities, physicsSnapshot.types)
      if (particlesDrawData.nonEmpty)
        particlesDrawData.get.bufferParticleData(physicsSnapshot.positions, physicsSnapshot.velocities, physicsSnapshot.types)
      settings = physicsSnapshot.settings.deepCopy()
      particleCount = physicsSnapshot.particleCount
      preferredNumberOfThreads = physics.preferredNumberOfThreads
      newSnapshotAvailable.set(false)
      drawCounter += 1
      Gdx.app.log(
        "Paint-Fluidity -- [ParticlesWrapper]",
        s"draw(), dt=${dt}, drawCounter=${drawCounter}"
      )
    // renderClock.tick
    loop.doOnce(() => {
      physicsSnapshot.take(physics, physicsSnapshotLoadDistributor)
      newSnapshotAvailable.set(true)
    }
      /*snapshotTaker()*/ )

  protected def beforeClose(): Unit =
    try
      loop.stop(1000)
      physics.shutdown(1000)
      // physicsSnapshotLoadDistributor.shutdown(1000)
    catch
      case e: InterruptedException =>
        e.printStackTrace()
    // renderer.dispose

  private def setInitialExportSettings(): Unit =
    exportSettings.timeStep = !autoDt

  private def exportData(): Unit =
    System.out.println("Now exporting...")
    if (exportSettings.particles) System.out.println("- particles")
    if (exportSettings.matrix) System.out.println("- matrix")
    if (exportSettings.timeStep) System.out.println("- time step")

  private def importData(): Unit =
    System.out.println("Now importing...")
    if (exportSettings.timeStep)
      System.out.println("- path: " + importSettings.path)
