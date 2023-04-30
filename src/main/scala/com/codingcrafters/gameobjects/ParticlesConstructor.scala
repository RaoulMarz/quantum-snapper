package com.codingcrafters.gameobjects

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap.Format

import java.io.File
import java.io.StringWriter
import scala.math.*
import java.time.{LocalDateTime, LocalTime}
import org.joml.Vector2d
import com.badlogic.gdx.utils.{Array, ScreenUtils}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Pixmap, PixmapIO, Texture}
import com.badlogic.gdx.graphics.g2d.{Animation, Batch, SpriteBatch, TextureRegion}
import com.badlogic.gdx.graphics.glutils.{FrameBuffer, ShapeRenderer}
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.actions.Actions

import java.time.format.DateTimeFormatter
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.{Polygon, Rectangle, Vector2}
import com.badlogic.gdx.Gdx
import com.particle_life.prime.ParticlesWrapper

import scala.collection.mutable.Map

class ParticlesConstructor(
                            override val xP: Float,
                            override val yP: Float,
                            override val s: Stage,
                            val regionWidth: Float,
                            val regionHeight: Float,
                          ) extends BaseActor(xP, yP, s) :

  private var colorTypesMap: Map[Int, Color] = Map.empty[Int, Color]
  private val boundWidth: Float = Gdx.graphics.getWidth.toFloat
  private val boundHeight: Float = Gdx.graphics.getHeight.toFloat
  private val shapeRenderer = new ShapeRenderer
  private val cam = new OrthographicCamera
  private val batch: SpriteBatch = new SpriteBatch()
  private var frameBuffer: Option[FrameBuffer] = Some(
    new FrameBuffer(
      Pixmap.Format.RGBA8888,
      boundWidth.toInt,
      boundHeight.toInt,
      false
    )
  )
  private var primeDisplayTexture: Option[Texture] = None
  private var myPixMap: Option[Pixmap] = None
  private var bufferTextureRegion: Option[TextureRegion] = None
  private var particlesMainDrawer : ParticlesWrapper = new ParticlesWrapper()
  particlesMainDrawer.setup()
  configureTypeColors()

  private def configureTypeColors(): Unit =
    colorTypesMap += (0 -> Color.GRAY)
    colorTypesMap += (1 -> Color.GREEN)
    colorTypesMap += (2 -> Color.RED)
    colorTypesMap += (3 -> Color.GOLDENROD)
    colorTypesMap += (4 -> Color.LIME)
    colorTypesMap += (5 -> Color.FIREBRICK)
    colorTypesMap += (6 -> Color.ORANGE)

  def updateDraw(scene: Scene,
                 dt: Float): Unit =
    val drawBufferData = particlesMainDrawer.getSnapshotValues
    if (drawBufferData.nonEmpty)
      updatePhysics(scene,
        drawBufferData.get.getPositions,
        drawBufferData.get.getVelocities,
        drawBufferData.get.getTypes,
        dt)

  def updatePhysics(
                     scene: Scene,
                     particlePositions: scala.Array[Double],
                     particleVelocities: scala.Array[Double],
                     particleTypes: scala.Array[Int],
                     dt: Float
                   ): Unit =
    
    updateRenderer(
      Color.PURPLE,
      Color.LIME,
      particlePositions,
      particleVelocities,
      particleTypes,
      dt
    )

  private def savePixmapToFile(imageFileBase: String, pixmap: Pixmap): Unit =
    try
      val currentTime = LocalDateTime.now
      val format = "yyyy-MM-dd'#'HH-mm-ss"
      val myFormatObj = DateTimeFormatter.ofPattern(format)
      var fh: FileHandle = null
      fh = new FileHandle(imageFileBase + {
        currentTime.format(myFormatObj)
      } + ".png")
      PixmapIO.writePNG(fh, pixmap)
    catch
      case e: Exception =>
        Gdx.app.log("savePixmapToFile ", s"exception= ${e.getMessage}")

  private def updateRenderer(
                      circleColor: Color,
                      outlineColor: Color,
                      particlePositions: scala.Array[Double],
                      particleVelocities: scala.Array[Double],
                      particleTypes: scala.Array[Int],
                      dt: Float
                    ): Unit = {
    //val xPP: Float = 10.0f
    //val yPP: Float = 10.0f
    //val rect =
    //  new Rectangle(10.0f, 10.0f, regionWidth - 20.0f, regionHeight - 20.0f)
    val radius = 80.0f
    particlesMainDrawer.draw(dt)
    cam.setToOrtho(false, regionWidth, regionHeight)
    batch.setProjectionMatrix(cam.combined)
    frameBuffer.get.begin()
    Gdx.gl.glEnable(GL20.GL_ARRAY_BUFFER_BINDING)
    Gdx.gl.glEnable(GL20.GL_BLEND)
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

    val circleOrigin : Vector2d = new Vector2d(regionWidth / 2f + 200f, regionHeight / 2f - 300f)
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    batch.begin()
    Gdx.gl.glLineWidth(4)
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    shapeRenderer.setColor(circleColor)
    shapeRenderer.circle(circleOrigin.x.toFloat, circleOrigin.y.toFloat, radius)
    // shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height)
    shapeRenderer.end()

    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
    shapeRenderer.setColor(outlineColor)
    shapeRenderer.circle(circleOrigin.x.toFloat, circleOrigin.y.toFloat, radius)
    shapeRenderer.end()

    if ( (particlePositions != null) && (particleVelocities != null) )
      var ipx = 0
      var idx: Int = 0
      var particleColor: Color = Color.BLUE // new Color()
      var pxPos_X: Double = 0
      var pxPos_Y: Double = 0
      var plotPoint: Vector2d = new Vector2d(0, 0)
      for (pxPos <- particlePositions)
        //println(pxPos)
        //3 values -> x, y, z ... z is always 0 in current implementation
        if (ipx % 3 == 0)
          pxPos_X = pxPos
        else
          if (ipx % 3 == 1)
          pxPos_Y = pxPos

        if (ipx % 3 == 2)
          plotPoint.x = (pxPos_X + 1.0) * regionWidth// * 0.5
          plotPoint.y = (pxPos_Y + 1.0) * regionHeight// * 0.5
          if ( (idx < particleTypes.length) && (colorTypesMap.keySet.contains(particleTypes(idx))) )
            particleColor = colorTypesMap(particleTypes(idx))
          //if (ipx < 6000) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.setColor(particleColor)
            shapeRenderer.circle(plotPoint.x.toFloat / 2f, plotPoint.y.toFloat / 2f, 4.0f)
            shapeRenderer.end()
          //}
          idx += 1
        ipx += 1

    Gdx.gl.glLineWidth(8)
    /*
    val starRect = new Rectangle(
      (regionWidth / 2f) - 20f,
      (regionHeight / 2f) - 20f,
      40f,
      40f
    )
    */
    shapeRenderer.flush()
    batch.flush() // flush batch
    batch.end();

    val texShapes: Texture = frameBuffer.get.getColorBufferTexture
    texShapes.setFilter(
      Texture.TextureFilter.Nearest,
      Texture.TextureFilter.Nearest
    )
    if (!(texShapes.getTextureData.isPrepared))
      texShapes.getTextureData.prepare()
    bufferTextureRegion = Some(
      new TextureRegion(texShapes, 0, 0, regionWidth.toInt, regionHeight.toInt)
    )
    frameBuffer.get.end()

    var textureRegionsArray = new com.badlogic.gdx.utils.Array[TextureRegion]
    textureRegionsArray.add(bufferTextureRegion.get)
    loadAnimationFromTextureRegions(textureRegionsArray, 0.1f, true)
  }