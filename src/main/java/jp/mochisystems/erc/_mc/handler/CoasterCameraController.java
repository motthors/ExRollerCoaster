//package jp.mochisystems.erc._mc.handler;
//
//import jp.mochisystems.core._mc.eventhandler.TickEventHandler;
//import jp.mochisystems.core.manager.RollingSeatManager;
//import jp.mochisystems.erc._mc.entity.EntityCoaster;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import org.lwjgl.opengl.Display;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.entity.player.EntityPlayer;
//
//@SideOnly(Side.CLIENT)
//public class CoasterCameraController {
//
//	private final Minecraft mc;
//
//	public CoasterCameraController(Minecraft minecraft)
//	{
//	    mc = minecraft;
//	    TickEventHandler.AddRenderTickPreListener(this::onRenderTickPre);
//	}
//
// //べつのカメラ視点変更処理を考えてみてもよさそう
//	private void onRenderTickPre(float partialTicks)
//	{
//		if(true)return;
//		if (Minecraft.getMinecraft().isGamePaused()) return;
//
//	    EntityPlayer player = this.mc.player;
//	    if (player == null) return;
//
//		EntityCoaster riddenCoaster = null;
//	    if (player.getRidingEntity() instanceof EntityCoaster)
//	    {
//			riddenCoaster = ((EntityCoaster) player.getRidingEntity());
//	    }
//
//	    if (riddenCoaster == null)
//		{
//			RollingSeatManager.ResetAngles();
//			return;
//		}
//
//		if (mc.inGameHasFocus && Display.isActive())
//		{
////			mc.mouseHelper.mouseXYChange();
////			float f1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
////			float f2 = f1 * f1 * f1 * 8.0F;
////			float f3 = (float)mc.mouseHelper.deltaX * f2;
////			float f4 = (float)mc.mouseHelper.deltaY * f2;
////
////			if (this.mc.gameSettings.invertMouse)
////			{
////				f4 *= -1;
////			}
////
////			RollingSeatManager.ShakeHeadOnRollingSeat(f3, f4);
////			RollingSeatManager.SetAttitude(riddenCoaster.GetCoaster().attitude, riddenCoaster.GetCoaster().prevAttitude);
//
////			mc.mouseHelper.deltaX = 0;
////			mc.mouseHelper.deltaY = 0;
////			Coaster coaster = riddenCoaster.GetCoaster();
////			RollingSeatManager.SetAttitude(coaster.attitude, coaster.prevAttitude, riddenCoaster.GetSeatRotation());
//
//		}
//	}
//
////	private void onRenderTickPost(float partialTicks) { }
//
//
////	private void onPlayerTickPre(EntityPlayer player) {}
////	private void onPlayerTickPost(EntityPlayer player) {}
//// 	private void onTickPre() { }
////	private void onTickPost() { }
//
//
////	@SubscribeEvent
////	public void onPlayerTickEvent(TickEvent.PlayerTickEvent event)
////	{
////		if (event.phase == TickEvent.Phase.START) {
////			onPlayerTickPre(event.player);
////		}
////		if (event.phase == TickEvent.Phase.END) {
////			onPlayerTickPost(event.player);
////		}
////	}
////
////	@SubscribeEvent
////	public void onClientTickEvent(TickEvent.ClientTickEvent event)
////	{
////		if (event.phase == TickEvent.Phase.START) {
////			onTickPre();
////		}
////		if (event.phase == TickEvent.Phase.END) {
////			onTickPost();
////		}
////	}
//
////	@SubscribeEvent
////	public void onRenderTickEvent(TickEvent.RenderTickEvent event)
////	{
////		if (event.phase == TickEvent.Phase.START) {
////			onRenderTickPre(event.renderTickTime);
////		}
////		if (event.phase == TickEvent.Phase.END) {
////			onRenderTickPost(event.renderTickTime);
////		}
////	}
//
//}
