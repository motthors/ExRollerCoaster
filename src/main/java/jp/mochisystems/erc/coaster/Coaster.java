package jp.mochisystems.erc.coaster;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core.math.*;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.erc.loader.ModelPackLoader;
import jp.mochisystems.erc.manager.CoasterIdManager;
import jp.mochisystems.erc.rail.Rail;

public class Coaster {

	public interface ICoasterController
	{
		Rail GetCurrentRail();
		void StoreRailPos(Rail rail);
		void LoadRailInfoAndGetRail();
		void StoreRailInfoAll(float t, float speed, Rail rail);
		void SyncRailPos();
		void StoreCoasterInfo(int coasterId, int parentId);
		void LoadCoasterInfo();
		void StoreSettings(CoasterSettings settings);
		void LoadSettings();
		void SetDead();
		void OnLostRail();
		boolean IsRemote();
	}

	public int coasterId = -1;
	public int parentId = -1;
	private int UpdatePacketCounter = 60;

	private final ICoasterController controller;
	public final CoasterPos pos;
    public final Vec3d position = new Vec3d();
    public final Vec3d prevPosition = new Vec3d();
	public final Quaternion attitude = new Quaternion();
	public final Quaternion prevAttitude = new Quaternion();
	public final Quaternion.V3Mat attitudeMat = new Quaternion.V3Mat();
	protected double speed = 0;

	private final CoasterSettings settings = CoasterSettings.Default();

	public int engineLevel = 0;

	public Rail GetCurrentRail(){return pos.rail;}
	public void setLengthT(double len){this.pos.lenAtT = len;}
	public void setPosition(double t) { pos.t = t; }
	public void setSpeed(double speed){this.speed = speed;}
	public double getSpeed(){ return this.speed; }

    private Seat[] seats = new Seat[0];
	public Seat GetSeat(int index){return seats[index];}
	public int SeatNum(){ return seats.length; }

//	/**
//	 * 連結されたコースターを表すリスト
//	 * 1つの連結コースターのグループで１インスタンスとすること
//	 */
	private Coaster rootCoaster;
	private Coaster parentCoaster;
	private Coaster childCoaster;
	public Coaster GetNext(){return childCoaster;}



	public Coaster(ICoasterController controller)
	{
		this.controller = controller;
	    pos = new CoasterPos();
		rootCoaster = this;
		parentCoaster = null;
		childCoaster = null;
	}

	public static void InitForParentCoasterFirstPlacing(Coaster coaster, Rail rail, String ModelId)
	{
		CoasterSettings settings = ModelPackLoader.Instance.GetHeadCoasterSettings(ModelId);
		coaster.SetSettingData(settings);
		coaster.SetNewRail(rail);
		rail.OnEnterCoaster();
		coaster.coasterId = CoasterIdManager.Instance.RegisterNew(coaster, coaster.controller.IsRemote());
		coaster.controller.StoreCoasterInfo(coaster.coasterId, coaster.parentId);
	}

	public static void InitForConnectToSpawnedParent(Coaster connectCoaster, Coaster parent, String ModelId)
	{
		CoasterSettings settings = ModelPackLoader.Instance.GetConnectCoasterSettings(ModelId);
		connectCoaster.SetSettingData(settings);
		connectCoaster.coasterId = CoasterIdManager.Instance.RegisterNew(connectCoaster, connectCoaster.controller.IsRemote());
		ConnectCoasterAtLast(parent, connectCoaster);
	}




	public Coaster getLastCoaster()
	{
		return (childCoaster != null) ? childCoaster.getLastCoaster() : this;
	}


	public static void ConnectCoasterAtLast(Coaster parent, Coaster child)
	{
		if(child.parentCoaster != null) return;
		if(parent == null) {
			child.controller.SetDead();
			return;
		}

		if(parent.getLastCoaster().coasterId == child.coasterId){
			Logger.error("yaba");
		}
		Coaster lastCoaster = parent.getLastCoaster();
//		Logger.debugInfo("SetChild : "+lastCoaster);
		lastCoaster.childCoaster = child;
		child.parentCoaster = lastCoaster;
		child.rootCoaster = lastCoaster.rootCoaster;
		child.pos.Clone(lastCoaster.pos);
		child.parentId = lastCoaster.coasterId;
		child.controller.StoreRailInfoAll((float)child.pos.t, (float)child.speed, child.GetCurrentRail());
		child.controller.StoreCoasterInfo(child.coasterId, child.parentId);
	}

