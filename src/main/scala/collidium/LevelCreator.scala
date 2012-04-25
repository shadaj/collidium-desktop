package collidium

import java.io._

object LevelCreator extends App {
  val level = new Level(1,
      (400, 400),
      10,
      List(new Line(new Point(10,10), new Point(490, 10)),
          new Line(new Point(490,10), new Point(490, 490)),
          new Line(new Point(490,490), new Point(10, 490)),
          new Line(new Point(10,490), new Point(10, 10))),
      new Circle(new Point(50,50), 20),
      new Circle(new Point(100,100), 40), 0)
  val out = new ObjectOutputStream(new FileOutputStream("level" + level.levelNum + ".collidiumLevel"))
  out.writeObject(level)
  out.close()
}