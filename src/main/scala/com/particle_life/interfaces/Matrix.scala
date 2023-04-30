package com.particle_life;

trait Matrix:
  def size(): Int
  def get(i: Int, j: Int): Double
  def set(i: Int, j: Int, value: Double): Unit
  def deepCopy(): Matrix
