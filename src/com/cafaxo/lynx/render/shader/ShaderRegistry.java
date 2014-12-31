package com.cafaxo.lynx.render.shader;

import java.util.HashMap;
import java.util.Map;

public class ShaderRegistry
{

    public final static ShaderRegistry instance = new ShaderRegistry();

    private Map<String, ShaderProgram> nameToShaderProgramMap = new HashMap<String, ShaderProgram>();

    private ShaderRegistry()
    {

    }

    public void register(String name, ShaderProgram shaderProgram)
    {
        if (this.nameToShaderProgramMap.containsKey(name))
        {
            throw new IllegalArgumentException(name + " has already been registered.");
        }

        this.nameToShaderProgramMap.put(name, shaderProgram);
    }

    public ShaderProgram get(String name)
    {
        ShaderProgram shaderProgram = this.nameToShaderProgramMap.get(name);

        if (shaderProgram == null)
        {
            throw new IllegalArgumentException("unknown shader program: " + name);
        }

        return shaderProgram;
    }

}
