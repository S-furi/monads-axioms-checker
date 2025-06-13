package scala

import checks.{MonadOptionCheck, MonadOptionalCheck, MonadSequenceCheck, MonadStateCheck}

object MonadLawsChecksRunner extends App {
  Map(
    "OptionalMonad" -> MonadOptionalCheck.optionalMonadLaws,
    "OptionMonad"   -> MonadOptionCheck.optionMonadLaws,
    "SequenceMonad" -> MonadSequenceCheck.sequenceMonadLaws,
    "StateMonad"    -> MonadStateCheck.stateMonadLaws
  ).foreach { case (name, props) =>
     MonadLawsChecker.runCheks(name)(props)
     println("=" * 50)
  }
}