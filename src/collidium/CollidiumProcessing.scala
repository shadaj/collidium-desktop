package collidium

import processing.core._
import java.awt.event._
import javax.swing.JFrame

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
  
  var started = false
  var cannonDecided = false
  var cannonY = 1
  var cannonX = 1
  var downFactor = 1D
  var rightFactor = 1
  var heightFromTop = 1D
  var distanceFromLeft = 1
  override def setup() = {
    frameRate(100)
    size(screenSize, screenSize)
    background(0)
    
  }

  override def draw() {
    background(0)
    if (started) {
	      if (heightFromTop >= 490 || heightFromTop <= 0) {
	        downFactor = -downFactor
	      }
	      if (distanceFromLeft >= 490 || distanceFromLeft <= 0) {
	        rightFactor = -rightFactor
	      }
	      
		  ellipse(distanceFromLeft,heightFromTop.toFloat,10,10)
		  
		  heightFromTop += downFactor
		  distanceFromLeft += rightFactor
    } else {
      ellipse(mouseX,mouseY,5,5)
      if(cannonDecided){
	      strokeWeight(5)
	      stroke(255)
	      line(cannonX,cannonY,mouseX,mouseY)
      }
    }
  }
  override def mousePressed {
    if (cannonDecided) {
    	downFactor = (mouseY.toDouble - cannonY.toDouble) / (mouseX.toDouble - cannonX.toDouble)
    	started = true
    } else {
      cannonX = mouseX
      cannonY = mouseY
      heightFromTop = cannonY
      distanceFromLeft = cannonX
      cannonDecided = true
    }
  }

}
