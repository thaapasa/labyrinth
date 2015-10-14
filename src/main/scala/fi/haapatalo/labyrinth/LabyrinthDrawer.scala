package fi.haapatalo.labyrinth

import Labyrinth.CoordinateExt
import javafx.scene.canvas.{ Canvas, GraphicsContext }
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap

/**
 * @author haapatu
 */
class LabyrinthDrawer(val labyrinth: Labyrinth) {

  import Labyrinth.CoordinateExt

  val width = labyrinth.width
  val height = labyrinth.height

  val roomSize = 1
  val wallSize = 1
  val doorSize = 1 * roomSize

  val imWidth = width * roomSize + (width + 1) * wallSize
  val imHeight = height * roomSize + (height + 1) * wallSize

  object Colors {
    val background = Color.BLACK
    val room = Color.WHITE
    val wall = Color.WHITE // new Color(1, 0.9, 0.9, 1.0)
  }

  @inline def imcx(x: Int): Double = imtx(x) + roomSize / 2d
  @inline def imcy(y: Int): Double = imty(y) + roomSize / 2d
  @inline def imtx(x: Int): Double = wallSize + x * (roomSize + wallSize)
  @inline def imty(y: Int): Double = wallSize + (height - y - 1) * (roomSize + wallSize)

  @inline def dot(x: Double, y: Double)(implicit g: GraphicsContext) = g.strokeLine(x, y, x, y)

  @inline def room(x: Int, y: Int)(implicit g: GraphicsContext) = dotRoom(x, y)
  @inline def circleRoom(x: Int, y: Int)(implicit g: GraphicsContext) =
    g.fillOval(imtx(x), imty(y), roomSize, roomSize)
  @inline def dotRoom(x: Int, y: Int)(implicit g: GraphicsContext) = dot(imcx(x), imcy(y))

  @inline def door(c1: Labyrinth.Coordinate, dir: Labyrinth.Direction)(implicit g: GraphicsContext) = {
    val c2 = c1.to(dir)
    /*
    val l = new Line(imcx(c1.x), imcy(c1.y), imcx(c2.x), imcy(c2.y))
    l.setStroke(Colors.wall)
    l.setStrokeWidth(doorSize)
    l.setBlendMode(BlendMode.SCREEN)
    l*/
    g.strokeLine(imcx(c1.x), imcy(c1.y), imcx(c2.x), imcy(c2.y))
  }

  def createRooms(implicit g: GraphicsContext) = {
    g.setFill(Colors.room)
    g.setStroke(Colors.wall)
    g.setLineWidth(roomSize)
    g.setLineCap(StrokeLineCap.SQUARE)
    for (x <- 0 until width; y <- 0 until height) room(x, y)
  }

  def createDoors(implicit g: GraphicsContext) = {
    g.setStroke(Colors.wall)
    g.setLineWidth(roomSize)
    g.setLineCap(StrokeLineCap.SQUARE)
    (for (x <- 0 until width; y <- 0 until height; dir <- Seq(Labyrinth.North, Labyrinth.East)) yield (x, y, dir)).
      filter(w => !labyrinth.hasWall(w._1, w._2, w._3)).map(w => door((w._1, w._2), w._3))
  }

  def draw(canvas: Canvas) {
    implicit val g = canvas.getGraphicsContext2D
    g.setFill(Colors.background)
    g.fillRect(0, 0, imWidth, imHeight)
    //createRooms
    createDoors
  }
}