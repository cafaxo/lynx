package com.cafaxo.lynx.render.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

public class TextureCubemap extends Texture
{

    public TextureCubemap(int size)
    {
        super(GL13.GL_TEXTURE_CUBE_MAP, GL14.GL_DEPTH_COMPONENT32, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, size, size);
    }

    public TextureCubemap(int internalFormat, int format, int type, int size)
    {
        super(GL13.GL_TEXTURE_CUBE_MAP, internalFormat, format, type, size, size);
    }

    @Override
    public void upload()
    {
        this.bind();

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        for (int face = 0; face < 6; face++)
        {
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0, this.internalFormat, this.width, this.height, 0, this.format, this.type, this.imageData);
        }
    }
}
