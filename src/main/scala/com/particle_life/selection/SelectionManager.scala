package com.particle_life.selection

import com.particle_life.selection.InfoWrapper

import scala.collection.Iterable
import scala.collection.mutable.ArrayBuffer

class SelectionManager[T]:

  private var items: ArrayBuffer[InfoWrapper[T]] =
    new ArrayBuffer[InfoWrapper[T]]()
  private var activeIndex = 0

  def size(): Int = items.size

  def get(i: Int): InfoWrapper[T] = items(i)

  def getActiveIndex(): Int = activeIndex

  def setActive(i: Int): Unit =
    if (i < 0 || i >= items.size)
      throw new IllegalArgumentException(
        String.format(
          "selection index %d out of bounds (size is %d)",
          i,
          items.size
        )
      )
    if (i != activeIndex)
      activeIndex = i
      activeChanged()

  /** You can override this method.
    */
  protected def activeChanged(): Unit = {}

  def getActive: InfoWrapper[T] =
    return items(activeIndex)

  def add(item: InfoWrapper[T]): Unit =
    val wasEmpty: Boolean = size() eq 0
    // items.add(item)
    items += item
    if (wasEmpty)
      activeChanged()

  def addAll(items: Array[InfoWrapper[T]]): Unit =
    val wasEmpty = size() eq 0
    this.items = this.items ++ items
    //this.items.appendedAll(items)
    if (wasEmpty) activeChanged()
  /*
  def addAll(items: Iterable[InfoWrapper[T]]): Unit =
    val wasEmpty = size() eq 0
    this.items.appendedAll(items)
    if (wasEmpty) activeChanged()
  */

  def contains(item: InfoWrapper[T]): Boolean = items.contains(item)

  /** Returns whether there exists an item whose name is equal to the given
    * string.
    */
  def hasName(name: String): Boolean =
    return getIndexByName(name) ne -(1)

  def getIndexByName(name: String): Int =
    var i = 0
    for (item <- items)
      if (name == item.name) return i
      i += 1
    -1
