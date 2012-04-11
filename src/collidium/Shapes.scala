package collidium

import processing.core.PApplet
import Angle._
import processing.core.PConstants
import math._

abstract class Sprite {
  def draw(graphics: PApplet): Unit
  def colliding(sprite: Sprite): Unit
  //  def bounds: (Point, Point)
  var theta: Double = 0
  var location: Point
  def next: Point
  def move(to: Point) {
    location = to
  }
  def update {
    move(next)
  }
}

class Circle(var location: Point, val height: Int, val width: Int) extends Sprite {
  def next = {
    //println(deltaX + " " + deltaY)
    new Point((location.x + deltaX), (location.y + deltaY))
  }

  def draw(graphics: PApplet): Unit = {
    graphics.ellipseMode(PConstants.CENTER)
    graphics.ellipse(location.x.toFloat, location.y.toFloat, width, height)
  }
  def colliding(sprite: Sprite) {
  }
  //  def bounds: (Point, Point) = {
  //    (location, new Point(location.x + width, location.y + height))
  //  }
  
  def inBoundsOf(circle: Circle) = {
    val xshift = (width - circle.width)/2
    val yshift = (height - circle.height)/2
    if (circle.location.x >= location.x && circle.location.x <= location.x + xshift && circle.location.y >= location.y && circle.location.y <= location.y + yshift) {
      true
    } else false
  }
  
  def deltaX = cos(theta) * magnitude
  def deltaY = sin(theta) * magnitude
  var magnitude = 0D
}

class Line(val start: Point, val end: Point) extends Sprite {
  val deltaX = end.x - start.x
  val deltaY = end.y - start.y
  val magnitude = sqrt(deltaX * deltaX + deltaY * deltaY)
  val m = deltaY / deltaX
  

  val c = start.y - (start.x * m)
  def y = (x: Double) => m * x + c // mx + c
  val minX = start.x min end.x
  val maxX = start.x max end.x
  val minY = start.y min end.y
  val maxY = start.y max end.y

  theta = atan(m)
  //println(m + "," + theta)
  
  if (start.x < end.x) {
    theta = theta + (180 deg)
  }

  def next = start
  var location = start

  def draw(papplet: PApplet) {
    papplet.strokeWeight(1)
    papplet.line(start.x.toFloat, start.y.toFloat, end.x.toFloat, end.y.toFloat)
  }

  def intersects(line: Line) = {
    //println(line.theta)
    if (abs(line.m - m) < 0.001 || abs(line.m - m).isNaN) {
      None // Parallel lines
    } else if(line.m.isInfinite) {
      //println(m)
      val intersectionY = y(line.minX)
      if (line.maxY > intersectionY && line.minY < intersectionY) {
        Option(new Point(line.minX, intersectionY))
      } else {
        None
      }
    } else {
      val (intersectionX, intersectionY) = if (m.isInfinite) {
        val intersectionX = start.x
        val intersectionY = line.y(intersectionX)
        (intersectionX, intersectionY)
      } else {
        val intersectionX = (line.c - c) / (m - line.m)
        val intersectionY = y(intersectionX)
        (intersectionX, intersectionY)
      }
      if (intersectionX >= line.minX && intersectionX <= line.maxX && intersectionY >= line.minY && intersectionY <= line.maxY
          && intersectionX >= minX && intersectionX <= maxX && intersectionY >= minY && intersectionY <= maxY) {
        Option(new Point(intersectionX, intersectionY))
      } else {
        None
      }
    }
  }
  def colliding(sprite: Sprite) {
    val spriteLine = new Line(sprite.location, sprite.next)
    intersects(spriteLine) match {
      case Some(point) =>
        sprite.move(point)
        sprite.theta = (2 * theta) - sprite.theta 
      case None =>
    }
    //    val spriteLine = new Line(sprite.location, sprite.next)
    //    val 
    //    
    ////    def theta = atan(m)
    ////    //magnitude = 
    ////    def c = start.y - (start.x * m)
    ////    def y = (x: Double) => m * x + c // mx + c
    ////    val myY = start.y
    ////    val myX = start.x
    ////    val spriteYMax = sprite.bounds._1.y max sprite.bounds._2.y
    ////    val spriteYMin = sprite.bounds._1.y min sprite.bounds._2.y
    ////    val spriteXMax = sprite.bounds._1.x max sprite.bounds._2.x
    ////    val spriteXMin = sprite.bounds._1.x min sprite.bounds._2.x
    ////    //println(spriteYMin + " " + myY + " " + spriteYMax)
    ////    if ((myY >= spriteYMin && myY <= spriteYMax && y(myX) == myY) || (myX >= spriteXMin && myX <= spriteXMax && y(myX) == myY)) {
    ////      sprite.theta = (180 deg) - (2 * sprite.theta) + theta
    ////    }
  }

  def height = 0
  def width = 0
  def bounds = (start, end)
}

class Sling(start: Point, end: Point) extends Line(start, end) {
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
