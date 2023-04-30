package com.particle_life.models

import org.joml.Vector3d;

object Range:
  def wrap(x: Vector3d): Unit =
    x.x = wrap(x.x)
    x.y = wrap(x.y)
    x.z = 0 // todo 3D


  def clamp(x: Vector3d): Unit =
    x.x = clamp(x.x)
    x.y = clamp(x.y)
    x.z = 0 // todo 3D


  private def wrap(value: Double) = modulo(value + 1, 2) - 1

  private def clamp(`val`: Double): Double =
    if (`val` < -1) return -1
    else if (`val` > 1) return 1
    `val`

  private def modulo(a: Double, b: Double): Double =
    var aa = a
    if (aa < 0)
      while (aa < 0)
      do (aa += b)
      return aa
    else if (aa >= b)
      while (aa >= b)
      do (aa -= b)
      return aa
    aa
