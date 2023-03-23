package jp.mochisystems.erc.model;

import jp.mochisystems.core.blockcopier.DefBlockModel;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;


public class CoasterBlockModel extends DefBlockModel {

    public CoasterBlockModel(IModelController controller) {
        super(controller);
    }

    private final Vec3d modelScale = new Vec3d(1, 1, 1);
    private final Vec3d modelOffset = new Vec3d(0, 0, 0);
    private final Quaternion modelRotate = new Quaternion();
    private final Quaternion renderRotation = new Quaternion();
    private final Quaternion rotation = new Quaternion();
    private final Quaternion prevRotation = new Quaternion();
    private final Quaternion.MatBuffer modelRotBuf = new Quaternion.MatBuffer();
    private final Quaternion.MatBuffer renderRotBuf = new Quaternion.MatBuffer();

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        modelScale.ReadFromNBT("scale", partNbtOnConstruct);
        modelOffset.ReadFromNBT("offset", partNbtOnConstruct);
//        modelRotate.ReadFromNBT("rotate", partNbtOnConstruct);
        modelRotate.ReadFromNBT("tilt", partNbtOnConstruct);
        modelRotBuf.Fix(modelRotate);
    }

    @Override
    public void Validate()
    {
        super.Validate();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
    }

    @Override
    public void RenderModel(int pass, float partialTick) {
        GL11.glPushMatrix();
        GL11.glTranslated(modelOffset.x, modelOffset.y, modelOffset.z);
        Quaternion.Lerp(renderRotation, prevRotation, rotation, partialTick);
        GL11.glMultMatrix(renderRotBuf.Fix(renderRotation));
        GL11.glMultMatrix(modelRotBuf.GetBuffer());
        GL11.glScaled(modelScale.x, modelScale.y, modelScale.z);
        super.RenderModel(pass, partialTick);
        GL11.glPopMatrix();
    }

    public boolean HasChild(){return false;}
    public IModel[] GetChildren(){return null;}
    public String GetName(){return "";}
    public void SetVisible(boolean active){}
    public void SetOffset(float x, float y, float z){}
    public void SetRotation(Quaternion q)
    {
        prevRotation.CopyFrom(rotation);
        rotation.CopyFrom(q);
        if(!rotation.IsSameDir(prevRotation)) prevRotation.Wrap();
    }
    public CommonAddress GetCommonAddress()
    {
        return controller.GetCommonAddress();
    }
}
