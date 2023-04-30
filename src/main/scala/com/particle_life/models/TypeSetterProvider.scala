package com.particle_life.models

import com.particle_life.interfaces.TypeSetter
import com.particle_life.selection.{InfoWrapper, InfoWrapperProvider}
import org.joml.Vector3d

class TypeSetterProvider extends InfoWrapperProvider[DefaultTypeSetter] :

  override def create: Array[InfoWrapper[DefaultTypeSetter]] = Array(
    new InfoWrapper[DefaultTypeSetter]("fully random", new DefaultTypeSetter((position: Vector3d, velocity: Vector3d, `type`: Int, nTypes: Int) => {Math.floor(Math.random * nTypes).toInt})),
    new InfoWrapper[DefaultTypeSetter]("randomize 10%", new DefaultTypeSetter((position: Vector3d, velocity: Vector3d, `type`: Int, nTypes: Int) =>
      if (Math.random < 0.1)
        mapType(Math.random, nTypes)
      else `type`)),
    new InfoWrapper[DefaultTypeSetter]("slices", new DefaultTypeSetter((position: Vector3d, velocity: Vector3d, `type`: Int, nTypes: Int) =>
      mapType(0.5 * position.x + 0.5, nTypes))),
    new InfoWrapper[DefaultTypeSetter]("onion", new DefaultTypeSetter((position: Vector3d, velocity: Vector3d, `type`: Int, nTypes: Int) =>
      mapType(position.length, nTypes))),
    new InfoWrapper[DefaultTypeSetter]("rotate", new DefaultTypeSetter((position: Vector3d, velocity: Vector3d, `type`: Int, nTypes: Int) =>
      (`type` + 1) % nTypes)),
    new InfoWrapper[DefaultTypeSetter]("flip", new DefaultTypeSetter((position: Vector3d, velocity: Vector3d, `type`: Int, nTypes: Int) =>
      nTypes - 1 - `type`)),
    new InfoWrapper[DefaultTypeSetter]("more of first", new DefaultTypeSetter((position: Vector3d, velocity: Vector3d, `type`: Int, nTypes: Int) =>
      mapType(Math.random * Math.random, nTypes))),
    new InfoWrapper[DefaultTypeSetter]("kill still", new DefaultTypeSetter((position: Vector3d, velocity: Vector3d, `type`: Int, nTypes: Int) =>
      if (velocity.length < 0.01)
        nTypes - 1
      else `type`))
  )

  private def constrain(value: Int, nTypes: Int) = Math.max(0, Math.min(nTypes - 1, value))

  private def mapType(value: Double, nTypes: Int) = constrain(Math.floor(value * nTypes).toInt, nTypes)