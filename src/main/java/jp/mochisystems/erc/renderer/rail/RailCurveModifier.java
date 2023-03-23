package jp.mochisystems.erc.renderer.rail;


import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.erc.rail.IRailController;
import jp.mochisystems.erc.rail.Rail;

public class RailCurveModifier {

    protected Rail rail;
    public void SetRail(Rail rail){
        this.rail = rail;
        UpdateRailData();
    }

    public void UpdateRailData()
    {
        IRailController controller = rail.GetController();
        center.x = controller.CorePosX()+0.5;
        center.y = controller.CorePosY()+0.5;
        center.z = controller.CorePosZ()+0.5;
    }
    private final Vec3d center = new Vec3d();
    private final Vec3d pos = new Vec3d();
    private final Vec3d dir = new Vec3d();
    private final Vec3d up = new Vec3d();
    private final Vec3d cross = new Vec3d();
//    protected double t = 0;

    public Vec3d renderPos = new Vec3d();
    public Vec3d renderNormal = new Vec3d();

    public void TransformVertex(double point, Vec3d vPos, Vec3d normal)
    {


//        pos.SetFrom(0, 0, 0);
//        dir.SetFrom(0, 0, 0);
//        up.SetFrom(0, 0, 0);
//        Math.Spline(pos, point, Base, Next, BaseDir, NextDir);
//        Math.SplineDirection(dir, point, Base, Next, BaseDir, NextDir);
////        Math.SplineNormal(up, point, baseUp, nextUp, BaseDir, NextDir);
//        Math.Slerp(up, point, baseUp, nextUp);

        //Upの回転にDirのズレを足せばうまく行けそう

        rail.Curve().PositionAt(pos, point).sub(center);
        rail.Curve().DirectionAt(dir, point);
        dir.normalize();
        rail.Curve().UpAt(up, dir, point);

        up.normalize();
        cross.CopyFrom(up).cross(dir);
        cross.normalize();

        double px = vPos.x;
        double py = vPos.y;
        double pz = vPos.z;
        double nx = normal.x;
        double ny = normal.y;
        double nz = normal.z;
        // RenderVertexPos = pos + cross * v.CorePosX + up * v.y; // + (v.z * length - pos.z) * v.z;
        renderPos.CopyFrom(pos).add(cross.New().mul(px)).add(up.New().mul(py)); // add(dir.mul(pz * n - pos.z));
        renderNormal.SetFrom(0, 0, 0).add(cross.mul(nx)).add(up.mul(ny)).add(dir.mul(nz));
    }
}
