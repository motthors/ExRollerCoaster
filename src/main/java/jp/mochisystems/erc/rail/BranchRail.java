package jp.mochisystems.erc.rail;


import jp.mochisystems.core.math.Vec3d;

public class BranchRail extends Rail {

    private BranchRail sideRail;

    public BranchRail() {
        curve = new RailCurveForBranch();
    }
    public void SetSide(BranchRail rail, boolean isMain)
    {
        sideRail = rail;
//        ((RailCurveForBranch)curve).SetMidRef(mid, isMain);
        ((RailCurveForBranch)curve).SetSide((RailCurveForBranch)sideRail.curve);
    }


//    public RailCurve Curve() { return GetCurrentRail().Curve(); }



    @Override
    public void SetPrevRail(Rail prevRail)
    {
//        Logger.debugInfo("BranchRail");
        super.SetPrevRail(prevRail);
        sideRail._SetPrevRail(prevRail);
    }

    private void _SetPrevRail(Rail prevRail)
    {
        super.SetPrevRail(prevRail);
    }


    @Override
    public void OffsetBase(double x, double y, double z)
    {
        Curve().Offset(x, y, z);
        _OffsetBase(x, y, z);
    }

    private void _OffsetBase(double x, double y, double z)
    {
        sideRail.Curve().Offset(x, y, z);

        Rail prev = sideRail.GetPrevRail();
        if (prev != null) {
            sideRail.Curve().ApplyToPrev(prev.Curve());
            prev.SyncData();
            if(prev.GetPrevRail() != null) {
                prev.Curve().ApplyToPrev(prev.GetPrevRail().Curve());
                prev.GetPrevRail().SyncData();
            }
        }
        Rail next = sideRail.GetNextRail();
        if(next != null) {
            sideRail.Curve().ApplyToNext(next.Curve());
            next.SyncData();
            if(next.GetNextRail() != null) {
                next.Curve().ApplyToNext(next.GetNextRail().Curve());
                next.GetNextRail().SyncData();
            }
        }
    }
}
