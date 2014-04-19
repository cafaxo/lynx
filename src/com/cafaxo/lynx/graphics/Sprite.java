package com.cafaxo.lynx.graphics;

public class Sprite extends RenderEntity
{

    public final static int NUM_VERTICES = 20;

    public final static int[] INDEX_DATA = new int[] { 0, 1, 2, 0, 2, 3 };

    protected final static int X1 = 0, Y1 = 1, U1 = 2, V1 = 3, C1 = 4;

    protected final static int X2 = 5, Y2 = 6, U2 = 7, V2 = 8, C2 = 9;

    protected final static int X3 = 10, Y3 = 11, U3 = 12, V3 = 13, C3 = 14;

    protected final static int X4 = 15, Y4 = 16, U4 = 17, V4 = 18, C4 = 19;

    protected TextureRegion textureRegion;

    public Sprite(ShaderProgram shaderProgram)
    {
        super(shaderProgram, 20, 6);

        this.vertexDataSize = 20;
        this.indexDataSize = 6;

        this.indexData = Sprite.INDEX_DATA;

        this.setColor(1.F, 1.F, 1.F, 1.F);
    }

    public Sprite(ShaderProgram shaderProgram, Texture[] textures)
    {
        this(shaderProgram);

        this.setTextures(textures);
        this.setTextureRegion(textures[0].getTextureRegion());
        this.setSize(textures[0].width, textures[0].height);
    }

    public Sprite(ShaderProgram shaderProgram, Texture texture)
    {
        this(shaderProgram, new Texture[] { texture });
    }

    public Sprite(ShaderProgram shaderProgram, Texture[] textures, int width, int height)
    {
        this(shaderProgram);

        this.setTextures(textures);
        this.setTextureRegion(textures[0].getTextureRegion());
        this.setSize(width, height);
    }

    public Sprite(ShaderProgram shaderProgram, Texture texture, int width, int height)
    {
        this(shaderProgram, new Texture[] { texture }, width, height);
    }

    public Sprite(ShaderProgram shaderProgram, Texture[] textures, TextureRegion textureRegion)
    {
        this(shaderProgram);

        this.setTextures(textures);
        this.setTextureRegion(textureRegion);
        this.setSize(textureRegion.width, textureRegion.height);
    }

    public Sprite(ShaderProgram shaderProgram, Texture texture, TextureRegion textureRegion)
    {
        this(shaderProgram, new Texture[] { texture }, textureRegion);
    }

    public Sprite(ShaderProgram shaderProgram, Texture[] textures, TextureRegion textureRegion, int width, int height)
    {
        this(shaderProgram);

        this.setTextures(textures);
        this.setTextureRegion(textureRegion);
        this.setSize(width, height);
    }

    public Sprite(ShaderProgram shaderProgram, Texture texture, TextureRegion textureRegion, int width, int height)
    {
        this(shaderProgram, new Texture[] { texture }, textureRegion, width, height);
    }

    @Override
    protected void refreshVertexAndIndexData()
    {
        float localX = -this.originX;
        float localY = -this.originY;
        float localX2 = localX + this.width;
        float localY2 = localY + this.height;
        float worldOriginX = this.x - localX;
        float worldOriginY = this.y - localY;

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
            this.transformedVertexData[X1] = x1;
            this.transformedVertexData[Y1] = y1;

            final float x2 = (localXCos - localY2Sin) + worldOriginX;
            final float y2 = localY2Cos + localXSin + worldOriginY;
            this.transformedVertexData[X2] = x2;
            this.transformedVertexData[Y2] = y2;

            final float x3 = (localX2Cos - localY2Sin) + worldOriginX;
            final float y3 = localY2Cos + localX2Sin + worldOriginY;
            this.transformedVertexData[X3] = x3;
            this.transformedVertexData[Y3] = y3;

            this.transformedVertexData[X4] = x1 + (x3 - x2);
            this.transformedVertexData[Y4] = y3 - (y2 - y1);
        }
        else
        {
            final float x1 = localX + worldOriginX;
            final float y1 = localY + worldOriginY;
            final float x2 = localX2 + worldOriginX;
            final float y2 = localY2 + worldOriginY;

            this.transformedVertexData[X1] = x1;
            this.transformedVertexData[Y1] = y1;

            this.transformedVertexData[X2] = x2;
            this.transformedVertexData[Y2] = y1;

            this.transformedVertexData[X3] = x2;
            this.transformedVertexData[Y3] = y2;

            this.transformedVertexData[X4] = x1;
            this.transformedVertexData[Y4] = y2;
        }
    }

    public void setColor(float r, float g, float b, float a)
    {
        int intBits = ((int) (255 * a) << 24) | ((int) (255 * b) << 16) | ((int) (255 * g) << 8) | ((int) (255 * r));
        float color = Float.intBitsToFloat(intBits & 0xfeffffff);

        this.transformedVertexData[C1] = color;
        this.transformedVertexData[C2] = color;
        this.transformedVertexData[C3] = color;
        this.transformedVertexData[C4] = color;
    }

    public TextureRegion getTextureRegion()
    {
        return this.textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion)
    {
        this.textureRegion = textureRegion;

        this.transformedVertexData[U1] = this.textureRegion.u1;
        this.transformedVertexData[V1] = this.textureRegion.v1;
        this.transformedVertexData[U2] = this.textureRegion.u2;
        this.transformedVertexData[V2] = this.textureRegion.v2;
        this.transformedVertexData[U3] = this.textureRegion.u3;
        this.transformedVertexData[V3] = this.textureRegion.v3;
        this.transformedVertexData[U4] = this.textureRegion.u4;
        this.transformedVertexData[V4] = this.textureRegion.v4;
    }
}
