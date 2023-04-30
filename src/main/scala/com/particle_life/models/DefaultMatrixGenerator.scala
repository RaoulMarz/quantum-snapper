package com.particle_life.models

import com.particle_life.Matrix
import com.particle_life.interfaces.MatrixGenerator

class DefaultMatrixGenerator(
                              var generateFunc : (size: Int) => DefaultMatrix
                            ) extends MatrixGenerator :
  def makeMatrix(size: Int): DefaultMatrix =
    //val m = new DefaultMatrix(size)
    //m.randomize()
    //m
    generateFunc(size)
