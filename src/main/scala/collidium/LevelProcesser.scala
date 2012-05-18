package collidium

import java.io.ObjectInputStream
import java.io.FileInputStream

object LevelProcesser {
	def processLevel(levelName: String) = {
	  val in = new ObjectInputStream(new FileInputStream("level" + levelName + ".collidiumLevel"))
      val level = in.readObject().asInstanceOf[Level]
	  new Board(level.name, level.cannonLocation, level.margin, level.walls, level.ball, level.hole, level.friction)
	}
}