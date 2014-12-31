package com.cafaxo.lynx.util;

import java.util.ArrayList;
import java.util.List;

public class GeometryProvider<T>
{

    protected List<T> vertices = new ArrayList<T>();

    protected List<TriangleIndices> faces = new ArrayList<TriangleIndices>();

    public List<T> getVertices()
    {
        return this.vertices;
    }

    public List<TriangleIndices> getFaces()
    {
        return this.faces;
    }

}
