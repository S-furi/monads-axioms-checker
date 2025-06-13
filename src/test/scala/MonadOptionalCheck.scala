package scala

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen, Properties}

import scala.Monads.Monad
import scala.monads.Optionals.Optional
import scala.monads.Optionals.Optional.{Empty, Just}

object MonadOptionalCheck:

  val optionalMonadLaws: Properties = MonadLaws.laws[Optional, Int, String, Boolean](
    name = "Optional"
  )(using
    summon[Monad[Optional]],
    summon[Arbitrary[Int]],
    summon[Arbitrary[Optional[Int]]],
    summon[Arbitrary[Int => Optional[String]]],
    summon[Arbitrary[String => Optional[Boolean]]],
    optionalEq
  )

  def optionalEq[A](x: Optional[A], y: Optional[A]): Boolean = (x, y) match
    case (Just(a1), Just(a2)) => a1 == a2
    case (Empty(), Empty())   => true
    case _                    => false

  given arbOptInt: Arbitrary[Optional[Int]]      = optionalArbitrary[Int]
  given arbOptStr: Arbitrary[Optional[String]]   = optionalArbitrary[String]
  given arbOptBool: Arbitrary[Optional[Boolean]] = optionalArbitrary[Boolean]

  def optionalArbitrary[A: Arbitrary]: Arbitrary[Optional[A]] = Arbitrary(
    Gen.frequency(
      1 -> Gen.const(Empty()),
      9 -> arbitrary[A].map(Just(_))
    )
  )

  given Arbitrary[Int => Optional[String]] = Arbitrary(
    Gen.oneOf(
      (x: Int) => Just(x.toString),
      (x: Int) => if x % 2 == 0 then Just("even") else Empty(),
      (_: Int) => Empty().asInstanceOf[Optional[String]]
    )
  )

  given arbStrToOptBool: Arbitrary[String => Optional[Boolean]] = Arbitrary(
    Gen.oneOf(
      (s: String) => Just(s.nonEmpty),
      (s: String) => if s.length > 5 then Just(true) else Empty(),
      (_: String) => Empty().asInstanceOf[Optional[Boolean]]
    )
  )

object CheckOptionalMonadLaws extends App:
  MonadLawsChecker.runCheks("OptionalMonad")(MonadOptionalCheck.optionalMonadLaws)
