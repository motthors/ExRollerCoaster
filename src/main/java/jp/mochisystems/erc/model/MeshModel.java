package jp.mochisystems.erc.model;

import jp.mochisystems.core._mc.renderer.MeshBuffer;

import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class MeshModel implements IModel {

    IModelController controller;
    MeshBuffer meshBuffer;
    Quaternion rotation, prevRotation, renderRotation;
    private final Quaternion.MatBuffer renderRotBuf = new Quaternion.MatBuffer();

    public MeshModel(IModelController controller, IBakedModel Obj, ResourceLocation Tex, String id)
    {
        this.controller = controller;
        meshBuffer = new MeshBuffer(Obj, Tex, id);
        meshBuffer.SetDirty();
        rotation = new Quaternion();
        prevRotation = new Quaternion();
        renderRotation = new Quaternion();
    }

    @Override
    public boolean IsLock() {
        return false;
    }

    @Override
    public boolean IsInvalid() {
        return controller.IsInvalid();
    }

    @Override
    public void Reset() {

    }

    @Override
    public void Update() {

    }

    @Override
    public void SetRotation(Quaternion rotation)
    {
        prevRotation.CopyFrom(this.rotation);
        this.rotation.CopyFrom(rotation);
        if(!rotation.IsSameDir(prevRotation)) prevRotation.Wrap();
    }

    @Override
    public void Unload() {

    }

    @Override
    public void Invalidate() {
        if(meshBuffer != null) meshBuffer.DeleteBuffer();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
    }

    @Override
    public void setRSPower(int power) {

    }

    @Override
    public void SetWorld(World world) {

    }

    @Override
    public void RenderModel(int pass, float partialTick) {
        if(pass==0) {
            GL11.glPushMatrix();
            Quaternion.Lerp(renderRotation, prevRotation, rotation, partialTick);
            GL11.glMultMatrix(renderRotBuf.Fix(renderRotation));
            meshBuffer.Render();
            GL11.glPopMatrix();
        }
    }

    @Override
    public boolean HasChild(){return false;}

    @Override
    public IModel[] GetChildren(){return null;}

    @Override
    public String GetName(){return "";}

    @Override
    public void SetVisible(boolean active){}

    @Override
    public void SetOffset(float x, float y, float z){}


    @Override
    public CommonAddress GetCommonAddress()
    {
        return controller.GetCommonAddress();
    }

    @Override
    public double ModelPosX() {
        return controller.CorePosX();
    }
    @Override
    public double ModelPosY() {
        return controller.CorePosY();
    }
    @Override
    public double ModelPosZ() {
        return controller.CorePosZ();
    }
}