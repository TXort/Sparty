package com.mygdx.game;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.BiFunction;

import static com.mygdx.game.Movement_and_fire_patterns.TriFunction;


abstract class SpaceObject {

    public float xPos, yPos, fwidth, fheight;
    public TextureRegion ObjectTexture;

    float fwidthShrinkFactor;
    float fheightShrinkFactor;

    public SpaceObject(float xPos, float yPos, float fwidth, float fheight, TextureRegion ObjectTexture) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.fwidth = fwidth;
        this.fheight = fheight;
        this.ObjectTexture = ObjectTexture;
        fwidthShrinkFactor = 1.0f;
        fheightShrinkFactor = 1.0f;
    }

    /*public boolean Intersects(SpaceObject s) {

        float fwidthShrinkFactor = 0.6f;
        float fheightShrinkFactor = 0.1f;

        float x1 = xPos - (fwidth/2 * fwidthShrinkFactor);
        float y1 = yPos - (fheight/2 * fheightShrinkFactor);
        float x2 = xPos + (fwidth/2 * fwidthShrinkFactor);
        float y2 = yPos + (fheight/2 * fheightShrinkFactor);

        float x3 = s.xPos - s.fwidth/2;
        float y3 = s.yPos - s.fheight/2;
        float x4 = s.xPos + s.fwidth/2;
        float y4 = s.yPos + s.fheight/2;

        return (x1 < x4) && (x3 < x2) && (y1 < y4) && (y3 < y2);
    }*/

    public boolean Intersects(SpaceObject s) {

        float x1 = xPos - (fwidth/2 * fwidthShrinkFactor);
        float y1 = yPos - (fheight/2 * fheightShrinkFactor);
        float x2 = xPos + (fwidth/2 * fwidthShrinkFactor);
        float y2 = yPos + (fheight/2 * fheightShrinkFactor);

        float x3 = s.xPos - (s.fwidth/2);
        float y3 = s.yPos - (s.fheight/2);
        float x4 = s.xPos + (s.fwidth/2);
        float y4 = s.yPos + (s.fheight/2);

        return (x1 < x4) && (x3 < x2) && (y1 < y4) && (y3 < y2);
    }

    public void Draw(Batch batch) {
  //      DrawHitbox(batch);
        batch.draw(ObjectTexture, xPos-fwidth/2, yPos-fheight/2, fwidth, fheight);
   //     DrawHitbox(batch);
    }

    private Texture texture;

    public void DrawHitbox(Batch batch) {

        Pixmap pixmap = new Pixmap((int)fwidth, (int)fheight, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fillRectangle(0, 0, (int)fwidth, (int)fheight);
        texture = new Texture(pixmap);
        pixmap.dispose();

        float boxWidth = fwidth * 1.0f;
        float boxHeight = fheight * 1.0f;

        batch.draw(texture, xPos-boxWidth/2, yPos-boxHeight/2, boxWidth, boxHeight);
    }

}

class PowerUp extends SpaceObject {

    boolean bRemove;
    String type;

    public PowerUp(float xPos, float yPos, float fwidth, float fheight, TextureRegion ObjectTexture, String type) {
        super(xPos, yPos, fwidth, fheight, ObjectTexture);
        bRemove = false;
        this.type = type;
    }

    public void GivePowerUp(PlayerShip ship) {
        switch (type) {
            case "HEALTH":
                float AddHealth = ship.fMaxHealth * 0.2f;
                if(ship.fHealth + AddHealth >= ship.fMaxHealth) ship.fHealth = ship.fMaxHealth;
                else ship.fHealth += AddHealth;
                break;
            case "MAX_HEALTH":
                ship.fHealth = ship.fMaxHealth;
                break;
            case "FIRERATE":
                ship.ActivateFirerateBoost();
                break;
            case "SHIELD":
                ship.ActivateShieldBoost();
                break;
            case "SPEED":
                ship.ActivateSpeedBoost();
                break;
            case "ROCKETS":
                ship.ActivateRocketBoost();
                break;
        }

    }

}

