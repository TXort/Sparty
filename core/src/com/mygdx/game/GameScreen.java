package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;

class GameScreen implements Screen {

    AssetManager manager;
    SpaceShooter game;
    //music
    private Music music;
    private Sound laser, destroy, rocketS;


    //stars
    private ArrayList<Star> alStars;
    private Random rand;

    //screen
    private Camera camera;
    private Viewport viewport;

    //gpu
    private TextureRegion background, bullet0, tExplosion, enemy0, star0, player0, healthIcon, backbutton, HPPowerTexture, ENPowerTexture, Shuriken, AMPowerTexture, SHPowerTexture, forwardbutton, RCPowerTexture, overlordSmall;
    private TextureRegion[] ArrayEnemyTextures;

    private SpriteBatch batch;
    private TextureAtlas textureAtlas;

    //timing
    private float fPlayerReloadTimer = 0.0f;
    private float fWorldTime = 0.0f;
    private float time_since_touch = 0.0f;
    private int state = 1;

    //world parameters
    private final int WORLD_WIDTH = 72;
    private final int WORLD_HEIGHT = 128;
    private final float TOUCH_MOVEMENT_THRESHOLD = 0.5f;
    private final float TOUCH_THRESHOLD = 0.1f;

    float total_damage;
    boolean DeveloperMode;

    private BitmapFont font;

    GUI_element HP_Icon, BK_Icon, SP_Icon, UP_Hull, UP_Engine, UP_Guns, UP_GunSystems, UP_Drone, UP_Rockets;

    Movement_and_fire_patterns MP;

    //Objects
    PlayerShip playerShip;
    LinkedList<Bullet> friendlyBullets;
    LinkedList<Bullet> enemyBullets;
    LinkedList<Ship> NormalShips;
    LinkedList<Ship> FriendyShips;
    LinkedList<Explosion> explosions;
    LinkedList<Pair> levelSpawns;
    LinkedList<PowerUp> powerUps;
    HashMap<String, TextureRegion> TextureMap;

    String sDynamicShop;

    FileHandle Fil;

    public GameScreen(SpaceShooter game, int level) {

        DeveloperMode = false;

        if(DeveloperMode) {
            Fil = Gdx.files.local("Points.txt");
            Fil.writeString("\n" ,false);
        }


        this.game = game;
        this.manager = this.game.getAssetManager();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        textureAtlas = manager.get("image.atlas");

        MP = new Movement_and_fire_patterns(textureAtlas, !this.game.IsRunOnce());
        this.game.Run();

        // Music
        music = manager.get("DeepSpaceA.mp3");
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();
        laser = Gdx.audio.newSound(Gdx.files.internal("laser.mp3"));
        rocketS = Gdx.audio.newSound(Gdx.files.internal("rocketP.mp3"));
        destroy = Gdx.audio.newSound(Gdx.files.internal("DestroyedStones.mp3"));

        // init Random
        rand = new Random();

        // init Lists
        friendlyBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        NormalShips = new LinkedList<>();
        FriendyShips = new LinkedList<>();
        explosions = new LinkedList<>();
        levelSpawns = new LinkedList<>();
        powerUps = new LinkedList<>();
        TextureMap = new HashMap<>();

        TextureRegion EnemyPatrolTexture = textureAtlas.findRegion("ship", 3);
        TextureRegion EnemyGuardTexture = textureAtlas.findRegion("ship", 8);
        TextureRegion EnemyCrusierTexture = textureAtlas.findRegion("ship", 51);
        TextureRegion EnemyDestroyerTexture = textureAtlas.findRegion("ship", 54);
        TextureRegion EnemyBattleCrusierTexture = textureAtlas.findRegion("ship", 84);
        TextureRegion EnemyBattleShip = textureAtlas.findRegion("ship", 172);
        TextureRegion OverlordTexture = textureAtlas.findRegion("Overlord_Nightmare");
        TextureRegion CarrierTexture = textureAtlas.findRegion("ship", 170);
        TextureRegion CarrierSmallTexture = textureAtlas.findRegion("ship", 80);

        Shuriken = textureAtlas.findRegion("red_shu");

        TextureRegion UpgradePlayer2 = textureAtlas.findRegion("Starship_B_96x120");
        TextureRegion UpgradePlayer3 = textureAtlas.findRegion("Starship_C_96x120");


        background = textureAtlas.findRegion("BlueSpaceBackground");
        tExplosion = textureAtlas.findRegion("explosion_texture");
        bullet0 = textureAtlas.findRegion("bulletGlow");
        enemy0 = textureAtlas.findRegion("ship", 3);
        star0 = textureAtlas.findRegion("Stars");
        player0 = textureAtlas.findRegion("Starship_A_96x120");
        healthIcon = textureAtlas.findRegion("HP_Icon");
        backbutton = textureAtlas.findRegion("Backward_BTN");
        forwardbutton = textureAtlas.findRegion("Forward_BTN");
        HPPowerTexture = textureAtlas.findRegion("Powerup_Health_png_processed");
        RCPowerTexture = textureAtlas.findRegion("Powerup_Rockets_png_processed");
        ENPowerTexture = textureAtlas.findRegion("Powerup_Energy_png_processed");
        AMPowerTexture = textureAtlas.findRegion("Powerup_Ammo_png_processed");
        SHPowerTexture = textureAtlas.findRegion("Powerup_Shields_png_processed");
        overlordSmall = textureAtlas.findRegion("OverlordSample");

        TextureMap.put("MAX_HEALTH", HPPowerTexture);
        TextureMap.put("HEALTH", HPPowerTexture);
        TextureMap.put("FIRERATE", AMPowerTexture);
        TextureMap.put("SHIELD", SHPowerTexture);
        TextureMap.put("SPEED", ENPowerTexture);
        TextureMap.put("ROCKETS", RCPowerTexture);

        //HUD////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // FONT
        font = manager.get("EdgeOfTheGalaxyRegular-OVEa6.ttf");
        font.getData().setScale(0.08f);

        ///

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        HP_Icon = new GUI_element(WORLD_WIDTH/15, WORLD_HEIGHT - WORLD_HEIGHT/15, 7, 7, healthIcon);
        BK_Icon = new GUI_element(WORLD_WIDTH/25, WORLD_HEIGHT/15, 4, 4, backbutton);
        SP_Icon = new GUI_element(WORLD_WIDTH - WORLD_WIDTH/25, WORLD_HEIGHT/15, 4, 4, forwardbutton);

        // remember to fix position values to non-hardcoded values
        UP_Hull = new GUI_element(WORLD_WIDTH - WORLD_WIDTH/3, WORLD_HEIGHT - WORLD_HEIGHT/12 - 2, 4, 4, forwardbutton); // placeholder textures...
        UP_Engine = new GUI_element(WORLD_WIDTH - WORLD_WIDTH/3, WORLD_HEIGHT - WORLD_HEIGHT/12 - 10 - 2, 4, 4, forwardbutton);
        UP_Guns = new GUI_element(WORLD_WIDTH - WORLD_WIDTH/3, WORLD_HEIGHT - WORLD_HEIGHT/12 - 20 - 2, 4, 4, forwardbutton);
        UP_Drone = new GUI_element(WORLD_WIDTH - WORLD_WIDTH/3, WORLD_HEIGHT - WORLD_HEIGHT/12 - 30 - 2, 4, 4, forwardbutton);
        UP_GunSystems = new GUI_element(WORLD_WIDTH - WORLD_WIDTH/3, WORLD_HEIGHT - WORLD_HEIGHT/12 - 40 - 2, 4, 4, forwardbutton);
        UP_Rockets = new GUI_element(WORLD_WIDTH - WORLD_WIDTH/3, WORLD_HEIGHT - WORLD_HEIGHT/12 - 50 - 2, 4, 4, forwardbutton);

        // init Stars
        alStars = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            alStars.add(new Star(rand.nextInt(WORLD_WIDTH - 5), rand.nextInt(WORLD_HEIGHT), 0.7f, 0.7f, star0, (float)(rand.nextInt(50)) + 40.0f) );
        }

