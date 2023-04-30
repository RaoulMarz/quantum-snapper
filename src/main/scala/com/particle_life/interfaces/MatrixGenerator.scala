package com.particle_life.interfaces

import com.particle_life.Matrix

trait MatrixGenerator:
  def makeMatrix(size: Int): Matrix
