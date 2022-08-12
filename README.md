# named-codec
Scala3 codec adapter that separates types and payloads
<a href="https://typelevel.org/cats/"><img src="https://typelevel.org/cats/img/cats-badge.svg" height="40px" align="right" alt="Cats friendly" /></a>

[![named-codec-core Scala version support](https://index.scala-lang.org/hnaderi/named-codec/named-codec-core/latest.svg?style=flat-square)](https://index.scala-lang.org/hnaderi/named-codec/named-codec-core)
[![javadoc](https://javadoc.io/badge2/dev.hnaderi/named-codec-docs_3/scaladoc.svg?style=flat-square)](https://javadoc.io/doc/dev.hnaderi/named-codec-docs_3) 
<img alt="GitHub Workflow Status" src="https://img.shields.io/github/workflow/status/hnaderi/named-codec/Continuous%20Integration?style=flat-square">
<img alt="GitHub" src="https://img.shields.io/github/license/hnaderi/named-codec?style=flat-square">  
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

this is a tiny utility library that provides a simple codec adapter, 
that helps with creating codec that encode/decode type name separately.

This is mostly helpful in messaging applications, where payload and message types are separated;
or for scenarios that you need to store a separate type name to enable type filtering.

### Usage

This library is currently available for Scala binary version 3.1 on both JVM and JS.

To use the latest version, include the following in your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "dev.hnaderi" %% "named-codec" % "@VERSION@"
)

// or circe module directly

libraryDependencies ++= Seq(
  "dev.hnaderi" %% "named-codec-circe" % "@VERSION@"
)
```

```scala
enum Data {
  case A
  case B(i: Int)
  case C(s: String, i: Int)
}

import io.circe.generic.auto.*
import dev.hnaderi.namedcodec.*

val codec = CirceAdapter.of[Data]

codec.encode(Data.C("string", 101))
```
