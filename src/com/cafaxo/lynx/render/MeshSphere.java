package com.cafaxo.lynx.render;

public class MeshSphere extends Mesh
{

    public MeshSphere(float radius, int rings, int sectors)
    {
        super(rings * sectors * 8, rings * sectors * 6);

        float R = 1.f / (rings - 1);
        float S = 1.f / (sectors - 1);

        for (int r = 0; r < rings; r++)
        {
            for (int s = 0; s < sectors; s++)
            {
                float y = (float) Math.sin(-(Math.PI / 2.f) + (Math.PI * r * R));
                float x = (float) (Math.cos(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));
                float z = (float) (Math.sin(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));

                this.addVertexData(x * radius);
                this.addVertexData(y * radius);
                this.addVertexData(z * radius);

                this.addVertexData(s * S);
                this.addVertexData(r * R);

                this.addVertexData(x);
                this.addVertexData(y);
                this.addVertexData(z);
            }
        }

        for (int r = 0; r < (rings - 1); r++)
        {
            for (int s = 0; s < (sectors - 1); s++)
            {
                this.addIndexData((r * sectors) + s);
                this.addIndexData((r * sectors) + (s + 1));
                this.addIndexData(((r + 1) * sectors) + (s + 1));
                this.addIndexData((r * sectors) + s);
                this.addIndexData(((r + 1) * sectors) + s);
                this.addIndexData(((r + 1) * sectors) + (s + 1));
            }
        }
    }

}
