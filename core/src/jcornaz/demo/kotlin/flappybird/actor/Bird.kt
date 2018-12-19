package jcornaz.demo.kotlin.flappybird.actor

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
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
import ktx.actors.onKeyDown

private const val HORIZONTAL_VELOCITY = 1.5f
private const val JUMP_VERTICAL_VELOCITY = 2.5f

class Bird(private val gameWorld: GameWorld, texture: TextureAtlas) : Actor(), Disposable, CoroutineScope by LibGdxScope() {
  private val flyingAnimation = Animation(1f / 12f, texture.findRegions("flying"))
  private val deadAnimation = Animation(1f / 5f, texture.findRegions("dead"))

  private var time = 0f

  private val body: Body get() = gameWorld.bird

  var isAlive = true
    private set

  init {
    val frame = flyingAnimation.getKeyFrame(0f)

    setSize(frame.regionWidth / PIXEL_PER_METER, frame.regionHeight / PIXEL_PER_METER)
    setPosition(0f, WORLD_HEIGHT / 2f, Align.center)

    body.setTransform(x, y, 0f)
    body.setLinearVelocity(HORIZONTAL_VELOCITY, 0f)

    onKeyDown {
      if (it == Input.Keys.SPACE && isAlive) {
        body.setLinearVelocity(HORIZONTAL_VELOCITY, JUMP_VERTICAL_VELOCITY)
      }
    }

    launch {
      gameWorld.openContactSubscription()
          .filterIsInstance<ContactEvent.ContactBegan>()
          .filter { body in it }
          .filter { event ->
            event.fixtures.none { it.isSensor }
          }
          .firstOrNull()
          ?: return@launch

      isAlive = false
    }
  }

  override fun act(delta: Float) {
    time += delta

    setPosition(body.position.x, body.position.y)
    rotation = body.angle * MathUtils.radiansToDegrees
  }

  override fun draw(batch: Batch, parentAlpha: Float) {

    val animation = if (isAlive) flyingAnimation else deadAnimation

    batch.draw(
        animation.getKeyFrame(time, true),
        x, y,
        originX, originY,
        width, height,
        scaleX, scaleY,
        rotation
    )
  }

  override fun dispose() {
    cancel()
  }
}
