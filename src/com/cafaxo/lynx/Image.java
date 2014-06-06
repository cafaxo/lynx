package com.cafaxo.lynx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import com.cafaxo.lynx.util.ResourceLocation;

public class Image
{
    protected int width, height;

    protected ByteBuffer imageData;

    protected ResourceLocation source;

    public Image(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.imageData = BufferUtils.createByteBuffer(this.width * this.height * 4);
        this.source = null;
    }

    public Image(ResourceLocation source)
    {
        this.source = source;

        BufferedImage image = null;

        try
        {
            image = ImageIO.read(source.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        this.width = image.getWidth();
        this.height = image.getHeight();

        this.init(image, 0, 0, image.getWidth(), image.getHeight());
    }

    public Image(ResourceLocation source, BufferedImage image, int xOffset, int yOffset, int width, int height)
    {
        this.source = source;

        this.init(image, xOffset, yOffset, width, height);
    }

    private void init(BufferedImage image, int xOffset, int yOffset, int width, int height)
    {
        this.width = width;
        this.height = height;

        int[] pixels = new int[this.width * this.height];
        image.getRGB(xOffset, yOffset, this.width, this.height, pixels, 0, this.width);

        this.imageData = BufferUtils.createByteBuffer(this.width * this.height * 4);

        for (int y = this.height - 1; y >= 0; y--)
        {
            for (int x = 0; x < this.width; x++)
            {
                int pixel = pixels[(y * this.width) + x];
                this.imageData.put((byte) ((pixel >> 16) & 0xFF));
                this.imageData.put((byte) ((pixel >> 8) & 0xFF));
                this.imageData.put((byte) (pixel & 0xFF));
                this.imageData.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        this.imageData.flip();
    }

    public void setPixel(int x, int y, int pixel)
    {
        int i = (y * this.width) * 4;

        this.imageData.put(i, (byte) ((pixel >> 24) & 0xFF));
        ++i;

        this.imageData.put(i, (byte) ((pixel >> 16) & 0xFF));
        ++i;

        this.imageData.put(i, (byte) ((pixel >> 8) & 0xFF));
        ++i;

        this.imageData.put(i, (byte) (pixel & 0xFF));
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }
}
