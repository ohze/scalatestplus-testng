name := "testng-6.7"

organization := "com.sandinh"

version := "3.2.0.0"

homepage := Some(url("https://github.com/scalatest/scalatestplus-testng"))

licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

developers := List(
  Developer(
    "bvenners",
    "Bill Venners",
    "bill@artima.com",
    url("https://github.com/bvenners")
  ),
  Developer(
    "cheeseng",
    "Chua Chee Seng",
    "cheeseng@amaseng.com",
    url("https://github.com/cheeseng")
  )
)

crossScalaVersions := List(
  "2.10.7",
  "2.11.12",
  "2.12.12",
  "2.13.3",
  "0.24.0",
  "0.25.0-RC2",
)
scalaVersion := System.getProperty("scalaVersion", crossScalaVersions.value.last)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest-core" % "3.2.0",
  "org.testng" % "testng" % "6.7", 
  "commons-io" % "commons-io" % "1.3.2" % "test", 
  "org.scalatest" %% "scalatest-funsuite" % "3.2.0" % "test"
)
libraryDependencies := {
  val old = libraryDependencies.value
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((0, n)) if n > 24 => old.map {
      case m if m.organization == "org.scalatest" => m.withOrganization("com.sandinh")
      case m => m
    }
    case _ => old
  }
}

import scala.xml.{Node => XmlNode, NodeSeq => XmlNodeSeq, _}
import scala.xml.transform.{RewriteRule, RuleTransformer}

// skip dependency elements with a scope
pomPostProcess := { (node: XmlNode) =>
  new RuleTransformer(new RewriteRule {
    override def transform(node: XmlNode): XmlNodeSeq = node match {
      case e: Elem if e.label == "dependency"
          && e.child.exists(child => child.label == "scope") =>
        def txt(label: String): String = "\"" + e.child.filter(_.label == label).flatMap(_.text).mkString + "\""
        Comment(s""" scoped dependency ${txt("groupId")} % ${txt("artifactId")} % ${txt("version")} % ${txt("scope")} has been omitted """)
      case _ => node
    }
  }).transform(node).head
}

testOptions in Test :=
  Seq(
    Tests.Argument(TestFrameworks.ScalaTest,
    "-l", "org.scalatest.tags.Slow",
    "-m", "org.scalatestplus.testng",
  ))

enablePlugins(SbtOsgi)

osgiSettings

OsgiKeys.exportPackage := Seq(
  "org.scalatestplus.testng.*"
)

OsgiKeys.importPackage := Seq(
  "org.scalatest.*",
  "org.scalactic.*", 
  "scala.*;version=\"$<range;[==,=+);$<replace;"+scalaBinaryVersion.value+";-;.>>\"",
  "*;resolution:=optional"
)

OsgiKeys.additionalHeaders:= Map(
  "Bundle-Name" -> "ScalaTestPlusTestNG",
  "Bundle-Description" -> "ScalaTest+TestNG is an open-source integration library between ScalaTest and TestNG for Scala projects.",
  "Bundle-DocURL" -> "http://www.scalatest.org/",
  "Bundle-Vendor" -> "Artima, Inc."
)

publishTo := sonatypePublishToBundle.value

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>https://github.com/scalatest/scalatestplus-selenium</url>
    <connection>scm:git:git@github.com:scalatest/scalatestplus-selenium.git</connection>
    <developerConnection>
      scm:git:git@github.com:scalatest/scalatestplus-selenium.git
    </developerConnection>
  </scm>
)