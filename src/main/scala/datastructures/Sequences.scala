package scala.datastructures

object Sequences:
  
  enum Sequence[E]:
    case Cons(head: E, tail: Sequence[E])
    case Nil()

  object Sequence:

    def of[A](n: Int, a: A): Sequence[A] =
      if (n == 0) then Nil[A]() else Cons(a, of(n - 1, a))

    extension (s: Sequence[Int])
      def sum: Int = s match
        case Cons(h, t) => h + t.sum
        case _          => 0

      def flatMap[B](mapper: Int => Sequence[B]): Sequence[B] = s match
        case Cons(h, t) => mapper(h) match
          case Cons(h2, t2) => Cons(h2, t.flatMap(mapper))
          case Nil()       => t.flatMap(mapper)
        case Nil()      => Nil()

      def size: Int = s match
        case Cons(_, t) => 1 + t.size
        case Nil()      => 0

    extension [A](s: Sequence[A])

      def map[B](mapper: A => B): Sequence[B] = s match
        case Cons(h, t) => Cons(mapper(h), t.map(mapper))
        case Nil()      => Nil()

      def filter(pred: A => Boolean): Sequence[A] = s match
        case Cons(h, t) if pred(h) => Cons(h, t.filter(pred))
        case Cons(_, t)            => t.filter(pred)
        case Nil()                 => Nil()

@main def trySequences() =
  import Sequences.*
  import Sequence.*
  
  val seq = Cons(10, Cons(20, Cons(30, Nil())))
  println(Sequence.sum(seq)) // 60
  println(seq.filter(_ >= 20).map(_ + 1).sum) // 21+31 = 52
  println(sum(map(filter(seq)(_ >= 20))(_ + 1))) // equally possible
