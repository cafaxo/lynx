package com.cafaxo.lynx.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import com.cafaxo.lynx.render.MeshStorageManager.MeshStorageInfo;

public class Mesh
{

    private MeshStorageInfo storageInfo;

    protected FloatBuffer vertexData;

    protected IntBuffer indexData;

    public Mesh()
    {
    }

    public Mesh(int vertexDataLimit, int indexDataLimit)
    {
        this.createBuffers(vertexDataLimit, indexDataLimit);
    }

    public void createBuffers(int vertexDataLimit, int indexDataLimit)
    {
        this.vertexData = BufferUtils.createFloatBuffer(vertexDataLimit);
        this.indexData = BufferUtils.createIntBuffer(indexDataLimit);
    }

    public void setStorageInfo(MeshStorageInfo storageInfo)
    {
        this.storageInfo = storageInfo;
    }

    protected void addVertexData(final float vertexData)
    {
        this.vertexData.put(vertexData);
    }

    protected void addIndexData(final int indexData)
    {
        this.indexData.put(indexData);
    }

    public final int getVertexDataSizeLimit()
    {
        return this.vertexData.capacity() * 4;
    }

    public final int getIndexDataSizeLimit()
    {
        return this.indexData.capacity() * 4;
    }

    public final int getIndexCount()
    {
        return this.indexData.limit();
    }

    public int getVertexDataOffset()
    {
        return this.storageInfo.vertexDataBlock.offset;
    }

    public void upload()
    {
        this.vertexData.flip();
        this.indexData.flip();

        this.storageInfo.vertexDataBlock.upload(this.vertexData);
        this.storageInfo.indexDataBlock.upload(this.indexData);
    }

    public MeshStorageInfo getStorageInfo()
    {
        return this.storageInfo;
    }

    public VertexBuffer getVertexBuffer()
    {
        return this.storageInfo.getStorageManager().getVertexBuffer();
    }

}
