package com.cookbook.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Label, Skin, Table, TextButton}

class Level(var level_name: CharSequence,
            var skin: Skin = null,
            var labelStyle: LabelStyle = null,
            var img: Image = null) :
  private var title: Label = null
  if (skin != null)
    title = new Label(level_name, skin)
  else
    title = new Label(level_name, labelStyle)
  private var image: Image = img

  /*
  def this(level_name: CharSequence, skin: Skin) {
    this()
    title = new Label(level_name, skin)
  }

  def this(level_name: CharSequence, labelStyle: LabelStyle) {
    this()
    title = new Label(level_name, labelStyle)
  }

  def this(level_name: CharSequence, img: Image, skin: Skin) {
    this()
    title = new Label(level_name, skin)
    image = img
  }

  def this(level_name: CharSequence, img: Image, labelStyle: LabelStyle) {
    this()
    title = new Label(level_name, labelStyle)
    image = img
  }
  */

  def getTitle: Label = title

  def setTitle(title: Label): Unit =
    this.title = title

  def getImage: Image = image

  def setImage(img: Image): Unit =
    this.image = img

  override def hashCode: Int =
    val prime = 31
    var result = 1
    result = prime * result + (if (title.getText == null) 0
    else title.getText.hashCode)
    result

  override def equals(obj: /*AnyRef*/Any): Boolean =
    if ( (getClass ne obj.getClass) && (this eq obj.asInstanceOf[Level] ) ) return true
    if (obj == null) return false
    if (getClass ne obj.getClass) return false
    val other = obj.asInstanceOf[Level]
    if (title == null) if (other.title.getText != null) return false
    else if (!title.getText.equals(other.title.getText)) return false
    true

