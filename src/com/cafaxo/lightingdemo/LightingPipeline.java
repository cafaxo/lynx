package com.cafaxo.lightingdemo;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.cafaxo.lynx.graphics.OrthographicCamera;
import com.cafaxo.lynx.graphics.RenderEntity;
import com.cafaxo.lynx.graphics.RenderManager;
import com.cafaxo.lynx.graphics.RenderPass;
import com.cafaxo.lynx.graphics.ShaderProgram;
import com.cafaxo.lynx.graphics.Sprite;
import com.cafaxo.lynx.graphics.Texture;
import com.cafaxo.lynx.util.Color4f;
import com.cafaxo.lynx.util.DepthSorter;
import com.cafaxo.lynx.util.ShaderRegistry;
import com.cafaxo.lynx.util.Vector2f;

public class LightingPipeline
{

    private int width;

    private int height;

    private int frameBufferId;

    public OrthographicCamera camera, camera2;

    private Texture diffuseMap;

    private Texture diffuseMapBlurPass1;

    private Texture diffuseMapBlurPass2;

    private Texture occlusionMap;

    private Texture shadowMapInfo;

    private Texture shadowMap;

    private Texture shadowMapBlurPass1;

    private Texture shadowMapBlurPass2;

    private Texture finalMap;

    private FloatBuffer lightBuffer;

    private DepthSorter<Light> lights;

    private boolean blurView;

    private Vector2f viewSource;

    private Color4f ambientLightColor;

    private RenderPass diffusePass, diffuseBlurPass1, diffuseBlurPass2, occlusionPass, shadowInfoPass, shadowPass, shadowBlurPass1, shadowBlurPass2, finalPass, fxaaPass;

    public LightingPipeline(RenderManager renderManager, int width, int height, boolean blurView)
    {
        this.width = width;
        this.height = height;
        this.blurView = blurView;

        this.frameBufferId = GL30.glGenFramebuffers();

        this.diffuseMap = new Texture(width, height);
        this.diffuseMap.upload();

        if (blurView)
        {
            this.diffuseMapBlurPass1 = new Texture(width, height);
            this.diffuseMapBlurPass1.upload();

            this.diffuseMapBlurPass2 = new Texture(width, height);
            this.diffuseMapBlurPass2.upload();
        }

        this.occlusionMap = new Texture(width, height);
        this.occlusionMap.upload();

        this.shadowMapInfo = new Texture(256, 16);
        this.shadowMapInfo.upload();

        this.shadowMap = new Texture(width, height);
        this.shadowMap.upload();

        this.shadowMapBlurPass1 = new Texture(width, height);
        this.shadowMapBlurPass1.upload();

        this.shadowMapBlurPass2 = new Texture(width, height);
        this.shadowMapBlurPass2.upload();

        this.finalMap = new Texture(width, height);
        this.finalMap.upload();

        this.camera = new OrthographicCamera(0, width, 0, height);
        this.camera2 = new OrthographicCamera(0, width, 0, height);

        this.lightBuffer = BufferUtils.createFloatBuffer(16 * Light.SIZE);

        this.lights = new DepthSorter<Light>();

        this.viewSource = new Vector2f(0.f, 0.f);
        this.ambientLightColor = new Color4f(0.f, 0.f, 0.f, 1.0f);

        this.initDiffusePass(renderManager);

        if (this.blurView)
        {
            this.initDiffuseBlurPass(renderManager);
        }

        this.initOcclusionPass(renderManager);

        this.initShadowInfoPass(renderManager);

        this.initShadowPass(renderManager);

        this.initShadowBlurPass(renderManager);

        this.initFinalPass(renderManager);
    }

    public void render(RenderManager renderManager)
    {
        this.lightBuffer.clear();

        Iterator<Light> iter = this.lights.iterator();

        while (iter.hasNext())
        {
            iter.next().writeToFloatBuffer(this.lightBuffer);
        }

        this.lightBuffer.flip();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBufferId);

        renderManager.render(this.diffusePass);

        if (this.blurView)
        {
            renderManager.render(this.diffuseBlurPass1);
            renderManager.render(this.diffuseBlurPass2);
        }

        renderManager.render(this.occlusionPass);

        renderManager.render(this.shadowInfoPass);

        renderManager.render(this.shadowPass);

        renderManager.render(this.shadowBlurPass1);

        renderManager.render(this.shadowBlurPass2);

        renderManager.render(this.finalPass);

