package com.cafaxo.lightingdemo3d;

import org.lwjgl.opengl.GL20;

import com.cafaxo.lynx.graphics.RenderManager;
import com.cafaxo.lynx.graphics.RenderPass;
import com.cafaxo.lynx.graphics.RenderPassEntity;
import com.cafaxo.lynx.graphics.ShaderProgram;
import com.cafaxo.lynx.util.CameraFps;

public class LightingPipeline
{

    private RenderManager renderManager;

    private CameraFps camera;

    public RenderPassEntity diffusePass;

    public LightingPipeline(RenderManager renderManager, int width, int height)
    {
        this.camera = new CameraFps((float) Math.toRadians(50), (float) width / height, 1.f, 1000.f);

        this.renderManager = renderManager;

        this.diffusePass = new RenderPassEntity(this.renderManager, RenderPass.Type.STATIC)
        {

            @Override
            public void setRenderState()
            {
            }

            @Override
            public void setPostRenderState()
            {
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("projection"), false, LightingPipeline.this.camera.getProjection().getBuffer());
                GL20.glUniformMatrix4(shaderProgram.getUniform("view"), false, LightingPipeline.this.camera.getView().getBuffer());
            }

        };
    }

    public void render()
    {
        this.renderManager.render(this.diffusePass);
    }

    public CameraFps getCamera()
    {
        return this.camera;
    }

}
