//package jp.mochisystems.erc.rail;
//
//import jp.mochisystems.core.math.Mat4;
//import jp.mochisystems.core.math.Quaternion;
//import jp.mochisystems.core.math.Vec3d;
//import jp.mochisystems.erc._mc.gui.GUIRail;
//import io.netty.buffer.ByteBuf;
//import jp.mochisystems.erc.renderer.rail.RailRenderer;
//import net.minecraft.nbt.NBTTagCompound;
//
//public interface IRail{
//
//	void InitRail(Vec3d pos, Vec3d dir, Vec3d up);
//	void SetRenderer(RailRenderer re);
//
//	void SetController(IRailController controller);
//	IRailController GetController();
//
//	RailPoint GetBasePoint();
//	RailPoint GetNextPoint();
//
////	int posX();
////	int posY();
////	int posZ();
//
//	double Length();
//	void ForceDirty();
//	int GetPointNum();
//	void SetPointNum(int num);
//	void AddPointNum(int add);
//	double AccelAt(double pos);
//	double RegisterAt(double pos, double speed);
//	Vec3d DirectionAt(Vec3d out, double pos);
//	void SetBasePoint(Vec3d pos, Vec3d dir, Vec3d up, float power);
//	void SetBasePoint(RailPoint refPoint);
//	void SetNextPoint(Vec3d pos, Vec3d dir, Vec3d up, float power);
//	void SetNextPoint(RailPoint refPoint);
//
//	void SetNextRail(IRail nextRail);
//	IRail GetNextRail();
//	void SetPrevRail(IRail prevRail);
//	IRail GetPrevRail();
//	IRail GetCurrentRail();
//	void SetRailPower(float power);
//	void RotateDirectionAsYaw(double angle);
//	void RotateDirectionAsPitch(double angle);
//	void RotateDirectionAsRoll(double angle);
//	void AddPower(int idx);
//	void ConstructCurve();
//	void Break();
//	void CalcAttitudeAt(double t, Mat4 outAttitudeMatrix, Vec3d pos);
//	void CalcAttitudeAt(double t, Quaternion outAttitude, Vec3d pos);
//	void Smoothing();
//	double FixParameter(double t);
//	void FixConnection();
//
//	void SetSpecialData(int i);
//	void SpecialGUIInit(GUIRail gui);
//
//	void SyncData();
//
//	void readFromNBT(NBTTagCompound nbt);
//	void writeToNBT(NBTTagCompound nbt);
//
//	// event
//	void OnEnterCoaster();
//	void OnLeaveCoaster();
//	void OnDeleteCoaster();
//
//	// option
//	void SetVisible(boolean active);
//	boolean IsActive();
//
//	// MessageHandler
//	void WriteToBytes(ByteBuf buffer);
//	void ReadFromBytes(ByteBuf buffer);
//	void WriteOptionToBytes(ByteBuf buffer);
//	void ReadOptionFromBytes(ByteBuf buffer);
//
//	double[] GetPointList();
//
//	void Render();
//}
