package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class GUI_element {

    float xPos, yPos, fwidth, fheight;
    TextureRegion texture;

    public GUI_element(float xPos, float yPos, float fwidth, float fheight, TextureRegion texture) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.fwidth = fwidth;
        this.fheight = fheight;
        this.texture = texture;
    }

    public void Draw(Batch batch) {
        batch.draw(texture, xPos-fwidth/2, yPos-fheight/2, fwidth, fheight);
    }

    public boolean Intersects(float x, float y) {

        float x1 = xPos - fwidth/2;
        float y1 = yPos - fheight/2;
        float x2 = xPos + fwidth/2;
        float y2 = yPos + fheight/2;

        return x >= x1 && y >= y1 && x<=x2 && y <= y2;
    }

}

class GUI_text extends GUI_element {

    public GUI_text(float xPos, float yPos, float fwidth, float fheight, TextureRegion texture) {
        super(xPos, yPos, fwidth, fheight, texture);
    }




}
