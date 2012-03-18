package collidium

import processing.core.PApplet
import Angle._

trait Sprite {
  def draw(graphics: PApplet): Unit
  def colliding(sprite: Sprite): Unit
  def bounds: (Point, Point)
  var theta: Double = 0
}

class Circle(var start: Point, val height: Int, val width: Int) extends Sprite {
  def next {
    //println(deltaX + " " + deltaY)
    start = new Point((start.x + deltaX), (start.y + deltaY))
  }
  def draw(graphics: PApplet): Unit = {
    graphics.ellipse(start.x.toFloat, start.y.toFloat, width, height)
  }
  def colliding(sprite: Sprite) {
  }
  def bounds: (Point, Point) = {
    (start, new Point(start.x + width, start.y + height))
  }
  
  def deltaX = Math.cos(theta) * magnitude
  def deltaY = Math.sin(theta) * magnitude
  var magnitude = 0D
}

class Line(start: Point, end: Point) extends Sprite {
  val deltaX = start.x - end.x
  val deltaY = start.y - end.y
  val magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
  theta = Math.atan(deltaY/deltaX)
  if (start.x < end.x) {
    println(theta + " " + (180 deg))
    theta = theta + (180 deg)
  }
  
  def draw(papplet: PApplet) {
    papplet.line(start.x.toFloat, start.y.toFloat, end.x.toFloat, end.y.toFloat)
  }

  def colliding(sprite: Sprite) {
    
    def m = deltaY / deltaX
    def theta = Math.atan(m)
    //magnitude = 
    def c = start.y - (start.x * m)
    def y = (x: Double) => m * x + c // mx + c
    val myY = start.y
    val myX = start.x
    val spriteYMax = sprite.bounds._1.y max sprite.bounds._2.y
    val spriteYMin = sprite.bounds._1.y min sprite.bounds._2.y
    val spriteXMax = sprite.bounds._1.x max sprite.bounds._2.x
    val spriteXMin = sprite.bounds._1.x min sprite.bounds._2.x
    //println(spriteYMin + " " + myY + " " + spriteYMax)
    if ((myY >= spriteYMin && myY <= spriteYMax && y(myX) == myY) || (myX >= spriteXMin && myX <= spriteXMax && y(myX) == myY)) {
      sprite.theta = (180 deg) - (2 * sprite.theta) + theta
    }
  }

  def height = 0
  def width = 0
  def bounds = (start, end)
}

class Sling(val start: Point, val end: Point) extends Line(start, end) {
  override def draw(papplet: PApplet) {
    papplet.strokeWeight(5)
    papplet.stroke(255)
    super.draw(papplet)
  }
}

class Point(var x: Double, var y: Double) {
  override def toString = {
    "Point(" + x + "," + y + ")"
  }
}
