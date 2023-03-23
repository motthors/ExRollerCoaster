package jp.mochisystems.erc.coaster;

import jp.mochisystems.core.math.*;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.erc._mc._core.ERC_Logger;
import jp.mochisystems.erc.loader.ModelPackLoader;

public class CoasterSettings{
    public String ModelID;
    public float Width;
    public float Height;
    public float Weight;
    public float AccelUnit;
    public int MaxEngineLevel;
    public double ConnectDistance;
    public SeatData[] Seats;
    public static class SeatData{
//        public float SeatSize;
        public Vec3d LocalPosition = new Vec3d();
        public Vec3d LocalRotationDegree = new Vec3d();
        public Quaternion LocalRotation = new Quaternion();
    }

    public CoasterSettings CopyFrom(CoasterSettings src)
    {
        this.ModelID = src.ModelID;
        this.Width = src.Width;
        this.Height = src.Height;
        this.AccelUnit = src.AccelUnit;
        this.MaxEngineLevel = src.MaxEngineLevel;
        this.ConnectDistance = src.ConnectDistance;
        this.Seats = new SeatData[src.Seats.length];
        for(int i = 0; i < src.Seats.length; ++i)
        {
            this.Seats[i] = new SeatData();
//            this.Seats[i].SeatSize = src.Seats[i].SeatSize;
            this.Seats[i].LocalPosition.CopyFrom(src.Seats[i].LocalPosition);
            this.Seats[i].LocalRotationDegree.CopyFrom(src.Seats[i].LocalRotationDegree);
            this.Seats[i].LocalRotation.CopyFrom(src.Seats[i].LocalRotation);
        }
        return this;
    }

    protected CoasterSettings(){}

    public static CoasterSettings Default()
    {
        CoasterSettings settings = new CoasterSettings();
        settings.Width = 1.2f;
        settings.Height = 1.2f;
        settings.Weight = 500;
        settings.AccelUnit = 0f;
        settings.MaxEngineLevel = 0;
        settings.ConnectDistance = 1.7;
        settings.setSeatNum(1);
//        settings.Seats[0].SeatSize = 1;
        settings.Seats[0].LocalPosition = new Vec3d(0, 0, 0);
        settings.Seats[0].LocalRotationDegree = new Vec3d(0, 0, 0);
        settings.Seats[0].LocalRotation = new Quaternion();
        settings.ModelID = ModelPackLoader.defaultCoasterID;
        return settings;
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        str.append(ModelID); str.append(',');
        str.append(Width); str.append(',');
        str.append(Height); str.append(',');
        str.append(Weight); str.append(',');
        str.append(AccelUnit); str.append(',');
        str.append(MaxEngineLevel); str.append(",");
        str.append(ConnectDistance); str.append(',');
        str.append(Seats.length); str.append(',');
        for (SeatData Seat : Seats) {
//            str.append(Seat.SeatSize); str.append(',');
            str.append(Seat.LocalPosition.x); str.append(',');
            str.append(Seat.LocalPosition.y); str.append(',');
            str.append(Seat.LocalPosition.z); str.append(',');
            str.append(Seat.LocalRotationDegree.x); str.append(',');
            str.append(Seat.LocalRotationDegree.y); str.append(',');
            str.append(Seat.LocalRotationDegree.z); str.append(',');
        }
        return str.toString();
    }

    public void FromString(String str)
    {
        if("".equals(str)) return;
        String[] data = str.split(",");
        int i = 0;
        try{
            ModelID = data[i++];
            Width = Float.parseFloat(data[i++]);
            Height = Float.parseFloat(data[i++]);
            Weight = Float.parseFloat(data[i++]);
            AccelUnit = Float.parseFloat(data[i++]);
            MaxEngineLevel = Integer.parseInt(data[i++]);
            ConnectDistance = Double.parseDouble(data[i++]);
            setSeatNum(Integer.parseInt(data[i++]));
            for(int s = 0; s < Seats.length; ++s)
            {
//                setSeatSize(s, Float.parseFloat(data[i++]));
                float x = Float.parseFloat(data[i++]);
                float y = Float.parseFloat(data[i++]);
                float z = Float.parseFloat(data[i++]);
                setSeatOffset(s, x, y, z);
                x = Float.parseFloat(data[i++]);
                y = Float.parseFloat(data[i++]);
                z = Float.parseFloat(data[i++]);
                setSeatRotation(s, x, y, z);
            }
        }catch (NumberFormatException e){
            ERC_Logger.warn("SeatData could not be restored from string.");
        }
    }

    public boolean setSeatNum(int num)
    {
        if(num < 0) throw new IllegalArgumentException("座席数が少なすぎます。0以上を指定してください。");
        this.Seats = new SeatData[num];
        for(int i = 0; i < num; ++i) Seats[i] = new SeatData();
        return true;
    }

    /*
	 * 指定の番号の座席の位置設定を行います。　index:座席番号[0,設定した数-1] CorePosX:横方向　y:高さ方向　z:進行方向　rotation:座席の回転量(degree)
	 */
    public boolean setSeatOffset(int index, double x, double y, double z)
    {
        //if(index < 0 || this.Seats.size() <= index)throw new IllegalArgumentException("座席が少なすぎます。");
        Seats[index].LocalPosition = new Vec3d(x, y, z);
        return true;
    }

    /*
     * 指定の番号の座席の回転量設定を行います。　index:座席番号[1-設定した数]　rotX:進行方向軸の回転量　rotY:垂直軸の回転量　rotZ:水平軸の回転量
     * 回転量の単位は弧度法（radian）です。
     * 回転の適用順はZ>Y>Xです。
     */
    public boolean setSeatRotation(int index, double rotX, double rotY, double rotZ)
    {
        //if(index < 0 || index >= this.Seats.size())return false;
        Seats[index].LocalRotationDegree = new Vec3d(rotX, rotY, rotZ);
        Quaternion q = Seats[index].LocalRotation;
        q.Make(Vec3d.Front, Math.toRadians(rotZ));
        Quaternion.mul(q, q, new Quaternion().Make(Vec3d.Up, Math.toRadians(rotY)));
        Quaternion.mul(q, q, new Quaternion().Make(Vec3d.Left, Math.toRadians(rotX)));
        return true;
    }

//    public boolean setSeatSize(int index, float size)
//    {
//        //if(index < 0 || index >= this.Seats.size())return false;
////        Seats[index].SeatSize = size;
//        return true;
//    }

    public void Fix()
    {
        for(int s = 0; s < Seats.length; ++s)
        {
            if(Seats[s].LocalRotation == null) Seats[s].LocalRotation = new Quaternion();
            Quaternion q = Seats[s].LocalRotation;
            q.Make(Vec3d.Front, Math.toRadians(Seats[s].LocalRotationDegree.z));
            Quaternion.mul(q, q, new Quaternion().Make(Vec3d.Up, Math.toRadians(Seats[s].LocalRotationDegree.y)));
            Quaternion.mul(q, q, new Quaternion().Make(Vec3d.Left, Math.toRadians(Seats[s].LocalRotationDegree.x)));
        }
    }
}