package jp.mochisystems.erc._mc.network;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.erc._mc.entity.EntityCoaster;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.coaster.Coaster;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestConnectingCoasterStC implements IMessage {

	public int parentEntityID;
	public int childEntityID;

	public MessageRequestConnectingCoasterStC() {}

	public MessageRequestConnectingCoasterStC(EntityCoaster parent, EntityCoaster child)
	{
		this.parentEntityID = parent.getEntityId();
		this.childEntityID = parent.getEntityId();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.parentEntityID);
		buf.writeInt(this.childEntityID);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		this.parentEntityID = buf.readInt();
		this.childEntityID = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageRequestConnectingCoasterStC, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestConnectingCoasterStC message, MessageContext ctx) {
			World world = FMLClientHandler.instance().getClient().world;
			EntityCoaster parentEntity = (EntityCoaster) world.getEntityByID(message.parentEntityID);
			EntityCoaster childEntity = (EntityCoaster) world.getEntityByID(message.childEntityID);

			if (parentEntity == null || childEntity == null){
				Logger.warn("EntityID unsync.");
				return null;
			}

			Coaster parent = parentEntity.GetCoaster();
			Coaster child = childEntity.GetCoaster();
			Coaster.ConnectCoasterAtLast(parent, child);
			return null;
		}
	}
}