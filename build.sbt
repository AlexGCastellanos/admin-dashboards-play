import com.github.play2war.plugin._

//name := """play-simple-web"""
name := """admin-dashboards"""

version := "web"

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  // If you enable PlayEbean plugin you must remove these
  // JPA dependencies to avoid conflicts.
    javaJdbc,
    javaEbean,
    cache,
    javaWs,
    "org.hibernate" % "hibernate-entitymanager" % "4.3.7.Final",
    "org.xerial" % "sqlite-jdbc" % "3.8.6",
    "commons-io" % "commons-io" % "2.5"
)

libraryDependencies ++= Seq(
    "mysql" % "mysql-connector-java" % "5.1.27"
)