class Star extends SpaceObject {

    float fSpeed;

    public Star(float xPos, float yPos, float fwidth, float fheight, TextureRegion ObjectTexture, float fSpeed) {
        super(xPos, yPos, fwidth, fheight, ObjectTexture);
        this.fSpeed = fSpeed;
    }
}

//Rectangle(int x, int y, int width, int height)
//Constructs a new Rectangle whose upper-left corner is specified as (x,y) and whose width and height are specified by the arguments of the same name.

abstract class Ship extends SpaceObject {

    float fHealth, fMaxHealth, fSpeed;
    float[] dataMove;
    float[] dataFire;

    BiFunction<Ship, Float, Void> funcMove;
    TriFunction<Ship, Float, LinkedList<Bullet>, Void> funcFire;

    public Ship(float xPos, float yPos, float fwidth, float fheight, TextureRegion ShipTexture, float fSpeed, float fHealth) {
        super(xPos, yPos, fwidth, fheight, ShipTexture);
        this.fHealth = fHealth;
        this.fMaxHealth = fHealth;
        this.fSpeed = fSpeed;
        dataMove = new float[5];
        dataFire = new float[5];
    }

    abstract void Update(float fElapsedTime);
    abstract void Fire(float fElapsedTime, LinkedList<Bullet> bullets);

}


class NormalShip extends Ship {

    public NormalShip(float xPos, float yPos, float fwidth, float fheight, TextureRegion ShipTexture, float fSpeed, float fHealth, BiFunction Movement, TriFunction Firement) {
        super(xPos, yPos, fwidth, fheight, ShipTexture, fSpeed, fHealth);
        funcFire = Firement;
        funcMove = Movement;
    }

    @Override
    void Update(float fElapsedTime) {
        funcMove.apply(this, fElapsedTime);
    }

    @Override
    void Fire(float fElapsedTime, LinkedList<Bullet> bullets) { funcFire.apply(this, fElapsedTime, bullets); }
}

class SmartShip extends NormalShip {

    Ship Target;

    public SmartShip(float xPos, float yPos, float fwidth, float fheight, TextureRegion shipTexture, float fSpeed, float fHealth, BiFunction Movement, TriFunction Firement, Ship Target) {
        super(xPos, yPos, fwidth, fheight, shipTexture, fSpeed, fHealth, Movement, Firement);
        this.Target = Target;
    }

}

class SelfMovingShip extends SmartShip {

    ArrayList<point> Waypoints;
    int index;

    public SelfMovingShip(float xPos, float yPos, float fwidth, float fheight, TextureRegion shipTexture, float fSpeed, float fHealth, BiFunction Movement, TriFunction Firement, Ship Target, ArrayList<point> Waypoints) {
        super(xPos, yPos, fwidth, fheight, shipTexture, fSpeed, fHealth, Movement, Firement, Target);
        this.Waypoints = Waypoints;
        index = 0;
    }

    void MoveIndexForward() {
        if (index + 1 >= Waypoints.size()) index = 0;
        else index++;
    }


}

class Carrier extends SmartShip {

    float fSpawnDelay, fSpawnTimer;
    SmartShip spawnShip;

    public Carrier(float xPos, float yPos, float fwidth, float fheight, TextureRegion shipTexture, float fSpeed, float fHealth, BiFunction Movement, TriFunction Firement, Ship Target, float fSpawnDelay, SmartShip spawnShip) {
        super(xPos, yPos, fwidth, fheight, shipTexture, fSpeed, fHealth, Movement, Firement, Target);
        this.fSpawnDelay = fSpawnDelay;
        this.spawnShip = spawnShip;
        fSpawnTimer = 0.0f;
        fwidthShrinkFactor = 0.7f;
        fheightShrinkFactor = 0.5f;
    }

    @Override
    void Update(float fElapsedTime) {
        fSpawnTimer += fElapsedTime;
        funcMove.apply(this, fElapsedTime);
    }

