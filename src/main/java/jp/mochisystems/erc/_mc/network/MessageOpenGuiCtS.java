package jp.mochisystems.erc._mc.network;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenGuiCtS implements IMessage {

	private int x, y, z;

	@SuppressWarnings("unused")
	public MessageOpenGuiCtS(){}

	public MessageOpenGuiCtS(int x, int y, int z)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
  	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
		this.z = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageOpenGuiCtS, IMessage> {
		@Override
		public IMessage onMessage(MessageOpenGuiCtS m, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().player;
			TileEntity tile = ctx.getServerHandler().player.world.getTileEntity(new BlockPos(m.x, m.y, m.z));
			if (!(tile instanceof TileEntityRail)) return null;

			player.openGui(ERC.INSTANCE, ERC.GUIID_RailBase, player.world, m.x, m.y, m.z);

			return null;
		}
	}
}
