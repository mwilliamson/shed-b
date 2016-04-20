import sbt._
import sbt.Keys._
import com.simplytyped._
import com.simplytyped.Antlr4Plugin._

object ShedBuild extends Build {

  lazy val root = Project(id = "shed", base = file("."))
    .settings(antlr4Settings:_*)
    .settings(
      antlr4PackageName in Antlr4 := Some("org.shedlang")
    )
}
