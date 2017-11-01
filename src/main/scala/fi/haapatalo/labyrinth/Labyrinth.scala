package fi.haapatalo.labyrinth

import scala.collection.mutable
import scala.collection.mutable.BitSet
import scala.util.Random

class Labyrinth(val width: Int, val height: Int, walls: BitSet) {

  import Labyrinth.CoordinateExt

  val numWalls: Int = width * height * 2

  def hasWall(x: Int, y: Int, dir: Labyrinth.Direction): Boolean = dir match {
    case Labyrinth.North if x < 0 || x >= width || y < 0 || y >= (height - 1) => true
    case Labyrinth.South if x < 0 || x >= width || y < 1 || y >= height => true
    case Labyrinth.East if x < 0 || x >= (width - 1) || y < 0 || y >= height => true
    case Labyrinth.West if x < 1 || x >= width || y < 0 || y >= height => true
    case _ => walls((x, y).wall(dir, width))
  }

}

object Labyrinth {

  type Coordinate = (Int, Int)

  implicit final class CoordinateExt(c: Coordinate) {
    @inline def x: Int = c._1

    @inline def y: Int = c._2

    @inline def to(d: Direction): Coordinate = (x + d.dx, y + d.dy)

    @inline def index(width: Int): Int = c._1 + c._2 * width

    @inline def wall(dir: Direction, width: Int): Int = (index(width) * 2, dir) match {
      case (x, North) => x
      case (x, East) => x + 1
      case (x, South) => to(South).wall(North, width)
      case (y, West) => to(West).wall(East, width)
    }
  }

  object Coordinate {
    @inline def fromIndex(index: Int, width: Int): Coordinate = {
      val y = index / width
      (index - y * width, y)
    }

    @inline def fromWall(wall: Int, width: Int): ((Int, Int), Direction with Product with Serializable) = (fromIndex(wall / 2, width), if (wall % 2 == 0) North else East)
  }

  sealed abstract class Direction(val dx: Int, val dy: Int, val wx: Int, val wy: Int) {
    @inline final def wallIndex(x: Int, y: Int): Coordinate = (x + wx, y * 2 + wy)

    @inline final def wallIndex(c: Coordinate): Coordinate = (c.x + wx, c.y * 2 + wy)
  }

  /** North is +y (up) */
  case object North extends Direction(0, 1, 0, 1)

  /** South is +y (down) */
  case object South extends Direction(0, -1, 0, -1)

  /** East is +x (left) */
  case object East extends Direction(1, 0, 0, 0)

  /** West is -x (right) */
  case object West extends Direction(-1, 0, -1, 0)

  /** Creates a new random labyrinth of width x height dimensions. */
  def apply(width: Int, height: Int) = new Labyrinth(width, height, new Builder(width, height).walls)

  def createWalls(numWalls: Int): mutable.BitSet = {
    val ar = new Array[Long](numWalls / 64 + 1)
    ar.indices.foreach(i => ar(i) = -1)
    mutable.BitSet.fromBitMask(ar)
  }

  private final class Builder(val width: Int, val height: Int) {
    val rand = new Random
    val numRooms: Int = width * height
    val rooms = new Array[Int](numRooms)
    (0 until numRooms).foreach(rooms(_) = -1)

    // A bit too much, but so what
    val numWalls: Int = numRooms * 2
    val walls: mutable.BitSet = createWalls(numWalls)

    val wallOrder: Array[Int] = (0 until numWalls).toArray
    shuffle(wallOrder)
    (0 until numWalls).foreach(i => punctureWall(wallOrder(i)))

    // Punctures the i'th wall, if the rooms are not already connected
    private def punctureWall(i: Int): Unit = {
      val (r1, dir) = Coordinate.fromWall(i, width)
      val r2 = r1.to(dir)

      if (inRange(r1) && inRange(r2)) {
        // Find rooms that the wall connects
        val r1i = r1.index(width)
        val r2i = r2.index(width)
        // Find roots of each room tree
        val r1Parent = findRoot(r1i)
        val r2Parent = findRoot(r2i)
        // If rooms are in different trees, then break the wall & connect room trees
        if (r1Parent != r2Parent) {
          walls(i) = false
          rooms(r2Parent) = r1Parent
        }
        // Shorten root paths (moves all room tree leafs on search path directly under the root)
        shortenPath(r1i, r1Parent)
        shortenPath(r2i, r1Parent)
      }
    }

    def shuffle(l: Array[Int]): Unit = {
      @inline def swap(a: Array[Int], i: Int, j: Int): Unit = {
        val s = a(i)
        a(i) = a(j)
        a(j) = s
      }

      // Fisher-Yates shuffle
      val size = l.length
      for (i <- (1 until size).reverse) swap(l, i, rand.nextInt(i + 1))
    }

    @inline private def inRange(c: Coordinate) = c.x >= 0 && c.x < width && c.y >= 0 && c.y < height

    @inline private def findRoot(c: Int): Int =
      if (rooms(c) == -1) c
      else findRoot(rooms(c))

    @inline private def shortenPath(c: Int, parent: Int): Unit = {
      val next = rooms(c)
      if (next != -1) {
        rooms(c) = parent
        shortenPath(next, parent)
      }
    }

  }

}
