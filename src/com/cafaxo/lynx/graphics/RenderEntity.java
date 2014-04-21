package com.cafaxo.lynx.graphics;

import com.cafaxo.lynx.util.IDepthContainer;
import com.cafaxo.lynx.util.Vector2f;

public abstract class RenderEntity extends Vector2f implements IDepthContainer
{

    protected ShaderProgram shaderProgram;

    protected Texture textures[];

    protected int originX;

    protected int originY;

    protected int width;

    protected int height;

    protected float vertexData[];

    protected float transformedVertexData[];

    protected int indexData[];

    protected int vertexDataSize;

    protected int indexDataSize;

    protected float rotation;

    protected int depth;

    protected boolean visible;

    private boolean hasChanged;

    public RenderEntity(ShaderProgram shaderProgram, int maxVertexDataSize, int maxIndexDataSize)
    {
        super(0.f, 0.f);

        this.shaderProgram = shaderProgram;
        this.vertexData = new float[maxVertexDataSize];
        this.transformedVertexData = new float[maxVertexDataSize];
        this.indexData = new int[maxIndexDataSize];

        this.visible = true;
        this.hasChanged = true;
    }

    protected void refreshVertexAndIndexData()
    {
        for (int i = 0; i < this.vertexData.length; ++i)
        {
            this.transformedVertexData[i] = this.vertexData[i];
        }
    }

    protected void addVertexData(final float vertexData)
    {
        this.vertexData[this.vertexDataSize] = vertexData;
        this.vertexDataSize++;
    }

    protected void addVertexData(float[] vertexDataArray)
    {
        for (float vertexData : vertexDataArray)
        {
            this.addVertexData(vertexData);
        }
    }

    protected void addIndexData(final int index)
    {
        this.indexData[this.indexDataSize] = index;
        this.indexDataSize++;
    }

    protected void addIndexData(int[] indexDataArray)
    {
        for (int indexData : indexDataArray)
        {
            this.addIndexData(indexData);
        }
    }

    protected void resetVertexAndIndexData()
    {
        this.vertexDataSize = 0;
        this.indexDataSize = 0;
    }

    public final float[] getVertexData()
    {
        if (this.hasChanged)
        {
            this.refreshVertexAndIndexData();
            this.hasChanged = false;
        }

        return this.transformedVertexData;
    }

    public final int getVertexDataSize()
    {
        return this.vertexDataSize;
    }

    public final int[] getIndexData()
    {
        return this.indexData;
    }

    public final int getIndexDataSize()
    {
        return this.indexDataSize;
    }

    public final ShaderProgram getShaderProgram()
    {
        return this.shaderProgram;
    }

    public final void setShaderProgram(ShaderProgram shaderProgram)
    {
        this.shaderProgram = shaderProgram;
    }

    public Texture[] getTextures()
    {
        return this.textures;
    }

    public void setTextures(Texture[] textures)
    {
        this.textures = textures;
    }

    public void setTexture(Texture texture)
    {
        this.textures = new Texture[] { texture };
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);

        this.hasChanged = true;
    }

    @Override
    public void translate(float x, float y)
    {
        super.translate(x, y);

        this.hasChanged = true;
    }

    public void setRotation(float rotation)
    {
        this.rotation = rotation;
        this.hasChanged = true;
    }

    public void rotate(float rotation)
    {
        this.rotation += rotation;
        this.hasChanged = true;
    }

    public float getRotation()
    {
        return this.rotation;
    }

    public void setOrigin(int originX, int originY)
    {
        this.originX = originX;
        this.originY = originY;
        this.hasChanged = true;
    }

    public int getOriginX()
    {
        return this.originX;
    }

    public int getOriginY()
    {
        return this.originY;
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.hasChanged = true;
    }

    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    @Override
    public int getDepth()
    {
        return this.depth;
    }

    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    public boolean hasChanged()
    {
        return this.hasChanged;
    }

    public void setHasChanged(boolean hasChanged)
    {
        this.hasChanged = hasChanged;
    }

}
