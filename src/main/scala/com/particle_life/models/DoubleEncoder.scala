package com.particle_life.models

import java.util.Locale

trait NumberEncoder:
  def encode(f: Double): String

class DoubleEncoder extends NumberEncoder:
  def encode(f: Double): String =
    ""

class DoubleEncoderDefault extends DoubleEncoder:
  override def encode(f: Double): String =
    String.format(Locale.US, "%f", f)

class DoubleEncoderRoundAndFormat extends DoubleEncoder:
  override def encode(f: Double): String =
    String.format(Locale.US, "%4.1f", f)