    public void SetNewRail(Rail rail)
    {
        pos.rail = rail;
		controller.StoreRailPos(rail);
    }

    public void SetSettingData(CoasterSettings settings)
    {
		if(settings == null) return;
        this.settings.CopyFrom(settings);
		UpdateSeatSettings();
		controller.StoreSettings(settings);
    }

    public void UpdateSettings(String SettingString)
	{
		settings.FromString(SettingString);
		UpdateSeatSettings();
	}

	private void UpdateSeatSettings()
	{
		seats = new Seat[settings.Seats.length];
		for(int i = 0; i < seats.length; i++)
		{
			seats[i] = new Seat(this);
			seats[i].SetSetting(settings.Seats[i]);
		}
	}

    public CoasterSettings GetSettings()
    {
        return settings;
    }

    private long lastUpdatedTick = -1;
    /**
	 * コースター全体更新関数
	 * 連結された全てのコースターの中で最初に呼ばれたUpdateで
	 * 連結しているコースター全ての更新のmoveを行う。
	 * 最初かどうか判定するために利用する値はTickを想定している
	 */
	public void Update(long tick) {
		if(!syncToClient()) return;

		if(pos.rail.IsDead()){
			controller.OnLostRail();
			return;
		}
		if (rootCoaster.lastUpdatedTick != tick) {
			rootCoaster.lastUpdatedTick = tick;
			_Update(rootCoaster, tick);
		}
	}

	private static void _Update(Coaster root, long tick)
	{
		root.move();
		Coaster coaster = root.childCoaster;
		while(coaster != null)
		{
			coaster.lastUpdatedTick = tick;
			coaster.moveForChildren();
			coaster = coaster.childCoaster;
		}
	}

	protected boolean syncToClient()
	{
		if(coasterId < 0) {
			controller.LoadCoasterInfo();
			if (coasterId >= 0) {
				CoasterIdManager.Instance.RegisterContained(coasterId, this, controller.IsRemote());
			}
		}
		if(parentId >= 0 && parentCoaster == null) {
//			Logger.debugInfo("p "+parentId+" > c "+coasterId);
			Coaster parent = CoasterIdManager.Instance.Get(parentId, controller.IsRemote());
			ConnectCoasterAtLast(parent,this);
		}

		if(!controller.IsRemote() && GetCurrentRail() != null) {
			controller.StoreRailPos(pos.rail);
		}
		if(GetCurrentRail() == null)
		{
			controller.LoadRailInfoAndGetRail();
			if(GetCurrentRail() == null) {
				if(this.UpdatePacketCounter-- <= 0) {
					if (!controller.IsRemote() && coasterId < 0) {
						controller.SetDead();
					}
				}
				return false;
			}
		}
		if(this.UpdatePacketCounter-- <= 0) {
			UpdatePacketCounter = 60;
			if(!controller.IsRemote() && rootCoaster==this) {
				controller.SyncRailPos();
//				controller.StoreRailInfoAll((float)pos.t, (float)speed, pos.rail);
			}
		}
		return true;
	}



	protected void move()
	{
		if(pos.hasNotRail())
		{
			Rail rail = controller.GetCurrentRail();
			if(rail == null) {
//				Delete();
				return;
			}
			SetNewRail(rail);
		}

        prevPosition.CopyFrom(position);
		prevAttitude.CopyFrom(attitude);
//		prevAttitudeMatrix.CopyFrom(AttitudeMatrix);

		speed = resist(speed);
		speed = accelerate(speed);
		speed = pos.move(speed);

		pos.rail.CalcAttitudeAt(pos.t, attitude, position);
		attitudeMat.Fix(attitude);
//		attitude.makeDirection();
//        coaster.AttitudeMatrix.makeBuffer();
		UpdateSeat();

//		Logger.debugInfo(pos.t+" : "+pos.rail.curve.Base+" ; "+position);

//		if(seats.length>0)Logger.debugInfo(position+" : "+seats[0].pos+" : "+seats[0].setting.LocalPosition);
	}