    SmartShip Copy(SmartShip s) {
        return new SmartShip(s.xPos, s.yPos, s.fwidth, s.fheight, s.ObjectTexture, s.fSpeed, s.fHealth, s.funcMove, s.funcFire, s.Target);
    }

    void SpawnShip(float fElapsedTime, LinkedList<Pair> lvlSpawns) {
        if (fSpawnTimer >= fSpawnDelay) {
            spawnShip.xPos = xPos;
            spawnShip.yPos = yPos;
            fSpawnTimer -= fSpawnDelay;
            SmartShip sSpawn = Copy(spawnShip);
            lvlSpawns.addFirst(new Pair(0.0f, sSpawn));
        }
    }

}

class HealingShip extends SmartShip {

    public HealingShip(float xPos, float yPos, float fwidth, float fheight, TextureRegion shipTexture, float fSpeed, float fHealth, BiFunction Movement, TriFunction Firement, Ship Target) {
        super(xPos, yPos, fwidth, fheight, shipTexture, fSpeed, fHealth, Movement, Firement, Target);
    }
}

class Drone extends SmartShip {

    float Angle;

    public Drone(float xPos, float yPos, float fwidth, float fheight, TextureRegion shipTexture, float fSpeed, float fHealth, BiFunction Movement, TriFunction Firement, Ship Target) {
        super(xPos, yPos, fwidth, fheight, shipTexture, fSpeed, fHealth, Movement, Firement, Target);
        Angle = 0;
    }

    public void Draw(Batch batch) {
        batch.draw(ObjectTexture, xPos-fwidth/2, yPos-fheight/2, fwidth/2, fheight/2, fwidth, fheight, 1, 1, Angle);
    }

}

class TreasureShip extends NormalShip {

    String type;

    public TreasureShip(float xPos, float yPos, float fwidth, float fheight, TextureRegion ShipTexture, float fSpeed, float fHealth, BiFunction Movement, TriFunction Firement, String type) {
        super(xPos, yPos, fwidth, fheight, ShipTexture, fSpeed, fHealth, Movement, Firement);
        this.type = type;
    }

}


class PlayerShip extends Ship {

    int UpgradeLevelFireRate;
    int UpgradeRocketFireRate;
    int UpgradeLevelFireAmount;
    int UpgradeLevelHealth;
    int UpgradeLevelSpeed;
    int MaxLevel;

    float[] dataLaserTimer;
    float[] dataLaserDelay;
    float[] dataRocketTimer;
    float[] dataRocketDelay;
    float[] dataPowerUpTimer;
    float[] dataPowerUpDelay;
    boolean[] dataPowerActive;

    boolean TotalUpgrade1;
    boolean TotalUpgrade2;

    float Armor;

    float xMove;
    float yMove;

    TextureRegion shipTexture2, shipTexture3;

    public PlayerShip(float xPos, float yPos, float fwidth, float fheight, TextureRegion shipTexture, TextureRegion shipTexture2, TextureRegion shipTexture3, TriFunction Firement, float fSpeed, float fHealth) {
        super(xPos, yPos, fwidth, fheight, shipTexture, fSpeed, fHealth);
        this.shipTexture2 = shipTexture2;
        this.shipTexture3 = shipTexture3;
        UpgradeLevelFireRate = 1;
        UpgradeLevelFireAmount = 1;
        UpgradeLevelHealth = 1;
        UpgradeLevelSpeed = 1;
        UpgradeRocketFireRate = 0;
        this.fMaxHealth = 100.0f;
        MaxLevel = 3;


        dataMove = new float[5];

        dataPowerUpTimer = new float[5];
        dataPowerUpDelay = new float[5];
        dataPowerActive = new boolean[5];

        dataLaserTimer = new float[5];
        dataLaserDelay = new float[5];

        dataRocketTimer = new float[5];
        dataRocketDelay = new float[5];

        for (int i = 0; i < 4; i++) {
            dataPowerUpTimer[i] = 0.0f;
            dataLaserTimer[i] = 0.0f;
            dataRocketTimer[i] = 0.0f;
        }

        dataPowerUpDelay[0] = 15.0f; // Firerate
        dataPowerUpDelay[1] = 15.0f; // Shield
        dataPowerUpDelay[2] = 15.0f; // Speed
        dataPowerUpDelay[3] = 15.0f; // Nothing

        // Laser lvls
        dataLaserDelay[1] = 0.5f;
        dataLaserDelay[2] = 0.35f;
        dataLaserDelay[3] = 0.2f;

        // Rocket lvls
        dataRocketDelay[0] = 1000000.0f;
        dataRocketDelay[1] = 6.0f;
        dataRocketDelay[2] = 4.5f;
        dataRocketDelay[3] = 2.5f;

        TotalUpgrade1 = false;
        TotalUpgrade2 = false;

        Armor = 1.0f;

        funcFire = Firement;
    }

