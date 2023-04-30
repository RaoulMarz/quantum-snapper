package com.particle_life.models

object MatrixParser:

  /** Expects input to look like this:<br><br> <code> 0.1 0.2 -0.3<br>
    * -0.1 0.4 0.1<br>
    * 1.0 -1.0 0.0 </code>
    *
    * @return
    *   the parsed matrix, or null.
    */
  def parseMatrix(s: String): DefaultMatrix =
    val parts = s.split("\\s")
    val numbers = new Array[Float](parts.length)
    var pix = 0
    for (p <- parts)
      var f = .0f
      try f = p.toFloat
      catch
        case e: NumberFormatException =>
        // continue //todo: continue is not supported

      numbers.update(pix, f)
      pix += 1
    val matrixSize = Math.sqrt(numbers.size).toInt
    if (matrixSize < 1) return null
    val matrix = new DefaultMatrix(matrixSize)
    for (i <- 0 until matrixSize)
      for (j <- 0 until matrixSize)
        matrix.set(i, j, numbers(i * matrixSize + j))
    matrix

  def matrixToString(matrix: DefaultMatrix): String =
    matrixToString(matrix, new DoubleEncoderDefault())

  def matrixToStringRoundAndFormat(matrix: DefaultMatrix): String =
    matrixToString(matrix, new DoubleEncoderRoundAndFormat())

  private def matrixToString(
      matrix: DefaultMatrix,
      doubleEncoder: DoubleEncoder
  ) =
    val sb = new StringBuilder()
    var i = 0
    while (i < matrix.size())
      var j = 0
      while (j < matrix.size() - 1)
        sb.append(doubleEncoder.encode(matrix.get(i, j)))
        sb.append(" ")

        j += 1
      sb.append(doubleEncoder.encode(matrix.get(i, matrix.size() - 1)))
      sb.append("\n")

      i += 1
    sb.toString
