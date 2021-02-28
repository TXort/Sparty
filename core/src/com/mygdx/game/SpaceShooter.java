package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

public class SpaceShooter extends Game  {

    private final AssetManager manager = new AssetManager();
    private boolean RunOnce = false;

    @Override
    public void create() {
       manager.load("image.atlas", TextureAtlas.class);

       FileHandleResolver resolver = new InternalFileHandleResolver();
       manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
       manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
       manager.load("DeepSpaceA.mp3", Music.class);

        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = "EdgeOfTheGalaxyRegular-OVEa6.ttf";
        fontParameter.fontParameters.size = 72;
        fontParameter.fontParameters.borderWidth = 1.6f;
        fontParameter.fontParameters.color = new Color(255, 255, 255, 0.3f);
        fontParameter.fontParameters.borderColor = new Color(0, 0, 0, 0.3f);

        manager.load("EdgeOfTheGalaxyRegular-OVEa6.ttf", BitmapFont.class, fontParameter);

       manager.finishLoading();
       setScreen(new MenuScreen(this));
    }

    public AssetManager getAssetManager() {
        return manager;
    }

    public boolean IsRunOnce() { return RunOnce; }

    public void Run() { RunOnce = true; }

    @Override
    public void render() {
        super.render();
    }

    public void dispose() {
        super.dispose();
    }

}

/* TO DO
*   - GUI
*   - MENU
*   - MUSIC
*   - SOUNDS
*   - EXPLOSIONS (Animation vs Particles)
*   - DROPS
*   - BETTER TEXTURE ATLAS
*   - ADD CIRCLE ENEMIES AND DROPS
*   - DYNAMIC SHIP ANIMATIONS
*   - SHOP AND MONEY SYSTEM
*   - ENEMY FIRE PATTERNS
*   - FIND TEXTURES AND ATLAS THEM
*   - LEVEL SYSTEM WITH SELECT LEVELS
*   - PAUSE SYSTEM WITH SHOP
*   - REMEMBER PURCHASED UPGRADES
* */


