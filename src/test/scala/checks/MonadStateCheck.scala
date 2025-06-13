package scala.checks

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

import scala.Monads.Monad
import scala.monads.States.State

object MonadStateCheck:

  val stateMonadLaws = MonadLaws.laws[[A] =>> State[Double, A], Int, String, Boolean](
    name = "State"
  )(using
    summon[Monad[[A] =>> State[Double, A]]],
    summon[Arbitrary[Int]],
    summon[Arbitrary[State[Double, Int]]],
    summon[Arbitrary[Int => State[Double, String]]],
    summon[Arbitrary[String => State[Double, Boolean]]],
    stateEq
  )

  private val ntests = 20
  def stateEq[S: Arbitrary, A](x: State[S, A], y: State[S, A]): Boolean =
    List.fill(ntests)(implicitly[Arbitrary[S]].arbitrary.sample).flatten.forall { s =>
      val (s1, a1) = x.run(s)
      val (s2, a2) = y.run(s)
      s1 == s2 && a1 == a2
    }

  given arbStateDoubleInt: Arbitrary[State[Double, Int]] = Arbitrary(
    Gen.frequency(
      2 -> Gen.const(State(s => (s, 0))),
      8 -> arbitrary[Double].map(d => State(s => (s + d, d.toInt)))
    )
  )

  given mapperIntToStateStr: Arbitrary[Int => State[Double, String]] = Arbitrary(
    Gen.oneOf(
      (x: Int) => State((s: Double) => (s + x, s"Int: $x")),
      (x: Int) => State((s: Double) => (s - x.toDouble, s"Negative Int: ${-x}")),
      (_: Int) => State((s: Double) => (s, ""))
    )
  )

  given mapperStrToStateBool: Arbitrary[String => State[Double, Boolean]] = Arbitrary(
    Gen.oneOf(
      (s: String) => State((d: Double) => (d + s.length, d % 2 == 0)),
      (s: String) => State((d: Double) => (d - s.length, s.isEmpty)),
      (_: String) => State((d: Double) => (0.0, true))
    )
  )

object CheckStateMonadLaws extends App:
  MonadLawsChecker.runCheks("StateMonad")(MonadStateCheck.stateMonadLaws)