        total_damage = 0.0f;
        // init Player
        playerShip = new PlayerShip(WORLD_WIDTH / 2.0f, WORLD_HEIGHT / 4.0f, 5, 6, player0, UpgradePlayer2, UpgradePlayer3, MP.Fire_Player, 30.0f, 100.0f);

        //LVL_GENERATION_FUNCTIONS/////////////////////////////////////////////////////////////////

        // to be shorter, and for spawns
        final int WWS = 8;
        final int WHS = 0;
        final int WWE = WORLD_WIDTH - 8;
        final int WHE = WORLD_HEIGHT+5;
        final int WX1 = WORLD_WIDTH/5;
        final int WX2 = WORLD_WIDTH - WORLD_WIDTH/5;

        float Timer = 0.0f;

        final int EnemyPatrolTextureW = 3;
        final int EnemyPatrolTextureH = 3;
        final int EnemyGuardTextureW = 4;
        final int EnemyGuardTextureH = 4;
        final int EnemyCrusierTextureW = 7;
        final int EnemyCrusierTextureH = 6;
        final int EnemyBattleShipTextureW = 12;
        final int EnemyBattleShipTextureH = 9;
        final int CarrierSmallTextureW = 12;
        final int CarrierSmallTextureH = 9;
        final int CarrierTextureW = 23;
        final int CarrierTextureH = 12;
        final int OverlordTextureW = 25;
        final int OverlordTextureH = 28;
        final int HealerTextureW = 13;
        final int HealerTextureH = 16;
        final int EnemyDestroyerTextureW = 5;
        final int EnemyDestroyerTextureH = 5;

        BiFunction<Integer, Integer, Float> rng = (s, e) -> {
            return (float)ThreadLocalRandom.current().nextInt(s, e+1);
        };

        Function<Void, Ship> GetPatrolEasy = s -> {
            return new NormalShip(rng.apply(WWS, WWE), WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 2, 2, MP.Move_Down, MP.Fire_None);
        };

        Function<Void, Ship> GetPatrolEasyFixed1 = s -> {
            return new NormalShip(WX1, WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 5, 2, MP.Move_Sinus_Narrow, MP.Fire_None);
        };

        Function<Void, Ship> GetPatrolEasyFixed2 = s -> {
            return new NormalShip(WX2, WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 5, 2, MP.Move_Sinus_Narrow, MP.Fire_None);
        };

        Function<Void, Ship> GetPatrolNormal = s -> {
            return new NormalShip(rng.apply(WWS, WWE), WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 3, 2, MP.Move_Down, MP.Fire_Straight_Slow_bullet0);
        };

        Function<Void, Ship> GetPatrolHard = s -> {
            return new NormalShip(rng.apply(WWS, WWE), WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 3, 4, MP.Move_Down, MP.Fire_Straight_bullet0);
        };

        Function<Void, Ship> GetTreasureShipFixedMH = s -> {
            return new TreasureShip(WX1, WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 3, 10, MP.Move_Down, MP.Fire_None, "MAX_HEALTH");
        };

