//package jp.mochisystems.erc._mc.network;
//
//import jp.mochisystems.erc._mc._core.ERC_Core;
//import jp.mochisystems.erc._mc.item.ItemWrench;
//import io.netty.buffer.ByteBuf;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.init.Blocks;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
//import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
//
//public class ERC_MessageItemWrenchSync implements IMessage {
//
//	public Item classItem;
//	public int mode=-1;
//	public int x;
//	public int y;
//	public int z;
//
//	public ERC_MessageItemWrenchSync(){}
//
//	public ERC_MessageItemWrenchSync(int mode,int x, int y, int z)
//	{
//		this.mode = mode;
//		this.x = x;
//		this.y = y;
//		this.z = z;
//	}
//
//	@Override
//	public void toBytes(ByteBuf buf)
//	{
//		buf.writeInt(mode);
//		buf.writeInt(x);
//		buf.writeInt(y);
//		buf.writeInt(z);
//	}
//
//	@Override
//	public void fromBytes(ByteBuf buf)
//	{
//		mode = buf.readInt();
//		x = buf.readInt();
//		y = buf.readInt();
//		z = buf.readInt();
//	}
//
//	public static class Handler implements IMessageHandler<ERC_MessageItemWrenchSync, IMessage> {
//		@Override
//		public IMessage onMessage (ERC_MessageItemWrenchSync message, MessageContext ctx)
//		{
////		((ItemWrench)ERC_Core.itemWrench).receivePacket(message.mode);
//
//			EntityPlayerMP player = ctx.getServerHandler().player;
//			switch (message.mode) {
//				case 0:
//					break;// connect �Ȃɂ��Ȃ�
//				case 1: //gui
//					player.openGui(ERC_Core.INSTANCE, ERC_Core.GUIID_RailBase, player.world, message.x, message.y, message.z);
//					break;
//				case 2:
//					((ItemWrench) ERC_Core.itemWrench).placeBlockAt(new ItemStack(ERC_Core.itemWrench), player, player.world, message.x, message.y, message.z, Blocks.DIRT);
//					break;
//			}
//			return null;
//		}
//	}
//}