	protected void moveForChildren()
	{
		if(pos.hasNotRail())
		{
			Rail rail = controller.GetCurrentRail();
			if(rail == null) {
//				Delete();
				return;
			}
			SetNewRail(rail);
		}
		if(parentCoaster == null) return;

		pos.Clone(parentCoaster.pos);
		pos.Shift(-settings.ConnectDistance);

		prevPosition.CopyFrom(position);
		prevAttitude.CopyFrom(attitude);

		speed = parentCoaster.speed;

		pos.rail.CalcAttitudeAt(pos.t, attitude, position);
//		attitude.makeDirection();
		attitudeMat.Fix(attitude);

		UpdateSeat();
	}

	private void UpdateSeat()
	{
		for(Seat s : seats) s.UpdatePos();
	}

	private double resist(double speed)
	{
		return pos.rail.RegisterAt(pos.t, speed);
	}
	
	private double accelerate(double speed)
	{
		double next = speed + TotalAccel();

		return next;
	}

	
	private double TotalAccel()
	{
        if(pos.hasNotRail()) return 0;
		double totalPower = 0;
		float weights = 0;

		// a = (F - kv) / m

		double k = 1.2;
		Coaster coaster = rootCoaster;
		while(coaster != null)
		{
			// F (engine)
			if(coaster.settings.MaxEngineLevel > 0) {
				double torque = 0;
				double enginePow = coaster.settings.AccelUnit;
				int max = coaster.settings.MaxEngineLevel;
				int lvl = coaster.engineLevel;
				// トルク制限の代わりに空気抵抗定数を上げ下げして代用
				if(lvl > 0){
					torque = enginePow * (max-lvl+1);//max / (double)lvl;
					k = torque * (max-lvl+1);
				}
				else if(lvl < 0){
					torque = -enginePow * (3+lvl+1);//max / (double)lvl;
					k = torque * (3+lvl+1);
				}
				totalPower += torque;
			}
			// F=mg (gravity)
			totalPower += coaster.pos.rail.AccelAt(coaster.pos.t);
			weights += coaster.settings.Weight;

			coaster = coaster.childCoaster;
		}
		double a = totalPower - k * java.lang.Math.abs(rootCoaster.speed);
		return a / weights;
	}

	public void Destroy()
	{
//		Logger.debugInfo("destroy, id:"+coasterId);
		Disconnect();
		if(GetCurrentRail()!=null) GetCurrentRail().OnDeleteCoaster();
		CoasterIdManager.Instance.Remove(coasterId, controller.IsRemote());
	}

	private void Disconnect()
	{
		parentId = -1;
		controller.StoreCoasterInfo(coasterId, -1);
		if(pos.rail != null) pos.rail.OnDeleteCoaster();
		if(parentCoaster != null){
			parentCoaster.childCoaster = null;
			parentCoaster = null;
		}
		if(childCoaster != null)
		{
			childCoaster.parentCoaster = null;
			if(!controller.IsRemote()) childCoaster.controller.SetDead();
		}

	}

	public class CoasterPos{
		private Rail rail;
		private double t = 0;
		private double lenAtT = 0;
		private int idxLast = 0;

		public double t()
		{
			return t;
		}
		public double Len() { return lenAtT; }

        public boolean hasNotRail()
		{
			return rail == null;
		}

		private boolean StepFront(double nextLen, boolean fire)
		{
			int pointNum = rail.Curve().GetPointNum();
			double[] LenByDivT = rail.Curve().LenByDivT();
			int i = idxLast;
			while(true) {
				if (nextLen < LenByDivT[i]) break;
				i++;
				if (i >= pointNum) {
					Rail OverRail = rail.GetNextRail();
					if (OverRail == null) {
						t = 1;
						lenAtT = rail.Length();
						idxLast = rail.Curve().GetPointNum();
						return true;
					}
					LenByDivT = OverRail.Curve().LenByDivT();
					i -= rail.Curve().GetPointNum();
					nextLen -= rail.Length();
					if(fire) {
						rail.OnLeaveCoaster();
						OverRail.OnEnterCoaster();
					}
					rail = OverRail;
				}
			}
			idxLast = i;
			lenAtT = nextLen;
			double dLenInDiv = lenAtT - LenByDivT[idxLast];
			t = idxLast / (float)pointNum;
			t += 1.0 / pointNum * dLenInDiv / (LenByDivT[i+1]-LenByDivT[i]);
//			Logger.debugInfo("[]"+t+" : "+lenAtT+" : "+i+" : "+dLenInDiv);
			return false;
		}

