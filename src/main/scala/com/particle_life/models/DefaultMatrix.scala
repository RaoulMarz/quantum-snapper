package com.particle_life.models

import com.particle_life.Matrix

class DefaultMatrix(matSize: Int) extends Matrix:
  // private var matSize : Int = 0
  // private var values: Array[Array[Double]] = null

  private val values = new Array[Array[Double]](matSize)
  for (i <- 0 until matSize)
    values(i) = new Array[Double](matSize)
  zero()

  def zero(): Unit =
    for (i <- 0 until matSize)
      for (j <- 0 until matSize)
        values(i)(j) = 0

  def randomize(): Unit =
    for (i <- 0 until matSize)
      for (j <- 0 until matSize)
        values(i)(j) = 2 * Math.random - 1

  def size(): Int = matSize

  def get(i: Int, j: Int): Double = values(i)(j)

  def set(i: Int, j: Int, value: Double): Unit =
    values(i)(j) = value

  def deepCopy(): DefaultMatrix =
    val copy = new DefaultMatrix(matSize)
    var i = 0
    while (i < matSize)
      var j = 0
      while (j < matSize)
        copy.values(i)(j) = values(i)(j)
        j += 1

      i += 1
    copy

