package collidium

import processing.core._
import java.awt.event._
import javax.swing.JFrame
import Angle._

object CollidiumApp extends App {
  val game = new Collidium
  val frame = new JFrame("Conway's Game of Life")
  frame.getContentPane().add(game)
  game.init

  frame.pack
  frame.setVisible(true)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
}

class Collidium extends PApplet {
  val black = 0
  val white = 255
  val screenSize = 500
  val margin = 50
  var started = false
  var pullingRubber = false
  var slingOption: Option[Sling] = None
  var walls = List(new Line(new Point(screenSize -margin, margin), new Point(margin, margin)),
		  	 	   new Line(new Point(screenSize -margin, screenSize - margin), new Point(margin, screenSize - margin)),
		   	 	   new Line(new Point(screenSize -margin, margin), new Point(screenSize - margin, screenSize - margin)),
		  		   new Line(new Point(margin, margin), new Point(margin, screenSize - margin)))
  val circle = new Circle(new Point(1, 1), 10, 10)
  
  override def setup() = {
    frameRate(75)
    size(screenSize, screenSize)
  }

  override def draw() {
    background(0)
    strokeWeight(0.5F)
    walls.foreach(_.draw(this))
    if (started) {
      walls.foreach { wall =>
        wall.colliding(circle)
      }
      circle.next
    } else {
      circle.start.x = mouseX
      circle.start.y = mouseY
      slingOption match {
        case Some(sling) => sling.draw(this)
        case None =>
      }
    }
    circle.draw(this)
  }

  override def mousePressed() {
    if (!started) {
      slingOption = Option(new Sling(new Point(mouseX, mouseY), new Point(mouseX, mouseY)))
      circle.start.x = mouseX
      circle.start.y = mouseY
      pullingRubber = true
    }
  }
  
  override def mouseDragged() {
    if (pullingRubber) {
        circle.start.x = mouseX
        circle.start.y = mouseY

    	slingOption = Option(new Sling(slingOption.get.start, new Point(mouseX, mouseY)))
    }
  }
  
  override def mouseReleased() {
    circle.theta = slingOption.get.theta
    circle.magnitude = slingOption.get.magnitude / 40
	circle.start.y = mouseY
	circle.start.x = mouseX

    pullingRubber = false
    started = true
  }
}

