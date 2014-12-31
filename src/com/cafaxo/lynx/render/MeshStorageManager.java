package com.cafaxo.lynx.render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL15;

import com.cafaxo.lynx.render.VertexBufferManaged.DataBlock;

public class MeshStorageManager
{

    public class MeshStorageInfo
    {

        public DataBlock vertexDataBlock;

        public DataBlock indexDataBlock;

        public MeshStorageManager getStorageManager()
        {
            return MeshStorageManager.this;
        }

    }

    private VertexBufferManaged vertexBuffer;

    private VertexBufferManaged indexBuffer;

    private ArrayList<Mesh> meshList = new ArrayList<Mesh>();

    public MeshStorageManager(int vertexBufferSize, int indexBufferSize, int type)
    {
        this.vertexBuffer = new VertexBufferManaged(GL15.GL_ARRAY_BUFFER, type, vertexBufferSize);
        this.indexBuffer = new VertexBufferManaged(GL15.GL_ELEMENT_ARRAY_BUFFER, type, indexBufferSize);
    }

    public void addMesh(Mesh mesh)
    {
        MeshStorageInfo storageInfo = new MeshStorageInfo();

        storageInfo.vertexDataBlock = this.vertexBuffer.allocate(mesh.getVertexDataSizeLimit());
        storageInfo.indexDataBlock = this.indexBuffer.allocate(mesh.getIndexDataSizeLimit());

        mesh.setStorageInfo(storageInfo);

        this.meshList.add(mesh);
    }

    public void uploadAll()
    {
        this.bind();
        this.orphan();
        this.map();

        for (Mesh mesh : this.meshList)
        {
            mesh.upload();
        }

        this.unmap();
    }

    public void bind()
    {
        this.vertexBuffer.bind();
        this.indexBuffer.bind();
    }

    public void orphan()
    {
        this.vertexBuffer.orphan();
        this.indexBuffer.orphan();
    }

    public void map()
    {
        this.vertexBuffer.map();
        this.indexBuffer.map();
    }

    public void unmap()
    {
        this.vertexBuffer.unmap();
        this.indexBuffer.unmap();
    }

    public VertexBuffer getVertexBuffer()
    {
        return this.vertexBuffer;
    }

}
