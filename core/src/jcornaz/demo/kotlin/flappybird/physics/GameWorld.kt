package jcornaz.demo.kotlin.flappybird.physics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable
import com.codeandweb.physicseditor.PhysicsShapeCache
import jcornaz.demo.kotlin.flappybird.PIXEL_PER_METER
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import ktx.box2d.body
import ktx.box2d.createWorld
import ktx.box2d.earthGravity

private const val STEP_TIME = 1f / 60f

sealed class ContactEvent {
  abstract val fixtures: Sequence<Fixture>

  operator fun contains(body: Body): Boolean =
      fixtures.any { it.body == body }

  class ContactBegan(override val fixtures: Sequence<Fixture>) : ContactEvent()
  class ContactEnded(override val fixtures: Sequence<Fixture>) : ContactEvent()
}

class GameWorld : Disposable {

  val world = createWorld(earthGravity)
  private val shapeCache = PhysicsShapeCache(Gdx.files.internal("physics.xml"))

  private var timeSinceLastStep = 0f

  private val broadcast = BroadcastChannel<ContactEvent>(16)

  val bird = world.createBody(shapeCache, "bird")

  init {
    world.setContactListener(object : ContactListener {
      override fun beginContact(contact: Contact) {
        broadcast.sendBlocking(
            ContactEvent.ContactBegan(sequenceOf(contact.fixtureA, contact.fixtureB))
        )
      }

      override fun endContact(contact: Contact) {
        broadcast.sendBlocking(
            ContactEvent.ContactEnded(sequenceOf(contact.fixtureA, contact.fixtureB))
        )
      }

      override fun preSolve(contact: Contact?, oldManifold: Manifold?) = Unit
      override fun postSolve(contact: Contact?, impulse: ContactImpulse?) = Unit
    })
  }

  fun createFloor() = world.createBody(shapeCache, "ground")
  fun createPipe() = world.createBody(shapeCache, "pipe")
  fun createScoreZone() = world.body {
    box(0.25f, 2.5f, Vector2(0f, 1.25f)) {
      filter.groupIndex = 1
      isSensor = true
    }
  }

  fun step(deltaTime: Float) {
    timeSinceLastStep += deltaTime

    while (timeSinceLastStep >= STEP_TIME) {
      world.step(STEP_TIME, 5, 2)
      timeSinceLastStep -= STEP_TIME
    }
  }

  fun openContactSubscription() = broadcast.openSubscription()

  override fun dispose() {
    world.dispose()
  }
}

fun World.createBody(shapeCache: PhysicsShapeCache, name: String): Body =
    shapeCache.createBody(name, this, 1f / PIXEL_PER_METER, 1f / PIXEL_PER_METER)

