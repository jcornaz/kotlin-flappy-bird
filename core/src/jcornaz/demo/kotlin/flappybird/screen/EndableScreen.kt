package jcornaz.demo.kotlin.flappybird.screen

import com.badlogic.gdx.Screen
import kotlinx.coroutines.CompletableDeferred

abstract class EndableScreen : Screen {
  private val end = CompletableDeferred<Unit>()

  protected fun notifyCompleted() {
    end.complete(Unit)
  }

  suspend fun awaitEnd() = end.await()
}