package com.cafaxo.lynx.graphics;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import com.cafaxo.lynx.util.Queue;

public class RenderManager
{

    private class Batch
    {

        public ShaderProgram shaderProgram;

        public Texture[] textures;

        public int iboBufferOffset, indexCount;

        public int vboBufferOffset;

    }

    public abstract class Pass
    {

        private Queue<Batch> batchQueue = new Queue<Batch>(20);

        public abstract void setPreRenderState();

        public abstract void setUniforms(ShaderProgram shaderProgram);

        public Pass(RenderEntity[] entities, int offset, int count)
        {
            if (!RenderManager.this.isInUse)
            {
                throw new RuntimeException("render batch is not in use");
            }

            if ((RenderManager.this.currentVboPosition >= RenderManager.this.vboSize) || (RenderManager.this.currentIboPosition >= RenderManager.this.iboSize))
            {
                throw new RuntimeException("render batch size exceeded limit");
            }

            RenderEntity entity = null;

            for (int i = offset; i < (offset + count);)
            {
                entity = entities[i];

                Batch batch = new Batch();

                batch.shaderProgram = entity.getShaderProgram();
                batch.textures = entity.getTextures();
                batch.vboBufferOffset = RenderManager.this.currentVboPosition;
                batch.iboBufferOffset = RenderManager.this.currentIboPosition;

                for (; i < (offset + count); ++i)
                {
                    entity = entities[i];

                    if (!entity.isVisible())
                    {
                        continue;
                    }

                    if (entity.getShaderProgram() != batch.shaderProgram)
                    {
                        break;
                    }

                    RenderManager.this.mappedVertexBufferAsFloats.put(entity.getVertexData(), 0, entity.getVertexDataSize());

                    for (int j = 0; j < entity.getIndexDataSize(); ++j)
                    {
                        int vboNumber = ((RenderManager.this.currentVboPosition - batch.vboBufferOffset) * 4) / batch.shaderProgram.getBytesPerVertex();
                        RenderManager.this.mappedIndexBufferAsInts.put(vboNumber + entity.getIndexData()[j]);
                    }

                    RenderManager.this.currentVboPosition += entity.getVertexDataSize();
                    RenderManager.this.currentIboPosition += entity.getIndexDataSize();
                }

                batch.indexCount = RenderManager.this.currentIboPosition - batch.iboBufferOffset;
                this.batchQueue.add(batch);
            }

            RenderManager.this.passQueue.add(this);
        }

        public Pass(RenderEntity[] entities)
        {
            this(entities, 0, entities.length);
        }

        public Pass(RenderEntity entity)
        {
            this(new RenderEntity[] { entity });
        }

        public void render()
        {
            this.setPreRenderState();

            while (this.batchQueue.hasNext())
            {
                Batch batch = this.batchQueue.pop();

                batch.shaderProgram.bind();
                batch.shaderProgram.bindAttributes(batch.vboBufferOffset * 4);

                this.setUniforms(batch.shaderProgram);

                if (batch.textures != null)
                {
                    for (int i = 0; i < batch.textures.length; ++i)
                    {
                        GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
                        batch.textures[i].bind();
                    }
                }

                GL11.glDrawElements(GL11.GL_TRIANGLES, batch.indexCount, GL11.GL_UNSIGNED_INT, batch.iboBufferOffset * 4);
            }

            this.setPostRenderState();
        }

        public void setPostRenderState()
        {
        }

    }

    private int vertexBufferId;

    private int indexBufferId;

    private boolean isInUse;

    private ByteBuffer mappedVertexBuffer;

    private FloatBuffer mappedVertexBufferAsFloats;

    private ByteBuffer mappedIndexBuffer;

    private IntBuffer mappedIndexBufferAsInts;

    private long vboSize;

    private long iboSize;

    private int currentVboPosition, currentIboPosition;

    private Queue<Pass> passQueue;

    public RenderManager(int vboSize, int iboSize)
    {
        this.vboSize = vboSize;
        this.iboSize = iboSize;

        this.vertexBufferId = GL15.glGenBuffers();
        this.indexBufferId = GL15.glGenBuffers();

        this.passQueue = new Queue<Pass>(20);
    }

    public void begin()
    {
        if (this.isInUse)
        {
            throw new RuntimeException("render batch is already in use");
        }

        this.isInUse = true;

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vertexBufferId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);

        this.orphanAndRemapBuffers();
    }

    public void end()
    {
        this.isInUse = false;

        this.mappedVertexBufferAsFloats.flip();
        this.mappedIndexBufferAsInts.flip();

        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
        GL15.glUnmapBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vertexBufferId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);

        while (this.passQueue.hasNext())
        {
            Pass pass = this.passQueue.pop();
            pass.render();
        }

        this.passQueue.clear();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void orphanAndRemapBuffers()
    {
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.vboSize, GL15.GL_STREAM_DRAW);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, this.iboSize, GL15.GL_STREAM_DRAW);

        this.mappedVertexBuffer = GL15.glMapBuffer(GL15.GL_ARRAY_BUFFER, GL15.GL_WRITE_ONLY, this.vboSize, null);
        this.mappedVertexBufferAsFloats = this.mappedVertexBuffer.asFloatBuffer();
        this.currentVboPosition = 0;

        this.mappedIndexBuffer = GL15.glMapBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, GL15.GL_WRITE_ONLY, this.iboSize, null);
        this.mappedIndexBufferAsInts = this.mappedIndexBuffer.asIntBuffer();
        this.currentIboPosition = 0;
    }

}
