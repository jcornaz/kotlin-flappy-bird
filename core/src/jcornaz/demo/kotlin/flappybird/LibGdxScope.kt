package jcornaz.demo.kotlin.flappybird

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ktx.async.KtxAsync

open class LibGdxScope : CoroutineScope {
  private val job = SupervisorJob()
  final override val coroutineContext = job + KtxAsync
}
