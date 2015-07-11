package fi.haapatalo.labyrinth

import scala.collection.mutable.BitSet
import scala.util.Random

class Labyrinth(val width: Int, val height: Int, walls: BitSet) {

  import Labyrinth.CoordinateExt

  val numWalls = width * height * 2

  def hasWall(x: Int, y: Int, dir: Labyrinth.Direction) = dir match {
    case Labyrinth.North if (x < 0 || x >= width || y < 0 || y >= (height - 1)) => true
    case Labyrinth.South if (x < 0 || x >= width || y < 1 || y >= height) => true
    case Labyrinth.East if (x < 0 || x >= (width - 1) || y < 0 || y >= height) => true
    case Labyrinth.West if (x < 1 || x >= width || y < 0 || y >= height) => true
    case _ => walls((x, y).wall(dir, width))
  }

}

object Labyrinth {

  type Coordinate = (Int, Int)

  implicit final class CoordinateExt(c: Coordinate) {
    @inline final def x = c._1
    @inline final def y = c._2
    @inline final def to(d: Direction): Coordinate = (x + d.dx, y + d.dy)
    @inline final def index(width: Int): Int = c._1 + c._2 * width
    @inline final def wall(dir: Direction, width: Int): Int = (index(width) * 2, dir) match {
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
    @inline def fromWall(wall: Int, width: Int) = (fromIndex(wall / 2, width), if (wall % 2 == 0) North else East)
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

  /** Creates a new random labyrinth of {@code width} x {@code height} dimensions. */
  def apply(width: Int, height: Int) = new Labyrinth(width, height, new Builder(width, height).walls)

  def createWalls(numWalls: Int): BitSet = {
    val ar = new Array[Long](numWalls / 64 + 1)
    (0 until ar.length).foreach(i => ar(i) = -1)
    BitSet.fromBitMask(ar)
  }

  private final class Builder(val width: Int, val height: Int) {
    val rand = new Random
    val numRooms = width * height
    val rooms = new Array[Int](numRooms)
    (0 until numRooms).foreach(rooms(_) = -1)

    // A bit too much, but so what
    val numWalls = numRooms * 2

    val wallOrder = (0 until numWalls).map(i => (i, rand.nextInt())).sortBy(_._2).map(_._1)
    val walls = createWalls(numWalls)
    (0 until numWalls).foreach(i => punctureWall(wallOrder(i)))

    // Punctures the i'th wall, if the rooms are not already connected
    private final def punctureWall(i: Int) = {
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
    
    @inline private final def inRange(c: Coordinate) = c.x >= 0 && c.x < width && c.y >= 0 && c.y < height

    @inline private final def findRoot(c: Int): Int =
      if (rooms(c) == -1) c
      else findRoot(rooms(c))

    @inline private final def shortenPath(c: Int, parent: Int): Unit = {
      val next = rooms(c)
      if (next != -1) {
        rooms(c) = parent
        shortenPath(next, parent)
      }
    }

  }

}
