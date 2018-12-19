package jcornaz.demo.kotlin.flappybird.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.graphics.use

class GameOverScreen(private val background: Screen, private val font: BitmapFont) : EndableScreen(), KtxScreen {

  private val batch = SpriteBatch()
  private val screenViewport = ScreenViewport()

  private var timeBeforeRestartEnabled = 1f

  private val gameOverLayout = GlyphLayout(
      font,
      "GAME OVER",
      font.color,
      0f,
      Align.center,
      false
  )

  private val messageLayout = GlyphLayout(
      font,
      "press SPACE to restart",
      font.color,
      0f,
      Align.center,
      false
  )

  override fun show() {
    Gdx.input.inputProcessor = object : KtxInputAdapter {
      override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.SPACE && timeBeforeRestartEnabled <= 0f) {
          notifyCompleted()
        }
        return false
      }
    }
  }

  override fun render(delta: Float) {
    timeBeforeRestartEnabled -= delta

    background.render(delta)

    screenViewport.apply()

    batch.use {
      font.draw(it, gameOverLayout, screenViewport.worldWidth / 2f, 3f * screenViewport.worldHeight / 4f)

      if (timeBeforeRestartEnabled <= 0) {
        font.draw(it, messageLayout, screenViewport.worldWidth / 2f, screenViewport.worldHeight / 2f)
      }
    }
  }

  override fun resize(width: Int, height: Int) {
    screenViewport.update(width, height)
  }

  override fun dispose() {
    batch.dispose()
  }
}