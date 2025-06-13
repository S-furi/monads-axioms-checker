package scala.checks

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen, Properties}

import scala.Monads.Monad

object MonadOptionCheck:

  val optionMonadLaws: Properties = MonadLaws.laws[Option, Int, String, Boolean](
    name = "Option"
  )(using
    summon[Monad[Option]],
    summon[Arbitrary[Int]],
    summon[Arbitrary[Option[Int]]],
    summon[Arbitrary[Int => Option[String]]],
    summon[Arbitrary[String => Option[Boolean]]],
    optionEq
  )

  def optionEq[A](x: Option[A], y: Option[A]): Boolean = x == y

  def optionArbitrary[A: Arbitrary]: Arbitrary[Option[A]] = Arbitrary(
    Gen.frequency(
      1 -> Gen.const(None),
      9 -> arbitrary[A].map(Some(_))
    )
  )

  given arbOptInt: Arbitrary[Option[Int]] = optionArbitrary[Int]
  given arbOptStr: Arbitrary[Option[String]] = optionArbitrary[String]
  given arbOptBool: Arbitrary[Option[Boolean]] = optionArbitrary[Boolean]

  given Arbitrary[Int => Option[String]] = Arbitrary(
    Gen.oneOf(
      (x: Int) => Some(x.toString),
      (x: Int) => if x % 2 == 0 then Some("even") else None,
      (_: Int) => None
    )
  )

  given arbStrToOptBool: Arbitrary[String => Option[Boolean]] = Arbitrary(
    Gen.oneOf(
      (s: String) => Some(s.nonEmpty),
      (s: String) => if s.length > 5 then Some(true) else None,
      (_: String) => None
    )
  )

  given Monad[Option] with
    def unit[A](a: A): Option[A] = Some(a)
    extension [A](m: Option[A])
      def flatMap[B](f: A => Option[B]): Option[B] = m match
        case Some(value) => f(value)
        case None        => None

object CheckOptionMonadLaws extends App:
  MonadLawsChecker.runCheks("OptionMonad")(MonadOptionCheck.optionMonadLaws)