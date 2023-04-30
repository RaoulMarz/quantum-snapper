package com.cookbook.scene2d

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Touchable}
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Label, Skin, Table, TextButton}
//import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Array

class LevelSelector(skin: Skin, numItems : Int) extends Table(skin) :
  private var style: LevelSelectorStyle = null
  private var buttonLeft: Image = new Image()
  private var buttonRight: Image = new Image()
  private var buttonGo: TextButton = null//new TextButton()
  private var currentLevelIndex = 0
  private var levels: Array[Level] = new Array[Level](numItems)
  private var imageWidth : Float = 400
  private var imageHeight : Float = 195

  //super(skin)
  setStyle(skin.get(classOf[LevelSelectorStyle]))
  initialize()
  setSize(getPrefWidth, getPrefHeight)

  def addLevel(level: Level): Unit =
    if (level != null && !levels.contains(level, false)) levels.add(level)
    update()

  def addLevels(arrayLevels: scala.Array[Level]): Unit =
    for (l <- arrayLevels)
      levels.add(l)
    update()

  def addLevels(arrayLevels: Array[Level]): Unit =
    for (i <- 0 until arrayLevels.size)
      levels.add(arrayLevels.get(i))
    update()

  def addLevels(levelsvar: Level*): Unit =
    for (level <- levelsvar)
      if (level != null && !levels.contains(level, false)) levels.add(level)
    update()

  private def initialize(): Unit =
    debug
    setTouchable(Touchable.enabled)
    levels = new Array[Level]
    buttonLeft.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        showPreviousLevel()
      }
    })
    buttonRight.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        showNextLevel()
      }
    })

  private def showPreviousLevel(): Unit =
    if (currentLevelIndex > 0)
      currentLevelIndex -= 1
      update()

  private def showNextLevel(): Unit =
    if (currentLevelIndex + 1 < levels.size)
      currentLevelIndex += 1
      update()

  private def update(): Unit =
    if (levels.size ne 0)
      clearChildren()
      val currentLevel = levels.get(currentLevelIndex)
      row()
      add(currentLevel.getTitle).colspan(3)
      row()
      add(buttonLeft).colspan(1).padRight(10f)
      add(currentLevel.getImage).colspan(1).size(imageWidth, imageHeight)
      add(buttonRight).colspan(1).padLeft(10f)
      row()
      add(buttonGo).colspan(3).padTop(10f).fillX
      row()
      pad(20f)
      pack()

  def setImageSize(width: Float, height: Float): Unit =
    this.imageWidth = width
    this.imageHeight = height

  def getButton: TextButton = buttonGo

  def getCurrentLevel: Int = currentLevelIndex + 1

  override def draw(batch: Batch, parentAlpha: Float): Unit =
    validate()
    super.draw(batch, parentAlpha)

  def getStyle: LevelSelectorStyle = style

  def setStyle(style: LevelSelectorStyle): Unit =
    if (style == null) throw new IllegalArgumentException("style cannot be null.")
    this.style = style
    this.buttonLeft = new Image(style.leftArrow)
    this.buttonRight = new Image(style.rightArrow)
    this.buttonGo = new TextButton("GO", style.textButtonStyle)
    setBackground(style.background)
    invalidateHierarchy()

  override def getPrefWidth: Float =
    var width = super.getPrefWidth
    if (style.background != null) width = Math.max(width, style.background.getMinWidth)
    width

  override def getPrefHeight: Float =
    var height = super.getPrefHeight
    if (style.background != null) height = Math.max(height, style.background.getMinHeight)
    height

  override def getMinWidth: Float = getPrefWidth

  override def getMinHeight: Float = getPrefHeight

