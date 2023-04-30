package com.codingcrafters.prime

import com.codingcrafters.gameobjects.BaseGame
import com.codingcrafters.gameobjects.BaseGame.setActiveScreen
import com.codingcrafters.screens.SkinnedWardRobe
import com.codingcrafters.screens.ParticlesUniverseScreen

class ParticleUniversesGame extends BaseGame:
  // private def myGameScreen : GameMenuScreen = new GameMenuScreen()
  // lazy val myGameScreen : GameMenuScreen = new GameMenuScreen()
  lazy val myWardrobeScreen: SkinnedWardRobe = new SkinnedWardRobe()
  lazy val myUniversesScreen: ParticlesUniverseScreen =
    new ParticlesUniverseScreen()

  override def create(): Unit =
    super.create()
    //setActiveScreen(myWardrobeScreen)
    setActiveScreen(myUniversesScreen)
