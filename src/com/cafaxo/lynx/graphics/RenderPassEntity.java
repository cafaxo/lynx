package com.cafaxo.lynx.graphics;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.cafaxo.lynx.util.DepthSorter;
import com.cafaxo.lynx.util.Queue;

public abstract class RenderPassEntity extends RenderPass
{

    private class Batch
    {

        public ShaderProgram shaderProgram;

        public Texture[] textures;

        public int indexCount;

        public long vboBufferOffset, iboBufferOffset;

    }

    private Queue<Batch> batchQueue = new Queue<Batch>(20);

    private DepthSorter<RenderEntity> entities = new DepthSorter<RenderEntity>();

    private VertexBuffer vertexBuffer;

    private VertexBuffer indexBuffer;

    public RenderPassEntity(RenderManager renderManager, Type type)
    {
        this.type = type;

        if (this.type == Type.DYNAMIC)
        {
            this.vertexBuffer = renderManager.dynamicVertexBuffer;
            this.indexBuffer = renderManager.dynamicIndexBuffer;
        }
        else if (this.type == Type.STATIC)
        {
            this.vertexBuffer = renderManager.staticVertexBuffer;
            this.indexBuffer = renderManager.staticIndexBuffer;
        }
        else
        {
            throw new RuntimeException("invalid type for RenderPassEntity");
        }
    }

    @Override
    public void refresh()
    {
        this.batchQueue.clear();

        RenderEntity entity = null;

        Iterator<RenderEntity> iter = this.entities.iterator();

        if (!iter.hasNext())
        {
            return;
        }

        entity = iter.next();

        while (entity != null)
        {
            Batch batch = new Batch();

            batch.shaderProgram = entity.getShaderProgram();
            batch.textures = entity.getTextures();
            batch.vboBufferOffset = this.vertexBuffer.currentPosition;
            batch.iboBufferOffset = this.indexBuffer.currentPosition;

            while (true)
            {
                if (!entity.isVisible())
                {
                    if (iter.hasNext())
                    {
                        entity = iter.next();
                    }
                    else
                    {
                        entity = null;
                        break;
                    }

                    continue;
                }

                if (entity.getShaderProgram() != batch.shaderProgram)
                {
                    break;
                }

                this.vertexBuffer.mappedBufferAsFloats.put(entity.getVertexData(), 0, entity.getVertexDataSize());

                for (int j = 0; j < entity.getIndexDataSize(); ++j)
                {
                    long vboNumber = ((this.vertexBuffer.currentPosition - batch.vboBufferOffset) * 4) / batch.shaderProgram.getBytesPerVertex();
                    this.indexBuffer.mappedBufferAsInts.put((int) vboNumber + entity.getIndexData()[j]);
                }

                this.vertexBuffer.currentPosition += entity.getVertexDataSize();
                this.indexBuffer.currentPosition += entity.getIndexDataSize();

                if (iter.hasNext())
                {
                    entity = iter.next();
                }
                else
                {
                    entity = null;
                    break;
                }
            }

            batch.indexCount = (int) (this.indexBuffer.currentPosition - batch.iboBufferOffset);
            this.batchQueue.add(batch);
        }

        this.hasChanged = false;
    }

    @Override
    public void render()
    {
        this.vertexBuffer.bind();
        this.indexBuffer.bind();

        this.setRenderState();

        this.batchQueue.reset();

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

    public abstract void setUniforms(ShaderProgram shaderProgram);

    public void setPostRenderState()
    {
    }

    public void addEntity(RenderEntity entity)
    {
        this.entities.add(entity);
        this.setHasChanged(true);
    }

    public void addEntitiesIntoLayer(int depth, ArrayList<RenderEntity> entities)
    {
        this.entities.addBulkIntoLayer(depth, entities);
        this.setHasChanged(true);
    }

    public void removeEntity(RenderEntity entity)
    {
        this.entities.remove(entity);
        this.setHasChanged(true);
    }

}
