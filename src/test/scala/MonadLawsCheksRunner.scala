package scala

import scala.monads.checks.{MonadOptionCheck, MonadOptionalCheck, MonadSequenceCheck, MonadStateCheck}

object MonadLawsCheksRunner extends App:
  Map(
    "scala.Option" -> MonadOptionCheck.optionMonadLaws,
    "Optional" -> MonadOptionalCheck.optionalMonadLaws,
    "Sequence" -> MonadSequenceCheck.sequenceMonadLaws,
    "State" -> MonadStateCheck.stateMonadLaws,
  ) foreach: (name, props)=>
    MonadLawsChecker.runCheks(name)(props)
    println("=" * 50)
