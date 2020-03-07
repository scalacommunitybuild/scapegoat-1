package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class NegationIsEmpty extends Inspection(
  text = "!isEmpty can be replaced with nonEmpty",
  defaultLevel = Levels.Info,
  description = "Checks whether !isEmpty can be replaced with nonEmpty.",
  explanation = "!Traversable.isEmpty can be replaced with Traversable.nonEmpty to make it easier to reason about."
) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val IsEmpty = TermName("isEmpty")
      private val Bang = TermName("unary_$bang")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Select(lhs, IsEmpty), Bang) if isTraversable(lhs) =>
            context.warn(tree.pos, self)
          case _ => continue(tree)
        }
      }
    }
  }
}