        renderManager.render(this.fxaaPass);
    }

    public void dumpToDisk()
    {
        try
        {
            ImageIO.write(this.diffuseMap.readImage(), "png", new File("/Users/cafaxo/Desktop/test/diffuseMap.png"));

            if (this.blurView)
            {
                ImageIO.write(this.diffuseMapBlurPass2.readImage(), "png", new File("/Users/cafaxo/Desktop/test/diffuseMapBlurred.png"));
            }

            ImageIO.write(this.occlusionMap.readImage(), "png", new File("/Users/cafaxo/Desktop/test/occlusionMap.png"));
            ImageIO.write(this.shadowMapInfo.readImage(), "png", new File("/Users/cafaxo/Desktop/test/shadowMapInfo.png"));
            ImageIO.write(this.shadowMap.readImage(), "png", new File("/Users/cafaxo/Desktop/test/shadowMap.png"));
            ImageIO.write(this.shadowMapBlurPass2.readImage(), "png", new File("/Users/cafaxo/Desktop/test/shadowMapBlurred.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void initDiffusePass(RenderManager renderManager)
    {
        this.diffusePass = new RenderPass(renderManager, RenderPass.Type.DYNAMIC)
        {

            @Override
            public void setPreRenderState()
            {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, LightingPipeline.this.diffuseMap.getId(), 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            @Override
            public void setPostRenderState()
            {
                GL11.glDisable(GL11.GL_BLEND);
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingPipeline.this.camera.getFloatBuffer());
            }

        };
    }

    private void initDiffuseBlurPass(RenderManager renderManager)
    {
        this.diffuseBlurPass1 = new RenderPass(renderManager, RenderPass.Type.STATIC)
        {

            @Override
            public void setPreRenderState()
            {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, LightingPipeline.this.diffuseMapBlurPass1.getId(), 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingPipeline.this.camera2.getFloatBuffer());
                GL20.glUniform2f(shaderProgram.getUniform("direction"), 1.f / (LightingPipeline.this.width), 0.f);
            }

        };

        this.diffuseBlurPass1.addEntity(new Sprite(ShaderRegistry.instance.get("blur"), this.diffuseMap));

        this.diffuseBlurPass2 = new RenderPass(renderManager, RenderPass.Type.STATIC)
        {

            @Override
            public void setPreRenderState()
            {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, LightingPipeline.this.diffuseMapBlurPass2.getId(), 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingPipeline.this.camera2.getFloatBuffer());
                GL20.glUniform2f(shaderProgram.getUniform("direction"), 0.f, 1.f / (LightingPipeline.this.height));
            }

        };

        this.diffuseBlurPass2.addEntity(new Sprite(ShaderRegistry.instance.get("blur"), this.diffuseMapBlurPass1));
    }

    private void initOcclusionPass(RenderManager renderManager)
    {
        this.occlusionPass = new RenderPass(renderManager, RenderPass.Type.DYNAMIC)
        {

            @Override
            public void setPreRenderState()
            {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, LightingPipeline.this.occlusionMap.getId(), 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingPipeline.this.camera.getFloatBuffer());
            }

        };
    }

    private void initShadowInfoPass(RenderManager renderManager)
    {
        this.shadowInfoPass = new RenderPass(renderManager, RenderPass.Type.STATIC)
        {

            @Override
            public void setPreRenderState()
            {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, LightingPipeline.this.shadowMapInfo.getId(), 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingPipeline.this.camera2.getFloatBuffer());
                GL20.glUniform1(shaderProgram.getUniform("lights[0]"), LightingPipeline.this.lightBuffer);
                GL20.glUniform1i(shaderProgram.getUniform("numLights"), LightingPipeline.this.lightBuffer.limit() / Light.SIZE);
                GL20.glUniform2f(shaderProgram.getUniform("shadowMapDimensions"), LightingPipeline.this.shadowMapInfo.getWidth(), LightingPipeline.this.shadowMapInfo.getHeight());
                GL20.glUniform2f(shaderProgram.getUniform("occlusionMapDimensions"), LightingPipeline.this.width, LightingPipeline.this.height);
            }
        };

        this.shadowInfoPass.addEntity(new Sprite(ShaderRegistry.instance.get("shadowMapInfo"), this.occlusionMap, this.shadowMapInfo.getWidth(), this.shadowMapInfo.getHeight()));
    }

    private void initShadowPass(RenderManager renderManager)
    {
        this.shadowPass = new RenderPass(renderManager, RenderPass.Type.STATIC)
        {

            @Override
            public void setPreRenderState()
            {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, LightingPipeline.this.shadowMap.getId(), 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingPipeline.this.camera2.getFloatBuffer());

                GL20.glUniform1(shaderProgram.getUniform("lights[0]"), LightingPipeline.this.lightBuffer);
                GL20.glUniform1i(shaderProgram.getUniform("numLights"), LightingPipeline.this.lightBuffer.limit() / Light.SIZE);
                GL20.glUniform2f(shaderProgram.getUniform("occlusionMapDimensions"), LightingPipeline.this.width, LightingPipeline.this.height);
            }

        };

        this.shadowPass.addEntity(new Sprite(ShaderRegistry.instance.get("shadowMap"), this.shadowMapInfo, this.width, this.height));
    }

    private void initShadowBlurPass(RenderManager renderManager)
    {
        this.shadowBlurPass1 = new RenderPass(renderManager, RenderPass.Type.STATIC)
        {

            @Override
            public void setPreRenderState()
            {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, LightingPipeline.this.shadowMapBlurPass1.getId(), 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingPipeline.this.camera2.getFloatBuffer());
                GL20.glUniform2f(shaderProgram.getUniform("direction"), 1.f / (LightingPipeline.this.width), 0.f);
            }

        };

        this.shadowBlurPass1.addEntity(new Sprite(ShaderRegistry.instance.get("blur"), this.shadowMap));

        this.shadowBlurPass2 = new RenderPass(renderManager, RenderPass.Type.STATIC)
        {

            @Override
            public void setPreRenderState()
            {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, LightingPipeline.this.shadowMapBlurPass2.getId(), 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingPipeline.this.camera2.getFloatBuffer());
                GL20.glUniform2f(shaderProgram.getUniform("direction"), 0.f, 1.f / (LightingPipeline.this.height));
            }

        };

        this.shadowBlurPass2.addEntity(new Sprite(ShaderRegistry.instance.get("blur"), this.shadowMapBlurPass1));
    }

    private void initFinalPass(RenderManager renderManager)
    {
        this.finalPass = new RenderPass(renderManager, RenderPass.Type.STATIC)
        {

            @Override
            public void setPreRenderState()
            {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, LightingPipeline.this.finalMap.getId(), 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingPipeline.this.camera2.getFloatBuffer());

                if (LightingPipeline.this.blurView)
                {
                    GL20.glUniform1i(shaderProgram.getUniform("blurView"), 1);
                    GL20.glUniform2f(shaderProgram.getUniform("sourceDimensions"), LightingPipeline.this.width, LightingPipeline.this.height);
                    GL20.glUniform2f(shaderProgram.getUniform("viewSource"), LightingPipeline.this.viewSource.getX(), LightingPipeline.this.viewSource.getY());
                    GL20.glUniform1f(shaderProgram.getUniform("viewDistance"), 128.f);
                    GL20.glUniform1f(shaderProgram.getUniform("viewFadeDistance"), 64.f);

                    GL20.glUniform1i(shaderProgram.getUniform("diffuseMap"), 0);
                    GL20.glUniform1i(shaderProgram.getUniform("diffuseMapBlurred"), 1);
                    GL20.glUniform1i(shaderProgram.getUniform("shadowMapBlurred"), 2);
                }
                else
                {
                    GL20.glUniform1i(shaderProgram.getUniform("blurView"), 0);

                    GL20.glUniform1i(shaderProgram.getUniform("diffuseMap"), 0);
                    GL20.glUniform1i(shaderProgram.getUniform("shadowMapBlurred"), 1);
                }

                GL20.glUniform4f(shaderProgram.getUniform("ambientLightColor"), LightingPipeline.this.ambientLightColor.r, LightingPipeline.this.ambientLightColor.g, LightingPipeline.this.ambientLightColor.b, LightingPipeline.this.ambientLightColor.a);
            }

        };

        Sprite finalPassSprite = new Sprite(ShaderRegistry.instance.get("final"));

        if (LightingPipeline.this.blurView)
        {
            finalPassSprite.setTextures(new Texture[] { this.diffuseMap, this.diffuseMapBlurPass2, this.shadowMapBlurPass2 });
        }
        else
        {
            finalPassSprite.setTextures(new Texture[] { this.diffuseMap, this.shadowMapBlurPass2 });
        }

        finalPassSprite.setTextureRegion(this.diffuseMap.getTextureRegion());
        finalPassSprite.setSize(this.width, this.height);

        this.finalPass.addEntity(finalPassSprite);

        this.fxaaPass = new RenderPass(renderManager, RenderPass.Type.STATIC)
        {

            @Override
            public void setPreRenderState()
            {
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingPipeline.this.camera2.getFloatBuffer());
                GL20.glUniform2f(shaderProgram.getUniform("texcoordOffset"), 1.f / (LightingPipeline.this.width), 1.f / (LightingPipeline.this.height));
            }

        };

        this.fxaaPass.addEntity(new Sprite(ShaderRegistry.instance.get("fxaa"), this.finalMap));
    }

    public DepthSorter<Light> getLights()
    {
        return this.lights;
    }

    public DepthSorter<RenderEntity> getDiffuseMapEntities()
    {
        return this.diffusePass.getEntities();
    }

    public DepthSorter<RenderEntity> getOcclusionMapEntities()
    {
        return this.occlusionPass.getEntities();
    }

    public Vector2f getViewSource()
    {
        return this.viewSource;
    }

    public Color4f getAmbientLightColor()
    {
        return this.ambientLightColor;
    }

    public OrthographicCamera getCamera()
    {
        return this.camera;
    }

}
