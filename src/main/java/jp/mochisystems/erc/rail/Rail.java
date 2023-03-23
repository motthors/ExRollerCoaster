package jp.mochisystems.erc.rail;

import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;


public class Rail {

	protected IRailController controller;

    public Rail() {
		this.curve = new RailCurve();
	}


	boolean isInvalid;
	public boolean IsDead(){return isInvalid;}


	public void SetController(IRailController controller)
	{
		this.controller = controller;
	}
	public IRailController GetController(){ return this.controller; }

	protected int nx = -999, ny = -999, nz = -999;
	protected int px = -999, py = -999, pz = -999;

	protected RailCurve curve;
	public RailCurve Curve() { return curve; }

	public double Length(){return curve.length;}

	protected Vec3d gravity = new Vec3d(0, -9.8, 0);
	public Vec3d Gravity(){return gravity;}
	public void SetGravity(Vec3d g){gravity.CopyFrom(g);}

    final Vec3d vForAccel = new Vec3d();
    public double AccelAt(double t)
	{
		return DirectionAt(vForAccel, t).normalize().dot(gravity);
	}

	public double RegisterAt(double pos, double speed)
	{
		return speed * 0.99865;
	}

	public Vec3d DirectionAt(Vec3d out, double t)
	{
		return curve.DirectionAt(out, t);
	}

	public void UpdateTwist(float angle)
	{
		Rail next = GetNextRail();
		Curve().SetTwist(angle, next==null?null:next.Curve());
	}

	public void OffsetBase(double x, double y, double z)
	{
		Curve().Offset(x, y, z);
	}



	public void SetNextRail(Rail nextRail)
	{
		if(nextRail == null)
		{
			nx = ny = nz = -999;
//			this.nextRail = null;
			return;
		}
		if(this == nextRail) return;
//		if(this.nextRail == nextRail) return;
//		if(this.nextRail != null)
//		{
//			this.nextRail.SetPrevRail(null);
//		}
//		this.nextRail = nextRail;

		IRailController controller = nextRail.GetController();
		nx = (int) controller.CorePosX();
		ny = (int) controller.CorePosY();
		nz = (int) controller.CorePosZ();
		curve.ApplyToNext(nextRail.Curve());
		nextRail.Curve().ApplyToPrev(curve);

//		nextRail.prevRail = this;
//		nextRail.px = this.controller.CorePosX();
//		nextRail.py = this.controller.CorePosY();
//		nextRail.pz = this.controller.CorePosZ();

		Rail nextNextRail = nextRail.GetNextRail();
		if(nextNextRail != null){
			nextRail.Curve().ApplyToNext(nextNextRail.Curve());
			nextNextRail.controller.UpdateRenderer();
		}

		nextRail.controller.UpdateRenderer();
		controller.UpdateRenderer();
	}

	public Rail GetNextRail()
	{
		return controller.GetRail(nx, ny, nz);
	}

	public Rail GetPrevRail()
	{
		return controller.GetRail(px, py, pz);
	}

	public void SetPrevRail(Rail prevRail)
	{
		if(this == prevRail) return;
		if(prevRail == null)
		{
			px = py = pz = -999;
			return;
		}

		IRailController prevController = prevRail.GetController();
		this.px = (int) prevController.CorePosX();
		this.py = (int) prevController.CorePosY();
		this.pz = (int) prevController.CorePosZ();

		prevRail.nx = (int) this.controller.CorePosX();
		prevRail.ny = (int) this.controller.CorePosY();
		prevRail.nz = (int) this.controller.CorePosZ();

		prevRail.Curve().ApplyToNext(curve);
		curve.ApplyToPrev(prevRail.Curve());

		Rail prevPrevRail = prevRail.GetPrevRail();
		if(prevPrevRail != null){
			prevRail.Curve().ApplyToPrev(prevPrevRail.Curve());
			prevPrevRail.controller.UpdateRenderer();
		}

		prevRail.controller.UpdateRenderer();
		controller.UpdateRenderer();

	}

