package com.codingcrafters.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.{Animation, Batch, SpriteBatch, TextureRegion}
import com.badlogic.gdx.graphics.glutils.{FrameBuffer, ShapeRenderer}
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Label, Skin, Table}
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.badlogic.gdx.utils.{Array, ScreenUtils}
import com.codingcrafters.gameobjects.BaseScreen
import com.cookbook.scene2d.{Level, LevelSelector, LevelSelectorStyle}

import java.time.{LocalDateTime, LocalTime}
import scala.math.*

/*
class SkinnedWardRobe(
                       override val xP: Float,
                       override val yP: Float,
                       override val s: Stage
                     ) extends BaseActor(xP, yP, s) : */
class SkinnedWardRobe extends BaseScreen :
  private val TAG = "Custom Widget"
  private val batch: SpriteBatch = new SpriteBatch()

  private val viewport: Viewport = new FitViewport(Gdx.graphics.getWidth.toFloat, Gdx.graphics.getHeight.toFloat)

  private val skin: Skin = new Skin(Gdx.files.internal("data/scene2d/customUI.json"))

  private val table: Table = new Table()
  private val level_menu = new Label("Level Selection Menu", skin)
  private val stage: Stage = new Stage(viewport, batch)
  Gdx.input.setInputProcessor(stage)

  val jungleTex: Texture = new Texture(Gdx.files.internal("data/jungle-level.png"))
  val mountainsTex: Texture = new Texture(Gdx.files.internal("data/blur/mountains.png"))
  val levels = new Array[Level](3)

  private val level1 = new Level("Level1", skin)
  level1.setImage(new Image(new TextureRegionDrawable(new TextureRegion(jungleTex))))

  private val level2 = new Level("Level2", skin)
  level2.setImage(new Image(new TextureRegionDrawable(new TextureRegion(mountainsTex))))

  private val level3 = new Level("Level3", skin)
  level3.setImage(new Image(new TextureRegionDrawable(new TextureRegion(jungleTex))))

  levels.addAll(level1, level2, level3)

  private val levelSelector: LevelSelector = LevelSelector(skin, 3)
  levelSelector.addLevels(levels)

  table.row
  table.add(level_menu).padBottom(20f)
  table.row
  table.add(levelSelector)

  table.setFillParent(true)
  table.pack()

  override def resize(width: Int, height: Int): Unit =
    viewport.update(width, height)

  override def dispose(): Unit =
    jungleTex.dispose()
    mountainsTex.dispose()
    batch.dispose()
    skin.dispose()
    stage.dispose()

  override def render(dt: Float): Unit =
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    stage.act(Math.min(Gdx.graphics.getDeltaTime, 1 / 60f))
    stage.draw


