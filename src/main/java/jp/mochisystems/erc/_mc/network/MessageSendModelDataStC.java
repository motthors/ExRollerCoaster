package jp.mochisystems.erc._mc.network;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.erc._mc.entity.EntityCoaster;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendModelDataStC implements IMessage {

	NBTTagCompound nbt;
	int entityId;

	public MessageSendModelDataStC(){}

	public MessageSendModelDataStC(int entityId, NBTTagCompound nbt)
	{
		this.nbt = nbt;
		this.entityId = entityId;
	}

	public MessageSendModelDataStC(int entityId, ItemStack stack)
	{
		this.nbt = new NBTTagCompound();
		this.entityId = entityId;
		stack.writeToNBT(nbt);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(entityId);
		ByteBufUtils.writeTag(buf, nbt);
    }

	@Override
	public void fromBytes(ByteBuf buf)
	{
		entityId = buf.readInt();
		nbt = ByteBufUtils.readTag(buf);
	}

	public static class Handler implements IMessageHandler<MessageSendModelDataStC, IMessage> {
		@Override
		public IMessage onMessage(MessageSendModelDataStC message, MessageContext ctx) {
			EntityPlayer player = _Core.proxy.GetPlayer(ctx);
			if(player == null) return null;
			Entity entity = player.world.getEntityByID(message.entityId);
			if(entity == null)
				return null;
			((EntityCoaster)entity).ChangeModel(new ItemStack(message.nbt));
//			((EntityCoaster)entity).OnMarkUpdateStack();
			return null;
		}
	}
}
