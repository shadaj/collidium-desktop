package collidium

import playn.core._
import Angle._
import math._

abstract class Sprite(val color: Int) extends Serializable {
  def draw(graphics: Canvas): Unit = {
    graphics.setStrokeColor(color)
    graphics.setFillColor(color)
  }
  def colliding(sprite: Sprite): Unit
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

class Circle(var location: Point, val diameter: Int, color: Int) extends Sprite(color) {
  def next = {
    new Point((location.x + deltaX), (location.y + deltaY))
  }

  override def draw(graphics: Canvas): Unit = {
    super.draw(graphics)
    graphics.fillCircle(location.x.toFloat, location.y.toFloat, diameter/2)
  }
  
  def colliding(sprite: Sprite) {
  }
  
  def inBoundsOf(circle: Circle) = {
    val xshift = circle.location.x - location.x
    val yshift = circle.location.y - location.y
    val deltaDiameter = (diameter - circle.diameter)/2
    if (circle.diameter > diameter) {
      false
    } else if ((xshift * xshift) + (yshift*yshift) < (deltaDiameter)*(deltaDiameter)) {
      true
    } else false
  }

  def deltaX = cos(theta) * magnitude
  def deltaY = sin(theta) * magnitude
  var magnitude = 0D
}

class Line(val start: Point, val end: Point, color: Int) extends Sprite(color) {
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
  
  if (start.x < end.x) {
    theta = theta + (180 deg)
  }

  def next = start
  var location = start

  override def draw(graphics: Canvas) {
    super.draw(graphics)
    graphics.drawLine(start.x.toFloat, start.y.toFloat, end.x.toFloat, end.y.toFloat)
  }

  def intersects(line: Line) = {
    if (abs(line.m - m) < 0.001 || abs(line.m - m).isNaN) {
      None // Parallel lines
    } else if(line.m.isInfinite) {
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
    val spriteLine = new Line(sprite.location, sprite.next, sprite.color)
    intersects(spriteLine) match {
      case Some(point) =>
        sprite.move(point)
        sprite.theta = (2 * theta) - sprite.theta 
      case None =>
    }
  }

  def height = 0
  def width = 0
  def bounds = (start, end)
}

class Sling(start: Point, end: Point, color: Int) extends Line(start, end, color)

class Point(var x: Double, var y: Double) extends Serializable {
  override def toString = {
    "Point(" + x + "," + y + ")"
  }
}


