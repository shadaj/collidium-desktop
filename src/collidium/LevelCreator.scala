package collidium

import java.io._

object LevelCreator extends App {
  val level = new Level(1,(50, 50), 10, List(new Line(new Point(1,1), new Point(10, 10))), new Circle(new Point(50,50), 40), new Circle(new Point(25,25), 20), 0.0000000001)
  val out = new ObjectOutputStream(new FileOutputStream("level" + level.levelNum + ".txt"))
  out.writeObject(level)
  out.close()
}