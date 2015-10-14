package fi.haapatalo.labyrinth

import java.io.File

import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.{ Group, Scene }
import javafx.scene.canvas.Canvas
import javafx.stage.Stage
import javax.imageio.ImageIO

class LabyrinthUI extends Application {

  import Labyrinth.CoordinateExt

  val width = 950
  val height = 500

  val labyrinth = Labyrinth(width, height)

  val saveImage = true

  override def start(stage: Stage) {
    val ld = new LabyrinthDrawer(labyrinth)
    val drawing = new Canvas(ld.imWidth, ld.imHeight)
    ld.draw(drawing)
    val root = new Group(drawing)
    val scene = new Scene(root, ld.imWidth, ld.imHeight)

    stage.setTitle("Labyrinth")
    stage.setScene(scene)
    stage.show()

    if (saveImage) {
      val image = drawing.snapshot(null, null)
      val bImage = SwingFXUtils.fromFXImage(image, null)
      ImageIO.write(bImage, "PNG", new File("output.png"));
    }
  }

}

object LabyrinthUI {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[LabyrinthUI], args: _*)
  }
}
