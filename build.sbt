name := "Collidium"

version := "0.1.0"

libraryDependencies ++= Seq(
	"com.googlecode.playn" % "playn-core" % "1.2",
	"com.googlecode.playn" % "playn-java" % "1.2"
)

resolvers += "Playn project" at "http://forplay.googlecode.com/svn/mavenrepo"
