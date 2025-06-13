## monads-axioms-checker

A simple scalacheck framework for checking Monad's axioms on custom monadic types.

## Structure

The project is a simple SBT project with a custom `Monad` definition, taken
from the lectures and lab activities of *Advanced Software Modeling and Design*
course. Inside the package `monads` lie the implementations of some monads:

- `Optional[A]`
- `Sequence[A]`
- `State[S, A]`

### ScalaCheck suite

We identify three main components:

- `MonadLaws`: the actual property checking of monad's axioms. The function
    uses the actual monad type as a contextual parameter, accepting some producers,
    namely `Arbitrary` generators: a producer for monadic object base type, a
    producer of such monads, a producer of function from the monadic object base
    type and an arbitrary value, and another producer of function from the latter
    arbitrary value and another arbitrary value. Lastly, an equality operator is
    requested in order to properly compute monads axioms, which are:
    1. Left Identity
    2. Right Identity
    3. Associativity
- `checks.Monad<Type>Check.scala`: the actual `Type` definition of the
    generators requested from the `MonadLaws` object. Generators are kept rather
    simple, but here the designer of monads can also showcase some usecases of
    their custom monadic type with more complex data generation.
- `MonadLawsCheksRunner`: a simple executable object that runs the available
    collection of monadic types checks.

### Usage

Just run `MonadLawsCheksRunner` to check properties of all implemented monadic
types, or run them individually with the runners provided in files
`checks.Monad<Type>Check.scala`.
