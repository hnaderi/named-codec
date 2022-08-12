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
import io.circe.syntax.*
import io.circe.Encoder
import io.circe.Decoder

object CirceAdapter extends CodecAdapter[Encoder, Decoder, Json] {
  def decode[A: Decoder](r: Json): Either[String, A] =
    r.as[A].left.map(_.getMessage)
  def encode[A: Encoder](a: A): Json = a.asJson
}
