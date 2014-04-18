package com.cafaxo.lynx.graphics;

import com.cafaxo.lynx.util.PackerNode;

public class TextureSheetNode implements Comparable<TextureSheetNode>
{

    public Image image;

    public PackerNode node;

    public TextureRegion textureRegion;

    public TextureSheetNode(Image image)
    {
        this.image = image;
    }

    @Override
    public int compareTo(TextureSheetNode other)
    {
        int size = this.image.width * this.image.height;
        int otherSize = other.image.width * other.image.height;

        if (size < otherSize)
        {
            return -1;
        }

        if (size > otherSize)
        {
            return 1;
        }

        return 0;
    }

}
