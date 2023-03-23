package jp.mochisystems.erc._mc.network;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.erc._mc.entity.EntityCoaster;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageAccelCoaster implements IMessage {

	private int id;
	private int accelLevel;

	@SuppressWarnings("unused")
	public MessageAccelCoaster(){}

	public MessageAccelCoaster(int entityId, int accelLevel)
	{
	    this.id = entityId;
		this.accelLevel = accelLevel;
  	}

	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.id);
		buf.writeInt(this.accelLevel);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.id = buf.readInt();
	    this.accelLevel = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageAccelCoaster, IMessage> {
		@Override
		public IMessage onMessage(MessageAccelCoaster m, MessageContext ctx) {
			World world = ctx.getServerHandler().player.world;
			EntityCoaster coaster = (EntityCoaster)world.getEntityByID(m.id);
			if(coaster == null) return null;

			coaster.GetCoaster().engineLevel = m.accelLevel;

			return null;
		}
	}
}
