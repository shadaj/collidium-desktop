package collidium

class Angle(degrees: Double) {
  def deg = degrees * (Math.Pi/180)
}

object Angle {
  implicit def doubleToAngle(degrees: Double) = new Angle(degrees)
}