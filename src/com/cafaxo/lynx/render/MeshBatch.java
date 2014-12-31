package com.cafaxo.lynx.render;

public class MeshBatch extends Mesh
{

    private Iterable<Mesh> meshContainer;

    public MeshBatch(Iterable<Mesh> meshContainer)
    {
        super(10000, 10000);

        this.meshContainer = meshContainer;
    }

    @Override
    public void upload()
    {
        this.vertexData.clear();
        this.indexData.clear();

        int indexDataOffset = 0;

        for (Mesh mesh : this.meshContainer)
        {
            mesh.vertexData.flip();
            this.vertexData.put(mesh.vertexData);

            mesh.indexData.flip();

            for (int i = 0; i < mesh.indexData.limit(); ++i)
            {
                this.addIndexData(mesh.indexData.get(i) + indexDataOffset);
            }

            indexDataOffset += mesh.indexData.limit();
        }

        super.upload();
    }

}
