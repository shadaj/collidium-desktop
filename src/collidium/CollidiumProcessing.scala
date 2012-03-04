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
  var slope: Float = 0
  var walls = List[Line](new Line(new Point(0, 0), new Point(500, 0)))
  val circle = new Circle(new Point(1, 1), 10, 10)

  override def setup() = {
    frameRate(75)
    size(screenSize, screenSize)
    background(0)

  }

  override def draw() {
    background(0)
    walls.foreach(_.draw(this))
    if (started) {
      walls.foreach { wall =>
        val collision = wall.colliding(circle, slope)
        collision match {
          case Some(newSlope) => slope = newSlope
          case None =>
        }
      }
      circle.draw(this)
      circle.start.y += slope
      circle.start.x += 1
    } else {
      ellipse(mouseX, mouseY, 5, 5)
      if (cannonDecided) {
        strokeWeight(5)
        stroke(255)
        line(cannonX, cannonY, mouseX, mouseY)
      }
    }
  }
  override def mousePressed {
    if (cannonDecided) {
      slope = (mouseY - cannonY).toFloat / (mouseX - cannonX)
      started = true
    } else {
      cannonX = mouseX
      cannonY = mouseY
      circle.start.y = cannonY
      circle.start.x = cannonX
      cannonDecided = true
    }
  }

}

trait Sprite {
  def draw(graphics: PApplet): Unit
  def colliding(sprite: Sprite, curSlope: Float): Option[Float]
  def bounds: (Point, Point)
}

class Circle(var start: Point, val height: Int, val width: Int) extends Sprite {
  def draw(graphics: PApplet): Unit = {
    graphics.ellipse(start.x, start.y, width, height)
  }
  def colliding(sprite: Sprite, curSlope: Float): Option[Float] = {
    None
  }
  def bounds: (Point, Point) = {
    (start, new Point(start.x + width, start.y + height))
  }
}

class Line(start: Point, end: Point) extends Sprite {
  val m = (end.y - start.y) / (end.x - start.x)
  val c = start.y - (start.x * m)
  def y = (x: Int) => m * x + c // mx + c

  def draw(papplet: PApplet) {
    papplet.line(start.x, start.y, end.x, end.y)
  }

  def colliding(sprite: Sprite, curSlope: Float) = {
    val myY = start.y
    val spriteYMax = sprite.bounds._1.y max sprite.bounds._2.y
    val spriteYMin = sprite.bounds._1.y min sprite.bounds._2.y
    //println(spriteYMin + " " + myY + " " + spriteYMax)
    if (myY >= spriteYMin && myY <= spriteYMax) {
      Some(m - curSlope)
    } else {
      None
    }
  }

  def height = 0
  def width = 0
  def bounds = (start, end)
}

class Point(var x: Float, var y: Float)
