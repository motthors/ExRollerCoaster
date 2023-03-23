//package jp.mochisystems.erc._mc.network;
//
//import jp.mochisystems.core.math.Quaternion;
//import jp.mochisystems.core.math.Vec3d;
//import jp.mochisystems.erc._mc.entity.EntityCoaster;
//import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
//import io.netty.buffer.ByteBuf;
//import jp.mochisystems.erc.coaster.Coaster;
//import jp.mochisystems.erc.coaster.CoasterSettings;
//import jp.mochisystems.erc.rail.Rail;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraftforge.fml.client.FMLClientHandler;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
//import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
//
//public class MessageSyncCoasterSettings implements IMessage {
//
//	public int entityID;
//	public double paramT;
//	public double lenT;
//	public double speed;
//	public int x;
//	public int y;
//	public int z;
//	public CoasterSettings settings;
//	public State state = State.request;
//
//	public enum State{
//	    request(0), answer(1);
//	    int idx;
//	    State(int i){idx = i;}
//	    public static State toState(int idx){return (idx==0)?State.request:State.answer;}
//	}
//
//	public MessageSyncCoasterSettings()
//	{
//		settings = CoasterSettings.Default();
//	}
//
//	public MessageSyncCoasterSettings(State state, int entityId)
//    {
//        this.state = state;
//        this.entityID = entityId;
//    }
//
//	public MessageSyncCoasterSettings(int id, double t, double v, Rail rail, CoasterSettings settings)
//	{
//		this.paramT = t;
////		this.lenT = coaster　使うなら
//	    this.entityID = id;
//	    this.speed = v;
//	    this.x = rail.GetController().CorePosX();
//	    this.y = rail.GetController().CorePosY();
//	    this.z = rail.GetController().CorePosZ();
//	    this.settings = settings;
//	    state = State.answer;
//	}
//
//	@Override
//	public void toBytes(ByteBuf buf)
//	{
//        buf.writeInt(this.entityID);
//        buf.writeInt(state.idx);
//        if(state == State.request) return;
//
//		buf.writeDouble(this.paramT);
//		buf.writeDouble(this.lenT);
//		buf.writeDouble(this.speed);
//		buf.writeInt(this.x);
//		buf.writeInt(this.y);
//		buf.writeInt(this.z);
//		byte[] data = settings.ModelID.getBytes();
//		buf.writeInt(data.length);
//		buf.writeBytes(data);
//		buf.writeFloat(settings.Width);
//		buf.writeFloat(settings.Height);
//		buf.writeFloat(settings.Weight);
//		buf.writeDouble(settings.ConnectDistance);
//
//
//
//        buf.writeInt(settings.Seats.length);
//        for (int i=0; i<settings.Seats.length; ++i)
//		{
////			buf.writeFloat(settings.Seats[i].SeatSize);
//			settings.Seats[i].LocalPosition.WriteBuf(buf);
//			settings.Seats[i].LocalRotationDegree.WriteBuf(buf);
//		}
//	}
//
//	@Override
//    public void fromBytes(ByteBuf buf)
//    {
//        this.entityID = buf.readInt();
//        if(State.toState(buf.readInt()) == State.request) return;
//
//		this.paramT = buf.readDouble();
//		this.lenT = buf.readDouble();
//	    this.speed = buf.readDouble();
//	    this.x = buf.readInt();
//	    this.y = buf.readInt();
//	    this.z = buf.readInt();
//		byte[] bytes = new byte[buf.readInt()];
//		buf.readBytes(bytes);
//		settings.ModelID = new String(bytes);
//		settings.Width = buf.readFloat();
//		settings.Height = buf.readFloat();
//		settings.Weight = buf.readFloat();
//		settings.ConnectDistance = buf.readDouble();
//
//        settings.setSeatNum(buf.readInt());
//		for (int i=0; i<settings.Seats.length; ++i)
//		{
////			settings.Seats[i].SeatSize = buf.readFloat();
//			settings.Seats[i].LocalPosition.ReadBuf(buf);
//			settings.Seats[i].LocalRotationDegree.ReadBuf(buf);
//			settings.Seats[i].LocalRotation.Make(Vec3d.Front, settings.Seats[i].LocalRotationDegree.z)
//					.mul(new Quaternion().Make(Vec3d.Up, settings.Seats[i].LocalRotationDegree.y))
//					.mul(new Quaternion().Make(Vec3d.Left, settings.Seats[i].LocalRotationDegree.x))
//					.normalized();
//		}
//    }
//
//    public static class Handler implements IMessageHandler<MessageSyncCoasterSettings, IMessage> {
//		@Override
//		public IMessage onMessage(MessageSyncCoasterSettings message, MessageContext ctx) {
//			World world = FMLClientHandler.instance().getClient().world;
//			EntityCoaster entityCoaster = (EntityCoaster) world.getEntityByID(message.entityID);
//
//			if (entityCoaster == null) return null;
//
//			Coaster coaster = entityCoaster.GetCoaster();
//
//			if (message.state == State.request) {
//				MessageSyncCoasterSettings packet =
//						new MessageSyncCoasterSettings(
//								entityCoaster.getEntityId(),
//								coaster.pos.t(), coaster.getSpeed(),
//								coaster.GetCurrentRail(),
//								coaster.GetSettings());
//				return packet;
//			}
//
//			TileEntityRail tileRail = (TileEntityRail) world.getTileEntity(new BlockPos(message.x, message.y, message.z));
//			Rail rail = tileRail.getRail();
//
//			coaster.setPosition(message.paramT);
//			coaster.setLengthT(message.lenT);
//			coaster.setSpeed(message.speed);
//			coaster.SetSettingData(message.settings);
//			coaster.SetNewRail(rail);
//
////		if(message.connectparentID > -1)
////		{
////			 ERC_EntityCoaster parent = (ERC_EntityCoaster)FMLClientHandler.instance().getClient().theWorld.getEntityByID(message.connectparentID);
////			 if(parent == null)
////			 {
////				 coaster.killCoaster();
////				 return null;
////			 }
////			 ((ERC_EntityCoasterConnector)coaster).setParentPointer(parent);
////			 parent.connectionCoaster((ERC_EntityCoasterConnector) coaster);
////			 ((ERC_EntityCoasterConnector)coaster).setConnectParentFlag(-1);
////		}
//			return null;
//		}
//	}
//}