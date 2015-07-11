package fi.haapatalo.labyrinth

class LabyrinthTest {

}

object LabyrinthTest {
  
  def output(l: Labyrinth) {
    def northWalls(y: Int) = { (0 until l.width).map(x => if (l.hasWall(x, y, Labyrinth.North)) "----" else "    ").mkString("+")  }
    def eastWalls(y: Int) = { (-1 until l.width).map(x => if (l.hasWall(x, y, Labyrinth.East)) "|" else " ").mkString("    ")  }
    
    ((l.height - 1) to (0, -1)).foreach { y =>
      println(f"+${northWalls(y)}+")
      println(f"${eastWalls(y)}")
    }
    println(f"+${northWalls(-1)}+")
    
  }
    
  def main(args: Array[String]): Unit = {
    val l = Labyrinth(20, 10)
    output(l)  
  }
  
}
