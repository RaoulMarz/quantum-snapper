package com.particle_life.models

import com.particle_life.PositionSetter
import org.joml.Vector3d

class DefaultPositionSetter(
                             var setFunc : (position: Vector3d, `type`: Int, nTypes: Int) => Unit
                           ) extends PositionSetter :

  def set(position: Vector3d, `type`: Int, nTypes: Int): Unit =
    //position.set(Math.random * 2 - 1, Math.random * 2 - 1, 0)
    setFunc(position, `type`, nTypes)
