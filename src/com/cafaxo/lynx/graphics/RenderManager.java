package com.cafaxo.lynx.graphics;

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

        public int indexCount;

        public long vboBufferOffset, iboBufferOffset;

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

            RenderEntity entity = null;

            for (int i = offset; i < (offset + count);)
            {
                entity = entities[i];

                Batch batch = new Batch();

                batch.shaderProgram = entity.getShaderProgram();
                batch.textures = entity.getTextures();
                batch.vboBufferOffset = RenderManager.this.dynamicVertexBuffer.currentPosition;
                batch.iboBufferOffset = RenderManager.this.dynamicIndexBuffer.currentPosition;

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

                    RenderManager.this.dynamicVertexBuffer.mappedBufferAsFloats.put(entity.getVertexData(), 0, entity.getVertexDataSize());

                    for (int j = 0; j < entity.getIndexDataSize(); ++j)
                    {
                        long vboNumber = ((RenderManager.this.dynamicVertexBuffer.currentPosition - batch.vboBufferOffset) * 4) / batch.shaderProgram.getBytesPerVertex();
                        RenderManager.this.dynamicIndexBuffer.mappedBufferAsInts.put((int) vboNumber + entity.getIndexData()[j]);
                    }

                    RenderManager.this.dynamicVertexBuffer.currentPosition += entity.getVertexDataSize();
                    RenderManager.this.dynamicIndexBuffer.currentPosition += entity.getIndexDataSize();
                }

                batch.indexCount = (int) (RenderManager.this.dynamicIndexBuffer.currentPosition - batch.iboBufferOffset);
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

    private VertexBuffer dynamicVertexBuffer;

    private VertexBuffer dynamicIndexBuffer;

    private boolean isInUse;

    private Queue<Pass> passQueue;

    public RenderManager(long vboSize, long iboSize)
    {
        this.dynamicVertexBuffer = new VertexBuffer(GL15.GL_ARRAY_BUFFER, GL15.GL_STREAM_DRAW, vboSize);
        this.dynamicIndexBuffer = new VertexBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, GL15.GL_STREAM_DRAW, iboSize);

        this.passQueue = new Queue<Pass>(20);
    }

    public void begin()
    {
        if (this.isInUse)
        {
            throw new RuntimeException("render batch is already in use");
        }

        this.isInUse = true;

        this.dynamicVertexBuffer.orphan();
        this.dynamicIndexBuffer.orphan();

        this.dynamicVertexBuffer.map();
        this.dynamicIndexBuffer.map();
    }

    public void end()
    {
        this.isInUse = false;

        this.dynamicVertexBuffer.unmap();
        this.dynamicIndexBuffer.unmap();

        this.dynamicVertexBuffer.bind();
        this.dynamicIndexBuffer.bind();

        while (this.passQueue.hasNext())
        {
            Pass pass = this.passQueue.pop();
            pass.render();
        }

        this.passQueue.clear();

        this.dynamicVertexBuffer.unbind();
        this.dynamicIndexBuffer.unbind();
    }

}
