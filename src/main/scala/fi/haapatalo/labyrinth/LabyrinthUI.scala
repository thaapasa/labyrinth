package fi.haapatalo.labyrinth

import java.io.File
import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.Canvas
import javafx.scene.{Group, Scene}
import javafx.stage.Stage
import javax.imageio.ImageIO

class LabyrinthUI extends Application {

  val resolutions = Map(
    "HD" -> (1920, 1080),
    "WUXGA" -> (1920, 1200),
    "WQHD" -> (2560, 1440),
    "4K" -> (3840, 2160)
  )

  val desiredSize = resolutions("HD")

  val width: Int = desiredSize._1 / 2 - 1
  val height: Int = desiredSize._2 / 2 - 1

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
