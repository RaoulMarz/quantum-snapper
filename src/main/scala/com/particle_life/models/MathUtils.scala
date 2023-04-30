package com.particle_life.models

object MathUtils:
  def constrain(value: Int, min: Int, max: Int): Int = if (value < min) min
  else if (value > max) max
  else value

  def constrain(value: Double, min: Double, max: Double): Double = if (
    value < min
  ) min
  else if (value > max) max
  else value

  def modulo(a: Int, b: Int): Int =
    var aa = a
    if (aa < 0)
      while (aa < 0)
      do (aa += b)
      return aa
    else if (aa >= b)
      while (aa >= b)
      do (aa -= b)
      return aa
    a

  def lerp(a: Double, b: Double, f: Double): Double = a + (b - a) * f

  /** Returns <code>Math.round(value)</code> instead of
    * <code>Math.floor(value)</code> if <code>value</code> is closer to the next
    * integer than <code>threshold</code>.
    *
    * @param value
    * @param threshold
    *   some positive value like 0.001
    * @return
    *   an integer
    */
  def tolerantFloor(value: Double, threshold: Double): Double =
    val x = value.round
    if (Math.abs(x - value) < threshold) return x
    Math.floor(value)

  /** See comment on {@link # tolerantFloor ( double, double)}.
    *
    * @param value
    * @param threshold
    * @return
    */
  def tolerantCeil(value: Double, threshold: Double): Double =
    val x = value.round
    if (Math.abs(x - value) < threshold) return x
    Math.ceil(value)
