package fi.haapatalo.labyrinth

import javafx.application.Application
import javafx.scene.{ Group, Scene }
import javafx.scene.canvas.Canvas
import javafx.stage.Stage

class LabyrinthUI extends Application {

  import Labyrinth.CoordinateExt

  val width = 950
  val height = 500

  val labyrinth = Labyrinth(width, height)

  override def start(stage: Stage) {
    val ld = new LabyrinthDrawer(labyrinth)
    val drawing = new Canvas(ld.imWidth, ld.imHeight)
    ld.draw(drawing)
    val root = new Group(drawing)
    val scene = new Scene(root, ld.imWidth, ld.imHeight)

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
