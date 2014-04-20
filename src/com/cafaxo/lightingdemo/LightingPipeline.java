package com.cafaxo.lightingdemo;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.cafaxo.lynx.graphics.OrthographicCamera;
import com.cafaxo.lynx.graphics.RenderEntity;
import com.cafaxo.lynx.graphics.RenderManager;
import com.cafaxo.lynx.graphics.ShaderProgram;
import com.cafaxo.lynx.graphics.Sprite;
import com.cafaxo.lynx.graphics.Texture;
import com.cafaxo.lynx.util.Color4f;
import com.cafaxo.lynx.util.ShaderRegistry;
import com.cafaxo.lynx.util.SortedPool;
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

    private Texture finalPass;

    private Sprite sprite;

    private FloatBuffer lightBuffer;

    private SortedPool<Light> lights;

    private SortedPool<RenderEntity> diffuseMapEntities;

    private SortedPool<RenderEntity> occlusionMapEntities;

    private boolean blurView;

    private Vector2f viewSource;

    private Color4f ambientLightColor;

    public LightingPipeline(int width, int height, boolean blurView)
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

        this.finalPass = new Texture(width, height);
        this.finalPass.upload();

        this.camera = new OrthographicCamera(0, width, 0, height);
        this.camera2 = new OrthographicCamera(0, width, 0, height);

        this.sprite = new Sprite(null);

        this.lightBuffer = BufferUtils.createFloatBuffer(16 * Light.SIZE);

        this.lights = new SortedPool<Light>(new Light[16]);
        this.diffuseMapEntities = new SortedPool<RenderEntity>(new RenderEntity[1000]);
        this.occlusionMapEntities = new SortedPool<RenderEntity>(new RenderEntity[1000]);

        this.viewSource = new Vector2f(0.f, 0.f);
        this.ambientLightColor = new Color4f(0.f, 0.f, 0.f, 1.0f);
    }

    public void render(RenderManager renderManager)
    {
        this.lightBuffer.clear();

        for (int i = 0; i < this.lights.getSize(); ++i)
        {
            this.lights.get(i).writeToFloatBuffer(this.lightBuffer);
        }

        this.lightBuffer.flip();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBufferId);

        this.renderDiffuseMap(renderManager);

        if (this.blurView)
        {
            this.renderDiffuseMapBlur(renderManager);
        }

        this.renderOcclusionMap(renderManager);

        this.renderShadowMapInfo(renderManager);

        this.renderShadowMap(renderManager);

        this.renderShadowMapBlur(renderManager);

        this.renderFinal(renderManager);
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

    private void renderDiffuseMap(RenderManager renderManager)
    {
        renderManager.new Pass(this.diffuseMapEntities.getBuffer(), 0, this.diffuseMapEntities.getSize())
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

    private void renderDiffuseMapBlur(RenderManager renderManager)
    {
        this.sprite.setTexture(this.diffuseMap);
        this.sprite.setTextureRegion(this.diffuseMap.getTextureRegion());
        this.sprite.setSize(this.width, this.height);
        this.sprite.setShaderProgram(ShaderRegistry.instance.get("blur"));

        renderManager.new Pass(this.sprite)
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

        this.sprite.setTexture(this.diffuseMapBlurPass1);
        this.sprite.setTextureRegion(this.diffuseMapBlurPass1.getTextureRegion());

        renderManager.new Pass(this.sprite)
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
    }

    private void renderOcclusionMap(RenderManager renderManager)
    {
        renderManager.new Pass(this.occlusionMapEntities.getBuffer(), 0, this.occlusionMapEntities.getSize())
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

    private void renderShadowMapInfo(RenderManager renderManager)
    {
        this.sprite.setTexture(this.occlusionMap);
        this.sprite.setTextureRegion(this.occlusionMap.getTextureRegion());
        this.sprite.setSize(this.shadowMapInfo.getWidth(), this.shadowMapInfo.getHeight());
        this.sprite.setShaderProgram(ShaderRegistry.instance.get("shadowMapInfo"));

        renderManager.new Pass(this.sprite)
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
    }

    private void renderShadowMap(RenderManager renderManager)
    {
        this.sprite.setTexture(this.shadowMapInfo);
        this.sprite.setTextureRegion(this.shadowMapInfo.getTextureRegion());
        this.sprite.setSize(this.width, this.height);
        this.sprite.setShaderProgram(ShaderRegistry.instance.get("shadowMap"));

        renderManager.new Pass(this.sprite)
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
    }

    private void renderShadowMapBlur(RenderManager renderManager)
    {
        this.sprite.setTexture(this.shadowMap);
        this.sprite.setTextureRegion(this.shadowMap.getTextureRegion());
        this.sprite.setSize(this.width, this.height);
        this.sprite.setShaderProgram(ShaderRegistry.instance.get("blur"));

        renderManager.new Pass(this.sprite)
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

        this.sprite.setTexture(this.shadowMapBlurPass1);
        this.sprite.setTextureRegion(this.shadowMapBlurPass1.getTextureRegion());

        renderManager.new Pass(this.sprite)
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
    }

    private void renderFinal(RenderManager renderManager)
    {
        if (LightingPipeline.this.blurView)
        {
            this.sprite.setTextures(new Texture[] { this.diffuseMap, this.diffuseMapBlurPass2, this.shadowMapBlurPass2 });
        }
        else
        {
            this.sprite.setTextures(new Texture[] { this.diffuseMap, this.shadowMapBlurPass2 });
        }

        this.sprite.setTextureRegion(this.diffuseMap.getTextureRegion());
        this.sprite.setSize(this.width, this.height);
        this.sprite.setShaderProgram(ShaderRegistry.instance.get("final"));

        renderManager.new Pass(this.sprite)
        {

            @Override
            public void setPreRenderState()
            {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, LightingPipeline.this.finalPass.getId(), 0);
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

        this.sprite.setTexture(this.finalPass);
        this.sprite.setTextureRegion(this.finalPass.getTextureRegion());
        this.sprite.setSize(this.width, this.height);
        this.sprite.setShaderProgram(ShaderRegistry.instance.get("fxaa"));

        renderManager.new Pass(this.sprite)
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
    }

    public SortedPool<Light> getLights()
    {
        return this.lights;
    }

    public SortedPool<RenderEntity> getDiffuseMapEntities()
    {
        return this.diffuseMapEntities;
    }

    public SortedPool<RenderEntity> getOcclusionMapEntities()
    {
        return this.occlusionMapEntities;
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
