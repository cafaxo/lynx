package com.cafaxo.lynx.util;

import java.util.ArrayList;
import java.util.Collections;

import com.cafaxo.lynx.graphics.TextureSheetNode;

public class Packer
{
    private PackerNode root;

    public Packer(int width, int height)
    {
        this.root = new PackerNode(0, 0, width, height);
    }

    public void fit(ArrayList<TextureSheetNode> imageContainers)
    {
        Collections.sort(imageContainers);

        for (TextureSheetNode imageContainer : imageContainers)
        {
            PackerNode node = this.findNode(this.root, imageContainer.image.getWidth(), imageContainer.image.getHeight());

            if (node != null)
            {
                imageContainer.node = this.splitNode(node, imageContainer.image.getWidth(), imageContainer.image.getHeight());
            }
        }
    }

    public PackerNode findNode(PackerNode root, int width, int height)
    {
        if (root.used)
        {
            PackerNode node;

            if ((node = this.findNode(root.right, width, height)) != null)
            {
                return node;
            }
            else if ((node = this.findNode(root.down, width, height)) != null)
            {
                return node;
            }
        }
        else if ((width <= root.width) && (height <= root.height))
        {
            return root;
        }

        return null;
    }

    public PackerNode splitNode(PackerNode node, int width, int height)
    {
        node.used = true;
        node.down = new PackerNode(node.x, node.y + height, node.width, node.height - height);
        node.right = new PackerNode(node.x + width, node.y, node.width - width, height);

        return node;
    }
}
