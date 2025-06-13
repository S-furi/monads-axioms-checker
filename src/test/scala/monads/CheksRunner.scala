package scala.monads

object CheksRunner extends App:
  Map(
    "scala.Option" -> MonadOptionCheck.optionMonadLaws,
    "Optional" -> MonadOptionalCheck.optionalMonadLaws,
    "Sequence" -> MonadSequenceCheck.sequenceMonadLaws,
  ) foreach: (name, props)=>
    MonadLawsRunner.runCheks(name)(props)
    println("=" * 50)
