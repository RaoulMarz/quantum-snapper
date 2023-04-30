package com.particle_life.interfaces

trait LoopCallback:
  def call(dt: Double): Unit
