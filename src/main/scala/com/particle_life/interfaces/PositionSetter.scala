package com.particle_life;

import org.joml.Vector3d;

trait PositionSetter:
  def set(position: Vector3d, `type`: Int, nTypes: Int): Unit
