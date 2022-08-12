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

final case class TypeName[T](value: String) extends AnyVal
object TypeName extends TypeNamePlatform

trait CodecAdapter[Enc[_], Dec[_], R] {
  def encode[A: Enc](a: A): R
  def decode[A: Dec](r: R): Either[String, A]
}

final case class EncodedMessage[R](
    name: DataTypeName,
    data: R
) {
  def rename(f: DataTypeName => DataTypeName): EncodedMessage[R] =
    copy(name = f(name))
}

trait NamedEncoder[A, R] { self =>
  def encode(a: A): EncodedMessage[R]

  final def contramap[B](f: B => A): NamedEncoder[B, R] =
    new NamedEncoder[B, R] {
      def encode(b: B): EncodedMessage[R] = self.encode(f(b))
    }
}

object NamedEncoder {
  def apply[A, R](f: A => EncodedMessage[R]): NamedEncoder[A, R] = new {
    def encode(a: A): EncodedMessage[R] = f(a)
  }
}

trait NamedDecoder[A, R] { self =>
  def decode(msg: EncodedMessage[R]): Either[String, A]
  def canDecode(msg: DataTypeName): Boolean

  final def map[B](f: A => B): NamedDecoder[B, R] = new NamedDecoder[B, R] {
    def decode(msg: EncodedMessage[R]): Either[String, B] =
      self.decode(msg).map(f)
    def canDecode(msg: DataTypeName): Boolean = self.canDecode(msg)
  }
}

trait NamedCodec[A, R] extends NamedEncoder[A, R], NamedDecoder[A, R] {
  self =>
  final def imap[B](fcon: B => A)(fcov: A => B): NamedCodec[B, R] =
    new NamedCodec[B, R] {
      def encode(b: B): EncodedMessage[R] = self.encode(fcon(b))
      def decode(msg: EncodedMessage[R]): Either[String, B] =
        self.decode(msg).map(fcov)
      def canDecode(msg: DataTypeName): Boolean = self.canDecode(msg)
    }
  final def eimap[B](
      fcon: B => A
  )(fcov: A => Either[String, B]): NamedCodec[B, R] =
    new NamedCodec[B, R] {
      def encode(b: B): EncodedMessage[R] = self.encode(fcon(b))
      def decode(msg: EncodedMessage[R]): Either[String, B] =
        self.decode(msg).flatMap(fcov)
      def canDecode(msg: DataTypeName): Boolean = self.canDecode(msg)
    }
}

object NamedCodec extends NamedCodecPlatform {
  def apply[A, R](
      enc: NamedEncoder[A, R],
      dec: NamedDecoder[A, R]
  ): NamedCodec[A, R] = new {
    export enc.*
    export dec.*
  }
}
