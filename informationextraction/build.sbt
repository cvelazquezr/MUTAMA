scalaVersion := "2.12.12"

name := "informationExtraction"
organization := "com.vub.be"
version := "1.0"

libraryDependencies ++= Seq(
    "commons-io" % "commons-io" % "2.7",
    "org.apache.bcel" % "bcel" % "6.5.0",
    "org.apache.spark" %% "spark-sql" % "2.4.6",
    "org.apache.spark" %% "spark-core" % "2.4.6"
)