	public static void Connect(Rail base, Rail next)
	{
		next.SetPrevRail(base);
		base.SetNextRail(next);
		next.Curve().Construct();
		base.Curve().Construct();
		next.controller.UpdateRenderer();
		base.controller.UpdateRenderer();
		base.SyncData();
		next.SyncData();
		if(base.GetPrevRail()!=null)base.GetPrevRail().controller.UpdateRenderer();
	}




	// 媒介変数から座標と回転量を計算
    public void CalcAttitudeAt(double t, Quaternion outAttitude, Vec3d pos)
    {
		curve.AttitudeAt(t, outAttitude, pos);
	}


    public void WriteToBytes(ByteBuf buf)
    {
        curve.WriteToBytes(buf);
		buf.writeInt(px);
		buf.writeInt(py);
		buf.writeInt(pz);
		buf.writeInt(nx);
		buf.writeInt(ny);
		buf.writeInt(nz);
    }

    public void ReadFromBytes(ByteBuf buf)
    {
       	curve.ReadFromBytes(buf);
        px = buf.readInt();
        py = buf.readInt();
        pz = buf.readInt();
        nx = buf.readInt();
        ny = buf.readInt();
        nz = buf.readInt();
	}

	public void WriteOptionToBytes(ByteBuf buf)
	{
	}

	public void ReadOptionFromBytes(ByteBuf buf)
	{
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		curve.ReadFromNBT("basepoint", nbt);
		px = nbt.getInteger("px");
		py = nbt.getInteger("py");
		pz = nbt.getInteger("pz");
		nx = nbt.getInteger("nx");
		ny = nbt.getInteger("ny");
		nz = nbt.getInteger("nz");
//		curve.Construct();

//		if(prevRail != null && (
//				prevRail.controller.CorePosX() != px ||
//				prevRail.controller.CorePosY() != py ||
//				prevRail.controller.CorePosZ() != pz
//		)){
//			prevRail = null;
//		}
//		if(nextRail != null && (
//				nextRail.controller.CorePosX() != nx ||
//				nextRail.controller.CorePosY() != ny ||
//				nextRail.controller.CorePosZ() != nz
//		)){
//			nextRail = null;
//		}
	}
	public void writeToNBT(NBTTagCompound nbt)
	{
		curve.WriteToNBT("basepoint", nbt);
		nbt.setInteger("px", px);
		nbt.setInteger("py", py);
		nbt.setInteger("pz", pz);
		nbt.setInteger("nx", nx);
		nbt.setInteger("ny", ny);
		nbt.setInteger("nz", nz);
	}

	public void Unlink()
	{
//		if(nextRail != null) nextRail.SetPrevRail(null);
//		if(prevRail != null) prevRail.SetNextRail(null);
//		nextRail = null;
//		prevRail = null;
	}

	public void Break()
	{
		isInvalid = true;
//		Unlink();
	}


	public void SetSpecialData(int i)
	{
	}

	public void OnEnterCoaster(){}

	public void OnLeaveCoaster(){}

	public void OnDeleteCoaster(){}

	public void SyncData(){
	    controller.SyncData();
    }

    public String StatusToString()
	{
		return "nop";
//		return (GetPrevRail()!=null?
//				"has"+GetPrevRail().GetBasePoint().Pos()
//				:"none["+px+"."+py+"."+pz+"]")+
//			(GetNextRail()!=null?
//					"has"+GetNextRail().GetBasePoint().Pos()
//					:"none["+nx+"."+ny+"."+nz+"]");
	}




	@Override
	public int hashCode() {
		int newHash = 0;
		newHash = newHash * 31 + 'x';
		newHash = newHash * 31 + (int)GetController().CorePosX();
		newHash = newHash * 31 + 'y';
		newHash = newHash * 31 + (int)GetController().CorePosY();
		newHash = newHash * 31 + 'z';
		newHash = newHash * 31 + (int)GetController().CorePosZ();
		newHash = newHash * 31 + (int)curve.End.x;
		newHash = newHash * 31 + (int)curve.End.y;
		newHash = newHash * 31 + (int)curve.End.z;
		newHash = newHash * 31 + (int)curve.Next.x;
		newHash = newHash * 31 + (int)curve.Next.y;
		newHash = newHash * 31 + (int)curve.Next.z;
		return newHash;
	}
}
