package jcornaz.demo.kotlin.flappybird

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import ktx.async.KtxAsync


open class LibGdxScope : CoroutineScope {
  private val job = Job()
  override val coroutineContext = job + KtxAsync
}
