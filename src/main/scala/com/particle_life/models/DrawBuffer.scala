package com.particle_life.models

class DrawBuffer(var particlePositions: scala.Array[Double] = null,
                 var particleVelocities: scala.Array[Double] = null,
                 var particleTypes: scala.Array[Int] = null) :

  def getPositions: scala.Array[Double] = particlePositions

  def getVelocities: scala.Array[Double] = particleVelocities

  def getTypes: scala.Array[Int] = particleTypes

  def bufferParticleData(x: Array[Double], v: Array[Double], types: Array[Int]): Unit = {
    particlePositions = x
    particleVelocities = v
    particleTypes = types
  }