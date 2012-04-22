package collidium

import processing.core._
import java.awt.event._
import javax.swing.JFrame
import Angle._
import java.io._
import javazoom.jl.player.Player
import scala.collection.mutable
import java.awt.Color

object CollidiumApp extends App {
  val game = new Collidium
  val frame = new JFrame("Collidium")
  frame.getContentPane().add(game)
  val musicPlayer = new MusicPlayer()
  musicPlayer.start
  game.init

  frame.pack
  frame.setVisible(true)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
}


class MusicPlayer extends Thread {
  var curFile = "BackgroundMusic.mp3"
  var player : Player = null
  var playing = true
  setDaemon(false)
  override def run {
    while (playing) {
      player = new Player(new FileInputStream(new File(curFile)))
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

class Collidium extends PApplet {
  val group = 1
  val levelNum = 1
  val in = new ObjectInputStream(new FileInputStream("level" + levelNum + ".txt")) 
  val level = in.readObject().asInstanceOf[Level]
  val cannonLocation = level.cannonLocation
  val black = 0
  val white = 255
  val screenSize = 500
  val margin = level.margin
  var started = false
  var pullingRubber = false
  var slingOption: Option[Sling] = None
  val walls = level.walls
  var obstacles = List[Sprite]()
  val ball = level.ball
  val hole = level.hole
  val friction = level.friction
  var curObstacle: Option[Line] = None

  var drawingLine = false

  override def setup() = {
    frameRate(50)
    size(screenSize, screenSize)
    stroke(white)
    background(0)
    ellipseMode(PConstants.CENTER)

  }

  override def draw() {
	background(0)
    walls.foreach(_.draw(this))
    obstacles.foreach(_.draw(this))
    fill(Color.GREEN.getRGB)
    stroke(Color.GREEN.getRGB)
    hole.draw(this)
    fill(white)
    stroke(white)

    if (started) {
      ball.magnitude = ball.magnitude - friction
      if (ball.magnitude <= 0.01) exit
      walls.foreach { wall =>
        wall.colliding(ball)
      }
      obstacles.foreach { obstacle =>
        obstacle.colliding(ball)
      }
      if (hole.inBoundsOf(ball)) {
        //CollidiumApp.backMusic.exit
        println("You Won!")
        CollidiumApp.musicPlayer.setMusic("YouWon.mp3")
        while (!CollidiumApp.musicPlayer.player.isComplete) {}
        CollidiumApp.musicPlayer.stopPlaying
        exit
      }
      ball.update
    } else {
      ball.location.x = mouseX
      ball.location.y = mouseY
      slingOption match {
        case Some(sling) => sling.draw(this)
        case None =>
      }
      fill(Color.red.getRGB)
      ellipse(cannonLocation._1, cannonLocation._2, 20, 20)
      fill(white)
    }

    ball.draw(this)

  }

  override def mousePressed() {
    if (!started) {
      if (key == 'L') {
        curObstacle = Option(new Line(new Point(mouseX, mouseY), new Point(mouseX, mouseY)))
      } else if (mouseX >= cannonLocation._1 - 10 && mouseX <= cannonLocation._1 + 10
        && mouseY >= cannonLocation._2 - 10 && mouseY <= cannonLocation._2 + 10) {
        slingOption = Option(new Sling(new Point(mouseX, mouseY), new Point(mouseX, mouseY)))
        ball.location.x = mouseX
        ball.location.y = mouseY
      }
    }
  }

  override def mouseDragged() {
    if (slingOption.isDefined && !started) {
      ball.location.x = mouseX
      ball.location.y = mouseY
      slingOption = Option(new Sling(slingOption.get.start, new Point(mouseX, mouseY)))
      slingOption.get.draw(this)
    } else if (key == 'L' && curObstacle.isDefined) {
      curObstacle = Option(new Line(curObstacle.get.start, new Point(mouseX, mouseY)))
      curObstacle.get.draw(this)
    }
  }

  override def mouseReleased() {
    if (slingOption.isDefined && !started) {
      ball.theta = slingOption.get.theta
      ball.magnitude = slingOption.get.magnitude / 40
      ball.location.y = mouseY
      ball.location.x = mouseX
      pullingRubber = false
      started = true
    } else if (key == 'L' && curObstacle.isDefined) {
      curObstacle = Option(new Line(curObstacle.get.start, new Point(mouseX, mouseY)))
      obstacles = curObstacle.get :: obstacles
      drawingLine = false
      curObstacle = None
    }

  }

  override def keyPressed() {
    if (key == 'l') {
      println("drawing")
      drawingLine = true
    } else if (key == 'w') {
      ball.theta = new Line(hole.location, ball.location).theta
    } else {
      val (xShift, yShift) =
        if (keyCode == PConstants.LEFT) {
          (-1D, 0D)
        } else if (keyCode == PConstants.RIGHT) {
          (1D, 0D)
        } else if (keyCode == PConstants.UP) {
          (0D, 1D)
        } else if (keyCode == PConstants.DOWN) {
          (0D, -1D)
        } else {
          (ball.deltaX, ball.deltaY)
        }

      ball.theta = new Line(new Point(ball.location.x + xShift, ball.location.y + yShift), ball.location).theta
    }
  }

}