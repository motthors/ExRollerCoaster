package jp.mochisystems.erc._mc.network;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.rail.IRailController;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncRailOptionStC implements IMessage {

	Rail rail;

	public MessageSyncRailOptionStC(){}

	public MessageSyncRailOptionStC(Rail rail)
	{
		this.rail = rail;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		IRailController controller = rail.GetController();
        buf.writeInt((int)controller.CorePosX());
        buf.writeInt((int)controller.CorePosY());
        buf.writeInt((int)controller.CorePosZ());
		buf.writeBoolean(controller.IsActive());
		rail.WriteOptionToBytes(buf);
    }

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		BlockPos pos = new BlockPos(x, y, z);
		TileEntityRail tile = (TileEntityRail) FMLClientHandler.instance().getClient().world.getTileEntity(pos);
		tile.SetActive(buf.readBoolean());
		rail = tile.getRail();
		rail.ReadOptionFromBytes(buf);
	}

	public static class Handler implements IMessageHandler<MessageSyncRailOptionStC, IMessage> {
		@Override
		public IMessage onMessage(MessageSyncRailOptionStC message, MessageContext ctx) {
			return null;
		}
	}
}
