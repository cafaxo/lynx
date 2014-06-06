package com.cafaxo.lightingdemo3d;

import com.cafaxo.lightingdemo3d.ObjFileParser.ModelData;
import com.cafaxo.lynx.graphics.RenderEntity;
import com.cafaxo.lynx.graphics.ShaderProgram;
import com.cafaxo.lynx.graphics.Texture;

public class Model extends RenderEntity
{

    public Model(ShaderProgram shaderProgram, ModelData modelData, Texture texture)
    {
        super(shaderProgram, (modelData.faces.length / 3) * 8, (modelData.faces.length / 3) * 8);

        // vertex layout:
        // position (3) / uv (2) / normal (3)

        for (int face = 0; face < (modelData.faces.length / 3); ++face)
        {
            int positionIndex = modelData.faces[face * 3] - 1;
            int texelIndex = modelData.faces[(face * 3) + 1] - 1;
            int normalIndex = modelData.faces[(face * 3) + 2] - 1;

            float positionX = modelData.positions[positionIndex * 3];
            float positionY = modelData.positions[(positionIndex * 3) + 1];
            float positionZ = modelData.positions[(positionIndex * 3) + 2];

            float u = modelData.texels[texelIndex * 2];
            float v = modelData.texels[(texelIndex * 2) + 1];

            float normalX = modelData.normals[normalIndex * 3];
            float normalY = modelData.normals[(normalIndex * 3) + 1];
            float normalZ = modelData.normals[(normalIndex * 3) + 2];

            this.addVertexData(positionX);
            this.addVertexData(positionY);
            this.addVertexData(positionZ);

            this.addVertexData(u);
            this.addVertexData(v);

            this.addVertexData(normalX);
            this.addVertexData(normalY);
            this.addVertexData(normalZ);

            this.addIndexData(face);
        }

        this.setTexture(texture);
    }

}
