/*
 * Copyright 2022 Hossein Naderi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.hnaderi.namedcodec

import io.circe.Json
import io.circe.generic.auto.*
import munit.FunSuite

class CirceAdapterSuite extends FunSuite {

  test("Codec from adapter") {
    val codec = CirceAdapter.of[Data]

    assertEquals(codec.decode(EncodedMessage("A", Json.obj())), Right(Data.A))
    assertEquals(codec.encode(Data.A), EncodedMessage("A", Json.obj()))

    val bJson = Json.obj("i" -> Json.fromInt(1))
    assertEquals(codec.decode(EncodedMessage("B", bJson)), Right(Data.B(1)))
    assertEquals(codec.encode(Data.B(1)), EncodedMessage("B", bJson))

    val cJson =
      Json.obj("i" -> Json.fromInt(10), "s" -> Json.fromString("string"))
    assertEquals(
      codec.decode(EncodedMessage("C", cJson)),
      Right(Data.C("string", 10))
    )
    assertEquals(codec.encode(Data.C("string", 10)), EncodedMessage("C", cJson))
  }
}

enum Data {
  case A
  case B(i: Int)
  case C(s: String, i: Int)
}
