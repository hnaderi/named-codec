## named-codec

### Usage

This library is currently available for Scala binary version 3.3 on both JVM, JS and native.

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
