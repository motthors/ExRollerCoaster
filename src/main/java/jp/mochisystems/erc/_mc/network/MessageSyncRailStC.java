package jp.mochisystems.erc._mc.network;

import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import io.netty.buffer.ByteBuf;
import jp.mochisystems.erc.rail.IRailController;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncRailStC implements IMessage {

	Rail rail;
	boolean isNeedReconstruct;

	public MessageSyncRailStC(){}

	public MessageSyncRailStC(Rail rail, boolean needToReconstructCurve)
	{
		this.rail = rail;
        isNeedReconstruct = needToReconstructCurve;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		IRailController controller = rail.GetController();
        buf.writeInt((int)controller.CorePosX());
        buf.writeInt((int)controller.CorePosY());
        buf.writeInt((int)controller.CorePosZ());
		rail.WriteToBytes(buf);
        buf.writeBoolean(isNeedReconstruct);
    }

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		BlockPos pos = new BlockPos(x, y, z);
        TileEntityRail tile = (TileEntityRail) FMLClientHandler.instance().getClient().world.getTileEntity(pos);
        rail = tile.getRail();
        rail.SetController(tile);
		rail.ReadFromBytes(buf);
        isNeedReconstruct = buf.readBoolean();
        if(isNeedReconstruct){
			rail.Curve().Construct();
        }
	}

	public static class Handler implements IMessageHandler<MessageSyncRailStC, IMessage> {
		@Override
		public IMessage onMessage(MessageSyncRailStC message, MessageContext ctx) {
			return null;
		}
	}
}
