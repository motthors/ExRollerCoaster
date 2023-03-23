package jp.mochisystems.erc._mc.network;

import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc.gui.GUICoasterModelConstructor;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ERC_PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ERC.MODID);
  
  	public static void init()
  	{
  		int i=0;
		INSTANCE.registerMessage(ERC_MessageRailGUICtS.Handler.class, ERC_MessageRailGUICtS.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageSyncRailStC.Handler.class, MessageSyncRailStC.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(ERC_MessageConnectRailCtS.Handler.class, ERC_MessageConnectRailCtS.class, i++, Side.SERVER);
		//INSTANCE.registerMessage(ERC_MessageCoasterCtS.class, ERC_MessageCoasterCtS.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(MessageSyncCoasterSettings.Handler.class, MessageSyncCoasterSettings.class, i++, Side.CLIENT);
//		INSTANCE.registerMessage(MessageSyncCoasterSettings.Handler.class, MessageSyncCoasterSettings.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageSyncCoasterPosStC.Handler.class, MessageSyncCoasterPosStC.class, i++, Side.CLIENT);
//		INSTANCE.registerMessage(ERC_MessageItemWrenchSync.Handler.class, ERC_MessageItemWrenchSync.class, i++, Side.SERVER);
		//INSTANCE.registerMessage(ERC_MessageCoasterMisc.class, ERC_MessageCoasterMisc.class, i++, Side.CLIENT);
		//INSTANCE.registerMessage(ERC_MessageCoasterMisc.class, ERC_MessageCoasterMisc.class, i++, Side.SERVER);
		INSTANCE.registerMessage(ERC_MessageRequestConnectCtS.Handler.class, ERC_MessageRequestConnectCtS.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(ERC_MessageSpawnRequestWithCoasterOpCtS.Handler.class, ERC_MessageSpawnRequestWithCoasterOpCtS.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(GUICoasterModelConstructor.Message.Handler.class, GUICoasterModelConstructor.Message.class, i++, Side.SERVER);
//		INSTANCE.registerMessage(MessageCheckAutoConnectionStCtS.Handler.class, MessageCheckAutoConnectionStCtS.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageCheckAutoConnectionStC.Handler.class, MessageCheckAutoConnectionStC.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(MessageSyncRailOptionStC.Handler.class, MessageSyncRailOptionStC.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(MessageOpenGuiCtS.Handler.class, MessageOpenGuiCtS.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageSendModelDataStC.Handler.class, MessageSendModelDataStC.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(MessageAccelCoaster.Handler.class, MessageAccelCoaster.class, i++, Side.SERVER);
  	}
}
