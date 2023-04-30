package com.particle_life.models

import com.particle_life.Accelerator
import org.joml.Vector3d

class DefaultAccelerator(
                          var accelerateFunc : (a: Double, pos: Vector3d) => Vector3d
                        ) extends Accelerator :

  def accelerate(a: Double, pos: Vector3d): Vector3d =
    //val accVec: Vector3d = new Vector3d()
    //accVec
    accelerateFunc(a, pos)

