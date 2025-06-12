package scala

import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalacheck.Prop.forAll

import scala.Monads.Monad

object MonadCheks extends Properties("Monad"):
  def monadGen[M[_]: Monad, A: Arbitrary](): Gen[M[A]] =
    Arbitrary.arbitrary[A].flatMap(a => summon[Monad[M]].unit(a))