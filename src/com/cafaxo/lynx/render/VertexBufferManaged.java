package com.cafaxo.lynx.render;

import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class VertexBufferManaged extends VertexBuffer
{

    public class DataBlock
    {

        public int offset;

        public int size;

        public int usedSize;

        boolean isFree;

        private DataBlock()
        {
        }

        private DataBlock(int offset, int size, boolean isFree)
        {
            this.offset = offset;
            this.size = size;
            this.isFree = isFree;
        }

        public void upload(FloatBuffer floatBuffer)
        {
            if (this.size < (floatBuffer.remaining() * 4))
            {
                throw new BufferOverflowException();
            }

            this.getBuffer().mappedBufferAsFloats.position(this.offset / 4);
            this.getBuffer().mappedBufferAsFloats.put(floatBuffer);

            this.usedSize = (this.getBuffer().mappedBufferAsFloats.position() * 4) - this.offset;
        }

        public void upload(IntBuffer intBuffer)
        {
            if (this.size < (intBuffer.remaining() * 4))
            {
                throw new BufferOverflowException();
            }

            this.getBuffer().mappedBufferAsInts.position(this.offset / 4);
            this.getBuffer().mappedBufferAsInts.put(intBuffer);

            this.usedSize = (this.getBuffer().mappedBufferAsInts.position() * 4) - this.offset;
        }

        public VertexBufferManaged getBuffer()
        {
            return VertexBufferManaged.this;
        }

        /*public CLMem getCLMem(CLContext context, int flags, IntBuffer errorBuffer) // doesnt work?
        {
            return CL10GL.clCreateFromGLBuffer(context, flags, this.getBuffer().id, errorBuffer).createSubBuffer(flags, CL11.CL_BUFFER_CREATE_TYPE_REGION, new CLBufferRegion(this.offset, this.size / 4), errorBuffer);
        }*/

    }

    private ArrayList<DataBlock> partition = new ArrayList<DataBlock>();

    public VertexBufferManaged(int target, int type, int size)
    {
        super(target, type, size);

        this.partition.add(new DataBlock(0, size, true));
    }

    @Override
    public void orphan()
    {
        super.orphan();

        this.partition.clear();
        this.partition.add(new DataBlock(0, this.size, true));
    }

    public DataBlock allocate(int size)
    {
        for (int i = 0; i < this.partition.size(); ++i)
        {
            DataBlock dataBlock = this.partition.get(i);

            if (dataBlock.isFree && (dataBlock.size >= size))
            {
                if (dataBlock.size == size)
                {
                    dataBlock.isFree = false;

                    return dataBlock;
                }

                DataBlock filler = new DataBlock(dataBlock.offset + size, dataBlock.size - size, true);
                this.partition.add(i + 1, filler);

                dataBlock.size = size;
                dataBlock.isFree = false;

                return dataBlock;
            }
        }

        throw new RuntimeException("could not allocate data block");
    }

    public void copy(DataBlock target, float source[], int offset, int length)
    {
        if ((target.size * 4) < length)
        {
            throw new RuntimeException();
        }

        this.mappedBufferAsFloats.position(target.offset / 4);
        this.mappedBufferAsFloats.put(source, offset, length);
    }

    public void free(DataBlock dataBlock)
    {
        int index = this.partition.indexOf(dataBlock);

        if (index == -1)
        {
            throw new RuntimeException("tried to free a DataBlock not managed by this instance");
        }

        DataBlock prior = null;

        if ((index > 0) && (prior = this.partition.get(index - 1)).isFree)
        {
            dataBlock.offset = prior.offset;
            dataBlock.size += prior.size;

            this.partition.remove(index - 1);
            index -= 1;
        }

        DataBlock following = null;

        if (((index + 1) < this.partition.size()) && (following = this.partition.get(index + 1)).isFree)
        {
            dataBlock.size += following.size;

            this.partition.remove(index + 1);
        }

        dataBlock.isFree = true;
    }

}
