package jp.mochisystems.erc._mc.network;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.erc.manager.AutoRailConnectionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCheckAutoConnectionStC implements IMessage {

    int x, y, z;

    public MessageCheckAutoConnectionStC(){}
    public MessageCheckAutoConnectionStC(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();

    }

    public static class Handler implements IMessageHandler<MessageCheckAutoConnectionStC, IMessage> {
        @Override
        public IMessage onMessage(MessageCheckAutoConnectionStC message, MessageContext ctx) {
//            BlockPos pos = new BlockPos(message.x, message.y, message.z);
//            World world = null;
//            world = ctx.getServerHandler().player.world;
//            TileEntityRail tile = (TileEntityRail) world.getTileEntity(pos);
            AutoRailConnectionManager.ConnectToMemorizedPosition(message.x, message.y, message.z);

            return null;
//            return new MessageCheckAutoConnectionStCtS(message.x, message.y, message.z).Response();
        }
    }
}
