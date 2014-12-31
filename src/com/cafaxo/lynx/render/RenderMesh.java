package com.cafaxo.lynx.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.cafaxo.lynx.math.Matrix4x4f;
import com.cafaxo.lynx.render.shader.IUniformData;
import com.cafaxo.lynx.render.shader.ShaderProgram;
import com.cafaxo.lynx.render.texture.Texture;

public class RenderMesh
{

    protected Mesh mesh;

    public Matrix4x4f transformation;

    public RenderMesh(Mesh mesh)
    {
        this.mesh = mesh;

        this.transformation = new Matrix4x4f();
        this.transformation.setIdentity();
    }

    public void render(ShaderProgram shaderProgram, IUniformData uniformData)
    {
        this.mesh.getStorageInfo().getStorageManager().bind();

        shaderProgram.bind();
        shaderProgram.bindAttributes(this.mesh.getVertexDataOffset());

        shaderProgram.bindUniforms(this, uniformData);

        GL11.glDrawElements(GL11.GL_TRIANGLES, this.mesh.getIndexCount(), GL11.GL_UNSIGNED_INT, this.mesh.getStorageInfo().indexDataBlock.offset);
    }

    public void render(ShaderProgram shaderProgram, IUniformData uniformData, Texture texture)
    {
        texture.bind();

        this.render(shaderProgram, uniformData);
    }

    public void render(ShaderProgram shaderProgram, IUniformData uniformData, Texture[] textures)
    {
        for (int i = 0; i < textures.length; ++i)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
            textures[i].bind();
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        this.render(shaderProgram, uniformData);
    }

}
