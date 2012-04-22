package collidium

class Level(val levelNum: Int, val cannonLocation: (Float,Float), val margin: Int, val walls: List[Line], val ball: Circle, val hole: Circle, val friction: Double) extends Serializable {
	
}