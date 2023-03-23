//package jp.mochisystems.erc.rail;
//
//public class RailFactory {
//
//    public static int toID(IRail rail)
//    {
//        if(rail instanceof AccelRail) return 2;
//        if(rail instanceof BranchRail) return 3;
//        if(rail instanceof ConstVelocityRail) return 4;
//        if(rail instanceof DetectorRail) return 5;
//        if(rail instanceof NonGravityRail) return 6;
//        if(rail instanceof Rail) return 1;
//        return 0;
//    }
//
//    public static IRail fromID(int id)
//    {
//        switch(id)
//        {
//            case 2 : return new AccelRail();
//            case 3 : return new BranchRail();
//            case 4 : return new ConstVelocityRail();
//            case 5 : return new DetectorRail();
//            case 6 : return new NonGravityRail();
//            case 1 : return new Rail();
//            default : return null;
//        }
//    }
//}
