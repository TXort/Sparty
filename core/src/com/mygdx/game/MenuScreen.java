package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class MenuScreen implements Screen {

    SpaceShooter game;
    AssetManager manager;
    Batch batch;
    // Screen
    private Camera camera;
    private Viewport viewport;

    private final int WORLD_WIDTH = 72;
    private final int WORLD_HEIGHT = 128;

    private TextureRegion background;

    private TextureAtlas textureAtlas;
    private TextureRegion PlayButtonTexture;
    private GUI_element PlayButton;

    private BitmapFont font;

    public MenuScreen(SpaceShooter game) {
        this.game = game;
        this.manager = this.game.getAssetManager();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

 //       textureAtlas = new TextureAtlas("image.atlas");
        textureAtlas = manager.get("image.atlas", TextureAtlas.class);

        // FONT
       font = manager.get("EdgeOfTheGalaxyRegular-OVEa6.ttf");
       font.getData().setScale(0.08f);


        ///

        // init textures
        PlayButtonTexture = textureAtlas.findRegion("Start_BTN");
        background = textureAtlas.findRegion("BlueSpaceBackground");

        PlayButton = new GUI_element(WORLD_WIDTH/2.0f, WORLD_HEIGHT/2.0f, 15, 10, PlayButtonTexture);
        batch = new SpriteBatch();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        PlayButton.Draw(batch);

        font.draw(batch, "Game made by Ort", 14, 50);
        font.draw(batch, "Version Alpha 1.2", 15, 30);
        font.draw(batch, "1.2 Update:\n - Improved hitboxes\n", 2, 124);

        float x = (float)Gdx.input.getX();
        float y = (float)Gdx.input.getY();

        Vector2 touchPoint = new Vector2(x, y);
        touchPoint = viewport.unproject(touchPoint);

        x = touchPoint.x;
        y = touchPoint.y;

        if(PlayButton.Intersects(x, y)) {
            PlayButton.texture = PlayButtonTexture;
            if(Gdx.input.isTouched()) {
                this.dispose();
                game.setScreen(new LevelScreen(game));
            }
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