    boolean UpgradeHull() {
        if (UpgradeLevelHealth >= MaxLevel) return false;
        UpgradeLevelHealth++;
        fMaxHealth += (40.0f * UpgradeLevelHealth);
        fHealth = fMaxHealth;
        return true;
    }

    boolean UpgradeEngine() {
        if (UpgradeLevelSpeed >= MaxLevel) return false;
        UpgradeLevelSpeed++;
        fSpeed += (0.35f * fSpeed);
        return true;
    }

    boolean UpgradeGuns() {
        if (UpgradeLevelFireRate >= MaxLevel) return false;
        UpgradeLevelFireRate++;
        return true;
    }

    boolean UpgradeGunSystems() {
        if (UpgradeLevelFireAmount >= MaxLevel) return false;
        UpgradeLevelFireAmount++;
        return true;
    }

    boolean UpgradeRockets() {
        if (UpgradeRocketFireRate >= MaxLevel) return false;
        UpgradeRocketFireRate++;
        return true;
    }

    int GetHullLvl() {
        return UpgradeLevelHealth;
    }
    int GetGunsLvl() {
        return UpgradeLevelFireRate;
    }
    int GetGunSystemsLvl() {
        return UpgradeLevelFireAmount;
    }
    int GetEngineLvl() {
        return UpgradeLevelSpeed;
    }
    int GetRocketLvl() {
        return UpgradeRocketFireRate;
    }

    void UpgradeVisuals() {
        if (UpgradeLevelHealth >= 2 && UpgradeLevelFireAmount >= 2 && UpgradeLevelFireRate >= 2 && UpgradeLevelSpeed >= 2) {
            super.ObjectTexture = shipTexture2;
            if (!TotalUpgrade1) {
                TotalUpgrade1 = true;
                Armor = 1.25f;
            }
        }
        if ( (UpgradeLevelHealth + UpgradeLevelFireAmount + UpgradeLevelFireRate + UpgradeLevelSpeed) == 12) {
            super.ObjectTexture = shipTexture3;
            if (!TotalUpgrade1) {
                TotalUpgrade1 = true;
                Armor = 1.5f;
            }
        }
    }

    void ActivateFirerateBoost() {
        dataPowerActive[0] = true;
    }

    void ActivateShieldBoost() {
        dataPowerActive[1] = true;
    }

    void ActivateSpeedBoost() {
        dataPowerActive[2] = true;
    }

    void ActivateRocketBoost() { dataPowerActive[3] = true;}

    void GetHit(Bullet b) {
        if(!dataPowerActive[1]) fHealth -= (b.fDamage - ( (Armor - 1.0f) * b.fDamage) );
    }

