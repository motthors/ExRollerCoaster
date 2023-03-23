//package erc._mc._1_7_10.network;
//
//import cpw.mods.fml.common.network.simpleimpl.IMessage;
//import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
//import erc._mc._1_7_10._core.ERC_Logger;
//import io.netty.buffer.ByteBuf;
//import net.minecraft.world.World;
//
//public class ERC_MessageCoasterCtS implements IMessage, IMessageHandler<ERC_MessageCoasterCtS, IMessage> {
//
//	// �N���C�A���g����̃��[�����W�p�p�����[�^�֘A���b�Z�[�W
//	public int entityID;
//	public float paramT;
//	public double speed;
//	// ���ݏ���Ă��郌�[���̍��W
//	public int CorePosX;
//	public int y;
//	public int z;
////	// ���f���`��I�v�V����
////	public int modelID;
////	public ModelOptions settings;
//
//	public ERC_MessageCoasterCtS(){/*settings = new ModelOptions();*/}
//
//	public ERC_MessageCoasterCtS(int id, float t, double v, int CorePosX, int y, int z)
//	{
//		super();
//
//	    this.paramT = t;
//	    this.entityID = id;
//	    this.speed = v;
//	    this.CorePosX = CorePosX;
//	    this.y = y;
//	    this.z = z;
//
////	    this.modelID = ID;
////	    this.settings = op;
//  	}
//
//	@Override
//	public void toBytes(ByteBuf buf)
//	{
//		buf.writeFloat(this.paramT);
//		buf.writeInt(this.entityID);
//		buf.writeDouble(this.speed);
//		buf.writeInt(this.CorePosX);
//		buf.writeInt(this.y);
//		buf.writeInt(this.z);
//
////		buf.writeInt(this.modelID);
////		settings.WriteBuf(buf);
//	}
//
//	@Override
//    public void fromBytes(ByteBuf buf)
//    {
//	    this.paramT = buf.readFloat();
//	    this.entityID = buf.readInt();
//	    this.speed = buf.readDouble();
//	    this.CorePosX = buf.readInt();
//	    this.y = buf.readInt();
//	    this.z = buf.readInt();
//
////	    this.modelID = buf.readInt();
////	    settings.ReadBuf(buf);
//    }
//
//	@Override
//    public IMessage onMessage(ERC_MessageCoasterCtS message, MessageContext ctx)
//    {
//		World world = ctx.getServerHandler().playerEntity.worldObj;
//		EntityCoaster coaster = (EntityCoaster)world.getEntityByID(message.entityID);
//		if(coaster == null)return null;
//		if(message.paramT > -50f)
//		{
//			coaster.setParamT(message.paramT);
//			coaster.Speed = message.speed;
//			coaster.setRail( ((TileEntityRail) world.getTileEntity(message.CorePosX, message.y, message.z)).getRail() );
////			coaster.setModel(message.modelID);
//		}
//		else
//		{
//			ERC_Logger.warn("MessageCoasterCtS : this code must not call.");
////			coaster.setModel(message.modelID);
////			coaster.setModelOptions(message.settings);
//		}
//        return null;
//    }
//
//}