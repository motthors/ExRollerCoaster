package jp.mochisystems.erc._mc.network;

import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc._mc.entity.EntityCoaster;
import io.netty.buffer.ByteBuf;
import jp.mochisystems.erc.coaster.Coaster;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageSyncCoasterPosStC implements IMessage {

	public int entityID;
	public double paramT;
	public double lenT;
	public double speed;
	public int x = -1;
	public int y = -1;
	public int z = -1;

	public MessageSyncCoasterPosStC() {}

	public MessageSyncCoasterPosStC(EntityCoaster entity)
	{
        this.entityID = entity.getEntityId();
		this.paramT = entity.GetCoaster().pos.t();
		this.lenT = entity.GetCoaster().pos.Len();
	    this.speed = entity.GetCoaster().getSpeed();
	    Rail rail = entity.GetCoaster().GetCurrentRail();
	    if(rail != null)
        {
            this.x = (int)rail.GetController().CorePosX();
            this.y = (int)rail.GetController().CorePosY();
            this.z = (int)rail.GetController().CorePosZ();
        }
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
        buf.writeInt(this.entityID);
		buf.writeDouble(this.paramT);
		buf.writeDouble(this.lenT);
		buf.writeDouble(this.speed);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityID = buf.readInt();
		this.paramT = buf.readDouble();
		this.lenT = buf.readDouble();
	    this.speed = buf.readDouble();
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageSyncCoasterPosStC, IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(MessageSyncCoasterPosStC message, MessageContext ctx) {
			World world = Minecraft.getMinecraft().world;
//			World world = net.minecraftforge.fml.client.FMLClientHandler.instance().getClient().world;
			EntityCoaster entityCoaster = (EntityCoaster) world.getEntityByID(message.entityID);

			if (entityCoaster == null) return null;

			Coaster coaster = entityCoaster.GetCoaster();

			TileEntityRail current = (TileEntityRail) coaster.GetCurrentRail().GetController();
			if(current != null && (
					current.getPos().getX() != message.x ||
					current.getPos().getY() != message.y ||
					current.getPos().getZ() != message.z)) {
				TileEntityRail tileRail = (TileEntityRail) world.getTileEntity(new BlockPos(message.x, message.y, message.z));
				Rail rail = tileRail.getRail();
				coaster.SetNewRail(rail);
			}

			coaster.setPosition(message.paramT);
			coaster.setLengthT(message.lenT);
			coaster.setSpeed(message.speed);
//			ERC_Logger.info("sync t");
//			ERC_Logger.debugInfo("update t");
//		if(message.connectparentID > -1)
//		{
//			 ERC_EntityCoaster parent = (ERC_EntityCoaster)FMLClientHandler.instance().getClient().theWorld.getEntityByID(message.connectparentID);
//			 if(parent == null)
//			 {
//				 coaster.killCoaster();
//				 return null;
//			 }
//			 ((ERC_EntityCoasterConnector)coaster).setParentPointer(parent);
//			 parent.connectionCoaster((ERC_EntityCoasterConnector) coaster);
//			 ((ERC_EntityCoasterConnector)coaster).setConnectParentFlag(-1);
//		}
			return null;
		}
	}
}