package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class NoOpOverride extends Inspection("No op Override", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def argumentsMatch(signatureArgs: List[ValDef], actualArgs: List[Tree]) = {
        signatureArgs.size == actualArgs.size &&
         signatureArgs.zip(actualArgs).forall {
          case (sig, act: Ident) => sig.name == act.name
          case _ => false
         }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case DefDef(mods, name, _, vparamss, _, Apply(Select(Super(This(_), _), name2), args))
            if name == name2 && vparamss.size == 1 && argumentsMatch(vparamss.head, args) =>
              context.warn(tree.pos, self,
                "This method is overridden yet only calls super: " + tree.toString().take(200))
          case _ => continue(tree)
        }
      }
    }
  }
}
