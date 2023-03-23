package jp.mochisystems.erc.rail;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class RailCurveForBranch extends RailCurve{

    public Vec3d Mid = new Vec3d();
    public boolean isMain;

    public RailCurveForBranch()
    {
        super();
//        this.Mid = new Vec3d();
    }

    RailCurveForBranch side;
    public void SetSide(RailCurveForBranch side)
    {
        this.side = side;
    }
//    public void SetMidRef(Vec3d mid, boolean isMain)
//    {
//        Mid = mid;
//        this.isMain = isMain;
//    }

    private void UpdateMid()
    {
//        if(!isMain) return;
//        Vec3d.Lerp(Mid, 1.6f, Prev, Base);
        double l1 = End.New().sub(Base).length() * 0.5;
        double l2 = side.End.New().sub(Base).length() * 0.5;
        double l = (l1+l2)*0.5;
        Vec3d d = new Vec3d(Base).sub(Prev).normalize().mul(l);
        Mid.CopyFrom(Base).add(d);
        //side._UpdateMid();
    }




    public void AttitudeAt(double t, Quaternion outAttitude, Vec3d pos)
    {
//        t = FixParameter(t);
        if(t < 0.5){
            Math.B_Spline(pos, t*2, Prev, Base, Mid, End);
            Vec3d dir = Math.B_SplineDir(new Vec3d(), t*2, Prev, Base, Mid, End).normalize();
            Vec3d up = UpAt(new Vec3d(), dir, t);
            Quaternion.MakeQuaternionFromDirUp(outAttitude, dir, up);
        }else{
            Math.B_Spline(pos, t*2-1, Base, Mid, End, Next);
            Vec3d dir = Math.B_SplineDir(new Vec3d(), t*2-1, Base, Mid, End, Next).normalize();
            Vec3d up = UpAt(new Vec3d(), dir, t);
            Quaternion.MakeQuaternionFromDirUp(outAttitude, dir, up);
        }

    }
    public Vec3d PositionAt(Vec3d out, double t)
    {
//        return Math.B_Spline(out, t, Prev, Base, Mid, Next);
        if(t < 0.5){
            return Math.B_Spline(out, t*2, Prev, Base, Mid, End);
        }else {
            return Math.B_Spline(out, t*2-1, Base, Mid, End, Next);
        }
    }
    public Vec3d DirectionAt(Vec3d out, double t)
    {
//        return Math.B_SplineDir(out, t, Prev, Base, Mid, Next);
        if(t < 0.5){
            return Math.B_SplineDir(out, t*2, Prev, Base, Mid, End);
        }else {
            return Math.B_SplineDir(out, t*2-1, Base, Mid, End, Next);
        }
    }
    public Vec3d UpAt(Vec3d out, Vec3d dir, double t)
    {
        return Vec3d.SLerp(out, (float) t, p0Up, p1Up);
    }


    protected void UpdateUp(@Nonnull RailCurveForBranch next)
    {
//        Logger.debugInfo("Update UP :"+p1Up);
        Vec3d p0Dir = DirectionAt(new Vec3d(), 0).normalize();
        Vec3d p1Dir = DirectionAt(new Vec3d(), 1).normalize();
        double angle = Math.AngleBetweenTwoVec(p0Dir, p1Dir);
        if(angle == 0) return;
        Math.rotateAroundVector(p1Up, p0Up, p0Dir.cross(p1Dir), -angle);
        Math.rotateAroundVector(p1Up, p1Up, p1Dir, twist);
        p1Up.normalize();
//        Logger.debugInfo("Updated :"+p1Up);

        next.p0Up.CopyFrom(p1Up).normalize();

    }

    public void ApplyToPrev(RailCurve prev)
    {
        prev.End.CopyFrom(Base);
        prev.Next.CopyFrom(Mid);
        prev.UpdateUp(this);
        Construct();
    }
    public void ApplyToNext(RailCurve next)
    {
        next.Prev.CopyFrom(Mid);
        UpdateUp(next);
        Construct();
    }


    public void Construct()
    {
        UpdateMid();
        super.Construct();
    }

    public void ReadFromNBT(String key, NBTTagCompound tag)
    {
        super.ReadFromNBT(key, tag);
        UpdateMid();
    }
    public void ReadFromBytes(ByteBuf buf)
    {
        super.ReadFromBytes(buf);
        UpdateMid();
    }
}
