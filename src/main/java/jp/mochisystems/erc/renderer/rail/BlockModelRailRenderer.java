package jp.mochisystems.erc.renderer.rail;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc.renderer.BlocksRenderer;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.manager.RenderTranslucentBlockModelManager;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.core.util.HashMaker;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class BlockModelRailRenderer implements IRailRenderer, IModel {
    static int currentRenderIdx;

    // temp
    World world;
    int x, y, z;
    NBTTagCompound nbt;

    protected Rail rail;
    public void SetRail(Rail rail){
        this.rail = rail;
    }

    private final RailCurveModifier modifier;

    private BlocksRendererForRail[] renderers;
    private final MTYBlockAccess blockAccess;
    private final float widthRatio;
    private final float heightRatio;
    private float lengthRatio = 1f;
    private final int copyNum;
    private final EnumFacing side;

    public BlockModelRailRenderer(NBTTagCompound nbt, int x, int y, int z)
    {
        this.nbt = nbt;
        this.x = x; this.y = y; this.z = z;
        blockAccess = new MTYBlockAccess();
        widthRatio =  nbt.getFloat("widthratio");
        heightRatio =  nbt.getFloat("heightratio");
        copyNum = nbt.hasKey("copyNum") ? nbt.getInteger("copyNum") : 1;
        side = EnumFacing.getFront(nbt.getByte("constructorside"));
        modifier = new RailCurveModifier();
    }

    public void Construct(World world)
    {
        blockAccess.setWorld(world);
        blockAccess.constructFromTag(nbt, x, y, z, false, this::SetDirty);
    }

    @Override
    public void SetDirty()
    {
        //setdirtyだけどDirtyのセットはせずにCompileの準備をしちゃおう
        modifier.SetRail(rail);
        modifier.UpdateRailData();
        if(renderers != null) for(BlocksRendererForRail r : renderers){
            r.delete();
        }
        lengthRatio = 1f / copyNum;
        renderers = new BlocksRendererForRail[copyNum];
        for(int i = 0; i < copyNum; ++i) {
            renderers[i] = new BlocksRendererForRail(blockAccess, rail, i);
            renderers[i].CompileRenderer();
            renderers[i].SetDirty();
        }
        for(BlocksRendererForRail renderer : renderers){
            renderer.SetDirty();
        }
    }

    @Override
    public void DeleteBuffer()
    {
        for(BlocksRendererForRail renderer : renderers) renderer.delete();
        renderers = null;
//        for(BlocksReplicator replicator : replicators)replicator.invalidate();
    }

    @Override
    public void Render()
    {
        for(BlocksRendererForRail renderer : renderers)
        {
            GL11.glPushMatrix();
            renderer.render();
            GL11.glPopMatrix();
        }
        RenderTranslucentBlockModelManager.add(this);
    }




    public void RenderModel(int pass, float partialTick) {
        for(BlocksRendererForRail renderer : renderers)
        {
            GL11.glPushMatrix();
            if(pass==0)renderer.render();
            else renderer.render2();
            GL11.glPopMatrix();
        }
    }
    public void SetWorld(World world) {
        blockAccess.setWorld(world);
    }
    public boolean HasChild() {
        return false;
    }
    public boolean IsLock() {
        return false;
    }
    public boolean IsInvalid() {
        return rail.GetController().IsInvalid();
    }
    public void Reset() {

    }
    public void Update() {

    }
    public void Unload() {

    }
    public void Invalidate() {

    }
    public void readFromNBT(NBTTagCompound nbt) {

    }
    public void writeToNBT(NBTTagCompound nbt) {

    }
    public void setRSPower(int power) {

    }
    public void SetVisible(boolean active) {

    }
    public void SetOffset(float x, float y, float z) {

    }
    public void SetRotation(Quaternion attitude) {

    }
    public IModel[] GetChildren() {
        return null;
    }
    public double ModelPosX() {
        return 0.5 + rail.GetController().CorePosX();
    }
    public double ModelPosY() {
        return 0.5 + rail.GetController().CorePosY();
    }
    public double ModelPosZ() {
        return 0.5 + rail.GetController().CorePosZ();
    }
    public String GetName() {
        return null;
    }
    public CommonAddress GetCommonAddress() {
        return null;
    }





    public class BlocksRendererForRail extends BlocksRenderer {

        Rail rail;
        int idx;

        public BlocksRendererForRail(MTYBlockAccess ba, Rail rail, int idx) {
            super(ba);
            this.rail = rail;
            this.idx = idx;
        }

        @Override
        protected void CalcExtHash(HashMaker hashMaker)
        {
            hashMaker.Append('x');
            hashMaker.Append((int)rail.GetController().CorePosX());
            hashMaker.Append('y');
            hashMaker.Append((int)rail.GetController().CorePosY());
            hashMaker.Append('z');
            hashMaker.Append((int)rail.GetController().CorePosZ());
            hashMaker.Append(idx);
        }

        @Override
        public void renderBlock(int cx, int cy, int cz, boolean isTranslucent)
        {
            currentRenderIdx = idx;
            adapter.original = Tessellator.getInstance().getBuffer();
            SetOriginalBufferBuilder(adapter);
            super.renderBlock(cx, cy, cz, isTranslucent);
            SetOriginalBufferBuilder(adapter.original);
        }

        private void SetOriginalBufferBuilder(BufferBuilder org)
        {
            try {
                Field field = Tessellator.class.getDeclaredField("buffer");
                field.setAccessible(true);
                field.set(Tessellator.getInstance(), org);
            }
            catch (Exception ignored){
                Logger.error(ignored.toString());
            }
        }
    }


    private final BufferBuilderAdapter adapter = new BufferBuilderAdapter();
    private class BufferBuilderAdapter extends BufferBuilder {
        public BufferBuilder original;
        Vec3d pos = new Vec3d();
        Vec3d normal = new Vec3d();

        public BufferBuilderAdapter() {
            super(0);
        }

        public void Modify(double x, double y, double z)
        {
            int length = 1;

            // offset to origin
            pos.SetFrom(x, y, z);
            pos.x -= blockAccess.originalCorePosX + 0.5f;
            pos.y -= blockAccess.originalCorePosY + 0.5f;
            pos.z -= blockAccess.originalCorePosZ + 0.5f;

            // ?
            double temp = 0;
            switch(side)
            {
                case UP: temp = pos.z; pos.z = pos.y; pos.y = -temp; length = (blockAccess.getActualSizeY()); break;
                case DOWN: temp = pos.z; pos.z = -pos.y; pos.y = temp; length = (blockAccess.getActualSizeY()); break;
                case NORTH: length = (blockAccess.getActualSizeZ()); break;
                case SOUTH: pos.x = -pos.x; pos.z = -pos.z; length = (blockAccess.getActualSizeZ()); break;
                case EAST: temp = pos.z; pos.z = -pos.x; pos.x = temp; length = (blockAccess.getActualSizeX()); break;
                case WEST: temp = pos.z; pos.z = pos.x; pos.x = -temp; length = (blockAccess.getActualSizeX()); break;
            }

            // expand scale
            pos.z -= 0.5f;
            pos.x *= widthRatio;
            pos.y *= heightRatio;
            pos.z += currentRenderIdx * length;
            pos.z *= lengthRatio;

//            double t = (pos.z >= 0) ? rail.Curve().FixParameter(pos.z / length) : (pos.z / length);
            double t = pos.z / length;
//            Logger.debugInfo(String.format("%.2f -> %.2f", (pos.z/length), t));
            modifier.TransformVertex(t, pos, normal);
            modifier.renderPos.x += blockAccess.originalCorePosX + 0.5;
            modifier.renderPos.y += blockAccess.originalCorePosY + 0.5;
            modifier.renderPos.z += blockAccess.originalCorePosZ + 0.5;
        }

        @Nonnull
        @Override
        public BufferBuilder normal(float x, float y, float z)
        {
            original.normal((float)modifier.renderNormal.x, (float)modifier.renderNormal.y, (float)modifier.renderNormal.z);
            return this;
        }

        @Override
        public void putNormal(float x, float y, float z)
        {
            original.putNormal(x, y, z);
        }


        @Nonnull
        @Override
        public BufferBuilder pos(double x, double y, double z)
        {
            Modify(x, y, z);
            original.pos(modifier.renderPos.x, modifier.renderPos.y, modifier.renderPos.z);
            original.pos(x, y, z);
            return this;
        }

        @Override
        public void putPosition(double x, double y, double z)
        {
            int i = getVertexFormat().getIntegerSize();
            int j = (getVertexCount() - 4) * i;

            for (int k = 0; k < 4; ++k)
            {
                int xi = j + k * i;
                int yi = xi + 1;
                int zi = yi + 1;
                try {
                    Field fieldIntBuffer = BufferBuilder.class.getDeclaredField("rawIntBuffer");
                    fieldIntBuffer.setAccessible(true);
                    IntBuffer rawIntBuffer = (IntBuffer) fieldIntBuffer.get(original);
                    float mx = Float.intBitsToFloat(rawIntBuffer.get(xi));
                    float my = Float.intBitsToFloat(rawIntBuffer.get(yi));
                    float mz = Float.intBitsToFloat(rawIntBuffer.get(zi));
                    Modify(mx+x, my+y, mz+z);
                    rawIntBuffer.put(xi, Float.floatToRawIntBits((float) (modifier.renderPos.x)));
                    rawIntBuffer.put(yi, Float.floatToRawIntBits((float) (modifier.renderPos.y)));
                    rawIntBuffer.put(zi, Float.floatToRawIntBits((float) (modifier.renderPos.z)));
                } catch(Exception ignored){}
            }
//            Modify(x, y, z);
//            original.putPosition(modifier.renderPos.x, modifier.renderPos.y, modifier.renderPos.z);
//            original.putPosition(x, y, z);
        }

        @Override
        public void addVertexData(@Nonnull int[] vertexData)
        {
//            {
//                float x = Float.intBitsToFloat(vertexData[0]);
//                float y = Float.intBitsToFloat(vertexData[1]);
//                float z = Float.intBitsToFloat(vertexData[2]);
//                Modify(x, y, z);
//                vertexData[0] = Float.floatToIntBits((float)modifier.renderPos.x);
//                vertexData[1] = Float.floatToIntBits((float)modifier.renderPos.y);
//                vertexData[2] = Float.floatToIntBits((float)modifier.renderPos.z);
//            }
            original.addVertexData(vertexData);
        }



        public void setVertexState(@Nonnull BufferBuilder.State state){original.setVertexState(state);}
        public void reset(){original.reset();}
        public void begin(int glMode, @Nonnull VertexFormat format){
            original.begin(glMode, format);
        }
        @Nonnull
        public BufferBuilder tex(double u, double v){ return original.tex(u, v);}
        @Nonnull
        public BufferBuilder lightmap(int p_187314_1_, int p_187314_2_){ return original.lightmap(p_187314_1_, p_187314_2_);}
        public void putBrightness4(int p_178962_1_, int p_178962_2_, int p_178962_3_, int p_178962_4_){original.putBrightness4(p_178962_1_,p_178962_2_,p_178962_3_,p_178962_4_);}
        public int getColorIndex(int vertexIndex){return original.getColorIndex(vertexIndex);}
        public void putColorMultiplier(float red, float green, float blue, int vertexIndex){original.putColorMultiplier(red,green,blue,vertexIndex);}
        public void putColorRGB_F(float red, float green, float blue, int vertexIndex){original.putColorRGB_F(red,green,blue,vertexIndex);}
        public void putColorRGBA(int index, int red, int green, int blue){original.putColorRGBA(index,red,green,blue);}
        public void noColor()
        {
            original.noColor();
        }
        @Nonnull
        public BufferBuilder color(float red, float green, float blue, float alpha){return original.color(red,green,blue,alpha);}
        @Nonnull
        public BufferBuilder color(int red, int green, int blue, int alpha){return original.color(red, green, blue, alpha);}
        public void endVertex(){original.endVertex();}
        public void setTranslation(double x, double y, double z){original.setTranslation(x, y, z);}
        public void finishDrawing(){original.finishDrawing();}
        public ByteBuffer getByteBuffer()
        {
            return original.getByteBuffer();
        }
        public VertexFormat getVertexFormat() { return original.getVertexFormat(); }
        public int getVertexCount()
        {
            return original.getVertexCount();
        }
        public int getDrawMode()
        {
            return original.getDrawMode();
        }
        public void putColor4(int argb){original.putColor4(argb);}
        public void putColorRGB_F4(float red, float green, float blue){original.putColorRGB_F4(red, green, blue);}
        public void putColorRGBA(int index, int red, int green, int blue, int alpha){original.putColorRGBA(index, red, green, blue, alpha);}
        public void sortVertexData(float x, float y, float z){ original.sortVertexData(x, y, z);}
        public boolean isColorDisabled()
        {
            return original.isColorDisabled();
        }
        public void putBulkData(ByteBuffer buffer){original.putBulkData(buffer);}
        public BufferBuilder.State getVertexState(){ return original.getVertexState(); }
    }
}
