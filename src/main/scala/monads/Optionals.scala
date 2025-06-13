package scala.monads

import scala.Monads.Monad

object Optionals:
  enum Optional[A]:
    case Just(a: A)
    case Empty()

  object Optional:
    extension [A](m: Optional[A])
      def filter(p: A => Boolean): Optional[A] = m match
        case Just(a) if p(a) => Just(a)
        case _ => Empty()


  given Monad[Optional] with
    import Optional.{Empty, Just}

    def unit[A](a: A): Optional[A] = Just(a)

    extension [A](m: Optional[A])
      override def flatMap[B](f: A => Optional[B]): Optional[B] =
        m match
          case Just(a) => f(a)
          case Empty() => Empty()