		private boolean StepBack(double nextLen, boolean fire) {
			double[] LenByDivT = rail.Curve().LenByDivT();
			int i = idxLast;
			while (true) {
				if (nextLen >= LenByDivT[i]) break;
				i--;
				if (i < 0) {
					Rail OverRail = rail.GetPrevRail();
					if (OverRail == null) {
						t = 0;
						lenAtT = 0;
						idxLast = 0;
						return true;
					}
					LenByDivT = OverRail.Curve().LenByDivT();
					i += OverRail.Curve().GetPointNum()+1;
					nextLen += OverRail.Length();
					if(fire){
						rail.OnLeaveCoaster();
						OverRail.OnEnterCoaster();
					}
					rail = OverRail;
				}
			}
			idxLast = i;
			lenAtT = nextLen;
			int pointNum = rail.Curve().GetPointNum();
			double dLenInDiv = lenAtT - LenByDivT[idxLast];
			t = idxLast / (float)pointNum;
			t += 1.0 / pointNum * dLenInDiv / (LenByDivT[i+1]-LenByDivT[i]);
//			Logger.debugInfo("[]"+t+" : "+lenAtT+" : "+i+" : "+dLenInDiv);
			return false;
		}

		public double move(double speed)
		{
			// update t
			int pointNum = rail.Curve().GetPointNum();
			boolean onHitEnd = false;
			if (speed > 0) {
				onHitEnd = StepFront(lenAtT + speed, true);
			}else if(speed < 0) {
				onHitEnd = StepBack(lenAtT + speed, true);
			}
			if(onHitEnd) speed *= -0.1;
			return speed;

//			// back and brake if next rail is nothing
//			boolean isExistRail = PassOverRail();
//			if(isExistRail)
//			{
//				return speed;
//			}
//			else
//			{
//				return speed * -0.1;
//			}
		}
		
		public void Clone(CoasterPos source)
		{
			this.rail = source.rail;
			this.t = source.t;
			this.lenAtT = source.lenAtT;
			this.idxLast = source.idxLast;
			controller.StoreRailPos(rail);
		}
		
		void Shift(double shiftLength)
		{
//			Logger.debugInfo("[1]"+t+" : "+lenAtT+" : "+idxLast);
			StepBack(lenAtT + shiftLength, false);
//			Logger.debugInfo("[2]"+t+" : "+lenAtT+" : "+idxLast);

//			t += shiftLength / rail.Length();
//			PassOverRail();
		}

		// @return : isExistRail
		private boolean PassOverRail()
		{
			Rail overRail;
			if(t >= 1.0)
				overRail = rail.GetNextRail();
			else if(t < 0)
				overRail = rail.GetPrevRail();
			else
				return true;

			if(overRail == null) {
//                rail.FixConnection();
                if(t >= 1.0){
                	overRail = rail.GetNextRail();
					if (overRail == null){
						t = 1;
						lenAtT = rail.Length();
						return false;
					}
				}
                else if(t < 0) {
                	overRail = rail.GetPrevRail();
					if (overRail == null) {
						t = 0;
						lenAtT = 0;
						return false;
					}
				}
			}

            rail.OnLeaveCoaster();
			assert overRail != null;
			overRail.OnEnterCoaster();

			if(t < 0){
				t *= rail.Length();
				t /= overRail.Length();
				t += 1.0;
				lenAtT += overRail.Length();
			}else{
				t -= 1.0;
				t *= rail.Length();
				t /= overRail.Length();
				lenAtT -= rail.Length();
			}
            rail = overRail;
            controller.StoreRailPos(rail);
			controller.StoreRailInfoAll((float)t, (float)speed, rail);
            return true;
		}

	}
}
