//package jp.mochisystems.erc.rail;
//
//import io.netty.buffer.ByteBuf;
//import jp.mochisystems.core.math.Vec3d;
//
//public class NonGravityRail extends Rail{
//
//    public NonGravityRail()
//    {
//        super();
//        gravity = new Vec3d(0, 0, 0);
//    }
//
//
//    @Override
//    public void WriteOptionToBytes(ByteBuf buffer)
//    {
//        gravity.ReadBuf(buffer);
//    }
//
//    @Override
//    public void ReadOptionFromBytes(ByteBuf buffer)
//    {
//        gravity.WriteBuf(buffer);
//    }
//}
