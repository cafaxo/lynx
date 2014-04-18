package com.cafaxo.lynx.graphics;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.cafaxo.lynx.util.ResourceLocation;

public class Texture extends Image
{

    protected final int id;

    public Texture(ResourceLocation textureSource)
    {
        super(textureSource);

        this.id = GL11.glGenTextures();
    }

    public Texture(int width, int height)
    {
        super(width, height);

        this.id = GL11.glGenTextures();
    }

    public void upload()
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA16, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageData);
    }

    public TextureRegion getTextureRegion(int x, int y, int width, int height)
    {
        TextureRegion textureRegion = new TextureRegion();

        textureRegion.u1 = (float) x / (float) this.width;
        textureRegion.v1 = (float) y / (float) this.height;

        textureRegion.u2 = ((float) (x + width)) / (float) this.width;
        textureRegion.v2 = textureRegion.v1;

        textureRegion.u3 = textureRegion.u2;
        textureRegion.v3 = ((float) (y + height)) / (float) this.height;

        textureRegion.u4 = textureRegion.u1;
        textureRegion.v4 = textureRegion.v3;

        textureRegion.width = width;
        textureRegion.height = height;

        return textureRegion;
    }

    public TextureRegion getTextureRegion()
    {
        return this.getTextureRegion(0, 0, this.width, this.height);
    }

    public void bind()
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
    }

    public int getId()
    {
        return this.id;
    }

    public BufferedImage readImage()
    {
        this.bind();

        this.imageData.clear();
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageData);

        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);

        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        for (int x = 0; x < this.width; x++)
        {
            for (int y = 0; y < this.height; y++)
            {
                int i = (x + (this.width * y)) * 4;
                int r = this.imageData.get(i) & 0xff;
                int g = this.imageData.get(i + 1) & 0xff;
                int b = this.imageData.get(i + 2) & 0xff;
                int a = this.imageData.get(i + 3) & 0xff;

                pixels[x + (this.width * (this.height - y - 1))] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        return image;
    }
}
