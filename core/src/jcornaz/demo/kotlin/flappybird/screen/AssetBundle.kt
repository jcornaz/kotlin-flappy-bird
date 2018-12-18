package jcornaz.demo.kotlin.flappybird.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ktx.async.assets.AssetStorage
import ktx.freetype.async.loadFreeTypeFont

data class AssetBundle(
    val birdTexture: TextureAtlas,
    val floorTexture: Texture,
    val pipeTexture: Texture,
    val font: BitmapFont
) {

  companion object {
    suspend fun load(assetStorage: AssetStorage) = AssetBundle(
        birdTexture = assetStorage.load("bird-animations.atlas"),
        floorTexture = assetStorage.load("floor.png"),
        pipeTexture = assetStorage.load("pipe.png"),
        font = assetStorage.loadFreeTypeFont("BradBunR.ttf") {
          size = 60
          color = Color.FIREBRICK
          shadowOffsetX = 3
          shadowOffsetY = 3
        }
    )
  }
}
