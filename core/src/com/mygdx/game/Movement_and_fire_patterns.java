package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Movement_and_fire_patterns {

    TextureAtlas textureAtlas;
    TextureRegion bullet0, rocket0, bulletPlayer, bullet1, bullet2;

    Movement_and_fire_patterns(TextureAtlas TA, boolean flip) {
        textureAtlas = TA;
        bulletPlayer = textureAtlas.findRegion("weapon_0063_Package-----------------");
        bullet0 = textureAtlas.findRegion("weapon_0031_Package-----------------");
        bullet1 = textureAtlas.findRegion("weapon_0032_Package-----------------");
        bullet2 = textureAtlas.findRegion("weapon_0034_Package-----------------");
        rocket0 = textureAtlas.findRegion("weapon_0000_Package-----------------");
        if(flip) rocket0.flip(false, true);
    }

    interface TriFunction<A,B,C,R> {
        R apply(A a, B b, C c);
        default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (A a, B b, C c) -> after.apply(apply(a, b, c));
        }
    }

    interface QuadFunction<A,B,C,D,R> {
        R apply(A a, B b, C c, D d);
        default <V> QuadFunction<A, B, C, D, V> andThen(Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (A a, B b, C c, D d) -> after.apply(apply(a, b, c, d));
        }
    }

    // Movement Patterns
    public BiFunction<Ship, Float, Void> Move_None = (s, fElapsedTime) -> null;
    public BiFunction<Ship, Float, Void> Move_Down = (s, fElapsedTime) -> {s.yPos -= s.fSpeed * fElapsedTime; return null;};
    public BiFunction<Ship, Float, Void> Move_Sinus_Narrow = (s, fElapsedTime) -> {s.yPos -= fElapsedTime * s.fSpeed; s.dataMove[0] += fElapsedTime; s.xPos += 5.0f * Math.cos(s.dataMove[0]) * fElapsedTime; return null;};
    public BiFunction<Ship, Float, Void> Move_Sinus_Wide = (s, fElapsedTime) -> {s.yPos -= fElapsedTime * s.fSpeed; s.dataMove[0] += fElapsedTime; s.xPos += 15.0f * Math.cos(s.dataMove[0]) * fElapsedTime; return null;};

    public BiFunction<SelfMovingShip, Float, Void> Move_waypoint = (s, fElapsedTime) -> {
        int ind = s.index;

        float currX = s.xPos;
        float currY = s.yPos;
        float targetX = (float)s.Waypoints.get(ind).x;
        float targetY = (float)s.Waypoints.get(ind).y;

        // this could use some optimization to only compute once per index movement, save tx and ty while not at the target;
        float tx = -(currX - targetX);
        float ty = -(currY - targetY);
        float inv_len = 1.0f / (float)Math.sqrt(tx * tx + ty * ty);
        tx *= inv_len;
        ty *= inv_len;
        tx *= s.fSpeed * 2;
        ty *= s.fSpeed * 2;

        s.xPos += tx * fElapsedTime;
        s.yPos += ty * fElapsedTime;

        if( Math.abs(targetX-currX) <= 0.2f && Math.abs(targetY-currY) <= 0.2f ) s.MoveIndexForward();

        return null;
    };

    public BiFunction<Ship, Float, Void> Move_Random = (s, fElapsedTime) -> {

        final float fDelay = 4.0f;
        s.dataMove[0] += fElapsedTime;

        if(s.dataMove[0] >= fDelay) {

            s.dataMove[0] -= fDelay;

            float xRand = ThreadLocalRandom.current().nextInt(1, 72);
            float yRand = ThreadLocalRandom.current().nextInt(1, 128);

            float tx, ty;

            tx = -(s.xPos - xRand);
            ty = -(s.yPos - yRand);
            float inv_len = 1.0f / (float)Math.sqrt(tx * tx + ty * ty);
            tx *= inv_len;
            ty *= inv_len;
            tx *= s.fSpeed * 2;
            ty *= s.fSpeed * 2;

            s.dataMove[1] = tx;
            s.dataMove[2] = ty;

        }

        s.xPos += s.dataMove[1] * fElapsedTime;
        s.yPos += s.dataMove[2] * fElapsedTime;
        return null;
    };

    public BiFunction<Drone, Float, Void> Move_Rotation2 = (s, fElapsedTime) -> {
        Vector2 Pos = new Vector2(s.xPos, s.yPos);
        Vector2 TargetPos = new Vector2(s.Target.xPos, s.Target.yPos);

        s.dataMove[0] += fElapsedTime;

        float x = TargetPos.x + (float) ((Math.cos(s.dataMove[0] * 6) * 20));
        float y = TargetPos.y + (float) ((Math.sin(s.dataMove[0] * 6) * 20));

        s.Angle += 18.0f;

     //   if(s.dataMove[0] % 10 <= 0.1) System.out.println(dist);

        s.xPos = x;
        s.yPos = y;

        return null;
    };

    public BiFunction<Drone, Float, Void> Move_Rotation3 = (s, fElapsedTime) -> {
        Vector2 Pos = new Vector2(s.xPos, s.yPos);
        Vector2 TargetPos = new Vector2(s.Target.xPos, s.Target.yPos);

        s.dataMove[0] += fElapsedTime;

        float x = TargetPos.x + (float) ((Math.cos(s.dataMove[0] * 4) * 25));
        float y = TargetPos.y + (float) ((Math.sin(s.dataMove[0] * 4) * 25));

        s.Angle += 18.0f;

        //   if(s.dataMove[0] % 10 <= 0.1) System.out.println(dist);

        s.xPos = x;
        s.yPos = y;

        return null;
    };

    public BiFunction<Drone, Float, Void> Move_Rotation4 = (s, fElapsedTime) -> {
        Vector2 Pos = new Vector2(s.xPos, s.yPos);
        Vector2 TargetPos = new Vector2(s.Target.xPos, s.Target.yPos);

        s.dataMove[0] += fElapsedTime;

        float x = TargetPos.x + (float) ((Math.cos(s.dataMove[0] * 2) * 30));
        float y = TargetPos.y + (float) ((Math.sin(s.dataMove[0] * 2) * 30));

        s.Angle += 18.0f;

        //   if(s.dataMove[0] % 10 <= 0.1) System.out.println(dist);

        s.xPos = x;
        s.yPos = y;

        return null;
    };

    public BiFunction<SmartShip, Float, Void> Move_Rotation = (s, fElapsedTime) -> {
        Vector2 Pos = new Vector2(s.xPos, s.yPos);
        Vector2 TargetPos = new Vector2(s.Target.xPos, s.Target.yPos);
        float dist = Pos.dst(TargetPos);
        float Angle = TargetPos.angle(Pos);

        s.dataMove[0] += fElapsedTime;

        float rad = (float) Math.toRadians(Angle);
        float x = TargetPos.x + (float) ((Math.cos(s.dataMove[0] * 2) * 20));
        float y = TargetPos.y + (float) ((Math.sin(s.dataMove[0] * 2) * 20));

        //   if(s.dataMove[0] % 10 <= 0.1) System.out.println(dist);

        s.xPos = x;
        s.yPos = y;

        return null;
    };


    // Fire Patterns

    int bulletW = 1;
    int bulletH = 1;

    public TriFunction<Ship, Float, LinkedList<Bullet>, Void> Fire_None = (s, fElapsedTime, bullets) -> null;

    public TriFunction<Ship, Float, LinkedList<Bullet>, Void> Fire_Straight_Slow_bullet0 = (s, fElapsedTime, bullets) -> {
        final float fDelay = 1.8f;
        s.dataFire[0] += fElapsedTime;
        if(s.dataFire[0] >= fDelay) {
            s.dataFire[0] -= fDelay;
            Bullet b = new Bullet(s.xPos, s.yPos, 0, -50, bulletW, bulletH, bullet0, 10);
            bullets.add(b);
        }
        return null;
    };


    public TriFunction<Ship, Float, LinkedList<Bullet>, Void> Fire_Straight_bullet0 = (s, fElapsedTime, bullets) -> {
        final float fDelay = 1.0f;
        s.dataFire[0] += fElapsedTime;
        if(s.dataFire[0] >= fDelay) {
            s.dataFire[0] -= fDelay;
            Bullet b = new Bullet(s.xPos, s.yPos, 0, -100, bulletW, bulletH, bullet0, 10);
            bullets.add(b);
        }
        return null;
    };

    public TriFunction<Ship, Float, LinkedList<Bullet>, Void> Fire_Straight_Rapid_bullet0 = (s, fElapsedTime, bullets) -> {
        final float fDelay = 0.4f;
        s.dataFire[0] += fElapsedTime;
        if(s.dataFire[0] >= fDelay) {
            s.dataFire[0] -= fDelay;
            Bullet b = new Bullet(s.xPos, s.yPos, 0, -200, bulletW, bulletH, bullet0, 10);
            bullets.add(b);
        }
        return null;
    };

    public TriFunction<Ship, Float, LinkedList<Bullet>, Void> Fire_Circle_Bullet0 = (s, fElapsedTime, bullets) -> {
        final float fDelay = 1.2f;
        final float fTheta = 3.14159f * 2.0f / (float)15;
        s.dataFire[0] += fElapsedTime;
        if(s.dataFire[0] >= fDelay) {
            s.dataFire[0] -= fDelay;

            for(int i = 0; i < 15; i++) {
                Bullet b = new Bullet(s.xPos, s.yPos, 50.0f * (float)Math.cos(fTheta * i), 50.0f * (float)Math.sin(fTheta * i), bulletW, bulletH, bullet0, 10);
                bullets.add(b);
            }

        }
        return null;
    };

    public TriFunction<SmartShip, Float, LinkedList<Bullet>, Void> Fire_Circle_and_Player_Bullet0_Rocket = (s, fElapsedTime, bullets) -> {
        final float fDelay = 1.2f;
        final float fRDelay = 3.0f;
        final float fTheta = 3.14159f * 2.0f / (float)20;
        s.dataFire[0] += fElapsedTime;
        s.dataFire[1] += fElapsedTime;
        if(s.dataFire[0] >= fDelay) {
            s.dataFire[0] -= fDelay;

            for(int i = 0; i < 20; i++) {
                Bullet b = new Bullet(s.xPos, s.yPos, 50.0f * (float)Math.cos(fTheta * i), 50.0f * (float)Math.sin(fTheta * i), bulletW, bulletH, bullet0, 10);
                bullets.add(b);
            }

        }
        if(s.dataFire[1] >= fRDelay) {
            s.dataFire[1] -= fRDelay;

            float tx = -(s.xPos - s.Target.xPos);
            float ty = -(s.yPos - s.Target.yPos);
            float inv_len = 1.0f / (float)Math.sqrt(tx * tx + ty * ty);
            tx *= inv_len;
            ty *= inv_len;
            tx *= 40;
            ty *= 40;

            Rocket b = new Rocket(s.xPos, s.yPos, tx, ty, 2, 5, rocket0, 30);
            bullets.add(b);
        }



        return null;
    };

    public TriFunction<SmartShip, Float, LinkedList<Bullet>, Void> Fire_Overlord = (s, fElapsedTime, bullets) -> {
        final float fDelay = 5.0f;
        final float fRDelay = 10.0f;
        final float fPDelay = 2.0f;
        final float fTheta = 3.14159f * 2.0f / (float)120;
        s.dataFire[0] += fElapsedTime;
        s.dataFire[1] += fElapsedTime;
        s.dataFire[2] += fElapsedTime;
        if(s.dataFire[0] >= fDelay) {
            s.dataFire[0] -= fDelay;

            for(int i = 0; i < 120; i++) {
                Bullet b = new Bullet(s.xPos, s.yPos, 50.0f * (float)Math.cos(fTheta * i), 50.0f * (float)Math.sin(fTheta * i), bulletW, bulletH, bullet0, 5);
                bullets.add(b);
            }

        }
        if(s.dataFire[1] >= fRDelay) {
            s.dataFire[1] -= fRDelay;

            float tx = -(s.xPos - s.Target.xPos);
            float ty = -(s.yPos - s.Target.yPos);
            float inv_len = 1.0f / (float)Math.sqrt(tx * tx + ty * ty);
            tx *= inv_len;
            ty *= inv_len;
            tx *= 35;
            ty *= 35;

            Rocket b = new Rocket(s.xPos, s.yPos, tx, ty, 4, 10, rocket0, 120);
            bullets.add(b);
        }
        if(s.dataFire[2] >= fPDelay) {
            s.dataFire[2] -= fPDelay;

            float tx = -(s.xPos - s.Target.xPos);
            float ty = -(s.yPos - s.Target.yPos);
            float inv_len = 1.0f / (float)Math.sqrt(tx * tx + ty * ty);
            tx *= inv_len;
            ty *= inv_len;
            tx *= 90;
            ty *= 90;

            Bullet b = new Bullet(s.xPos, s.yPos, tx, ty, 3, 3, bullet2, 70);
            bullets.add(b);
        }



        return null;
    };

    public TriFunction<Ship, Float, LinkedList<Bullet>, Void> Fire_Star_Rapid_Bullet0 = (s, fElapsedTime, bullets) -> {
        final float fDelay = 0.2f;
        s.dataFire[0] += fElapsedTime;
        if(s.dataFire[0] >= fDelay) {
            s.dataFire[0] -= fDelay;
            s.dataFire[1] += 0.2f;
            Bullet b = new Bullet(s.xPos, s.yPos, 180.0f * (float)Math.cos(s.dataFire[1]), 180.0f * (float)Math.sin(s.dataFire[1]), bulletW, bulletH, bullet0, 10);
            bullets.add(b);
        }
        return null;
    };

    public TriFunction<Ship, Float, LinkedList<Bullet>, Void> Fire_Straight_rocket0 = (s, fElapsedTime, bullets) -> {
        final float fDelay = 1.0f;
        s.dataFire[0] += fElapsedTime;
        if(s.dataFire[0] >= fDelay) {
            s.dataFire[0] -= fDelay;
            Rocket b = new Rocket(s.xPos, s.yPos, 0, -20, 2, 5, rocket0, 50);
            bullets.add(b);
        }
        return null;
    };

    public TriFunction<SmartShip, Float, LinkedList<Bullet>, Void> Fire_at_Player_Bullet0 = (s, fElapsedTime, bullets) -> {
        final float fDelay = 1.0f;
        s.dataFire[0] += fElapsedTime;
        if(s.dataFire[0] >= fDelay) {
            s.dataFire[0] -= fDelay;

            float tx = -(s.xPos - s.Target.xPos);
            float ty = -(s.yPos - s.Target.yPos);
            float inv_len = 1.0f / (float)Math.sqrt(tx * tx + ty * ty);
            tx *= inv_len;
            ty *= inv_len;
            tx *= 100;
            ty *= 100;

            Bullet b = new Bullet(s.xPos, s.yPos, tx, ty, bulletW, bulletH, bullet0, 10);
            bullets.add(b);
        }
        return null;
    };

    public TriFunction<SmartShip, Float, LinkedList<Bullet>, Void> Fire_at_Target_Healing = (s, fElapsedTime, bullets) -> {
        final float fDelay = 1.0f;
        s.dataFire[0] += fElapsedTime;
        if(s.dataFire[0] >= fDelay) {
            s.dataFire[0] -= fDelay;

            float tx = -(s.xPos - s.Target.xPos);
            float ty = -(s.yPos - s.Target.yPos);
            float inv_len = 1.0f / (float)Math.sqrt(tx * tx + ty * ty);
            tx *= inv_len;
            ty *= inv_len;
            tx *= 50;
            ty *= 50;

            Bullet b = new Bullet(s.xPos, s.yPos, tx, ty, bulletW+2, bulletH+2, bullet1, -10);
            bullets.add(b);
        }
        return null;
    };

    public TriFunction<SmartShip, Float, LinkedList<Bullet>, Void> Fire_at_Player_Rocket = (s, fElapsedTime, bullets) -> {
        final float fDelay = 1.0f;
        s.dataFire[0] += fElapsedTime;
        if(s.dataFire[0] >= fDelay) {
            s.dataFire[0] -= fDelay;

            float tx = -(s.xPos - s.Target.xPos);
            float ty = -(s.yPos - s.Target.yPos);
            float inv_len = 1.0f / (float)Math.sqrt(tx * tx + ty * ty);
            tx *= inv_len;
            ty *= inv_len;
            tx *= 60;
            ty *= 60;

            Rocket b = new Rocket(s.xPos, s.yPos, tx, ty, 2, 5, rocket0, 10);
            bullets.add(b);
        }
        return null;
    };

    public TriFunction<PlayerShip, Float, LinkedList<Bullet>, Void> Fire_Player = (s, fElapsedTime, bullets) -> {
     //   final float fDelay = s.dataLaserTimer[s.UpgradeLevelFireRate] * (s.dataPowerActive[0] ? 0.5f : 1.0f);
        s.dataLaserTimer[s.UpgradeLevelFireRate] += fElapsedTime;
        if(s.dataLaserTimer[s.UpgradeLevelFireRate] >= s.dataLaserDelay[s.UpgradeLevelFireRate] * (s.dataPowerActive[0] ? 0.5f : 1.0f)) {
            s.dataLaserTimer[s.UpgradeLevelFireRate] -= s.dataLaserDelay[s.UpgradeLevelFireRate] * (s.dataPowerActive[0] ? 0.5f : 1.0f);
            float sPos = s.xPos;
            float gap = 2.0f;
            if ( s.UpgradeLevelFireAmount==1 ) {
                bullets.add(new Bullet(sPos, s.yPos, 0, 100, 1, 5, bulletPlayer, 1));
            } else if ( s.UpgradeLevelFireAmount==2 ) {
                bullets.add(new Bullet(sPos - gap, s.yPos, 0, 100, 1, 5, bulletPlayer, 1));
                bullets.add(new Bullet(sPos + gap, s.yPos, 0, 100, 1, 5, bulletPlayer, 1));
            } else if ( s.UpgradeLevelFireAmount==3 ) {
                bullets.add(new Bullet(sPos - gap, s.yPos, 0, 100, 1, 5, bulletPlayer, 1));
                bullets.add(new Bullet(sPos, s.yPos, 0, 100, 1, 5, bulletPlayer, 1));
                bullets.add(new Bullet(sPos + gap, s.yPos, 0, 100, 1, 5, bulletPlayer, 1));
            }
        }
        s.dataRocketTimer[s.UpgradeRocketFireRate] += fElapsedTime;
        if(s.dataRocketTimer[s.UpgradeRocketFireRate] >= s.dataRocketDelay[s.UpgradeRocketFireRate] * (s.dataPowerActive[3] ? 0.6f : 1.0f)) {
            s.dataRocketTimer[s.UpgradeRocketFireRate] -= s.dataRocketDelay[s.UpgradeRocketFireRate] * (s.dataPowerActive[3] ? 0.6f : 1.0f);
            float sPos = s.xPos;
            if (!s.dataPowerActive[3])
                bullets.add(new Rocket(sPos + 2, s.yPos, 0, 50, 2, 4, rocket0, 20));
            else
                bullets.add(new Rocket(sPos + 2, s.yPos, 0, 70, 3, 5, rocket0, 40));
        }

        return null;
    };



}
