//package jp.mochisystems.erc._mc.handler;
//
//import jp.mochisystems.erc._mc.item.ItemCoaster;
//import jp.mochisystems.erc._mc._core.ERC_Reflection;
//import jp.mochisystems.erc._mc.item.ERC_ItemSwitchingRailModel;
//import net.minecraft.util.EnumHand;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import org.lwjgl.input.Keyboard;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.client.event.MouseEvent;
//
//public class ERC_InputEventHandler {
//
//	Minecraft mc;
//
//	int wheelsum;
//
//	public ERC_InputEventHandler(Minecraft minecraft)
//	{
//	    mc = minecraft;
//	    wheelsum = 0;
//	}
//
//	@SubscribeEvent
//	public void interceptMouseInput(MouseEvent event)
//	{
//	    if(Keyboard.isKeyDown(Keyboard.KEY_LMENU))
//	    {
//	    	wheelsum = event.getDwheel();
//	    	ERC_Reflection.setMouseDHweel(0);
//
//		    if(wheelsum != 0)
//		    {
//				if(mc.player == null)return;
//				ItemStack heldItemstack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
//				if(heldItemstack.isEmpty())return;
//				Item heldItem = heldItemstack.getItem();
//				if(heldItem instanceof ItemCoaster)
//				{
//					((ItemCoaster)heldItem).ScrollMouseWheel(event.getDwheel());
//				}
//				else if(heldItem instanceof ERC_ItemSwitchingRailModel)
//				{
//					((ERC_ItemSwitchingRailModel)heldItem).ScrollMouseHweel(event.getDwheel());
//				}
//		    }
//	    }
//	}
//}
