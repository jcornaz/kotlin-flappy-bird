package jcornaz.demo.kotlin.flappybird.actor

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Pool
import com.github.jcornaz.miop.filterIsInstance
import jcornaz.demo.kotlin.flappybird.LibGdxScope
import jcornaz.demo.kotlin.flappybird.PIXEL_PER_METER
import jcornaz.demo.kotlin.flappybird.WORLD_HEIGHT
import jcornaz.demo.kotlin.flappybird.physics.ContactEvent
import jcornaz.demo.kotlin.flappybird.physics.GameWorld
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.firstOrNull
import kotlinx.coroutines.launch


class Pipe(private val body: Body, texture: Texture, flipVertically: Boolean) : Actor() {
  private val sprite = Sprite(texture).apply { flip(false, flipVertically) }

  init {
    setSize(sprite.width / PIXEL_PER_METER, sprite.height / PIXEL_PER_METER)
  }

  public override fun positionChanged() {
    body.setTransform(localToStageCoordinates(Vector2()), 0f)
  }

  override fun draw(batch: Batch, parentAlpha: Float) {
    batch.draw(sprite, x, y, width, height)
  }
}

private const val MIN_Y = 0.4f
private const val MAX_Y = WORLD_HEIGHT

private const val MIN_OPEN = 0.5f
private const val MAX_OPEN = 1f

class PipeDoor(private val gameWorld: GameWorld, pipeTexture: Texture, private val score: () -> Unit) : Group(), Pool.Poolable, Disposable, CoroutineScope by LibGdxScope() {
  private val ceil = Pipe(gameWorld.createPipe(), pipeTexture, true).also { addActor(it) }
  private val floor = Pipe(gameWorld.createPipe(), pipeTexture, false).also { addActor(it) }

  private val scoreZone = gameWorld.createScoreZone()

  init {
    width = ceil.width
    x = 1f
    reset()
  }

  override fun positionChanged() {
    ceil.positionChanged()
    floor.positionChanged()
    scoreZone.setTransform(x + width / 2f, y, 0f)
  }

  override fun reset() {
    val openSize = random(MIN_OPEN, MAX_OPEN)
    val floorY = random(MIN_Y, MAX_Y - openSize)
    val ceilY = floorY + openSize

    floor.setPosition(0f, floorY, Align.topLeft)
    ceil.setPosition(0f, ceilY, Align.bottomLeft)

    launch {
      gameWorld.openContactSubscription()
          .filterIsInstance<ContactEvent.ContactEnded>()
          .filter { scoreZone in it }
          .filter { gameWorld.bird in it }
          .firstOrNull()
          ?: return@launch

      score()
    }
  }

  override fun dispose() {
    cancel()
  }
}

fun pipeDoorCycle(gameWorld: GameWorld, texture: Texture, score: () -> Unit) =
    Cycle(1f) { PipeDoor(gameWorld, texture, score) }