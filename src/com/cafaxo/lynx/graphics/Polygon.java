package com.cafaxo.lynx.graphics;

public class Polygon extends RenderEntity
{

    public Polygon(ShaderProgram shaderProgram, int maxVertexDataSize, int maxIndicesCount)
    {
        super(shaderProgram, maxVertexDataSize, maxIndicesCount);
    }

    public static Polygon fromRectangle(ShaderProgram shaderProgram, int width, int height, float r, float g, float b, float a)
    {
        Polygon polygon = new Polygon(shaderProgram, 12, 6);

        polygon.addVertex(0, 0, r, g, b, a);
        polygon.addVertex(width, 0, r, g, b, a);
        polygon.addVertex(width, height, r, g, b, a);
        polygon.addVertex(0, height, r, g, b, a);

        polygon.generateIndices();

        return polygon;
    }

    public void addVertex(float x, float y, float r, float g, float b, float a)
    {
        this.addVertexData(x);
        this.addVertexData(y);

        int intBits = ((int) (255 * a) << 24) | ((int) (255 * b) << 16) | ((int) (255 * g) << 8) | ((int) (255 * r));
        this.addVertexData(Float.intBitsToFloat(intBits & 0xfeffffff));
    }

    public void generateIndices()
    {
        for (int i = 2; i < (this.getVertexDataSize() / 3); ++i)
        {
            this.addIndexData(0);
            this.addIndexData(i - 1);
            this.addIndexData(i);
        }
    }

    @Override
    protected void refreshVertexAndIndexData()
    {
        for (int i = 0; i < (this.vertexData.length / 3); ++i)
        {
            this.transformedVertexData[i * 3] = this.vertexData[i * 3] + this.x;
            this.transformedVertexData[(i * 3) + 1] = this.vertexData[(i * 3) + 1] + this.y;
            this.transformedVertexData[(i * 3) + 2] = this.vertexData[(i * 3) + 2];
        }
    }

}