        Function<Void, Ship> GetTreasureShipFixed1 = s -> {
            return new TreasureShip(WX1, WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 3, 10, MP.Move_Down, MP.Fire_None, "HEALTH");
        };

        Function<Void, Ship> GetTreasureShipFixed2 = s -> {
            return new TreasureShip(WX2, WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 3, 10, MP.Move_Down, MP.Fire_None, "HEALTH");
        };

        Function<Void, Ship> GetTreasureShipF = s -> {
            return new TreasureShip(rng.apply(WWS, WWE), WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 3, 10, MP.Move_Down, MP.Fire_None, "FIRERATE");
        };

        Function<Void, Ship> GetTreasureShipR = s -> {
            return new TreasureShip(rng.apply(WWS, WWE), WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 3, 10, MP.Move_Down, MP.Fire_None, "ROCKETS");
        };

        Function<Void, Ship> GetTreasureShipS = s -> {
            return new TreasureShip(rng.apply(WWS, WWE), WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 3, 10, MP.Move_Down, MP.Fire_None, "SHIELD");
        };

        Function<Void, Ship> GetTreasureShipE = s -> {
            return new TreasureShip(rng.apply(WWS, WWE), WHE, EnemyPatrolTextureW, EnemyPatrolTextureH, EnemyPatrolTexture, 3, 10, MP.Move_Down, MP.Fire_None, "SPEED");
        };

        Function<Void, SmartShip> GetGuardEasyFixed1 = s -> {
            return new SmartShip(WX1, WHE, EnemyGuardTextureW, EnemyGuardTextureH, EnemyGuardTexture, 5, 3, MP.Move_Sinus_Narrow, MP.Fire_at_Player_Bullet0, playerShip);
        };

        Function<Void, SmartShip> GetGuardEasyFixed2 = s -> {
            return new SmartShip(WX2, WHE, EnemyGuardTextureW, EnemyGuardTextureH, EnemyGuardTexture, 5, 3, MP.Move_Sinus_Narrow, MP.Fire_at_Player_Bullet0, playerShip);
        };

        Function<Void, Ship> GetGuardNormal = s -> {
            return new NormalShip(rng.apply(WWS, WWE), WHE, EnemyGuardTextureW, EnemyGuardTextureH, EnemyGuardTexture, 5, 3, MP.Move_Sinus_Narrow, MP.Fire_Straight_bullet0);
        };

        Function<Void, SmartShip> GetGuardHard = s -> {
            return new SmartShip(rng.apply(WWS, WWE), WHE, EnemyGuardTextureW, EnemyGuardTextureH, EnemyGuardTexture, 5, 6, MP.Move_Sinus_Narrow, MP.Fire_at_Player_Bullet0, playerShip);
        };

        Function<Void, SmartShip> GetGuardInsane = s -> {
            return new SmartShip(rng.apply(WWS, WWE), WHE, EnemyGuardTextureW, EnemyGuardTextureH, EnemyGuardTexture, 4, 10, MP.Move_Sinus_Wide, MP.Fire_at_Player_Bullet0, playerShip);
        };

        Function<Void, Ship> GetCrusierNormal = s -> {
            return new SmartShip(rng.apply(WWS, WWE), WHE, EnemyCrusierTextureW, EnemyCrusierTextureH, EnemyCrusierTexture, 5, 12, MP.Move_Sinus_Narrow, MP.Fire_at_Player_Bullet0, playerShip);
        };

        Function<Void, SmartShip> GetCrusierHard = s -> {
            return new SmartShip(rng.apply(WWS, WWE), WHE, EnemyCrusierTextureW, EnemyCrusierTextureH, EnemyCrusierTexture, 5, 18, MP.Move_Sinus_Wide, MP.Fire_at_Player_Bullet0, playerShip);
        };

        Function<Void, Ship> GetDummy = s -> {
            return new NormalShip(WORLD_WIDTH/2.0f, WORLD_HEIGHT/2.0f, EnemyBattleShipTextureW, EnemyBattleShipTextureH, EnemyBattleShip, 1, 10000, MP.Move_None, MP.Fire_None);
        };

        Function<Void, SmartShip> OverlordHealer1 = s -> {
            assert levelSpawns.peekFirst() != null;
            return new HealingShip(WORLD_WIDTH/4.0f, WHE, HealerTextureW, HealerTextureH, overlordSmall, 0.3f, 400.0f, MP.Move_Sinus_Narrow, MP.Fire_at_Target_Healing, levelSpawns.peekFirst().s);
        };

        Function<Void, SmartShip> OverlordHealer2 = s -> {
            return new HealingShip(WORLD_WIDTH - WORLD_WIDTH/4.0f, WHE, HealerTextureW, HealerTextureH, overlordSmall, 0.3f, 400.0f, MP.Move_Sinus_Narrow, MP.Fire_at_Target_Healing, levelSpawns.get(0).s);
        };

        Function<Void, Ship> GetDestroyerNormal = s -> {
            return new NormalShip(rng.apply(WWS, WWE), WHE, EnemyDestroyerTextureW, EnemyDestroyerTextureH, EnemyDestroyerTexture, 7, 6, MP.Move_Sinus_Wide, MP.Fire_Star_Rapid_Bullet0);
        };

        Function<Void, Ship> GetBattleshipNormal = s -> {
            return new SmartShip(rng.apply(WWS, WWE), WHE, EnemyBattleShipTextureW, EnemyBattleShipTextureH, EnemyBattleShip, 0.8f, 50.0f, MP.Move_Down, MP.Fire_Circle_and_Player_Bullet0_Rocket, playerShip);
        };

