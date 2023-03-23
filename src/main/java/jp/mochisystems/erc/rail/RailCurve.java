package jp.mochisystems.erc.rail;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.erc.coaster.Coaster;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class RailCurve {
    //for B-Spline
    public final Vec3d Prev = new Vec3d();
    public final Vec3d Base = new Vec3d();
    public final Vec3d End = new Vec3d();
    public final Vec3d Next = new Vec3d();
    public final Vec3d p0Up = new Vec3d();
    public final Vec3d p1Up = new Vec3d();
    float twist = 0;
    public float Twist(){return twist;}

    private int pointNum = -1;
    public int GetPointNum(){return pointNum;}
//    private double[] fixedParamTInPointIndex;
//    public double[] GetPointList() { return fixedParamTInPointIndex; }

    private double[] LenByDivT;
    public double[] LenByDivT() { return LenByDivT; }

    double length;


    public RailCurve()
    {
        Base.y = -999;
        twist = 0f;
    }

    public void Reset(Vec3d BlockPos)
    {
        Base.CopyFrom(BlockPos).add(0.5);
        twist = 0f;
    }



    public void Init(Vec3d pos, Vec3d dir, Vec3d up, float power)
    {
        dir = dir.New().mul(power);
        Base.CopyFrom(pos);
        Prev.CopyFrom(pos).sub(dir);
        End.CopyFrom(pos).add(dir);
        Next.CopyFrom(pos).add(dir).add(dir);
        p0Up.CopyFrom(up).normalize();
        p1Up.CopyFrom(up).normalize();
        SetPointNum(20);
        Construct();
    }

    public void ApplyToPrev(RailCurve prev)
    {
        prev.End.CopyFrom(Base);
        prev.Next.CopyFrom(End);
        prev.UpdateUp(this);
        Construct();
    }
    public void ApplyToNext(RailCurve next)
    {
        next.Prev.CopyFrom(Base);
        UpdateUp(next);
        Construct();
    }


    public void AddPointNum(int add)
    {
        if(pointNum >= 100) return;
        if(pointNum <= 2) return;
        else SetPointNum(pointNum + add);
        Construct();
    }

    public void SetPointNum(int num)
    {
        if(pointNum == num) return;
        this.pointNum = num;
//        fixedParamTInPointIndex = new double[pointNum];
        LenByDivT = new double[pointNum+1];
        Construct();
    }


    public void SetTwist(float angle, RailCurve next)
    {
        twist = angle;
        UpdateUp(next);
    }

    public void Offset(double x, double y, double z)
    {
        Base.add(x, y, z);
        Construct();
    }


    public void AttitudeAt(double t, Quaternion outAttitude, Vec3d pos)
    {
//        t = FixParameter(t);
        PositionAt(pos, t);
        Vec3d dir = DirectionAt(new Vec3d(), t).normalize();
        Vec3d up = UpAt(new Vec3d(), dir, t);

        Quaternion.MakeQuaternionFromDirUp(outAttitude, dir, up);
    }
    public Vec3d PositionAt(Vec3d out, double t)
    {
        return Math.B_Spline(out, t, Prev, Base, End, Next);
    }
    public Vec3d DirectionAt(Vec3d out, double t)
    {
        return Math.B_SplineDir(out, t, Prev, Base, End, Next);
    }
    public Vec3d UpAt(Vec3d out, Vec3d dir, double t)
    {
//        double u = Math.CatmullRom1(t, twist, twistBase, twistEnd, twistNext);
//        Math.rotateAroundVector(out, p0Up, dir, u);
        return Vec3d.SLerp(out, (float) t, p0Up, p1Up);
    }


    protected void UpdateUp(RailCurve next)
    {
//        Logger.debugInfo("Update UP :"+p1Up);
        Vec3d p0Dir = DirectionAt(new Vec3d(), 0).normalize();
        Vec3d p1Dir = DirectionAt(new Vec3d(), 1).normalize();
        if(Double.isNaN(p1Dir.x)){
            p1Dir.CopyFrom(p0Dir);
        }
//        p1Dir  = DirectionAt(p1Dir, 1).normalize();
        double angle = Math.AngleBetweenTwoVec(p0Dir, p1Dir);
        if(angle != 0) Math.rotateAroundVector(p1Up, p0Up, p0Dir.cross(p1Dir), -angle);
        Math.rotateAroundVector(p1Up, p1Up, p1Dir, twist);
        p1Up.normalize();
        p1Up.cross(p1Dir).mul(-1).cross(p1Dir).normalize();
//        Logger.debugInfo("Updated :"+p1Up);

        if(Double.isNaN(p0Up.x)) p0Up.SetFrom(0, 1, 0);
        if(next!=null) {
            next.p0Up.CopyFrom(p1Up);
        }

    }

    public void Construct()
    {
        length = 0;
        Vec3d center = PositionAt(new Vec3d(), 0);
        Vec3d prevCenter = center.New();
//        double[] lengthInPointIndex = new double[pointNum];
//        lengthInPointIndex[0] = 0;

        for(int i = 1; i < pointNum+1; ++i)
        {
            float f = (float)i/(float)(pointNum);

            PositionAt(center, f);
            double dLen = center.distanceTo(prevCenter);
            length += dLen;
            LenByDivT[i] = length;
//            lengthInPointIndex[i] = length;
            prevCenter.CopyFrom(center);
        }

//        calcFixedParamT(lengthInPointIndex); // 点の分布の偏りを補正
//		MakeQuaternionForEachPoint();

//        Logger.debugInfo("CONSTRUCT"+Base);
    }


//    private void calcFixedParamT(double[] lengthInPointIndex)
//    {
//        double lengthPerPoint = length / (float)(pointNum-1);
//        double tPerPoint = 1d / (pointNum-1);
//
//        int I = 0;
//        for(int i = 1; i < pointNum; ++i)
//        {
//            double fixedLengthInPoint = lengthPerPoint * i;
//            boolean isExistFixedPointBetweenPoint =
//                    (lengthInPointIndex[I] <= fixedLengthInPoint)
//                            && (fixedLengthInPoint < lengthInPointIndex[I+1]);
//            if( isExistFixedPointBetweenPoint )
//            {
//                double t = (fixedLengthInPoint - lengthInPointIndex[I])
//                        / (lengthInPointIndex[I+1] - lengthInPointIndex[I]);
//
//                fixedParamTInPointIndex[i] = (I + t) * tPerPoint;
//            }
//            else
//            {
//                if(I < pointNum - 2)
//                {
//                    ++I;
//                    --i;
//                }
//                else
//                {
//                    fixedParamTInPointIndex[i] = 1.0f;
//                }
//            }
//        }
//        fixedParamTInPointIndex[pointNum-1] = 1.0f;
//    }
//    public double FixParameter(double t)
//    {
//        int indexT = (int) java.lang.Math.floor(t * (pointNum - 1));
//        return (pointNum - 1 <= indexT) ?
//                fixedParamTInPointIndex[pointNum-1] :
//                Math.Lerp(t*(pointNum-1)-indexT, fixedParamTInPointIndex[indexT], fixedParamTInPointIndex[indexT+1]);
//    }



    public void WriteToBytes(ByteBuf buf)
    {
//        _attitude.WriteToBytes(buf);
//        _attitude.makeDirection();
        Prev.WriteBuf(buf);
        Base.WriteBuf(buf);
        End.WriteBuf(buf);
        Next.WriteBuf(buf);
        p0Up.WriteBuf(buf);
        p1Up.WriteBuf(buf);
        buf.writeInt(pointNum);
        buf.writeFloat(twist);
    }
    public void ReadFromBytes(ByteBuf buf)
    {
//        _attitude.ReadFromBytes(buf);
//        _attitude.makeDirection();
        Prev.ReadBuf(buf);
        Base.ReadBuf(buf);
        End.ReadBuf(buf);
        Next.ReadBuf(buf);
        p0Up.ReadBuf(buf);
        p1Up.ReadBuf(buf);
        pointNum = buf.readInt();
        twist = buf.readFloat();
        Construct();
    }
    public void WriteToNBT(String key, NBTTagCompound tag)
    {
//        _attitude.WriteToNBT(key+"q", tag);
//        _attitude.makeDirection();
        Prev.WriteToNBT(key+"prev", tag);
        Base.WriteToNBT(key+"base", tag);
        End.WriteToNBT(key+"end", tag);
        Next.WriteToNBT(key+"next", tag);
        p0Up.WriteToNBT(key+"p0up", tag);
        p1Up.WriteToNBT(key+"p1up", tag);
        tag.setInteger(key+"pointNum", pointNum);
        tag.setFloat(key + "twist", twist);
    }
    public void ReadFromNBT(String key, NBTTagCompound tag)
    {
//        _attitude.ReadFromNBT(key+"q", tag);
//        _attitude.makeDirection();
        Prev.ReadFromNBT(key+"prev", tag);
        Base.ReadFromNBT(key+"base", tag);
        End.ReadFromNBT(key+"end", tag);
        Next.ReadFromNBT(key+"next", tag);
        p0Up.ReadFromNBT(key+"p0up", tag);
        p1Up.ReadFromNBT(key+"p1up", tag);
        int num = tag.hasKey("pointNum") ? tag.getInteger("pointNum") : 20;
        twist = tag.getFloat(key + "twist");
        SetPointNum(num);
    }

    public boolean equals(RailCurve that)
    {
        return false;
//        return _attitude.equals(that._attitude)
//                && power == that.power;
    }
}
