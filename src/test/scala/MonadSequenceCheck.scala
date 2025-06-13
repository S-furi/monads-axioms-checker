package scala

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen, Properties}

import scala.Monads.Monad
import scala.monads.Sequences.Sequence
import scala.monads.Sequences.Sequence.*

object MonadSequenceCheck:

  val sequenceMonadLaws: Properties = MonadLaws.laws[Sequence, Int, P2D, String](
    name = "Sequence"
  )(using
    summon[Monad[Sequence]],
    summon[Arbitrary[Int]],
    summon[Arbitrary[Sequence[Int]]],
    summon[Arbitrary[Int => Sequence[P2D]]],
    summon[Arbitrary[P2D => Sequence[String]]],
    sequenceEq
  )

  def sequenceEq[A](x: Sequence[A], y: Sequence[A]): Boolean = (x, y) match
    case (Cons(h1, t1), Cons(h2, t2)) => h1 == h2 && sequenceEq(t1, t2)
    case (Nil(), Nil())               => true
    case _                            => false

  def sequenceGen[A: Arbitrary](): Gen[Sequence[A]] = for
    i <- arbitrary[A]
    b <- Gen.prob(0.8)
    s <- if b then sequenceGen().map(s2 => Cons(i, s2)) else Gen.const(Nil())
  yield s

  given p2dArbitrary: Arbitrary[P2D] = Arbitrary(
    for
      a <- arbitrary[Double]
      b <- arbitrary[Double]
    yield P2D(a, b)
  )

  given intSeqArbitrary: Arbitrary[Sequence[Int]] = Arbitrary(sequenceGen[Int]())

  given p2dSeqArbitrary: Arbitrary[Sequence[P2D]] = Arbitrary(sequenceGen[P2D]())

  given arbIntToP2DSeq: Arbitrary[Int => Sequence[P2D]] = Arbitrary(
    Gen.oneOf[Int => Sequence[P2D]](
      x => Cons(P2D(x.toDouble, -x.toDouble ), Cons(P2D(-x.toDouble, x.toDouble), Nil())),
      x => Cons(P2D(x.toDouble, 0), Nil()),
      _ => Nil()
    )
  )

  given arbP2DToStrSeq: Arbitrary[P2D => Sequence[String]] = Arbitrary(
    Gen.oneOf[P2D => Sequence[String]](
      p => Cons(s"(${p.a}, ${p.b})", Nil()),
      p => if p.a > 0 && p.b > 0 then Cons("positive P2D", Nil()) else Nil(),
      _ => Nil()
    )
  )

  // Simple test class for not always using primitives checks
  case class P2D(a: Double, b: Double)

object CheckSequenceMonadLaws extends App:
    MonadLawsRunner.runCheks("SequenceMonad")(MonadSequenceCheck.sequenceMonadLaws)