        Function<Void, Carrier> GetCarrierNormal = s -> {
            return new Carrier(rng.apply(WWS, WWE), WHE, CarrierSmallTextureW, CarrierSmallTextureH, CarrierSmallTexture, 1.0f, 45.0f, MP.Move_Down, MP.Fire_Circle_and_Player_Bullet0_Rocket, playerShip, 4.0f, GetGuardInsane.apply(null));
        };

        Function<Void, SmartShip> GetBattleshipHard = s -> {
            return new SmartShip(rng.apply(WWS, WWE), WHE, EnemyBattleShipTextureW, EnemyBattleShipTextureH, EnemyBattleShip, 1.2f, 100.0f, MP.Move_Sinus_Narrow, MP.Fire_at_Player_Rocket, playerShip);
        };

        Function<Void, Ship> GetCarrierHard = s -> {
            return new Carrier(WWE/2.0f, WHE, CarrierTextureW, CarrierTextureH, CarrierTexture, 1.0f, 180.0f, MP.Move_Down, MP.Fire_Circle_and_Player_Bullet0_Rocket, playerShip, 4.0f, GetCrusierHard.apply(null));
        };

        Function<Void, Carrier> GetOverlord = s -> {
            return new Carrier(WORLD_WIDTH/2.0f, WHE, OverlordTextureW, OverlordTextureH, OverlordTexture, 0.25f, 1000.0f, MP.Move_Down, MP.Fire_Overlord, playerShip, 15.0f, GetBattleshipHard.apply(null));
        };

        Function<Void, SelfMovingShip> GetCrusierAI = s -> {
            ArrayList<point> P = new ArrayList<>();
            P.add(new point( 14,117 ));
            P.add(new point( 17,96 ));
            P.add(new point( 31,86 ));
            P.add(new point( 49,87 ));
            P.add(new point( 60,100 ));
            P.add(new point( 55,115 ));
            P.add(new point( 40,120 ));
            P.add(new point( 28,118 ));
            return new SelfMovingShip(rng.apply(WWS, WWE), WHE, EnemyCrusierTextureW, EnemyCrusierTextureH, EnemyCrusierTexture, 5, 18, MP.Move_waypoint, MP.Fire_at_Player_Bullet0, playerShip, P);
        };



