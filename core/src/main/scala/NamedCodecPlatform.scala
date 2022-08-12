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

import scala.compiletime.*
import scala.deriving.Mirror

import NamedCodecPlatform.Builder

transparent trait NamedCodecPlatform {
  def from[Enc[_], Dec[_], R](
      adapter: CodecAdapter[Enc, Dec, R]
  ): Builder[Enc, Dec, R] =
    new Builder(adapter)

  def from[Enc[_], Dec[_], R](
      adapter: CodecAdapter[Enc, Dec, R],
      transform: String => String
  ): Builder[Enc, Dec, R] = new Builder(adapter, Some(transform))
}

object NamedCodecPlatform {

  final class Builder[Enc[_], Dec[_], R](
      adapter: CodecAdapter[Enc, Dec, R],
      transform: Option[String => String] = None
  ) {
    inline def of[T](using m: Mirror.Of[T]): NamedCodec[T, R] =
      inline m match {
        case s: Mirror.SumOf[T]     => sumInst(s)
        case p: Mirror.ProductOf[T] => productInst(p)
      }

    private inline def getTypeName[T]: String =
      val tn = summonInline[TypeName[T]].value
      transform.getOrElse(identity[String]).apply(tn)

    private inline def productInst[T](
        m: Mirror.ProductOf[T]
    ): NamedCodec[T, R] = {
      val mt = getTypeName[T]
      val encoder: Enc[T] = summonInline[Enc[T]]
      val decoder: Dec[T] = summonInline[Dec[T]]

      new NamedCodec[T, R] {
        def encode(t: T): EncodedMessage[R] =
          EncodedMessage(mt, adapter.encode(t)(using encoder))
        def decode(msg: EncodedMessage[R]): Either[String, T] =
          if canDecode(msg.name) then adapter.decode(msg.data)(using decoder)
          else Left("Invalid message type")
        def canDecode(msg: DataTypeName): Boolean = msg == mt
      }
    }

    private inline def summonAll[T <: Tuple]: List[NamedCodec[?, R]] =
      inline erasedValue[T] match {
        case _: EmptyTuple => Nil
        case _: (h *: t) =>
          of(using summonInline[Mirror.Of[h]]) +: summonAll[t]
      }

    private inline def sumInst[T](m: Mirror.SumOf[T]): NamedCodec[T, R] = {
      val codecs =
        summonAll[m.MirroredElemTypes].asInstanceOf[List[NamedCodec[T, R]]]

      new NamedCodec[T, R] {
        def encode(t: T): EncodedMessage[R] =
          codecs(m.ordinal(t)).encode(t)

        def decode(msg: EncodedMessage[R]): Either[String, T] =
          getDecoder(msg.name)
            .toRight(s"Unknown message type ${msg.name}")
            .flatMap(_.decode(msg))

        def canDecode(msg: DataTypeName): Boolean = getDecoder(msg).isDefined

        private def getDecoder(msg: DataTypeName): Option[NamedCodec[T, R]] =
          codecs.find(_.canDecode(msg))
      }
    }
  }

}
