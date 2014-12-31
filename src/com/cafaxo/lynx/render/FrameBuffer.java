package com.cafaxo.lynx.render;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import com.cafaxo.lynx.render.texture.Texture;

public class FrameBuffer
{

    protected int id;

    private int width;

    private int height;

    private IntBuffer drawBuffers;

    private Texture[] colorAttachments = new Texture[16];

    private Texture depthAttachment;

    private int numColorAttachments;

    public FrameBuffer(int width, int height)
    {
        this.width = width;
        this.height = height;

        this.id = GL30.glGenFramebuffers();
        this.drawBuffers = BufferUtils.createIntBuffer(16);
    }

    public void bind()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.id);
    }

    public void unbind()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void attach(int location, Texture texture, int target)
    {
        if ((texture.getWidth() != this.width) || (texture.getHeight() != this.height))
        {
            throw new IllegalArgumentException("texture dimensions must match framebuffer dimensions");
        }

        this.bind();

        if (location == GL30.GL_DEPTH_ATTACHMENT)
        {
            this.depthAttachment = texture;
        }
        else
        {
            int offset = location - GL30.GL_COLOR_ATTACHMENT0;

            if ((offset < 0) || (offset > 15))
            {
                throw new IllegalArgumentException("invalid location");
            }

            this.colorAttachments[offset] = texture;
            this.drawBuffers.put(offset, location);
            this.numColorAttachments++;
        }

        if (target == -1)
        {
            GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, location, texture.getId(), 0);
        }
        else
        {
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, location, target, texture.getId(), 0);
        }

        this.unbind();
    }

    public void attach(int location, Texture texture)
    {
        this.attach(location, texture, -1);
    }

    public void check()
    {
        switch (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER))
        {
        case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
        case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
        case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
        case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
        case GL30.GL_FRAMEBUFFER_UNSUPPORTED:
            throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED");
        case GL30.GL_FRAMEBUFFER_COMPLETE:
            return;
        }

        throw new RuntimeException("unknown framebuffer status");
    }

    public void activate()
    {
        this.bind();
        this.check();

        GL11.glViewport(0, 0, this.width, this.height);

        if ((this.numColorAttachments == 0) && (this.depthAttachment != null))
        {
            GL11.glDrawBuffer(GL11.GL_NONE);
            GL11.glReadBuffer(GL11.GL_NONE);
        }
        else
        {
            this.drawBuffers.limit(this.numColorAttachments);
            GL20.glDrawBuffers(this.drawBuffers);
        }
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public Texture getAttachedColorTexture(int i)
    {
        return this.colorAttachments[i];
    }

}
