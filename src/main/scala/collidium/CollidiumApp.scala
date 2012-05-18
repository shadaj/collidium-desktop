package collidium

import playn.core._
import playn.java._
import playn.core.PlayN._
import java.awt.event._
import javax.swing.JFrame
import Angle._
import java.io._
import javazoom.jl.player.Player
import scala.collection.mutable
import playn.core.Keyboard
import playn.core.Key._
import Colors._

object CollidiumApp extends App {
  JavaPlatform.register
  val game = new Collidium
  PlayN.run(game)
  MusicPlayer.run
}

class Collidium extends Game with Keyboard.Listener with Mouse.Listener {
  val board = LevelProcesser.processLevel("0.0")
  val group = 1
  val levelNum = 1
  val cannonLocation = board.cannonLocation
  
  var curstart = 0
  val screenSize = 500
  var canvas: Canvas = null
  val margin = board.margin
  
  var pullingRubber = false
  
  var curObstacle: Option[Line] = None

  var drawingLine = false

  
  
  override def init() = {
    val image = graphics().createImage(screenSize, screenSize);
    canvas = image.canvas();
    canvas.setStrokeWidth(2);
    val layer = graphics().createImageLayer(image);
    layer.setSize(screenSize, screenSize)
    graphics().setSize(screenSize + margin * 2, screenSize + margin * 2)
    graphics().rootLayer().add(layer);
    keyboard.setListener(this)
    mouse.setListener(this)
  }

  override def paint(alpha: Float) {
    board.paint(canvas)
  }
  
  
  override def updateRate() = 20
  
  override def update(delta: Float) {
    board.update
  }
  
  override def onMouseDown(event: Mouse.ButtonEvent) {
    val x = event.x
    val y = event.y
    val xDiff = cannonLocation._1 - x
    val yDiff = cannonLocation._2 - y
    if (!board.started) {
      if ((xDiff * xDiff) + (yDiff * yDiff) <= (25 * 25)) {
        board.slingOption = Option(new Sling(new Point(x, y), new Point(x, y), white))
        board.ball.location.x = x
        board.ball.location.y = y
      } else {
        curObstacle = Option(new Line(new Point(x, y), new Point(x, y), white))
        drawingLine = true
      }
    }
  }

  override def onMouseMove(event: Mouse.MotionEvent) {
    val x = event.x
    val y = event.y
    if (board.slingOption.isDefined && !board.started) {
      board.ball.location.x = x
      board.ball.location.y = y
      board.slingOption = Option(new Sling(board.slingOption.get.start, new Point(x, y), white))
      board.slingOption.get.draw(canvas)
    } else if (drawingLine && curObstacle.isDefined) {
      curObstacle = Option(new Line(curObstacle.get.start, new Point(x, y), white))
      curObstacle.get.draw(canvas)
    }
  }

  override def onMouseUp(event: Mouse.ButtonEvent) {
    val x = event.x
    val y = event.y
    if (board.slingOption.isDefined && !board.started) {
      board.ball.theta = board.slingOption.get.theta
      board.ball.magnitude = board.slingOption.get.magnitude / 40
      board.ball.location.y = y
      board.ball.location.x = x
      pullingRubber = false
      board.started = true
    } else if (drawingLine && curObstacle.isDefined) {
      curObstacle = Option(new Line(curObstacle.get.start, new Point(x, y), white))
      board.obstacles = curObstacle.get :: board.obstacles
      drawingLine = false
      curObstacle = None
    }

  }
  
  override def onMouseWheelScroll(event: Mouse.WheelEvent) {}

  override def onKeyDown(event: Keyboard.Event) {
    val key = event.key
    if (key == Key.R) {
      board.started = false
      var savedSling = board.slingOption
      board.slingOption = None
    } else if (key == Key.W) {
      board.ball.theta = new Line(board.hole.location, board.ball.location, white).theta
    } else {
      val (xShift, yShift) =
        if (key == LEFT) {
          (-1D, 0D)
        } else if (key == RIGHT) {
          (1D, 0D)
        } else if (key == UP) {
          (0D, 1D)
        } else if (key == DOWN) {
          (0D, -1D)
        } else {
          (board.ball.deltaX, board.ball.deltaY)
        }

      board.ball.theta = new Line(new Point(board.ball.location.x + xShift, board.ball.location.y + yShift), board.ball.location, white).theta
    }
  }
  override def onKeyUp(event: Keyboard.Event) {}
  override def onKeyTyped(event: Keyboard.TypedEvent) {}
}