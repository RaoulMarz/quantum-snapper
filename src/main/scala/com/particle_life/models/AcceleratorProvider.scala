package com.particle_life.models

import com.particle_life.selection.{InfoWrapper, InfoWrapperProvider}
import org.joml.Vector3d

class AcceleratorProvider extends InfoWrapperProvider[DefaultAccelerator] :

  //private def createDefaultAccelerators : Array[InfoWrapper[DefaultAccelerator]] = Array(
  override def create : Array[InfoWrapper[DefaultAccelerator]] = Array(
    new InfoWrapper[DefaultAccelerator]("particle life", new DefaultAccelerator((a: Double, pos: Vector3d) => {
    val rmin = 0.3
    val dist = pos.length
    val force = if (dist < rmin) dist / rmin - 1
    else a * (1 - Math.abs(1 + rmin - 2 * dist) / (1 - rmin))
    pos.mul(force / dist)

  })),
    new InfoWrapper[DefaultAccelerator]("particle life / r", new DefaultAccelerator((a: Double, pos: Vector3d) => {
    val rmin = 0.3
    val dist = pos.length
    val force = if (dist < rmin) dist / rmin - 1
    else a * (1 - Math.abs(1 + rmin - 2 * dist) / (1 - rmin))
    pos.mul(force / (dist * dist))

  })),
    new InfoWrapper[DefaultAccelerator]("particle life / r^2", new DefaultAccelerator((a: Double, pos: Vector3d) => {
    val rmin = 0.3
    val dist = pos.length
    val force = if (dist < rmin) dist / rmin - 1
    else a * (1 - Math.abs(1 + rmin - 2 * dist) / (1 - rmin))
    pos.mul(force / (dist * dist * dist))

  })), new InfoWrapper[DefaultAccelerator]("rotator 90deg", new DefaultAccelerator((a: Double, pos: Vector3d) => {
    val dist = pos.length
    val force = a * (1 - dist)
    val delta = new Vector3d(-pos.y, pos.x, 0)
    delta.mul(force / dist)

  })), new InfoWrapper[DefaultAccelerator]("rotator attr", new DefaultAccelerator((a: Double, pos: Vector3d) => {
    val dist = pos.length
    val force = 1 - dist
    val angle = -a * Math.PI
    val delta = new Vector3d(Math.cos(angle) * pos.x + Math.sin(angle) * pos.y, -Math.sin(angle) * pos.x + Math.cos(angle) * pos.y, 0)
    delta.mul(force / dist)

  })), new InfoWrapper[DefaultAccelerator]("planets", "works best without friction (value = 1.0)", new DefaultAccelerator((a: Double, pos: Vector3d) => {
    val delta = new Vector3d(pos)
    var r = delta.length
    r = Math.max(r, 0.01)
    delta.mul(0.01 / (r * r * r))

  })))