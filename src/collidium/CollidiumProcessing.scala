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
  val backMusic = new BackMusic
  val frame = new JFrame("Conway's Game of Life")
  val musicFrame = new JFrame("BackMusic")
  frame.getContentPane().add(game)
  musicFrame.getContentPane.add(backMusic)
  game.init
  backMusic.init

  frame.pack
  musicFrame.pack
  frame.setVisible(true)
  musicFrame.setVisible(false)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  musicFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
}


class BackMusic extends PApplet {
  var backAudio = new Player(new FileInputStream(new File("My Song 2.mp3")))
  var framesCount = 10000
  var shouldPlay = false
  override def draw {
    if (shouldPlay) {
      backAudio = new Player(new FileInputStream(new File("My Song 2.mp3")))
      backAudio.play
      
    }
  }
}
class Collidium extends PApplet {
  val black = 0
  val white = 255
  val screenSize = 500
  val margin = 50
  var started = false
  var pullingRubber = false
  var slingOption: Option[Sling] = None
  val walls = mutable.MutableList(new Line(new Point(margin, margin), new Point(screenSize - margin, margin)),
    new Line(new Point(screenSize - margin, margin), new Point(screenSize - margin, screenSize - margin)),
    new Line(new Point(screenSize - margin, screenSize - margin), new Point(margin, screenSize - margin)),
    new Line(new Point(margin, screenSize - margin), new Point(margin, margin)))
  val ball = new Circle(new Point(1, 1), 10, 10)
  val hole = new Circle(new Point(margin + 20, margin + 20), 30, 30)
  
  

  var drawingLine = false

  override def setup() = {
    frameRate(50)
    size(screenSize, screenSize)
    stroke(white)
    background(0)
    
    
  }

  override def draw() {
    if (CollidiumApp.backMusic.framesCount == 10000) {
      CollidiumApp.backMusic.shouldPlay = true
      CollidiumApp.backMusic.framesCount = 0
      //CollidiumApp.backMusic.shouldPlay = false
      println("reset")
    } else {
      println("increasing")
      CollidiumApp.backMusic.framesCount += 1
    }
    println(CollidiumApp.backMusic.framesCount)
    background(0)
    walls.foreach(_.draw(this))
    fill(Color.GREEN.getRGB)
    stroke(Color.GREEN.getRGB)
    hole.draw(this)
    fill(white)
    stroke(white)

    if (started) {
      walls.foreach { wall =>
        wall.colliding(ball)
      }
      if (hole.inBoundsOf(ball)) {
        exit
        CollidiumApp.backMusic.exit
        println("You Won!")
        val audio = new Player(new FileInputStream(new File("My Song.mp3")))
        audio.play

      }
      ball.update
    } else {
      ball.location.x = mouseX
      ball.location.y = mouseY
      slingOption match {
        case Some(sling) => sling.draw(this)
        case None =>
      }
    }

    ball.draw(this)

  }

  override def mousePressed() {
    if (!started) {
      if (drawingLine) {
        new Line(new Point(mouseX, mouseY), new Point(mouseX, mouseY)) +: walls
      } else {
        slingOption = Option(new Sling(new Point(mouseX, mouseY), new Point(mouseX, mouseY)))
        ball.location.x = mouseX
        ball.location.y = mouseY
        pullingRubber = true
      }
    }
  }

  override def mouseDragged() {
    if (pullingRubber) {
      ball.location.x = mouseX
      ball.location.y = mouseY

      slingOption = Option(new Sling(slingOption.get.start, new Point(mouseX, mouseY)))
    } else if (drawingLine) {
      walls(0) = new Line(walls(0).start, new Point(mouseX, mouseY))
    }
  }

  override def mouseReleased() {
    if (pullingRubber) {
      ball.theta = slingOption.get.theta
      ball.magnitude = slingOption.get.magnitude / 40
      ball.location.y = mouseY
      ball.location.x = mouseX
      pullingRubber = false
      started = true
    } else if (drawingLine) {
      walls(0) = new Line(walls(0).start, new Point(mouseX, mouseY))
      drawingLine = false
    }

  }

  override def keyPressed() {
    if (keyCode == PConstants.SHIFT) {
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
  

