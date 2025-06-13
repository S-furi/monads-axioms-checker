package scala.types

import scala.Monads.Monad

object Sequences:
  enum Sequence[E]:
    case Cons(head: E, tail: Sequence[E])
    case Nil()

  object Sequence:
    extension [A](s1: Sequence[A])
      def append(s2: Sequence[A]): Sequence[A] = s1 match
        case Cons(h, t) => Cons(h, t.append(s2))
        case Nil()      => s2

  given Monad[Sequence] with
    import Sequence.*

    def unit[A](a: A): Sequence[A] = Cons(a, Nil())

    extension [A](m: Sequence[A])
      def flatMap[B](f: A => Sequence[B]): Sequence[B] =
        m match
          case Cons(h, t) => f(h).append(t.flatMap(f))
          case Nil()      => Nil()
