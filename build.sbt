ThisBuild / tlBaseVersion := "0.3"

ThisBuild / organization := "dev.hnaderi"
ThisBuild / organizationName := "Hossein Naderi"
ThisBuild / startYear := Some(2022)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  tlGitHubDev("hnaderi", "Hossein Naderi")
)
ThisBuild / tlSitePublishBranch := Some("main")

val Scala3 = "3.3.3"
ThisBuild / scalaVersion := Scala3 // the default Scala

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = tlCrossRootProject.aggregate(core, circe, unidocs)

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "named-codec",
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % "1.2.1" % Test
    )
  )

lazy val circe = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("circe"))
  .dependsOn(core)
  .settings(
    name := "named-codec-circe",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % "0.14.15",
      "io.circe" %%% "circe-generic" % "0.14.15" % Test,
      "org.scalameta" %%% "munit" % "1.2.1" % Test
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

// This job is used as a sign that all build jobs have been successful and is used by mergify
ThisBuild / githubWorkflowAddedJobs += WorkflowJob(
  id = "post-build",
  name = "post build",
  needs = List("build"),
  steps = List(
    WorkflowStep.Run(
      commands = List("echo success!"),
      name = Some("post build")
    )
  ),
  scalas = Nil,
  javas = Nil
)
