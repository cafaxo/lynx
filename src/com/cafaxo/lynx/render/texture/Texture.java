package com.cafaxo.lynx.render.texture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

public class Texture
{

    protected int width, height;

    protected final int id;

    protected int target;

    protected int internalFormat;

    protected int format;

    protected int type;

    protected ByteBuffer imageData;

    public Texture(int target, int internalFormat, int format, int type, int width, int height)
    {
        this.target = target;
        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
        this.width = width;
        this.height = height;

        this.id = GL11.glGenTextures();
    }

    public Texture(int internalFormat, int format, int type, int width, int height)
    {
        this(GL11.GL_TEXTURE_2D, internalFormat, format, type, width, height);
    }

    public Texture(int width, int height)
    {
        this(GL11.GL_TEXTURE_2D, GL11.GL_RGBA16, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, width, height);
    }

    public Texture(Image image)
    {
        this(GL11.GL_TEXTURE_2D, GL11.GL_RGBA16, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image.getWidth(), image.getHeight());

        this.imageData = image.getImageData();
    }

    public void upload()
    {
        this.bind();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, this.internalFormat, this.width, this.height, 0, this.format, this.type, this.imageData);
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
        GL11.glBindTexture(this.target, this.id);
    }

    public int getId()
    {
        return this.id;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public BufferedImage readImage()
    {
        this.bind();

        if ((this.internalFormat == GL11.GL_RGBA16) && (this.format == GL11.GL_RGBA) && (this.type == GL11.GL_UNSIGNED_BYTE))
        {
            ByteBuffer imageData = BufferUtils.createByteBuffer(this.width * this.height * 4);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);

            BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);

            int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

            for (int x = 0; x < this.width; x++)
            {
                for (int y = 0; y < this.height; y++)
                {
                    int i = (x + (this.width * y)) * 4;
                    int r = imageData.get(i) & 0xff;
                    int g = imageData.get(i + 1) & 0xff;
                    int b = imageData.get(i + 2) & 0xff;
                    int a = imageData.get(i + 3) & 0xff;

                    pixels[x + (this.width * (this.height - y - 1))] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }

            return image;
        }
        else if (this instanceof TextureCubemap)
        {
            FloatBuffer imageData = BufferUtils.createFloatBuffer(this.width * this.height);
            System.out.println(GL11.glGetError());
            GL11.glGetTexImage(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, imageData);
            System.out.println(GL11.glGetError());
            BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);

            int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

            for (int x = 0; x < this.width; x++)
            {
                for (int y = 0; y < this.height; y++)
                {
                    int i = (x + (this.width * y));

                    int r = (int) (imageData.get(i) * 255.f * 10.f);

                    if (r > 255)
                    {
                        r = 255;
                    }
                    int g = r;
                    int b = g;
                    int a = 255;

                    pixels[x + (this.width * (this.height - y - 1))] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }

            return image;
        }

        throw new UnsupportedOperationException();
    }
}
