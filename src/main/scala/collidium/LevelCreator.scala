package collidium

import java.io._
import Colors._

object LevelCreator extends App {
  val level = new Level("0.0",
      (400, 400),
      250,
      List(new Line(new Point(10,10), new Point(490, 10), blue),
          new Line(new Point(490,10), new Point(490, 490), blue),
          new Line(new Point(490,490), new Point(10, 490), blue),
          new Line(new Point(10,490), new Point(10, 10), blue)),
      new Circle(new Point(100,100), 20, white),
      new Circle(new Point(250,250), 40, red), 0)
  val out = new ObjectOutputStream(new FileOutputStream("level" + level.name + ".collidiumLevel"))
  out.writeObject(level)
  out.close()
}