package com.particle_life.models

import com.particle_life.PositionSetter
import com.particle_life.selection.{InfoWrapper, InfoWrapperProvider}
import org.joml.Vector3d

import scala.util.Random

class PositionSetterProvider extends InfoWrapperProvider[DefaultPositionSetter] :

  private val random = new Random()

  override def create: Array[InfoWrapper[DefaultPositionSetter]] = Array(
    new InfoWrapper[DefaultPositionSetter]("uniform", new DefaultPositionSetter((position: Vector3d, `type`: Int, nTypes: Int) => {
      position.set(Math.random * 2 - 1, Math.random * 2 - 1, 0)
    })),
    new InfoWrapper[DefaultPositionSetter]("uniform circle", new DefaultPositionSetter((position: Vector3d, `type`: Int, nTypes: Int) => {
    val angle = Math.random * 2 * Math.PI
    val radius = Math.sqrt(Math.random)
    position.x = Math.cos(angle) * radius
    position.y = Math.sin(angle) * radius

  })),
    new InfoWrapper[DefaultPositionSetter]("centered", new DefaultPositionSetter((position: Vector3d, `type`: Int, nTypes: Int) => {
    position.x = random.nextGaussian * 0.3f
    position.y = random.nextGaussian * 0.3f

  })),
    new InfoWrapper[DefaultPositionSetter]("centered circle", new DefaultPositionSetter((position: Vector3d, `type`: Int, nTypes: Int) => {
    val angle = Math.random * 2 * Math.PI
    val radius = Math.random
    position.x = Math.cos(angle) * radius
    position.y = Math.sin(angle) * radius

  })),
    new InfoWrapper[DefaultPositionSetter]("ring", new DefaultPositionSetter((position: Vector3d, `type`: Int, nTypes: Int) => {
    val angle = Math.random * 2 * Math.PI
    val radius = 0.9 + 0.02 * random.nextGaussian
    position.x = Math.cos(angle) * radius
    position.y = Math.sin(angle) * radius

  })),
    new InfoWrapper[DefaultPositionSetter]("type battle", new DefaultPositionSetter((position: Vector3d, `type`: Int, nTypes: Int) => {
    val centerAngle = `type` / nTypes.asInstanceOf[Double] * 2 * Math.PI
    val centerRadius = 0.5f
    val angle = Math.random * 2 * Math.PI
    val radius = Math.random * 0.1f
    position.x = centerRadius * Math.cos(centerAngle) + Math.cos(angle) * radius
    position.y = centerRadius * Math.sin(centerAngle) + Math.sin(angle) * radius

  })),
    new InfoWrapper[DefaultPositionSetter]("type wheel", new DefaultPositionSetter((position: Vector3d, `type`: Int, nTypes: Int) => {
    val centerAngle = `type` / nTypes.asInstanceOf[Double] * 2 * Math.PI
    val centerRadius = 0.3f
    val individualRadius = 0.2f
    position.x = centerRadius * Math.cos(centerAngle) + random.nextGaussian * individualRadius
    position.y = centerRadius * Math.sin(centerAngle) + random.nextGaussian * individualRadius

  })),
    new InfoWrapper[DefaultPositionSetter]("line", new DefaultPositionSetter((position: Vector3d, `type`: Int, nTypes: Int) => {
    position.x = 2 * random.nextDouble - 1
    position.y = (2 * random.nextDouble - 1) * 0.15f

  })),
    new InfoWrapper[DefaultPositionSetter]("spiral", new DefaultPositionSetter((position: Vector3d, `type`: Int, nTypes: Int) => {
    val maxRotations = 2
    val f = random.nextDouble
    val angle = maxRotations * 2 * Math.PI * f
    val spread = 0.5 * Math.min(f, 0.2)
    val radius = 0.9 * f + spread * random.nextGaussian * spread
    position.x = radius * Math.cos(angle)
    position.y = radius * Math.sin(angle)

  })),
    new InfoWrapper[DefaultPositionSetter]("rainbow spiral", new DefaultPositionSetter((position: Vector3d, `type`: Int, nTypes: Int) => {
    val maxRotations = 2
    val typeSpread = 0.3 / nTypes
    var f = (`type` + 1) / (nTypes + 2).toDouble + typeSpread * random.nextGaussian
    if (f < 0) f = 0
    else if (f > 1) f = 1
    val angle = maxRotations * 2 * Math.PI * f
    val spread = 0.5 * Math.min(f, 0.2)
    val radius = 0.9 * f + spread * random.nextGaussian * spread
    position.x = radius * Math.cos(angle)
    position.y = radius * Math.sin(angle)

  }))
  )