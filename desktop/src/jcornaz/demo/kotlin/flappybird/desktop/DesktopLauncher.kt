@file:JvmName("DesktopLauncher")

package jcornaz.demo.kotlin.flappybird.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import jcornaz.demo.kotlin.flappybird.KotlinFlappyBirdGame


fun main() {
  val config = LwjglApplicationConfiguration().apply {
    width *= 2
    height *= 2
    vSyncEnabled = false
  }

  LwjglApplication(KotlinFlappyBirdGame(), config)
}
