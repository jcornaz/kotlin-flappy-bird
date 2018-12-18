package jcornaz.demo.kotlin.flappybird.actor

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.Actor
import jcornaz.demo.kotlin.flappybird.PIXEL_PER_METER
import jcornaz.demo.kotlin.flappybird.WORLD_WIDTH
import jcornaz.demo.kotlin.flappybird.physics.GameWorld

class Floor(private val body: Body, private val texture: Texture) : Actor() {

  init {
    setBounds(-WORLD_WIDTH / 2f, 0f, texture.width / PIXEL_PER_METER, texture.height / PIXEL_PER_METER)
    body.setTransform(x, y, 0f)
  }

  override fun positionChanged() {
    body.setTransform(x, y, 0f)
  }

  override fun draw(batch: Batch, parentAlpha: Float) {
    batch.draw(texture, x, y, width, height)
  }
}

fun floorCycle(gameWorld: GameWorld, texture: Texture) =
    Cycle(0f) { Floor(gameWorld.createFloor(), texture) }