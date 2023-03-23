package jp.mochisystems.erc.coaster;


import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Vec3d;

public class Seat{
    private final Coaster coaster;
    public CoasterSettings.SeatData setting;
    public final Vec3d pos = new Vec3d();

    public Seat(Coaster coaster)
    {
        this.coaster = coaster;
    }

    void SetSetting(CoasterSettings.SeatData setting)
    {
        this.setting = setting;
    }

    public void UpdatePos()
    {
        pos.CopyFrom(setting.LocalPosition);
        Math.RotateVecByQuaternion(pos, coaster.attitude);
        pos.add(coaster.position);
    }
}
