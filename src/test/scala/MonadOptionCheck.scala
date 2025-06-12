package scala

import org.scalacheck.{Arbitrary, Cogen, Gen, Properties, Test}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.*

import scala.Monads.Monad

object MonadOptionCheck:

  given Monad[Option] with
    def unit[A](a: A): Option[A] = Some(a)
    extension [A](m: Option[A])
      def flatMap[B](f: A => Option[B]): Option[B] = m match
        case Some(value) => f(value)
        case None        => None

  def optionEq[A](x: Option[A], y: Option[A]): Boolean = x == y

  given Cogen[Int] = Cogen.cogenInt
  given Cogen[String] = Cogen.cogenString
  given Arbitrary[String] = Arbitrary(arbitrary[String])
  given Arbitrary[Boolean] = Arbitrary(arbitrary[Boolean])

  given Arbitrary[Option[Int]] = Arbitrary(
    Gen.frequency(
      1 -> Gen.const(None),
      9 -> arbitrary[Int].map(Some(_))
    )
  )


  given arbOptStr: Arbitrary[Option[String]] = Arbitrary(
    Gen.frequency(
      1 -> Gen.const(None),
      9 -> arbitrary[String].map(Some(_))
    )
  )

  given arbOptBool: Arbitrary[Option[Boolean]] = Arbitrary(arbitrary[Option[Boolean]])

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

  val optionMonadLaws: Properties = MonadLaws.laws[Option, Int, String, Boolean](
    name = "Option"
  )(
    using summon[Monad[Option]],
    summon[Arbitrary[Int]],
    summon[Arbitrary[Option[Int]]],
    summon[Arbitrary[Int => Option[String]]],
    summon[Arbitrary[String => Option[Boolean]]],
    optionEq
  )

object CheckOptionMonadLaws extends App:
  import org.scalacheck.Test
  import org.scalacheck.Test.Parameters

  val params = Parameters.default.withMinSuccessfulTests(100)

  MonadOptionCheck.optionMonadLaws.properties.foreach { case (name, prop) =>
    println(s"Checking property: $name")
    val result = Test.check(params, prop)
    println:
      s"Property '$name' ${if result.passed then "passed ✅" else "failed ❌"} with ${result.succeeded} tests (discarded: ${result.discarded})"
  }