//package erc.coaster;
//
//import io.netty.buffer.ByteBuf;
//
//import java.util.ArrayList;
//
//public class SeatHolder {
//
//    private Coaster parent;
//    private ArrayList<Seat> seatList = new ArrayList<Seat>();
//
//    public SeatHolder(Coaster parent)
//    {
//        this.parent = parent;
//    }
//
//    public void Update(long tick)
//    {
//        for(Seat seat : seatList)
//        {
//            seat.Update(tick);
//        }
//    }
//
//
//    public void WriteBuf(ByteBuf buf)
//    {
//        buf.writeInt(seatList.size());
//        for(Seat seat : seatList)
//        {
//            seat.AttitudeMatrix.Pos().WriteBuf(buf);
//            seat.AttitudeMatrix.Dir().WriteBuf(buf);
//            seat.AttitudeMatrix.Up().WriteBuf(buf);
//        }
//    }
//
//    public void ReadBuf(ByteBuf buf)
//    {
//        int seatNum = buf.readInt();
//        seatList = new ArrayList<>(seatNum);
//        for(int i=0; i < seatNum; ++i)
//        {
//            Seat seat = new Seat(parent);
//            seat.AttitudeMatrix.Pos().ReadBuf(buf);
//            seat.AttitudeMatrix.Dir().ReadBuf(buf);
//            seat.AttitudeMatrix.Up().ReadBuf(buf);
//        }
//    }
//}
