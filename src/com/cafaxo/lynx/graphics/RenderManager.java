package com.cafaxo.lynx.graphics;

import org.lwjgl.opengl.GL15;

import com.cafaxo.lynx.util.Queue;

public class RenderManager
{

    VertexBuffer dynamicVertexBuffer;

    VertexBuffer dynamicIndexBuffer;

    VertexBuffer staticVertexBuffer;

    VertexBuffer staticIndexBuffer;

    private Queue<RenderPass> passQueue;

    private Queue<RenderPass> staticPassQueue;

    private Queue<RenderPass> oldStaticPassQueue;

    private boolean passAssemblyInProgress;

    public RenderManager(long vboSize, long iboSize)
    {
        this.dynamicVertexBuffer = new VertexBuffer(GL15.GL_ARRAY_BUFFER, GL15.GL_STREAM_DRAW, vboSize);
        this.dynamicIndexBuffer = new VertexBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, GL15.GL_STREAM_DRAW, iboSize);

        this.staticVertexBuffer = new VertexBuffer(GL15.GL_ARRAY_BUFFER, GL15.GL_STATIC_DRAW, vboSize);
        this.staticIndexBuffer = new VertexBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, GL15.GL_STATIC_DRAW, iboSize);

        this.passQueue = new Queue<RenderPass>(20);
    }

    public void begin()
    {
        this.oldStaticPassQueue = this.staticPassQueue;
        this.staticPassQueue = new Queue<RenderPass>(20);

        this.passAssemblyInProgress = true;
    }

    public void render(RenderPass pass)
    {
        this.passQueue.add(pass);

        if (pass.getType() == RenderPass.Type.STATIC)
        {
            this.staticPassQueue.add(pass);
        }
    }

    public void end()
    {
        if (!this.passAssemblyInProgress)
        {
            throw new RuntimeException("pass assembly was not in progress");
        }

        this.passAssemblyInProgress = false;

        // detect changes in static passes
        boolean staticPassesChanged = false;

        if ((this.oldStaticPassQueue != null) && (this.staticPassQueue.getSize() == this.oldStaticPassQueue.getSize()))
        {

            for (int i = 0; i < this.staticPassQueue.getSize(); ++i)
            {
                if (this.staticPassQueue.get(i).equals(this.oldStaticPassQueue.get(i)))
                {
                    if (this.staticPassQueue.get(i).hasChanged())
                    {
                        staticPassesChanged = true;
                        break;
                    }
                }
                else
                {
                    staticPassesChanged = true;
                    break;
                }
            }
        }
        else
        {
            staticPassesChanged = true;
        }

        this.dynamicVertexBuffer.orphan();
        this.dynamicIndexBuffer.orphan();

        this.dynamicVertexBuffer.map();
        this.dynamicIndexBuffer.map();

        this.passQueue.reset();

        while (this.passQueue.hasNext())
        {
            RenderPass pass = this.passQueue.pop();

            if (pass.getType() == RenderPass.Type.DYNAMIC)
            {
                pass.refreshBatchQueue();
            }
        }

        this.dynamicVertexBuffer.unmap();
        this.dynamicIndexBuffer.unmap();

        if (staticPassesChanged)
        {
            this.staticVertexBuffer.orphan();
            this.staticIndexBuffer.orphan();

            this.staticVertexBuffer.map();
            this.staticIndexBuffer.map();

            this.passQueue.reset();

            while (this.passQueue.hasNext())
            {
                RenderPass pass = this.passQueue.pop();

                if (pass.getType() == RenderPass.Type.STATIC)
                {
                    pass.refreshBatchQueue();
                }
            }

            System.out.println("sent static entities to GPU. if you see this message too many times, you are doing something wrong!");

            this.staticVertexBuffer.unmap();
            this.staticIndexBuffer.unmap();
        }

        this.passQueue.reset();

        while (this.passQueue.hasNext())
        {
            RenderPass pass = this.passQueue.pop();
            pass.render();
        }

        this.passQueue.clear();
    }

}
