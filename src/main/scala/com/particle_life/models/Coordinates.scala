package com.particle_life.models

import org.joml.Matrix4d
import org.joml.Vector2d
import org.joml.Vector3d

class Coordinates:
  val width: Double = .0
  val height: Double = .0
  val shift: Vector3d = null
  var zoom = .0

  /** Maps x: [-1, 1] -> [0, width], y: [-1, 1] -> [0, height]
    */
  private def map(vector: Vector2d) =
    vector.add(1.0, 1.0).div(2.0).mul(width, height)

  /** Scales square to fit smaller dimension (will be scaled along larger
    * dimension of screen)
    */
  private def quad(vector: Vector2d) =
    if (width >= height) vector.mul(height / width.asInstanceOf[Double], 1)
    else vector.mul(1, width / height.asInstanceOf[Double])
    vector

  /** screen(x) = map(quad(zoom(shift(x))))
    * \= map(quad(zoom * (x + shift)))
    */
  def screen(vector: Vector3d): Vector2d =
    vector.add(shift).mul(zoom)
    map(quad(new Vector2d(vector.x, vector.y)))

  def world(screenX: Double, screenY: Double): Vector3d =
    val screenTopLeft = screen(new Vector3d(-1, -1, 0))
    val screenBottomRight = screen(new Vector3d(1, 1, 0))
    new Vector3d(
      new Vector2d(screenX, screenY)
        .sub(screenTopLeft)
        .div(screenBottomRight.sub(screenTopLeft))
        .mul(2.0)
        .sub(1.0, 1.0),
      0
    )

  def apply(transform: Matrix4d): Unit =
    transform.scale(1, -1, 1) // flip y

    // quad(x)
    if (width >= height) transform.scale(height / width, 1, 1) // fit width
    else transform.scale(1, width / height, 1) // fit height
    transform.scale(zoom)
    transform.translate(shift)

  def mouseShift(mouseBefore: Vector2d, mouseAfter: Vector2d): Coordinates =
    val w1 = world(mouseBefore.x, mouseBefore.y)
    val w2 = world(mouseAfter.x, mouseAfter.y)
    shift.add(w2).sub(w1)
    this

  def zoomInOnMouse(mouse: Vector2d, zoomFactor: Double): Coordinates =
    val w = world(mouse.x, mouse.y)
    zoom = zoom * zoomFactor
    shift.set(new Vector3d(w).add(shift).div(zoomFactor).sub(w))
    this
