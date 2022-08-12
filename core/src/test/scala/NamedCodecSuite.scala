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

import munit.FunSuite

class NamedCodecSuite extends FunSuite {

  val tcAdapter: CodecAdapter[TC, Int] = new CodecAdapter[TC, Int] {
    def encode[A: TC](a: A): Int = implicitly[TC[A]].enc(a)
    def decode[A: TC](r: Int): Either[String, A] =
      implicitly[TC[A]].dec(r).toRight("Invalid data!")
  }

  test("Codec from adapter") {
    given TypeName[Data.B.type] = TypeName("b")
    val codec = NamedCodec.from(tcAdapter).of[Data]

    assertEquals(codec.decode(EncodedMessage("A", 1)), Right(Data.A))
    assertEquals(codec.encode(Data.A), EncodedMessage("A", 1))

    assertEquals(codec.decode(EncodedMessage("b", 2)), Right(Data.B))
    assertEquals(codec.encode(Data.B), EncodedMessage("b", 2))

    assertEquals(codec.decode(EncodedMessage("C", 3)), Right(Data.C))
    assertEquals(codec.encode(Data.C), EncodedMessage("C", 3))
  }

  test("Codec from adapter with transformation") {
    val codec = NamedCodec.from(tcAdapter, _.toLowerCase).of[Data]

    assertEquals(codec.decode(EncodedMessage("a", 1)), Right(Data.A))
    assertEquals(codec.encode(Data.A), EncodedMessage("a", 1))

    assertEquals(codec.decode(EncodedMessage("b", 2)), Right(Data.B))
    assertEquals(codec.encode(Data.B), EncodedMessage("b", 2))

    assertEquals(codec.decode(EncodedMessage("c", 3)), Right(Data.C))
    assertEquals(codec.encode(Data.C), EncodedMessage("c", 3))
  }

  test("type name") {
    assertEquals(typeName[Data.A.type], "A")
    compileErrors("typeName[Data]")
  }
}

trait TC[A] {
  def enc(a: A): Int
  def dec(i: Int): Option[A]
}

enum Data {
  case A, B, C
}

object Data {
  given TC[A.type] = new {
    def enc(a: A.type) = 1
    def dec(i: Int) = Option.when(i == 1)(A)
  }
  given TC[B.type] = new {
    def enc(a: B.type) = 2
    def dec(i: Int) = Option.when(i == 2)(B)
  }
  given TC[C.type] = new {
    def enc(a: C.type) = 3
    def dec(i: Int) = Option.when(i == 3)(C)
  }
}
