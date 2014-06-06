package com.cafaxo.lynx;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;

public class VertexBuffer
{

    public int id;

    public int target;

    public int type;

    public ByteBuffer mappedBuffer;

    public FloatBuffer mappedBufferAsFloats;

    public IntBuffer mappedBufferAsInts;

    public long size;

    public long currentPosition;

    public VertexBuffer(int target, int type, long size)
    {
        this.target = target;
        this.type = type;
        this.size = size;

        this.id = GL15.glGenBuffers();
    }

    public void bind()
    {
        GL15.glBindBuffer(this.target, this.id);
    }

    public void unbind()
    {
        GL15.glBindBuffer(this.target, 0);
    }

    public void orphan()
    {
        this.bind();

        GL15.glBufferData(this.target, this.size, this.type);
    }

    public void map()
    {
        this.bind();

        this.mappedBuffer = GL15.glMapBuffer(this.target, GL15.GL_WRITE_ONLY, this.size, null);
        this.mappedBufferAsFloats = this.mappedBuffer.asFloatBuffer();
        this.mappedBufferAsInts = this.mappedBuffer.asIntBuffer();

        this.currentPosition = 0;
    }

    public void unmap()
    {
        this.bind();

        GL15.glUnmapBuffer(this.target);

        this.mappedBuffer = null;
        this.mappedBufferAsFloats = null;
        this.mappedBufferAsInts = null;
    }

}
