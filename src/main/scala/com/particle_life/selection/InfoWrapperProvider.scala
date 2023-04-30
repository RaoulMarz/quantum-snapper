package com.particle_life.selection

trait InfoWrapperProvider[T]:
  def create: Array[InfoWrapper[T]]
