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


public class LevelScreen implements Screen {

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
    private TextureRegion[] levelTextureRegions;
    private GUI_element[] levelGUI;

    private BitmapFont font;

    private float totalPassetTime;

    public LevelScreen(SpaceShooter game) {
        this.game = game;
        this.manager = this.game.getAssetManager();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // FONT
        font = manager.get("EdgeOfTheGalaxyRegular-OVEa6.ttf");
        font.getData().setScale(0.08f);


        ///


        textureAtlas = manager.get("image.atlas", TextureAtlas.class);

        levelTextureRegions = new TextureRegion[5];
        levelGUI = new GUI_element[5];
        // init textures
        String Name = "icon_lvl";
        for (int i = 1; i <= 5; i++) {
            String NameLvL = Name + (char)(i + '0');
            levelTextureRegions[i-1] = textureAtlas.findRegion(NameLvL);
        }

        for (int i = 0; i < 5; i++) {
            levelGUI[i] = new GUI_element(i + 17 + i*8, WORLD_HEIGHT/2.0f, 8, 10, levelTextureRegions[i]);
        }

        background = textureAtlas.findRegion("BlueSpaceBackground");

        totalPassetTime = 0.0f;

        batch = new SpriteBatch();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        totalPassetTime += deltaTime;

        batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        font.draw(batch, "Choose level:", 20, 80);

        for (int i = 0; i < 1; i++) {
            levelGUI[i].Draw(batch);
        }

        float xTouch = Gdx.input.getX();
        float yTouch = Gdx.input.getY();

        Vector2 touchPoint = new Vector2(xTouch, yTouch);
        touchPoint = viewport.unproject(touchPoint);

        float x = touchPoint.x;
        float y = touchPoint.y;

        for (int i = 0; i < 1; i++) {

            if(levelGUI[i].Intersects(x, y) && totalPassetTime >= 0.1f) {
                if(Gdx.input.isTouched() ) {
                    this.dispose();
                    game.setScreen(new GameScreen(game,  i+1));
                }
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
    //    textureAtlas.dispose();
    }
}
