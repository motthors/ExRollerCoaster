package jp.mochisystems.erc._mc.network;

import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import io.netty.buffer.ByteBuf;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ERC_MessageConnectRailCtS implements IMessage {

	public int bx, by, bz;
	public int nx, ny, nz;

	public ERC_MessageConnectRailCtS() {
	}

	public ERC_MessageConnectRailCtS(int bx, int by, int bz, int nx, int ny, int nz)
	{
	    this.bx = bx;
	    this.by = by;
	    this.bz = bz;
	    this.nx = nx;
	    this.ny = ny;
	    this.nz = nz;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.bx);
		buf.writeInt(this.by);
		buf.writeInt(this.bz);
		buf.writeInt(this.nx);
		buf.writeInt(this.ny);
		buf.writeInt(this.nz);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.bx = buf.readInt();
	    this.by = buf.readInt();
	    this.bz = buf.readInt();
	    this.nx = buf.readInt();
	    this.ny = buf.readInt();
	    this.nz = buf.readInt();
    }

    public static class Handler implements IMessageHandler<ERC_MessageConnectRailCtS, IMessage> {

		@Override
		public IMessage onMessage(ERC_MessageConnectRailCtS message, MessageContext ctx) {
			TileEntityRail BaseRail = (TileEntityRail) ctx.getServerHandler().player.world.getTileEntity(new BlockPos(message.bx, message.by, message.bz));
			TileEntityRail NextRail = (TileEntityRail) ctx.getServerHandler().player.world.getTileEntity(new BlockPos(message.nx, message.ny, message.nz));

//		ERC_Logger.info("CorePosX:"+ message.bx+"y:"+message.by+"z:"+message.bz);
//		ERC_Logger.info("CorePosX:"+ message.nx+"y:"+message.ny+"z:"+message.nz);
			if ((BaseRail != null && NextRail != null)) {
				Rail.Connect(BaseRail.getRail(), NextRail.getRail());
			}
			return null;
		}
	}
    
}