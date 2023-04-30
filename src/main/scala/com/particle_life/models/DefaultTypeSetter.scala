package com.particle_life.models

import com.particle_life.interfaces.TypeSetter
import org.joml.Vector3d

class DefaultTypeSetter(
                            getFunc : (position: Vector3d, velocity: Vector3d, `type`: Int, nTypes: Int) => Int
                          ) extends TypeSetter :
  def getType(
      position: Vector3d,
      velocity: Vector3d,
      `type`: Int,
      nTypes: Int
  ): Int = getFunc(position, velocity,`type`, nTypes) //Math.floor(Math.random * nTypes).toInt

