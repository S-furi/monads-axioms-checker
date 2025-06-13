package scala.monads

object MonadLawsCheksRunner extends App:
  Map(
    "scala.Option" -> MonadOptionCheck.optionMonadLaws,
    "Optional" -> MonadOptionalCheck.optionalMonadLaws,
    "Sequence" -> MonadSequenceCheck.sequenceMonadLaws,
  ) foreach: (name, props)=>
    MonadLawsChecker.runCheks(name)(props)
    println("=" * 50)
