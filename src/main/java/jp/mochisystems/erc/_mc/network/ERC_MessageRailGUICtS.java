package jp.mochisystems.erc._mc.network;

import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.erc._mc.gui.GUIRail;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import io.netty.buffer.ByteBuf;
import jp.mochisystems.erc.rail.IRailController;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ERC_MessageRailGUICtS implements IMessage {

	public int x, y, z;
	public int FLAG;
	public int MiscInt;
	public int MiscInt2;
	public int MiscInt3;

	public ERC_MessageRailGUICtS(){}

	public ERC_MessageRailGUICtS(IRailController controller, int flag, int imisc)
	{
		this((int)controller.CorePosX(), (int)controller.CorePosY(), (int)controller.CorePosZ(), flag, imisc);
	}

	public ERC_MessageRailGUICtS(int x, int y, int z, int flag, int imisc)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.FLAG = flag;
		this.MiscInt = imisc;
  	}
	public ERC_MessageRailGUICtS(IRailController controller, int flag, Vec3d v)
	{
		this.x = (int) controller.CorePosX();
		this.y = (int) controller.CorePosY();
		this.z = (int) controller.CorePosZ();
		this.FLAG = flag;
		this.MiscInt = (int)(v.x * 100);
		this.MiscInt2 = (int)(v.y * 100);
		this.MiscInt3 = (int)(v.z * 100);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.FLAG);
		buf.writeInt(this.MiscInt);
		buf.writeInt(this.MiscInt2);
		buf.writeInt(this.MiscInt3);
	}

	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    this.FLAG = buf.readInt();
		this.MiscInt = buf.readInt();
		this.MiscInt2 = buf.readInt();
		this.MiscInt3 = buf.readInt();
    }


    public static class Handler implements IMessageHandler<ERC_MessageRailGUICtS, IMessage> {
		@Override
		public IMessage onMessage(ERC_MessageRailGUICtS message, MessageContext ctx) {
			TileEntity tile = ctx.getServerHandler().player.world.getTileEntity(new BlockPos(message.x, message.y, message.z));
			if ((tile instanceof TileEntityRail)) {
				Rail rail = ((TileEntityRail) tile).getRail();
				GUIRail.editFlag[] values = GUIRail.editFlag.values();
				GUIRail.editFlag align = values[message.FLAG];
				float angle = 0;
				switch(align) {
					case ROTRED:
					case ROTGREEN:
					case ROTBLUE:
					switch(message.MiscInt)
					{
						case 0 : angle = -0.5f; break;
						case 1 : angle = -0.05f; break;
						case 2 : angle =  0.05f; break;
						case 3 : angle =  0.5f; break;
					}
				}
				switch (align) {
					case CONTROLPOINT:
//						rail.AddPointNum(message.MiscInt);
						break;

					case SMOOTH:
//						rail.Smoothing();
						break;

					case POW:
//						rail.AddPower(message.MiscInt);
						break;

					case ROTRED:
//						rail.RotateDirectionAsYaw(angle);
						break;
					case ROTGREEN:
//						rail.RotateDirectionAsPitch(angle);
						break;
					case ROTBLUE:
						rail.UpdateTwist(angle+rail.Curve().Twist());
//						Rail next = rail.GetNextRail();
//						rail.Curve().RotateUp(angle, next!=null ? next.Curve() : null);
						break;
					case SET_TWIST:
						rail.UpdateTwist(message.MiscInt*0.01f);
						break;

					case RESET:
						rail.Curve().Reset(new Vec3d(tile.getPos()));
						break;

					case SPECIAL:
						rail.SetSpecialData(message.MiscInt);
						break;

//					case RailModelIndex:
////						((TileEntityRail) tile).ChangeModel(message.MiscInt);
//						return null;

					case OFFSET_X:
						rail.OffsetBase(message.MiscInt*0.2, 0, 0);
						break;
					case OFFSET_Y:
						rail.OffsetBase(0, message.MiscInt*0.2, 0);
						break;
					case OFFSET_Z:
						rail.OffsetBase(0, 0, message.MiscInt*0.2);
						break;
					case SETOFFSET:
						rail.Curve().Base.CopyFrom(new Vec3d(message.MiscInt*0.01, message.MiscInt2*0.01, message.MiscInt3*0.01));
						break;
				}

				rail.Curve().Construct();
				rail.SyncData();

				Rail prev = rail.GetPrevRail();
				if (prev != null) {
					rail.Curve().ApplyToPrev(prev.Curve());
					prev.SyncData();
					if(prev.GetPrevRail() != null) {
						prev.Curve().ApplyToPrev(prev.GetPrevRail().Curve());
						prev.GetPrevRail().SyncData();
					}
				}
				Rail next = rail.GetNextRail();
				if(next != null) {
					rail.Curve().ApplyToNext(next.Curve());
					next.SyncData();
					if(next.GetNextRail() != null) {
						next.Curve().ApplyToNext(next.GetNextRail().Curve());
						next.GetNextRail().SyncData();
					}
				}
			}
			return null;
		}
	}
    
}
