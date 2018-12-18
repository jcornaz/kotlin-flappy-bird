package jcornaz.demo.kotlin.flappybird

import com.badlogic.gdx.Screen
import com.badlogic.gdx.physics.box2d.Box2D
import jcornaz.demo.kotlin.flappybird.screen.AssetBundle
import jcornaz.demo.kotlin.flappybird.screen.EndableScreen
import jcornaz.demo.kotlin.flappybird.screen.GameOverScreen
import jcornaz.demo.kotlin.flappybird.screen.MainScreen
import jcornaz.demo.kotlin.flappybird.screen.StartingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ktx.app.KtxGame
import ktx.async.KtxAsync
import ktx.async.assets.AssetStorage
import ktx.async.enableKtxCoroutines
import ktx.freetype.async.registerFreeTypeFontLoaders

class KotlinFlappyBirdGame : KtxGame<Screen>(), CoroutineScope {

  private val job = Job()
  override val coroutineContext = job + KtxAsync

  private lateinit var assetStorage: AssetStorage

  override fun create() {
    enableKtxCoroutines(1)
    Box2D.init()

    assetStorage = AssetStorage().apply {
      registerFreeTypeFontLoaders()
    }

    launch {
      val bundle = AssetBundle.load(assetStorage)

      do {
        val mainScreen = MainScreen(bundle)

        show(StartingScreen(mainScreen, bundle.font))
        show(mainScreen)
        show(GameOverScreen(mainScreen, bundle.font))

      } while (isActive)
    }
  }

  private suspend inline fun <reified T : EndableScreen> show(screen: T) {
    addScreen(screen)
    setScreen<T>()
    screen.awaitEnd()
    removeScreen<T>()
  }

  override fun dispose() {
    job.cancel()
    assetStorage.dispose()
  }
}
