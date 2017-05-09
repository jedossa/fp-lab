import Dependencies._
import sbt.Keys.scalacOptions

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaOrganization := "org.typelevel",
      scalaVersion := "2.12.2-bin-typelevel-4",
      scalacOptions ++= Seq(
        "-deprecation",
        "-encoding",
        "UTF-8",
        "-feature",
        "-language:existentials",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-language:experimental.macros",
        "-unchecked",
        "-Xfatal-warnings",
        "-Xlint",
        "-Yno-adapted-args",
        "-Ypartial-unification",
        "-Ywarn-dead-code",
        "-Ywarn-numeric-widen",
        "-Ywarn-value-discard",
        "-Ywarn-unused-import",
        "-Xfuture"
      )
    )),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats" % "0.9.0",
      "com.chuusai" %% "shapeless" % "2.3.2"
      )
)