        switch (level) {
            case 1:

                for (int i = 0; i < 5; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolEasy.apply(null)));
                    Timer += 1.0f;
                }
                Timer += 3.0f;
                for (int i = 0; i < 10; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolEasyFixed1.apply(null)));
                    Timer += 1.0f;
                }
                Timer += 7.0f;
                for (int i = 0; i < 10; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolEasyFixed2.apply(null)));
                    Timer += 1.0f;
                }
                Timer += 7.0f;
                for (int i = 0; i < 3; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolNormal.apply(null)));
                    Timer += 1.0f;
                }
                Timer += 3.0f;
                for (int i = 0; i < 3; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolNormal.apply(null)));
                    Timer += 1.0f;
                }
                Timer += 5.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixed1.apply(null)));
                Timer += 2.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipS.apply(null)));
                Timer += 9.0f;
                for (int i = 0; i < 2; i++) {
                    Timer += 7.0f;
                    for (int j = 0; j < 2; j++) {
                        levelSpawns.add(new Pair(Timer, GetPatrolHard.apply(null)));
                        Timer += 1.0f;
                    }
                }
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixed1.apply(null)));
                Timer += 2.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixed2.apply(null)));
                Timer += 25.0f;
                for (int i = 0; i < 3; i++) {
                    levelSpawns.add(new Pair(Timer, GetGuardNormal.apply(null)));
                    Timer += 1.0f;
                }
                Timer += 5.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixedMH.apply(null)));
                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipE.apply(null)));
                Timer += 7.0f;
                levelSpawns.add(new Pair(Timer, GetCrusierNormal.apply(null)));
                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixedMH.apply(null)));
                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipF.apply(null)));
                Timer += 5.0f;
                for (int i = 0; i < 5; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolNormal.apply(null)));
                    Timer += 1.0f;
                }
                Timer += 7.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixed1.apply(null)));
                Timer += 3.0f;
                levelSpawns.add(new Pair(Timer, GetGuardHard.apply(null)));
                levelSpawns.add(new Pair(Timer, GetCrusierNormal.apply(null)));
                Timer += 5.0f;
                for (int i = 0; i < 3; i++) {
                    levelSpawns.add(new Pair(Timer, GetDestroyerNormal.apply(null)));
                    Timer += 0.5f;
                }

                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetBattleshipNormal.apply(null)));
                Timer += 20.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixed1.apply(null)));
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixed2.apply(null)));
                Timer += 12.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipE.apply(null)));
                levelSpawns.add(new Pair(Timer, GetTreasureShipF.apply(null)));
                Timer += 13.0f;
                for (int i = 0; i < 15; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolEasyFixed1.apply(null)));
                    levelSpawns.add(new Pair(Timer, GetPatrolEasyFixed2.apply(null)));
                    Timer += 0.75f;
                }
                Timer += 1.5f;
                levelSpawns.add(new Pair(Timer, GetBattleshipNormal.apply(null)));
                for (int i = 0; i < 3; i++) {
                    levelSpawns.add(new Pair(Timer, GetGuardEasyFixed1.apply(null)));
                    levelSpawns.add(new Pair(Timer, GetGuardEasyFixed2.apply(null)));
                    Timer += 0.75f;
                }
                Timer += 20.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixedMH.apply(null)));

                Timer += 17.0f;
                for (int i = 0; i < 1; i++) {
                    levelSpawns.add(new Pair(Timer, GetCrusierNormal.apply(null)));
                    Timer += 1.0f;
                }
                for (int i = 0; i < 2; i++) {
                    levelSpawns.add(new Pair(Timer, GetDestroyerNormal.apply(null)));
                    Timer += 1.0f;
                }
                Timer += 4.0f;
                for (int i = 0; i < 15; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolEasyFixed1.apply(null)));
                    levelSpawns.add(new Pair(Timer, GetPatrolEasyFixed2.apply(null)));
                    Timer += 0.75f;
                }
                for(int i = 0; i < 5; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolNormal.apply(null)));
                    Timer += 0.5f;
                }

                Timer += 10.0f;
                for (int i = 0; i < 5; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolEasy.apply(null)));
                    Timer += 1.0f;
                }
                Timer += 3.0f;
                for (int i = 0; i < 10; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolEasyFixed1.apply(null)));
                    Timer += 1.0f;
                }
                Timer += 2.0f;
                for (int i = 0; i < 10; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolEasyFixed2.apply(null)));
                    Timer += 1.0f;
                }
                Timer += 5.0f;
                for (int i = 0; i < 3; i++) {
                    levelSpawns.add(new Pair(Timer, GetDestroyerNormal.apply(null)));
                    Timer += 0.5f;
                }
                levelSpawns.add(new Pair(Timer, GetBattleshipNormal.apply(null)));
                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetBattleshipNormal.apply(null)));
                Timer += 15.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixedMH.apply(null)));
                Timer += 15.0f;
                levelSpawns.add(new Pair(Timer, GetCarrierNormal.apply(null)));
                Timer += 35.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixedMH.apply(null)));
                Timer += 10.0f;

                for(int i = 0; i < 3; i++) {
                    levelSpawns.add(new Pair(Timer, GetGuardEasyFixed1.apply(null)));
                    levelSpawns.add(new Pair(Timer, GetGuardEasyFixed2.apply(null)));
                    Timer += 2.0f;
                }
                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipS.apply(null)));
                for(int i = 0; i < 3; i++) {
                    levelSpawns.add(new Pair(Timer, GetGuardEasyFixed1.apply(null)));
                    levelSpawns.add(new Pair(Timer, GetGuardEasyFixed2.apply(null)));
                    Timer += 1.0f;
                }
                levelSpawns.add(new Pair(Timer, GetBattleshipNormal.apply(null)));
                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetCarrierNormal.apply(null)));
                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetBattleshipHard.apply(null)));
                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetCarrierNormal.apply(null)));
                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetBattleshipHard.apply(null)));
                Timer += 25.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixedMH.apply(null)));
                levelSpawns.add(new Pair(Timer, GetTreasureShipR.apply(null)));
                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetBattleshipHard.apply(null)));
                Timer += 5.0f;
                levelSpawns.add(new Pair(Timer, GetCarrierHard.apply(null)));
                Timer += 3.0f;
                levelSpawns.add(new Pair(Timer, GetBattleshipHard.apply(null)));
                Timer += 25.0f;
                for (int i = 0; i < 25; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolEasy.apply(null)));
                    Timer += 0.5f;
                }
                Timer += 3.0f;
                for (int i = 0; i < 15; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolEasyFixed1.apply(null)));
                    Timer += 0.5f;
                }
                Timer += 7.0f;
                for (int i = 0; i < 10; i++) {
                    levelSpawns.add(new Pair(Timer, GetPatrolEasyFixed2.apply(null)));
                    Timer += 0.5f;
                }
                Timer += 10.5;

                levelSpawns.add(new Pair(Timer, GetTreasureShipFixedMH.apply(null)));
                levelSpawns.add(new Pair(Timer, GetTreasureShipF.apply(null)));
                levelSpawns.add(new Pair(Timer, GetTreasureShipS.apply(null)));
                levelSpawns.add(new Pair(Timer, GetTreasureShipE.apply(null)));
                levelSpawns.add(new Pair(Timer, GetTreasureShipR.apply(null)));
                Timer += 5.0f;
                levelSpawns.add(new Pair(Timer, GetOverlord.apply(null)));
                Timer += 5.0f;
                levelSpawns.add(new Pair(Timer, OverlordHealer1.apply(null)));
                levelSpawns.add(new Pair(Timer, OverlordHealer2.apply(null)));
                Timer += 10.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipS.apply(null)));
                Timer += 15.0f;
                levelSpawns.add(new Pair(Timer, GetTreasureShipFixedMH.apply(null)));
                levelSpawns.add(new Pair(Timer, GetTreasureShipS.apply(null)));
                levelSpawns.add(new Pair(Timer, GetTreasureShipF.apply(null)));

                break;
            case 2:
                levelSpawns.add(new Pair(Timer, GetCarrierHard.apply(null)));

                break;
            case 3:
                total_damage += 5000.0f;
            //    levelSpawns.add(new Pair(Timer, GetCarrierNormal.apply(null)));
                levelSpawns.add(new Pair(Timer, GetOverlord.apply(null)));
                Timer += 5.0f;
                levelSpawns.add(new Pair(Timer, OverlordHealer1.apply(null)));
                levelSpawns.add(new Pair(Timer, OverlordHealer2.apply(null)));
            //    levelSpawns.add(new Pair(Timer, GetCarrierHard.apply(null)));
                break;
            case 4:
                total_damage += 5000.0f;
                if(DeveloperMode) levelSpawns.add(new Pair(Timer, GetOverlord.apply(null)));
                break;
            case 5:
                levelSpawns.add(new Pair(Timer, GetDummy.apply(null)));
                total_damage += 5000.0f;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + level);
        }

        sDynamicShop = "";

        batch = new SpriteBatch();
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        playerShip.Update(deltaTime);
        time_since_touch += deltaTime;
        // shop state
        if (state == 2) {
            // possible placeholder
            batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            BK_Icon.Draw(batch);
            UP_Hull.Draw(batch);
            UP_GunSystems.Draw(batch);
            UP_Engine.Draw(batch);
            UP_Guns.Draw(batch);
            UP_Drone.Draw(batch);
            UP_Rockets.Draw(batch);
            pause_and_shop(deltaTime);
        }

        // game state
        if (state == 1) {

            fPlayerReloadTimer += deltaTime;
            fWorldTime += deltaTime;

            spawnEnemies();
            userInput(deltaTime);
            moveShips(deltaTime);
            moveProjectiles(deltaTime);
            renderTextures(deltaTime);
            removeObjects();

            font.draw(batch, String.format(Locale.getDefault(), "%03d", (int) playerShip.fHealth), (float) WORLD_WIDTH / 9, WORLD_HEIGHT - (float) WORLD_HEIGHT / 23);
            font.draw(batch, String.format(Locale.getDefault(), "%06d", (int) total_damage), (float) WORLD_WIDTH / 15, WORLD_HEIGHT - (float) WORLD_HEIGHT / 10);

            if (NormalShips.size() == 0 && levelSpawns.size()==0) {
                font.draw(batch, "DEMO LEVEL COMPLETE", 10, WORLD_HEIGHT/2.0f);
            }

            for (int i = 0; i < 4; i++) {
                if (playerShip.dataPowerActive[i]) {
                    float TimerLeft = 15.0f - playerShip.dataPowerUpTimer[i];
                    font.draw(batch, String.format(Locale.getDefault(), "%02d", (int) TimerLeft), (float) WORLD_WIDTH - WORLD_WIDTH / 10.0f, WORLD_HEIGHT - (float) WORLD_HEIGHT / 23 - i * 10.0f);
                }
            }

        }

        batch.end();
    }

    private void pause_and_shop(float fElapsedTime) {

        int top = WORLD_HEIGHT - WORLD_HEIGHT/12;
        font.draw(batch, String.format(Locale.getDefault(), "POINTS:  %06d", (int) total_damage ), WORLD_WIDTH/10, top - 80);
        font.draw(batch, "HULL", WORLD_WIDTH/12, top);
        font.draw(batch, "ENGINE", WORLD_WIDTH/12, top - 10);
        font.draw(batch, "GUNS", WORLD_WIDTH/12, top - 20);
        font.draw(batch, "DRONE", WORLD_WIDTH/12, top - 30);
        font.draw(batch, "GUN SYSTEMS", WORLD_WIDTH/12, top - 40);
        font.draw(batch, "ROCKETS", WORLD_WIDTH/12, top - 50);
        font.draw(batch, "COST  OF   ANY  UPGRADE:\n     150 POINTS", WORLD_WIDTH/20, top - 60);
        font.draw(batch, sDynamicShop, WORLD_WIDTH/20, top - 90);

        top = WORLD_HEIGHT - WORLD_HEIGHT/12;
        font.draw(batch, String.format(Locale.getDefault(), "%01d", (int) playerShip.GetHullLvl() ), WORLD_WIDTH - WORLD_WIDTH/10.0f, top);
        font.draw(batch, String.format(Locale.getDefault(), "%01d", (int) playerShip.GetEngineLvl() ), WORLD_WIDTH - WORLD_WIDTH/10.0f, top - 10);
        font.draw(batch, String.format(Locale.getDefault(), "%01d", (int) playerShip.GetGunsLvl() ), WORLD_WIDTH - WORLD_WIDTH/10.0f, top - 20);
        font.draw(batch, String.format(Locale.getDefault(), "%01d", (int) FriendyShips.size() ), WORLD_WIDTH - WORLD_WIDTH/10.0f, top - 30);
        font.draw(batch, String.format(Locale.getDefault(), "%01d", (int) playerShip.GetGunSystemsLvl() ), WORLD_WIDTH - WORLD_WIDTH/10.0f, top - 40);
        font.draw(batch, String.format(Locale.getDefault(), "%01d", (int) playerShip.GetRocketLvl() ), WORLD_WIDTH - WORLD_WIDTH/10.0f, top - 50);

        font.draw(batch, "GAME",WORLD_WIDTH/15.0f,  WORLD_HEIGHT/15.0f + 2);

        if (total_damage < 150.0f) {
            sDynamicShop = "Not enough money\n for upgrades";
        } else {
            sDynamicShop = "Upgrades available";
        }

        if(Gdx.input.isTouched()) {

            float xTouch = Gdx.input.getX();
            float yTouch = Gdx.input.getY();

            Vector2 touchPoint = new Vector2(xTouch, yTouch);
            touchPoint = viewport.unproject(touchPoint);

            float x = touchPoint.x;
            float y = touchPoint.y;

            if (BK_Icon.Intersects(x, y)) {
                state = 1;
                time_since_touch = 0.0f;
            }

            if (UP_Drone.Intersects(x, y) && time_since_touch > TOUCH_THRESHOLD) {
                time_since_touch = 0.0f;
                if (total_damage >= 150.0f && FriendyShips.size() < 3) {
                    if(FriendyShips.size()==0) FriendyShips.add(new Drone(10, 10, 5, 5, Shuriken, 1, 1000.0f, MP.Move_Rotation2, MP.Fire_None, playerShip));
                    else if(FriendyShips.size()==1) FriendyShips.add(new Drone(10, 10, 5, 5, Shuriken, 1, 1000.0f, MP.Move_Rotation3, MP.Fire_None, playerShip));
                    else if(FriendyShips.size()==2) FriendyShips.add(new Drone(10, 10, 5, 5, Shuriken, 1, 1000.0f, MP.Move_Rotation4, MP.Fire_None, playerShip));
                    total_damage -= 150.0f;
                }
            }

            if (UP_Guns.Intersects(x, y) && time_since_touch > TOUCH_THRESHOLD) {
                time_since_touch = 0.0f;
             //   laser.play(0.2f); // sound placeholder
                if ( total_damage >= 150.0f && playerShip.UpgradeGuns() ) total_damage -= 150.0f;
            }
            if (UP_Engine.Intersects(x, y) && time_since_touch > TOUCH_THRESHOLD) {
                time_since_touch = 0.0f;
            //    laser.play(0.2f); // sound placeholder
                if ( total_damage >= 150.0f && playerShip.UpgradeEngine() ) total_damage -= 150.0f;
            }
            if (UP_Hull.Intersects(x, y) && time_since_touch > TOUCH_THRESHOLD) {
                time_since_touch = 0.0f;
              //  laser.play(0.2f); // sound placeholder
                if ( total_damage >= 150.0f && playerShip.UpgradeHull() ) total_damage -= 150.0f;
            }
            if (UP_GunSystems.Intersects(x, y) && time_since_touch > TOUCH_THRESHOLD) {
                time_since_touch = 0.0f;
             //   laser.play(0.2f); // sound placeholder
                if ( total_damage >= 150.0f && playerShip.UpgradeGunSystems() ) total_damage -= 150.0f;
            }
            if (UP_Rockets.Intersects(x, y) && time_since_touch > TOUCH_THRESHOLD) {
                time_since_touch = 0.0f;
                if ( total_damage >= 150.0f && playerShip.UpgradeRockets() ) total_damage -= 150.0f;
            }
            if (time_since_touch == 0.0f) { // Im just lazy
                playerShip.UpgradeVisuals();
            }


        }

    }

    private void spawnEnemies() {

        while( !levelSpawns.isEmpty() && levelSpawns.getFirst().fAppearTime <= fWorldTime) {
            Ship Spawn = levelSpawns.poll().s;
            NormalShips.add(Spawn);
        }

    }

    private void renderTextures(float fElapsedTime) {

        // Background //////////////////////////////////////////////////////////////////////////
        batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        for (int i = 0; i < alStars.size(); i++) {
            Star curr = alStars.get(i);
            curr.yPos -= (curr.fSpeed * fElapsedTime) / 4;
            if(curr.yPos <= 0) {
                curr.yPos = WORLD_HEIGHT;
                curr.xPos = rand.nextInt(WORLD_WIDTH - 5);
                curr.fSpeed = (float)(rand.nextInt(50) )+ 40.0f;
            }
            alStars.set(i, curr);
            batch.draw(alStars.get(i).ObjectTexture, alStars.get(i).xPos, alStars.get(i).yPos, alStars.get(i).fwidth, alStars.get(i).fheight);
        }
        ///////////////////////////////////////////////////////////////////////////////////////

        playerShip.Draw(batch);

        for(Bullet b : friendlyBullets) {
            b.Draw(batch);
        }
        for(Bullet b : enemyBullets) {
            b.Draw(batch);
        }
        for(Ship s : NormalShips) {
            s.Draw(batch);
        }
        for(PowerUp p : powerUps) {
            p.Draw(batch);
        }
        for(Ship s : FriendyShips) {
            s.Draw(batch);
        }

        for(Explosion e : explosions) {
           e.Draw(batch);
           e.Update(fElapsedTime);
        }

        //// HP AND STUFF
        HP_Icon.Draw(batch);
        BK_Icon.Draw(batch);
        SP_Icon.Draw(batch);
        font.draw(batch, "MENU",WORLD_WIDTH/15.0f,  WORLD_HEIGHT/15.0f + 2);
        font.draw(batch, "SHOP",WORLD_WIDTH - WORLD_WIDTH/4.0f + 2,  WORLD_HEIGHT/15.0f + 2);

    }

    private void userInput(float fElapsedTime) {

        if(playerShip.fHealth <= 0) {
            this.dispose();
            game.setScreen(new MenuScreen(game));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && playerShip.xPos < WORLD_WIDTH ) {
            playerShip.xPos += playerShip.fSpeed * fElapsedTime;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && playerShip.xPos > 0) {
            playerShip.xPos -= playerShip.fSpeed * fElapsedTime;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP) && playerShip.yPos < WORLD_HEIGHT) {
            playerShip.yPos += playerShip.fSpeed * fElapsedTime;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && playerShip.yPos > 0) {
            playerShip.yPos -= playerShip.fSpeed * fElapsedTime;
        }

        if( playerShip.CanFire(fElapsedTime) ) {
            laser.play(0.2f);
        }
        if (playerShip.CanFireRocket(fElapsedTime)) {
            rocketS.play(0.2f);
        }
        playerShip.Fire(fElapsedTime, friendlyBullets);

        if(Gdx.input.isTouched()) {

            float xTouch = Gdx.input.getX();
            float yTouch = Gdx.input.getY();

            Vector2 touchPoint = new Vector2(xTouch, yTouch);
            touchPoint = viewport.unproject(touchPoint);

            Vector2 playerShipCentre = new Vector2(playerShip.xPos, playerShip.yPos);

            float touchDistance = touchPoint.dst(playerShipCentre);

            if (DeveloperMode && time_since_touch > TOUCH_MOVEMENT_THRESHOLD) {
                //  P.add(new Point(WWE/4, WHE/4));
                int ix = (int)touchPoint.x;
                int iy = (int)touchPoint.y;

                String s = "P.add(new point( ";
                String add1 = Integer.toString(ix);
                String z = ",";
                String add2 = Integer.toString(iy);
                String e = " ));\n";

                String sol = s + add1 + z + add2 + e;

                System.out.println(sol);

                Fil.writeString(sol ,true);


                time_since_touch = 0.0f;

            }

            if(touchDistance > TOUCH_MOVEMENT_THRESHOLD) {
                float xTouchDiff = touchPoint.x - playerShipCentre.x;
                float yTouchDiff = touchPoint.y - playerShipCentre.y;

                float xMove = xTouchDiff / touchDistance;
                float yMove = yTouchDiff / touchDistance;

                playerShip.xMove = xMove;
                playerShip.yMove = yMove;

            }

            float x = touchPoint.x;
            float y = touchPoint.y;

            if(BK_Icon.Intersects(x, y) && time_since_touch > TOUCH_MOVEMENT_THRESHOLD) {
                this.dispose();
                game.setScreen(new MenuScreen(game));
            }

            if(SP_Icon.Intersects(x, y)) {
                state = 2;
            }

        }

    }

    private void moveProjectiles(float fElapsedTime) {

        for(Bullet b : friendlyBullets) {
            b.Update(fElapsedTime);
            for(Ship s : NormalShips) {
                if( s.Intersects(b) ) {
                    if(b instanceof Rocket) {
                        explosions.add(new Explosion(b.xPos, b.yPos, b.fwidth * 5.0f, b.fheight * 5.0f, tExplosion, 0.7f));
                        destroy.play(0.3f);
                    }
                    s.fHealth -= b.fDamage;
                    total_damage += b.fDamage;
                    b.bRemove = true;
                    if(s.fHealth <= 0) {
                        if(s.fMaxHealth >= 40.0f) explosions.add(new Explosion(s.xPos, s.yPos, s.fwidth, s.fheight, tExplosion, 1.7f));
                        else explosions.add(new Explosion(s.xPos, s.yPos, s.fwidth * 2.5f, s.fheight * 2.5f, tExplosion, 0.7f));
                        if(s instanceof TreasureShip) {
                            powerUps.add(new PowerUp(s.xPos, s.yPos, 5, 5, TextureMap.get(((TreasureShip) s).type), ((TreasureShip) s).type));
                        }
                        destroy.play(0.4f);
                    }
                }
            }
        }

        for(Bullet b : enemyBullets) {

            if (b.fDamage <= 0.0f) {

                for (Ship s : NormalShips) {
                    if (s.Intersects(b) && !(s instanceof HealingShip)) {
                        if(s.fHealth - b.fDamage <= s.fMaxHealth) s.fHealth -= b.fDamage;
                        else s.fHealth = s.fMaxHealth;
                        b.bRemove = true;
                    }
                }

            }

            for(Ship s : FriendyShips) {
                if( b.Intersects(s) ) {
                    if(b instanceof Rocket) {
                        explosions.add(new Explosion(b.xPos, b.yPos, b.fwidth * 8.0f, b.fheight * 8.0f, tExplosion, 1.0f));
                        destroy.play(0.6f);
                    }
                    s.fHealth -= b.fDamage;
                    b.bRemove = true;
                }
            }

            if( b.Intersects(playerShip) && b.fDamage > 0) {
                if(b instanceof Rocket) {
                    explosions.add(new Explosion(b.xPos, b.yPos, b.fwidth * 8.0f, b.fheight * 8.0f, tExplosion, 1.0f));
                    destroy.play(0.6f);
                }
                playerShip.GetHit(b);
                b.bRemove = true;
            }
        }

        for(PowerUp p : powerUps) {
            p.yPos -= (10 * fElapsedTime);
            if(playerShip.Intersects(p)) {
                p.GivePowerUp(playerShip);

                p.bRemove = true;
            }
        }

        for(Bullet b : enemyBullets) {
            b.Update(fElapsedTime);
        }

    }

    private void removeObjects() {

        explosions.removeIf(Explosion::IsFinished);
        powerUps.removeIf(b -> b.bRemove || b.yPos < 0);
        friendlyBullets.removeIf(b -> b.bRemove || b.yPos < 0 || b.xPos < 0 || b.xPos > WORLD_WIDTH || b.yPos > WORLD_HEIGHT + 5);
        enemyBullets.removeIf(b -> b.bRemove || b.yPos < 0 || b.xPos < 0 || b.xPos > WORLD_WIDTH || b.yPos > WORLD_HEIGHT + 5);
        NormalShips.removeIf(s -> s.fHealth <= 0.0 || s.yPos <= 0);
    }

    private void moveShips(float fElapsedTime) {
        for(Ship s : NormalShips) {
            s.Update(fElapsedTime);
            s.Fire(fElapsedTime, enemyBullets);
            if (s instanceof Carrier) {
                ((Carrier) s).SpawnShip(fElapsedTime, levelSpawns);
            }
        }
        for(Ship s : FriendyShips) {
            s.Update(fElapsedTime);
            s.Fire(fElapsedTime, friendlyBullets);
        }
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
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        music.stop();
        laser.dispose();
    //    font.dispose();

        batch.dispose();

        alStars.clear();
        friendlyBullets.clear();
        NormalShips.clear();
        enemyBullets.clear();
    }
}
