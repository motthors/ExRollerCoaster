package jp.mochisystems.erc._mc.network;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.erc.manager.AutoRailConnectionManager;
import jp.mochisystems.erc.rail.IRailController;
import jp.mochisystems.erc.rail.Rail;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ERC_MessageSaveBreakRailStC implements IMessage{

	// �󂵂����[���u���b�N�̐ڑ�����
	public int bx = -1, by = -1, bz = -1;
	public int nx = -1, ny = -1, nz = -1;
	
	public ERC_MessageSaveBreakRailStC(){}
	
	public ERC_MessageSaveBreakRailStC(int bx, int by, int bz, int nx, int ny, int nz)
	{
	    this.bx = bx;
	    this.by = by;
	    this.bz = bz;
	    this.nx = nx;
	    this.ny = ny;
	    this.nz = nz;
	}

	public ERC_MessageSaveBreakRailStC(Rail prev, Rail next)
	{
		IRailController controller;
		if(prev != null)
		{
			controller = prev.GetController();
			this.bx = (int)controller.CorePosX();
			this.by = (int)controller.CorePosY();
			this.bz = (int)controller.CorePosZ();
		}
		if(next != null)
		{
			controller = next.GetController();
			this.nx = (int)controller.CorePosX();
			this.ny = (int)controller.CorePosY();
			this.nz = (int)controller.CorePosZ();
		}
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

    public static class Handler implements IMessageHandler<ERC_MessageSaveBreakRailStC, IMessage> {
		@Override
		public IMessage onMessage(ERC_MessageSaveBreakRailStC message, MessageContext ctx) {
			AutoRailConnectionManager.SetPrevRailPosConnectedDestroyBlock(message.bx, message.by, message.bz);
			AutoRailConnectionManager.SetNextRailPosConnectedDestroyBlock(message.nx, message.ny, message.nz);
			return null;
		}
	}
}