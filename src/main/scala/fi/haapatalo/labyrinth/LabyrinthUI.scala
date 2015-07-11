package fi.haapatalo.labyrinth

import Labyrinth.CoordinateExt
import javafx.application.Application
import javafx.scene.{ Group, Scene }
import javafx.scene.effect.BlendMode
import javafx.scene.paint.Color
import javafx.scene.shape.{ Circle, Line, Rectangle, Shape }
import javafx.stage.Stage

class LabyrinthUI extends Application {

  import Labyrinth.CoordinateExt

  val width = 30
  val height = 25
  val roomSize = 30
  val wallSize = 5
  val doorSize = 0.7d * roomSize
  val imWidth = width * roomSize + (width + 1) * wallSize
  val imHeight = height * roomSize + (height + 1) * wallSize

  object Colors {
    val background = Color.BLACK
    val room = Color.WHITE
    val wall = new Color(1, 0.9, 0.9, 1.0)
  }

  val labyrinth = Labyrinth(width, height)

  @inline def imx(x: Int): Double = wallSize + x * (roomSize + wallSize) + roomSize / 2d
  @inline def imy(y: Int): Double = wallSize + (height - y - 1) * (roomSize + wallSize) + roomSize / 2d

  @inline def room(x: Int, y: Int) = circleRoom(x, y)
  @inline def circleRoom(x: Int, y: Int): Circle = {
    val r = new Circle(imx(x), imy(y), roomSize.toDouble / 2d, Colors.room)
    r
  }

  @inline def door(c1: Labyrinth.Coordinate, dir: Labyrinth.Direction): Shape = {
    val c2 = c1.to(dir)
    val l = new Line(imx(c1.x), imy(c1.y), imx(c2.x), imy(c2.y))
    l.setStroke(Colors.wall)
    l.setStrokeWidth(doorSize)
    l.setBlendMode(BlendMode.SCREEN)
    l
  }

  def createRooms = for (x <- 0 until width; y <- 0 until height) yield room(x, y)
  def createDoors = (for (x <- 0 until width; y <- 0 until height; dir <- Seq(Labyrinth.North, Labyrinth.East)) yield (x, y, dir)).
    filter(w => !labyrinth.hasWall(w._1, w._2, w._3)).map(w => door((w._1, w._2), w._3))

  override def start(stage: Stage) {
    val background = new Rectangle(imWidth, imHeight, Colors.background)
    val rooms = new Group(createRooms: _*)
    val doors = new Group(createDoors: _*)
    val root = new Group(background, rooms, doors)
    val scene = new Scene(root, imWidth, imHeight)

    stage.setTitle("Labyrinth")
    stage.setScene(scene)
    stage.show()
  }

}

object LabyrinthUI {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[LabyrinthUI], args: _*)
  }
}
