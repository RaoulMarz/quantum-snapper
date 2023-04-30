package com.particle_life.selection

class InfoWrapper[T](
    val name: String,
    val description: String,
    var `object`: T
):

  def this(name: String, `object`: T) =
    this(name, "", `object`)
