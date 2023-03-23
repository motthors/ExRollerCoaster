package jp.mochisystems.erc._mc.network;

import jp.mochisystems.erc._mc.entity.EntityCoaster;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ERC_MessageRequestConnectCtS implements IMessage {

	public int playerEntityID;
	public int CoasterID;
	
	public ERC_MessageRequestConnectCtS(){}
	
	public ERC_MessageRequestConnectCtS(int playerid, int coasterid)
	{
		playerEntityID = playerid;
		CoasterID = coasterid;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.playerEntityID);
		buf.writeInt(this.CoasterID);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		this.playerEntityID = buf.readInt();
		this.CoasterID = buf.readInt();
    }	    	

    public static class Handler implements IMessageHandler<ERC_MessageRequestConnectCtS, IMessage> {
		@Override
		public IMessage onMessage(ERC_MessageRequestConnectCtS message, MessageContext ctx) {
			World world = ctx.getServerHandler().player.world;
			EntityCoaster coaster = (EntityCoaster) world.getEntityByID(message.CoasterID);
//		ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageCoasterMisc(coaster, 2));
			return null;
		}
	}
}