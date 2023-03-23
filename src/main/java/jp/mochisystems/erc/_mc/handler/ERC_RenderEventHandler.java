//package jp.mochisystems.erc._mc.handler;
//
//import jp.mochisystems.core.math.Quaternion;
//import jp.mochisystems.erc._mc._core.ERC_Logger;
//import jp.mochisystems.erc._mc.entity.EntityCoaster;
//import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
//import net.minecraftforge.fml.common.eventhandler.EventPriority;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import org.lwjgl.opengl.GL11;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.util.Timer;
//import net.minecraftforge.client.event.RenderLivingEvent;
//import net.minecraftforge.event.entity.living.LivingEvent;
//
//@SideOnly(Side.CLIENT)
//public class ERC_RenderEventHandler {
//
//
//	EntityCoaster getCoaster(EntityLivingBase target)
//	{
//		if (!target.isRiding()) {
//			return null;
//		}
//		if (!(target.getRidingEntity() instanceof EntityCoaster)) {
//			return null;
//		}
//		return (EntityCoaster) target.getRidingEntity();
//	}
//
//	@SideOnly(Side.CLIENT)
//  	@SubscribeEvent(priority= EventPriority.LOWEST)
//	public void renderPre(RenderLivingEvent.Pre event)
//	{
////		MeshCache.instance.Buffer();
////		MeshCache.instance.Render();
//		if(true) return;
//		if (event.isCanceled()) {
//			return;
//		}
//		EntityLivingBase entityRider = event.getEntity();
//		EntityCoaster entityCoaster = getCoaster(entityRider);
//		if (entityCoaster == null) {
//			return;
//		}
//		GL11.glPushMatrix();
//
//		Timer timer = (Timer) ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), new String[] { "field_71428_T", "timer" });
//	    float partialTicks = timer.renderPartialTicks;
//
////	    float yaw = entityCoaster.parent.ERCPosMat.getFixedYaw(partialTicks);
////	    float pitch = entityCoaster.parent.ERCPosMat.getFixedPitch(partialTicks);
////	    float roll = entityCoaster.parent.ERCPosMat.getFixedRoll(partialTicks) + (float)Math.toDegrees(entityCoaster.getRotZ());
//		Quaternion rotation = entityCoaster.GetCoaster().attitude;
//
////        event.entityCoaster.renderYawOffset = yaw;
////        event.entityCoaster.rotationYawHead = yaw;
//	    Entity e = event.getEntity();
//	    double x = entityRider.prevPosX+(entityRider.posX-entityRider.prevPosX)*partialTicks - (e.prevPosX+(e.posX-e.prevPosX)*partialTicks);
//	    double y = entityRider.prevPosY+(entityRider.posY-entityRider.prevPosY)*partialTicks - (e.prevPosY+(e.posY-e.prevPosY)*partialTicks);
//	    double z = entityRider.prevPosZ+(entityRider.posZ-entityRider.prevPosZ)*partialTicks - (e.prevPosZ+(e.posZ-e.prevPosZ)*partialTicks);
////	    double CorePosX = event.entityCoaster.prevPosX+(event.entityCoaster.posX-event.entityCoaster.prevPosX)*partialTicks;
////	    double y = event.entityCoaster.prevPosY+(event.entityCoaster.posY-event.entityCoaster.prevPosY)*partialTicks;
////	    double z = event.entityCoaster.prevPosZ+(event.entityCoaster.posZ-event.entityCoaster.prevPosZ)*partialTicks;
//	    GL11.glTranslated(-x,-y,-z);
////	    GL11.glRotatef(yaw, 0.0F, -1.0F, 0.0F);
////	    GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
////	    GL11.glRotatef(roll, 0.0F, 0.0F, 1.0F);
////	    GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
//		GL11.glMultMatrix(rotation.GetBuffer());
//	    GL11.glTranslated(x,y,z);
//	}
//
//	@SideOnly(Side.CLIENT)
//	@SubscribeEvent(priority=EventPriority.HIGHEST)
//	public void renderPost(RenderLivingEvent.Post event)
//	{
//		if(true) return;
//		EntityLivingBase entityRider = event.getEntity();
//		EntityCoaster entityCoaster = getCoaster(entityRider);
//		if (entityCoaster == null) {
//			return;
//		}
//		GL11.glPopMatrix();
//	}
//
//
////	@SideOnly(Side.CLIENT)
////	@SubscribeEvent
////	public void ridingErc(LivingEvent.LivingUpdateEvent event)
////	{
////		Minecraft mc = Minecraft.getMinecraft();
////	    if (!(event.entity instanceof EntityClientPlayerMP)) return;
////
////	    EntityClientPlayerMP player = (EntityClientPlayerMP)event.entity;
////
////	    String key = "RideERC";
////	    if (player.getEntityData().hasKey(key))
////	    {
////	    	if ((player.ridingEntity == null) || (!(player.ridingEntity instanceof EntityCoasterSeat)))
////	    	{
////	    		player.getEntityData().removeTag(key);
////	    	}
////	    }
////	    else if ((player.ridingEntity != null) && ((player.ridingEntity instanceof EntityCoasterSeat)))
////	    {
////	    	mc.getSoundHandler().playSound(new ERCMovingSoundRiding(player, (EntityCoasterSeat)player.ridingEntity));
////	    	mc.getSoundHandler().playSound(new ERCMovingSound(player, (EntityCoasterSeat)player.ridingEntity));
////	    	player.getEntityData().setBoolean(key, true);
////	    	ERC_Logger.debugInfo("sound update");
////	    }
////	}
//}
