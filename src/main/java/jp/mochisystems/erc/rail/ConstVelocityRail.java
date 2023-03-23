package jp.mochisystems.erc.rail;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core.util.gui.GuiGroupCanvas;
import jp.mochisystems.core.util.gui.GuiUtil;
import jp.mochisystems.erc._mc.gui.GUIRail;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConstVelocityRail extends Rail{

    private double velocitySetting = 0.1;
    public double GetVelocity(){ return velocitySetting; }
    public void SetVelocity(double velocity){ this.velocitySetting = velocity; }

    @Override
    public double RegisterAt(double pos, double speed)
    {
        if(!controller.IsActive()) return speed * 0.75;
        double speedDiff = speed - velocitySetting;
        return speedDiff * 0.55 + velocitySetting;
    }

    @Override
    public void WriteOptionToBytes(ByteBuf buf)
    {
        buf.writeDouble(velocitySetting);
    }

    @Override
    public void ReadOptionFromBytes(ByteBuf buf)
    {
        velocitySetting = buf.readDouble();
    }

    @Override
    public void SetSpecialData(int i)
    {
        velocitySetting = i * 0.01;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        velocitySetting = nbt.getDouble("velocity");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setDouble("velocity", velocitySetting);
    }
}
