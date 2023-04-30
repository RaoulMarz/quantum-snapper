package com.codingcrafters.gameobjects

import scala.math._
import java.time.{LocalDateTime, LocalTime}
import com.badlogic.gdx.utils.{Array, ScreenUtils}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.{
  Color,
  GL20,
  OrthographicCamera,
  Pixmap,
  PixmapIO,
  Texture
}
import com.badlogic.gdx.graphics.g2d.{
  Animation,
  Batch,
  SpriteBatch,
  TextureRegion
}
import com.badlogic.gdx.graphics.glutils.{FrameBuffer, ShapeRenderer}
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.actions.Actions

import java.time.format.DateTimeFormatter
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.{Polygon, Rectangle, Vector2}
import com.badlogic.gdx.Gdx

/*
stage = new Stage(new ScreenViewport());
skin = new Skin(Gdx.files.internal("skin.json"));
Gdx.input.setInputProcessor(stage);

Table table = new Table();
table.setFillParent(true);

TextButton textButton = new TextButton("Enter Space", skin);
textButton.setName("Title");
table.add(textButton);

TextArea textArea = new TextArea(null, skin);
textArea.setName("TextZone");
textArea.setMaxLength(120);
table.add(textArea);

table.add();

table.add();

table.row();
table.add();

table.add();

table.add();

table.add();

table.row();
table.add();

table.add();

table.add();

table.add();
stage.addActor(table);
*/


class ContextSelectionPainter(
                       override val xP: Float,
                       override val yP: Float,
                       override val s: Stage,
                       val regionWidth: Float,
                       val regionHeight: Float,
                       val menuWidth: Float,
                       val menuHeight: Float,
                       val menuTitle: String,
                       val selectionOptions: scala.Array[String],
                       val borderThickness: Float = 2.0f,
                       val startIndex: Int = 0
                     ) extends BaseActor(xP, yP, s) :

  private val boundWidth: Float = Gdx.graphics.getWidth.toFloat
  private val boundHeight: Float = Gdx.graphics.getHeight.toFloat
  private val shapeRenderer = new ShapeRenderer
  private val cam = new OrthographicCamera
  private val batch: SpriteBatch = new SpriteBatch()
  private var selectedIndex : Int = startIndex
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

  def updatePhysics(
                     scene: Scene,
                     /*
                     menuWidth: Float,
                     menuHeight: Float,
                     menuTitle: String,
                     selectionOptions: scala.Array[String],
                     borderThickness: Float = 2.0f
                     */
                   ): Unit =
    //val compartmentCoverAngles: scala.Array[Float] =
    //  new scala.Array[Float](compartmentVolumes.length);
    //for (compartmentIndex <- compartmentVolumes.indices) {
    //
    //}
    updateRenderer(
      Color.WHITE,
      Color.LIME,
      menuWidth,
      menuHeight,
      menuTitle,
      selectionOptions,
      borderThickness,
      selectedIndex
    )

  private def updateRenderer(
                      circleColor: Color,
                      outlineColor: Color,
                      menuWidth: Float,
                      menuHeight: Float,
                      menuTitle: String,
                      selectionOptions: scala.Array[String],
                      borderThickness: Float = 2.0f,
                      selectedIndex: Int = 0
                    ): Unit =
    val xPP: Float = 10.0f
    val yPP: Float = 10.0f
    val radius = 120f
    val rect =
      new Rectangle(10.0f, 10.0f, regionWidth - 20.0f, regionHeight - 20.0f)
    // val batch: SpriteBatch = new SpriteBatch()
    // val frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, boundWidth.toInt, boundHeight.toInt, false)
    cam.setToOrtho(false, regionWidth, regionHeight)
    batch.setProjectionMatrix(cam.combined)
    frameBuffer.get.begin()
    Gdx.gl.glEnable(GL20.GL_ARRAY_BUFFER_BINDING)
    Gdx.gl.glEnable(GL20.GL_BLEND)
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    batch.begin()
    Gdx.gl.glLineWidth(4)
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    shapeRenderer.setColor(circleColor)
    shapeRenderer.circle(regionWidth / 2f, regionHeight / 2f, radius)
    // shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height)
    shapeRenderer.end()

    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
    shapeRenderer.setColor(outlineColor)
    shapeRenderer.circle(regionWidth / 2f, regionHeight / 2f, radius)
    shapeRenderer.end()

    Gdx.gl.glLineWidth(8)

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
