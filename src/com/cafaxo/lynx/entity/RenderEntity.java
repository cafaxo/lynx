package com.cafaxo.lynx.entity;

import com.cafaxo.lynx.ShaderProgram;
import com.cafaxo.lynx.Texture;
import com.cafaxo.lynx.util.IDepthContainer;
import com.cafaxo.lynx.util.Vector2f;

public abstract class RenderEntity implements IDepthContainer
{

    private Vector2f position, origin;

    protected ShaderProgram shaderProgram;

    protected Texture textures[];

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
        this.shaderProgram = shaderProgram;
        this.vertexData = new float[maxVertexDataSize];
        this.transformedVertexData = new float[maxVertexDataSize];
        this.indexData = new int[maxIndexDataSize];

        this.position = new Vector2f(0.f, 0.f);
        this.origin = new Vector2f(0.f, 0.f);

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

    public void setPosition(float x, float y)
    {
        this.position.setPosition(x, y);
        this.hasChanged = true;
    }

    public void translate(float x, float y)
    {
        this.position.translate(x, y);
        this.hasChanged = true;
    }

    public float getX()
    {
        return this.position.x;
    }

    public float getY()
    {
        return this.position.y;
    }

    public void setOrigin(float x, float y)
    {
        this.origin.setPosition(x, y);
        this.hasChanged = true;
    }

    public float getOriginX()
    {
        return this.origin.x;
    }

    public float getOriginY()
    {
        return this.origin.y;
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
