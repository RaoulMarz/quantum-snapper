package com.particle_life.models

object ArrayUtils:
  //def swap(array: Array[AnyRef], i: Int, j: Int): Unit = {
  def swap[T](array: Array[T], i: Int, j: Int): Unit =
    val h = array(i)
    array(i) = array(j)
    array(j) = h

  /** Returns the first index <code>i</code>, where <code>a[i] < b[i]</code>, or
    * -1. Arrays must be of the same size.
    */
  def findFirstIndexWithLess(a: Array[Int], b: Array[Int]): Int =
    assert(a.length == b.length)
    for (i <- a.indices)
      if (a(i) < b(i)) return i
    -1
