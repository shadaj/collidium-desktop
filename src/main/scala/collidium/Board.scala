package collidium

import playn.core._
import javazoom.jl.player._
import java.io.InputStream
import java.io.FileInputStream
import java.io.File

object MusicPlayer extends Thread {
  var curFile = "src/main/resources/skulls_adventure_0.mp3"
  var player : Player = null
  var playing = true
  setDaemon(true)
  override def run {
    while (playing) {
      player = new Player(new FileInputStream(new File(curFile)))
      println("playing")
      player.play
    }
  }
  
  def stopPlaying {
    playing = false
  }
  
  def setMusic(fileName: String) {
    curFile = fileName
    player.close
  }
}

class Board(val name: String, val cannonLocation: (Float, Float), val margin: Int, val walls: List[Line], val ball: Circle, val hole: Circle, val friction: Double) {
  var slingOption: Option[Sling] = None
  var started = false
  var obstacles = List[Sprite]()
  var winnable = true
  
  def paint(canvas: Canvas) {
    canvas.clear
    walls.foreach(_.draw(canvas))
    obstacles.foreach(_.draw(canvas))
      hole.draw(canvas)
      if (started) {
    	  ball.draw(canvas)
      } else {
        (new Circle(new Point(cannonLocation._1, cannonLocation._2), 50, Colors.green)).draw(canvas)
    	  if (slingOption.isDefined) {
    		  slingOption.get.draw(canvas)
    	  }
      }
  }
  
  def update {
    if (started) {
      if (ball.magnitude >= 0.01) {
        ball.magnitude = ball.magnitude - friction
      }
      walls.foreach { wall =>
        wall.colliding(ball)
      }
      obstacles.foreach { obstacle =>
        obstacle.colliding(ball)
      }
      if (hole.inBoundsOf(ball) && winnable == true) {
        println("You Won!")
        winnable = false
        MusicPlayer.setMusic("src/main/resources/YouWon.mp3")
        ball.magnitude = 0
      }
      ball.update
    }
    
    //TODO: Add cursor in update
  }
}