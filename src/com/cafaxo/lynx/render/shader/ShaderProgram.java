package com.cafaxo.lynx.render.shader;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;

import com.cafaxo.lynx.render.RenderMesh;
import com.cafaxo.lynx.util.ResourceLocation;

public abstract class ShaderProgram
{

    ResourceLocation vertexShaderSource, geometryShaderSource, fragmentShaderSource;

    private int id;

    private Map<String, ShaderAttribute> attributes = new HashMap<String, ShaderAttribute>();

    public Map<String, Integer> uniforms = new HashMap<String, Integer>();

    public Map<String, Integer> uniformBlocks = new HashMap<String, Integer>();

    private boolean isVerified;

    private static ShaderProgram boundShaderProgram;

    public ShaderProgram(ResourceLocation vertexShaderSource, ResourceLocation geometryShaderSource, ResourceLocation fragmentShaderSource)
    {
        if (vertexShaderSource == null)
        {
            throw new IllegalArgumentException(this.getShaderPaths() + ": vertex shader must not be null");
        }

        if (fragmentShaderSource == null)
        {
            throw new IllegalArgumentException(this.getShaderPaths() + ": fragment shader must not be null");
        }

        this.vertexShaderSource = vertexShaderSource;
        this.geometryShaderSource = geometryShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;

        if (!this.compileShaders())
        {
            throw new RuntimeException("compiling shader failed");
        }

        this.fetchAttributes();
        this.fetchUniforms();
        this.fetchUniformBlocks();

        this.defineAttributes();
    }

    public ShaderProgram(ResourceLocation vertexShaderSource, ResourceLocation fragmentShaderSource)
    {
        this(vertexShaderSource, null, fragmentShaderSource);
    }

    private boolean compileShaders()
    {
        int vertexShader = this.loadShader(GL20.GL_VERTEX_SHADER, this.vertexShaderSource);

        if (vertexShader == -1)
        {
            return false;
        }

        int fragmentShader = this.loadShader(GL20.GL_FRAGMENT_SHADER, this.fragmentShaderSource);

        if (fragmentShader == -1)
        {
            return false;
        }

        int geometryShader = -1;

        if (this.geometryShaderSource != null)
        {
            geometryShader = this.loadShader(GL32.GL_GEOMETRY_SHADER, this.geometryShaderSource);

            if (geometryShader == -1)
            {
                return false;
            }
        }

        this.id = GL20.glCreateProgram();

        GL20.glAttachShader(this.id, vertexShader);

        GL20.glAttachShader(this.id, fragmentShader);

        if (this.geometryShaderSource != null)
        {
            GL20.glAttachShader(this.id, geometryShader);
        }

        GL20.glLinkProgram(this.id);

        if (GL20.glGetProgrami(this.id, GL20.GL_LINK_STATUS) == 0)
        {
            int logLength = GL20.glGetProgrami(this.id, GL20.GL_INFO_LOG_LENGTH);
            String infoLog = GL20.glGetProgramInfoLog(this.id, logLength);

            if (vertexShader > 0)
            {
                GL20.glDeleteShader(vertexShader);
            }

            if (fragmentShader > 0)
            {
                GL20.glDeleteShader(fragmentShader);
            }

            if (geometryShader > 0)
            {
                GL20.glDeleteShader(geometryShader);
            }

            if (this.id > 0)
            {
                GL20.glDeleteProgram(this.id);
            }

            System.out.println(infoLog);

            return false;
        }

        return true;
    }

    private int loadShader(int type, ResourceLocation source)
    {
        int shader = GL20.glCreateShader(type);

        if (shader == 0)
        {
            return -1;
        }

        GL20.glShaderSource(shader, source.getString());
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0)
        {
            int logLength = GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH);
            String infoLog = GL20.glGetShaderInfoLog(shader, logLength);

            GL20.glDeleteShader(shader);

            System.out.println(source.getPath() + ": " + infoLog);

