package jp.mochisystems.erc.rail;


import io.netty.buffer.ByteBuf;
import jp.mochisystems.core.math.Mat4;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import net.minecraft.nbt.NBTTagCompound;

public class RailPoint {

    final Mat4 attitude = new Mat4();
    float power;
    final Vec3d out_dirPower = new Vec3d();

    public RailPoint()
    {
        Reset();
    }

    public void Reset()
    {
        attitude.Identifier();
        attitude.Pos().y = -999;
        power = 10f;
    }

    public void ResetRot()
    {
        attitude.ResetRot();
    }

    public void Set(Vec3d pos, Vec3d dir, Vec3d up, float power)
    {
        attitude.SetFrom(pos, dir.normalize(), up.normalize());
        this.power = power;
        out_dirPower.CopyFrom(Dir()).mul(power);
    }

    public void CopyFrom(RailPoint point)
    {
        attitude.CopyFrom(point.attitude);
        this.power = point.power;
        out_dirPower.CopyFrom(Dir()).mul(power);
    }

    public boolean SetPower(float power)
    {
        if(this.power != power)
        {
            this.power = power;
            out_dirPower.CopyFrom(Dir()).mul(power);
            return true;
        }
        else return false;
    }

    public void RotateYaw(double angle)
    {
        attitude.rotation(angle, attitude.Up());
        out_dirPower.CopyFrom(Dir()).mul(power);

    }
    public void RotateRoll(double angle)
    {
        attitude.rotation(angle, attitude.Dir());
        out_dirPower.CopyFrom(Dir()).mul(power);
    }
    public void RotatePitch(double angle)
    {
        attitude.rotation(angle, attitude.Side());
        out_dirPower.CopyFrom(Dir()).mul(power);
    }

    public void Offset(double x, double y, double z)
    {
        attitude.Pos().add(x, y, z);
        attitude.makeBuffer();
    }

    public boolean AddPower(double ratio)
    {
        if(power > 100f) power = 100;
        else if( power < 0.1f) power = 0.1f;
        else power *= ratio;
        out_dirPower.CopyFrom(Dir()).mul(power);
        return true;
    }

    public Vec3d Pos()
    {
        return attitude.Pos();
    }

    public Vec3d Dir()
    {
        return attitude.Dir();
    }

    public Vec3d DirPower()
    {
        return out_dirPower;
    }

    public Vec3d Up()
    {
        return attitude.Up();
    }

    public Vec3d Side()
    {
        return attitude.Side();
    }

    public void WriteToBytes(ByteBuf buf)
    {
        attitude.WriteToBytes(buf);
        buf.writeFloat(power);
        out_dirPower.CopyFrom(Dir()).mul(power);
    }

    public void ReadFromBytes(ByteBuf buf)
    {
        attitude.ReadFromBytes(buf);
        power = buf.readFloat();
    }

    public void WriteToNBT(String key, NBTTagCompound tag)
    {
        attitude.WriteToNBT(key, tag);
        tag.setFloat(key + "power", power);
    }

    public void ReadFromNBT(String key, NBTTagCompound tag)
    {
        attitude.ReadFromNBT(key, tag);
        power = tag.getFloat(key + "power");
        out_dirPower.CopyFrom(Dir()).mul(power);
    }

    public boolean equals(RailPoint that)
    {
        return attitude.Equals(that.attitude) && power == that.power;
    }
}
