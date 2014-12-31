package com.cafaxo.lynx.render;

import com.cafaxo.lynx.math.Vector2f;
import com.cafaxo.lynx.render.texture.TextureRegion;

public class MeshSprite extends Mesh
{

    public final static int[] INDEX_DATA = new int[] { 0, 1, 2, 0, 2, 3 };

    protected final static int X1 = 0, Y1 = 1, U1 = 2, V1 = 3, C1 = 4;

    protected final static int X2 = 5, Y2 = 6, U2 = 7, V2 = 8, C2 = 9;

    protected final static int X3 = 10, Y3 = 11, U3 = 12, V3 = 13, C3 = 14;

    protected final static int X4 = 15, Y4 = 16, U4 = 17, V4 = 18, C4 = 19;

    protected TextureRegion textureRegion;

    private Vector2f position = new Vector2f(0.f, 0.f);

    private Vector2f origin = new Vector2f(0.f, 0.f);

    protected float width;

    protected float height;

    private float rotation;

    public MeshSprite()
    {
        super(20, 6);

        this.vertexData.position(this.vertexData.limit());
        this.indexData.put(MeshSprite.INDEX_DATA);
    }

    public MeshSprite(TextureRegion textureRegion)
    {
        this();

        this.setTextureRegion(textureRegion);
        this.setSize(textureRegion.width, textureRegion.height);
        this.setColor(1.F, 1.F, 1.F, 1.F);
        this.refreshPositionData();
    }

    public void refreshPositionData()
    {
        float localX = -this.origin.x;
        float localY = -this.origin.y;
        float localX2 = localX + this.width;
        float localY2 = localY + this.height;
        float worldOriginX = this.position.x;// - localX;
        float worldOriginY = this.position.y;// - localY;

        if (this.rotation != 0.F)
        {
            final float cos = (float) Math.cos(this.rotation);
            final float sin = (float) Math.sin(this.rotation);
            final float localXCos = localX * cos;
            final float localXSin = localX * sin;
            final float localYCos = localY * cos;
            final float localYSin = localY * sin;
            final float localX2Cos = localX2 * cos;
            final float localX2Sin = localX2 * sin;
            final float localY2Cos = localY2 * cos;
            final float localY2Sin = localY2 * sin;

            final float x1 = (localXCos - localYSin) + worldOriginX;
            final float y1 = localYCos + localXSin + worldOriginY;
            this.vertexData.put(X1, x1);
            this.vertexData.put(Y1, y1);

            final float x2 = (localXCos - localY2Sin) + worldOriginX;
            final float y2 = localY2Cos + localXSin + worldOriginY;
            this.vertexData.put(X4, x2);
            this.vertexData.put(Y4, y2);

            final float x3 = (localX2Cos - localY2Sin) + worldOriginX;
            final float y3 = localY2Cos + localX2Sin + worldOriginY;
            this.vertexData.put(X3, x3);
            this.vertexData.put(Y3, y3);

            this.vertexData.put(X2, x1 + (x3 - x2));
            this.vertexData.put(Y2, y3 - (y2 - y1));
        }
        else
        {
            final float x1 = localX + worldOriginX;
            final float y1 = localY + worldOriginY;
            final float x2 = localX2 + worldOriginX;
            final float y2 = localY2 + worldOriginY;

            this.vertexData.put(X1, x1);
            this.vertexData.put(Y1, y1);

            this.vertexData.put(X2, x2);
            this.vertexData.put(Y2, y1);

            this.vertexData.put(X3, x2);
            this.vertexData.put(Y3, y2);

            this.vertexData.put(X4, x1);
            this.vertexData.put(Y4, y2);
        }
    }

    public void setColor(float r, float g, float b, float a)
    {
        int intBits = ((int) (255 * a) << 24) | ((int) (255 * b) << 16) | ((int) (255 * g) << 8) | ((int) (255 * r));
        float color = Float.intBitsToFloat(intBits & 0xfeffffff);

        this.vertexData.put(C1, color);
        this.vertexData.put(C2, color);
        this.vertexData.put(C3, color);
        this.vertexData.put(C4, color);
    }

    public void setSize(float width, float height)
    {
        this.width = width;
        this.height = height;
    }

    public TextureRegion getTextureRegion()
    {
        return this.textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion)
    {
        this.textureRegion = textureRegion;

        this.vertexData.put(U1, this.textureRegion.u1);
        this.vertexData.put(V1, this.textureRegion.v1);
        this.vertexData.put(U2, this.textureRegion.u2);
        this.vertexData.put(V2, this.textureRegion.v2);
        this.vertexData.put(U3, this.textureRegion.u3);
        this.vertexData.put(V3, this.textureRegion.v3);
        this.vertexData.put(U4, this.textureRegion.u4);
        this.vertexData.put(V4, this.textureRegion.v4);
    }

    public void setPosition(float x, float y)
    {
        this.position.setPosition(x, y);
    }

    public Vector2f getPosition()
    {
        return this.position;
    }

    public void setRotation(float rotation)
    {
        this.rotation = rotation;
    }

    public void setOriginToCenter()
    {
        this.origin.setPosition(this.width / 2.f, this.height / 2.f);
    }

}
