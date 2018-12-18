package jcornaz.demo.kotlin.flappybird.actor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Queue

class Cycle(private val spacing: Float, create: () -> Actor) : Group() {

  private val pool = object : Pool<Actor>() {
    override fun newObject(): Actor = create().also { addActor(it) }
  }

  private val actors = Queue<Actor>()

  init {
    actors.addLast(pool.obtain())
  }

  override fun act(delta: Float) {
    super.act(delta)

    val minX = stage.viewport.unproject(Vector2()).x
    val maxX = with(stage.viewport) { unproject(Vector2(screenWidth.toFloat(), 0f)).x }

    var left = actors.first()
    while (actors.size > 1 && left.x + left.width < minX) {
      pool.free(actors.removeFirst())
      left = actors.first()
    }

    var right = actors.last()
    while (right.x + right.width + spacing < maxX) {
      right = pool.obtain().apply {
        x = right.x + right.width + spacing
      }
      actors.addLast(right)
    }
  }
}