    @Override
    void Update(float fElapsedTime) {

        float NewPosX = xPos + (xMove * fElapsedTime * fSpeed *  (dataPowerActive[2] ? 2.0f : 1.0f)  );
        float NewPosY = yPos + (yMove * fElapsedTime * fSpeed *  (dataPowerActive[2] ? 2.0f : 1.0f)  );

        if (NewPosX > 1 && NewPosY > 1 && NewPosX < 72 - 1 && NewPosY < 128 - 1) {
            xPos = NewPosX;
            yPos = NewPosY;
        }


        xMove = 0.0f;
        yMove = 0.0f;

        for (int i = 0; i < 4; i++)
            if (dataPowerActive[i]) {
                dataPowerUpTimer[i] += fElapsedTime;
                if (dataPowerUpTimer[i] >= dataPowerUpDelay[i]) {
                    dataPowerUpTimer[i] = 0.0f;
                    dataPowerActive[i] = false;
                }
            }
    }

    @Override
    void Fire(float fElapsedTime, LinkedList<Bullet> bullets) {
        funcFire.apply(this, fElapsedTime, bullets);
    }

    // currently used to sync laser with sound
    public boolean CanFire(float fElapsedTime) {
        return dataLaserTimer[UpgradeLevelFireRate] + fElapsedTime >= dataLaserDelay[UpgradeLevelFireRate] * (dataPowerActive[0] ? 0.5f : 1.0f);
    }
    // currently used to sync rocket with sound
    public boolean CanFireRocket(float fElapsedTime) {
        return dataRocketTimer[UpgradeRocketFireRate] + fElapsedTime >= dataRocketDelay[UpgradeRocketFireRate] * (dataPowerActive[3] ? 0.6f : 1.0f);
    }

}


class Bullet extends SpaceObject {

    float xVel, yVel, fDamage;
    public boolean bRemove;

    public Bullet(float xPos, float yPos, float xVel, float yVel, float fwidth, float fheight, TextureRegion ObjectTexture, float fDamage) {
        super(xPos, yPos, fwidth, fheight, ObjectTexture);
        this.xVel = xVel;
        this.yVel = yVel;
        this.fDamage = fDamage;
    }

    public void Update(float fElapsedTime) {
        xPos += xVel * fElapsedTime;
        yPos += yVel * fElapsedTime;
    }

}

class Rocket extends Bullet {

    public Rocket(float xPos, float yPos, float xVel, float yVel, float fwidth, float fheight, TextureRegion ObjectTexture, float fDamage) {
        super(xPos, yPos, xVel, yVel, fwidth, fheight, ObjectTexture, fDamage);
    }

   @Override

    public void Draw(Batch batch) {
        batch.draw(ObjectTexture, xPos-fwidth/2, yPos-fheight/2, 0, 0, fwidth, fheight, 1, 1, new Vector2(xVel, yVel).angle()+90);
    }

}


class Explosion extends SpaceObject {

    public Animation<TextureRegion> explosionAntimation;
    public float explosionTimer;

    public Explosion(float xPos, float yPos, float fwidth, float fheight, TextureRegion textureRegion, float totalAnimationTime) {
        super(xPos, yPos, fwidth, fheight, textureRegion);

        TextureRegion[][] textureRegion2D = textureRegion.split(textureRegion.getRegionWidth()/4, textureRegion.getRegionHeight()/4);

        TextureRegion[] textureRegion1D = new TextureRegion[16];
        int index = 0;

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                textureRegion1D[index++] = textureRegion2D[i][j];

        explosionAntimation = new Animation<>(totalAnimationTime / 16, textureRegion1D);
        explosionTimer = 0;
    }

    public void Update(float fElapsedTime) {
        explosionTimer += fElapsedTime;
    }

    public void Draw(Batch batch) {
        batch.draw(explosionAntimation.getKeyFrame(explosionTimer), xPos-fwidth/2, yPos-fheight/2, fwidth, fheight);
    }

    public boolean IsFinished() {
        return explosionAntimation.isAnimationFinished(explosionTimer);
    }

}


class Pair {

    public float fAppearTime;
    public Ship s;

    public Pair(float fAppearTime, Ship s) {
        this.fAppearTime = fAppearTime;
        this.s = s;
    }

}

class point {
    int x, y;
    public point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}