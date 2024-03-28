ThisBuild / tlBaseVersion := "0.2"

ThisBuild / organization := "dev.hnaderi"
ThisBuild / organizationName := "Hossein Naderi"
ThisBuild / startYear := Some(2022)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  tlGitHubDev("hnaderi", "Hossein Naderi")
)

ThisBuild / tlSonatypeUseLegacyHost := false

ThisBuild / tlSitePublishBranch := Some("main")

val Scala3 = "3.4.1"
ThisBuild / scalaVersion := Scala3 // the default Scala

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = tlCrossRootProject.aggregate(core, circe, unidocs)

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "named-codec",
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % "1.0.0-M11" % Test
    )
  )

lazy val circe = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("circe"))
  .dependsOn(core)
  .settings(
    name := "named-codec-circe",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % "0.14.6",
      "io.circe" %%% "circe-generic" % "0.14.6" % Test,
      "org.scalameta" %%% "munit" % "1.0.0-M11" % Test
    )
  )

import laika.helium.config._
import laika.ast.Path.Root

lazy val docs = project
  .in(file("site"))
  .settings(
    tlSiteHelium ~= {
      _.site
        .topNavigationBar(
          homeLink = IconLink.internal(Root / "index.md", HeliumIcon.home)
        )
    }
  )
  .enablePlugins(TypelevelSitePlugin)

lazy val unidocs = project
  .in(file("unidocs"))
  .enablePlugins(TypelevelUnidocPlugin)
  .settings(
    name := "named-codec-docs",
    description := "unified docs for named codec",
    ScalaUnidoc / unidoc / unidocProjectFilter := inAnyProject
  )
