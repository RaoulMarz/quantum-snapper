package com.particle_life.models

import com.particle_life.interfaces.MatrixGenerator
import com.particle_life.selection.{InfoWrapper, InfoWrapperProvider}

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class MatrixGeneratorProvider extends InfoWrapperProvider[DefaultMatrixGenerator] :

  //private val logger: Logger = LogManager.getLogger()
  //MessageFormat formatter = new MessageFormat("[MatrixGeneratorProvider], type={0}, size={1}");

  override def create: Array[InfoWrapper[DefaultMatrixGenerator]] = Array(
    new InfoWrapper[DefaultMatrixGenerator]("fully random", new DefaultMatrixGenerator((size: Int) => {
      val m: DefaultMatrix = new DefaultMatrix(size)
      m.randomize()
      m
    } )),
      new InfoWrapper[DefaultMatrixGenerator]("symmetry", new DefaultMatrixGenerator((size: Int) => {

      val result: String = String.format("MatrixGeneratorProvider, type=%1$s, size=%2$s", "symmetry", size)
      //logger.info(result)
      val m: DefaultMatrix = new DefaultMatrix(size)
      m.randomize()
      var i: Int = 0
      while (i < m.size()) {
        var j: Int = i
        while (j < m.size()) {
          m.set(i, j, m.get(j, i))
          j += 1
        }
        i += 1
      }
      m

    })),
      new InfoWrapper[DefaultMatrixGenerator]("chains", new DefaultMatrixGenerator((size: Int) => {
      val result: String = String.format("MatrixGeneratorProvider, type=%1$s, size=%2$s", "chains", size)
      //logger.info(result)
      val m: DefaultMatrix = new DefaultMatrix(size)
      var i: Int = 0
      while (i < size) {
        var j: Int = 0
        while (j < size) {
          if (j == i || j == (i + 1) % size || j == (i + size - 1) % size) {
            m.set(i, j, 1)
          }
          else {
            m.set(i, j, -(1))
          }

          j += 1
        }

        i += 1
      }
      m

    })),
        new InfoWrapper[DefaultMatrixGenerator]("chains 2", new DefaultMatrixGenerator((size: Int) => {
      val result: String = String.format("MatrixGeneratorProvider, type=%1$s, size=%2$s", "chains 2", size)
      //logger.info(result)
      val m: DefaultMatrix = new DefaultMatrix(size)
      var i: Int = 0
      while (i < size) {
        var j: Int = 0
        while (j < size) {
          if (j == i) {
            m.set(i, j, 1)
          }
          else {
            if (j == (i + 1) % size || j == (i + size - 1) % size) {
              m.set(i, j, 0.2)
            }
            else {
              m.set(i, j, -(1))
            }
          }

          j += 1
        }

        i += 1
      }
      m

    })),
      new InfoWrapper[DefaultMatrixGenerator]("chains 3", new DefaultMatrixGenerator((size: Int)  => {
      val result: String = String.format("MatrixGeneratorProvider, type=%1$s, size=%2$s", "chains 3", size)
      //logger.info(result)
      val m: DefaultMatrix = new DefaultMatrix(size)
      var i: Int = 0
      while (i < size) {
        var j: Int = 0
        while (j < size) {
          if (j == i) {
            m.set(i, j, 1)
          }
          else {
            if (j == (i + 1) % size || j == (i + size - 1) % size) {
              m.set(i, j, 0.2)
            }
            else {
              m.set(i, j, 0)
            }
          }

          j += 1
        }

        i += 1
      }
      m

    })),
      new InfoWrapper[DefaultMatrixGenerator]("snakes", new DefaultMatrixGenerator((size: Int) => {
      val result: String = String.format("MatrixGeneratorProvider, type=%1$s, size=%2$s", "snakes", size)
      //logger.info(result)
      val m: DefaultMatrix = new DefaultMatrix(size)
      var i: Int = 0
      while (i < size) {
        m.set(i, i, 1)
        m.set(i, (i + 1) % m.size(), 0.2)

        i += 1
      }
      m

    })),
      new InfoWrapper[DefaultMatrixGenerator]("zero", new DefaultMatrixGenerator((size: Int)  => {
        val m = new DefaultMatrix(size)
        m
      }))
  )