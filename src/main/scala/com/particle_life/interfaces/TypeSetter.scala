package com.particle_life.interfaces

import org.joml.Vector3d

trait TypeSetter:
  def getType(
      position: Vector3d,
      velocity: Vector3d,
      `type`: Int,
      nTypes: Int
  ): Int
