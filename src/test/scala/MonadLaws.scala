package scala

import org.scalacheck.Prop.*
import org.scalacheck.{Arbitrary, Prop, Properties}

import scala.Monads.Monad

object MonadLaws:
  def laws[M[_], A, B, C](
      name: String
  )(
    using monad: Monad[M],
    arbA: Arbitrary[A],
    arbM: Arbitrary[M[A]],
    arbF: Arbitrary[A => M[B]],
    arbG: Arbitrary[B => M[C]],
    eqM: (M[C], M[C]) => Boolean
  ): Properties =
    new Properties(s"Monad Laws for $name"):

      def eqProp[X](m1: M[X], m2: M[X])(eq: (M[X], M[X]) => Boolean): Prop =
        Prop(eq(m1, m2)) :| s"$m1 == $m2"

      property("left identity") =
        forAll: (a: A, f: A => M[B]) =>
          val lhs = monad.unit(a).flatMap(f)
          val rhs = f(a)
          eqProp(lhs, rhs)(eqM.asInstanceOf[(M[B], M[B]) => Boolean]) :| s"unit($a).flatMap(f) == f($a)"

      property("right identity") =
        forAll: (ma: M[A]) =>
          val lhs = ma.flatMap(monad.unit)
          val rhs = ma
          eqProp(lhs, rhs)(eqM.asInstanceOf[(M[A], M[A]) => Boolean])

      property("associativity") =
        forAll: (ma: M[A], f: A => M[B], g: B => M[C]) =>
          val lhs = ma.flatMap(f).flatMap(g)
          val rhs = ma.flatMap(a => f(a).flatMap(g))
          eqProp(lhs, rhs)(eqM)