            return -1;
        }

        return shader;
    }

    private void fetchAttributes()
    {
        int maxAttributeLength = GL20.glGetProgrami(this.id, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH);
        int numAttributes = GL20.glGetProgrami(this.id, GL20.GL_ACTIVE_ATTRIBUTES);

        for (int i = 0; i < numAttributes; i++)
        {
            String name = GL20.glGetActiveAttrib(this.id, i, maxAttributeLength);
            this.attributes.put(name, new ShaderAttribute(name, GL20.glGetAttribLocation(this.id, name)));
        }
    }

    private void fetchUniforms()
    {
        int maxUniformLength = GL20.glGetProgrami(this.id, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
        int numUniforms = GL20.glGetProgrami(this.id, GL20.GL_ACTIVE_UNIFORMS);

        for (int i = 0; i < numUniforms; i++)
        {
            String name = GL20.glGetActiveUniform(this.id, i, maxUniformLength);
            this.uniforms.put(name, GL20.glGetUniformLocation(this.id, name));
        }
    }

    private void fetchUniformBlocks()
    {
        int maxUniformLength = GL20.glGetProgrami(this.id, GL31.GL_ACTIVE_UNIFORM_BLOCK_MAX_NAME_LENGTH);
        int numUniforms = GL20.glGetProgrami(this.id, GL31.GL_ACTIVE_UNIFORM_BLOCKS);

        for (int i = 0; i < numUniforms; i++)
        {
            String name = GL31.glGetActiveUniformBlockName(this.id, i, maxUniformLength);
            this.uniformBlocks.put(name, GL31.glGetUniformBlockIndex(this.id, name));
        }
    }

    public abstract void defineAttributes();

    public void bindAttributes(long offset)
    {
        for (Map.Entry<String, ShaderAttribute> entry : this.attributes.entrySet())
        {
            ShaderAttribute shaderAttribute = entry.getValue();

            GL20.glEnableVertexAttribArray(shaderAttribute.getLocation());
            GL20.glVertexAttribPointer(shaderAttribute.getLocation(), shaderAttribute.getComponents(), shaderAttribute.getType(), shaderAttribute.isNormalized(), shaderAttribute.getStride(), offset + shaderAttribute.getBufferOffset());
        }
    }

    private void unbindAttributes()
    {
        for (Map.Entry<String, ShaderAttribute> entry : this.attributes.entrySet())
        {
            ShaderAttribute shaderAttribute = entry.getValue();

            GL20.glDisableVertexAttribArray(shaderAttribute.getLocation());
        }
    }

    public void bindUniforms(RenderMesh renderMesh, IUniformData uniformData)
    {
    }

    public void bind()
    {
        if (this == ShaderProgram.boundShaderProgram)
        {
            //return;
        }

        if (ShaderProgram.boundShaderProgram != null)
        {
            ShaderProgram.boundShaderProgram.unbindAttributes();
        }

        //this.verify();
        GL20.glUseProgram(this.id);

        ShaderProgram.boundShaderProgram = this;
    }

    public ShaderAttribute getAttribute(String attribute)
    {
        ShaderAttribute shaderAttribute = this.attributes.get(attribute);

        // fixes driver inconsistencies
        if (shaderAttribute == null)
        {
            System.out.println("unknown attribute: " + attribute + ", generating dummy to prevent npe.");
            return new ShaderAttribute(attribute, 0);
        }

        return shaderAttribute;
    }

    public int getUniform(String uniform)
    {
        Integer uniformId = this.uniforms.get(uniform);

        // fixes driver inconsistencies
        if (uniformId == null)
        {
            int indexOfBracket = uniform.indexOf("[");

            if (indexOfBracket > 0)
            {
                return this.uniforms.get(uniform.substring(0, indexOfBracket));
            }
            else
            {
                throw new RuntimeException("setting undefined uniform. glsl compiler may have optimized unused uniform away.");
            }
        }

        return uniformId;
    }

    public int getUniformBlock(String uniformBlock)
    {
        return this.uniformBlocks.get(uniformBlock);
    }

    public int getId()
    {
        return this.id;
    }

    public int getBytesPerVertex()
    {
        return this.attributes.entrySet().iterator().next().getValue().getStride();
    }

    public void verify()
    {
        if (!this.isVerified)
        {
            for (ShaderAttribute shaderAttribute : this.attributes.values())
            {
                if (!shaderAttribute.isDefined())
                {
                    throw new RuntimeException(this.getShaderPaths() + ": you didn't define the following shader attribute: " + shaderAttribute.getName());
                }
            }

            this.isVerified = true;
        }
    }

    public String getShaderPaths()
    {
        return this.vertexShaderSource.getPath() + ", " + this.fragmentShaderSource.getPath();
    }

}
