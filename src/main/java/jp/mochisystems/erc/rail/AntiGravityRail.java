package jp.mochisystems.erc.rail;


import io.netty.buffer.ByteBuf;
import jp.mochisystems.core.math.Vec3d;
import net.minecraft.nbt.NBTTagCompound;

public class AntiGravityRail extends Rail {

    public AntiGravityRail()
    {
        super();
        gravity.SetFrom(0, 0, 0);
    }

//    @Override
//    public double AccelAt(double t)
//    {
//        return 0;
//    }

    @Override
    public double RegisterAt(double pos, double speed)
    {
        return speed;
    }

    @Override
    public void WriteOptionToBytes(ByteBuf buffer)
    {
        gravity.ReadBuf(buffer);
    }

    @Override
    public void ReadOptionFromBytes(ByteBuf buffer)
    {
        gravity.WriteBuf(buffer);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        gravity.ReadFromNBT("gravity", nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        gravity.WriteToNBT("gravity", nbt);
    }
}
