package fi.haapatalo.labyrinth

import scala.collection.mutable.BitSet
import scala.util.Random

class Labyrinth(val width: Int, val height: Int, walls: BitSet) {

  val numWalls = width * height * 2

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

  def apply(width: Int, height: Int) = new Labyrinth(width, height, new Builder(width, height).walls)
  
  def createWalls(numWalls: Int): BitSet = BitSet.fromBitMask(new Array[Long](numWalls / 64 + 1))
  
  private class Builder(val width: Int, val height: Int) {
    val rand = new Random
    val numRooms = width * height
    val rooms = new Array[Int](numRooms)
    (1 to numRooms).foreach(rooms(_) = -1)

    // A bit too much, but so what
    val numWalls = numRooms * 2
    
    val wallOrder = (1 to numWalls).map(i => (i, rand.nextFloat())).sortBy(_._2)
    val walls = createWalls(numWalls)
    (1 to numWalls).foreach(i => punctureWall(wallOrder(i)._1))
        
    // Punctures the i'th wall, if the rooms are not already connected
    private def punctureWall(i: Int) = {
      
    }
  }
  
}

/*
 
 North/south walls have odd y-indices, east/west walls have even y-indices.
 
 
 +- x,2y+1 -+
 |   x,y    |
 x-1,2y    x,2y
 -- x,2y-1 -+
 
 
 + 0,5 +-1,5-+-2,5-+
 |    0,4   1,4  2,4
 | 0,2 | 1,2 | 2,2 
 + 0,3 +-1,3-+-2,3-+
 |    0,2   1,2  2,2
 | 0,1 | 1,1 | 2,1 
 + 0,1 +-1,1-+-2,1-+
 |    0,0   1,0   2,0
 | 0,0 | 1,0   2,0
 +-----+-----+-----+

*/
