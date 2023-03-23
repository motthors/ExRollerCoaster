package jp.mochisystems.erc.rail;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;


public class AccelRail extends Rail {

    private double accel = 5.0;
    public double getAccel(){ return accel; }
    public void setAccel(double accel){ this.accel = accel; }

    @Override
    public double RegisterAt(double pos, double speed)
    {
        return controller.IsActive() ? speed : speed * 0.8;
    }

    @Override
    public double AccelAt(double t)
    {
        if(controller.IsActive())
        {
            return super.AccelAt(t) + accel;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public void WriteOptionToBytes(ByteBuf buf)
    {
        buf.writeDouble(accel);
    }

    @Override
    public void ReadOptionFromBytes(ByteBuf buf)
    {
        accel = buf.readDouble();
    }

    @Override
    public void SetSpecialData(int i)
    {
        accel = i * 0.01;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        accel = nbt.getDouble("accel");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setDouble("accel", accel);
    }
}
