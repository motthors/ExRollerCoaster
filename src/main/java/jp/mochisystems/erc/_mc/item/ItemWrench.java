package jp.mochisystems.erc._mc.item;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.erc._mc.block.rail.BlockRail;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.manager.AutoRailConnectionManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWrench extends Item {

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		String[] usages = _Core.I18n("usage.wrench").split("\\\\ ");
		for(String s : usages){
			tooltip.add(TextFormatting.AQUA+s);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(player.isSneaking())
		{
			AutoRailConnectionManager.ResetData();
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, player.getHeldItem(hand));
		}
		return super.onItemRightClick(world, player, hand);
	}
	
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
    	if (BlockRail.isBlockRail(world.getBlockState(pos)))
    	{
    		if(world.isRemote)
    		{
				BlockRail.selected = ((TileEntityRail)world.getTileEntity(pos)).getRail();
    			int x = pos.getX();
    			int y = pos.getY();
    			int z = pos.getZ();
				AutoRailConnectionManager.MemoryOrConnect(x, y, z);
//			    ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(0,x,y,z));
        		return EnumActionResult.SUCCESS;
    		}
    		
    		return EnumActionResult.SUCCESS;
    	}
        return EnumActionResult.PASS;
	}


	float motion = 0;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void DrawPlacePoint(DrawBlockHighlightEvent event)
	{
		EntityPlayer player = event.getPlayer();
		ItemStack stack = player.getHeldItemMainhand();

		if(!stack.isEmpty() && stack.getItem() instanceof ItemWrench || stack.getItem() instanceof ItemBlockRail) {

			motion += 0.04f;
			if(motion > 1f) motion -= 2f;
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			GlStateManager.disableAlpha();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);

			float t = event.getPartialTicks();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();

			GlStateManager.pushMatrix();
			double yaw = Math.toRadians(player.rotationYaw);
			double pit = Math.toRadians(player.rotationPitch);
			double ix = Math.sin(-yaw) * Math.cos(pit);
			double iy = player.eyeHeight - Math.sin(pit);
			double iz = Math.cos(-yaw) * Math.cos(pit);

			if(AutoRailConnectionManager.isSavedPrevRail())
			{
				BlockPos pos = AutoRailConnectionManager.GetPrevBlockPos();

				double x = 0.5 + pos.getX() - (player.prevPosX + (player.posX-player.prevPosX) * t);
				double y = 0.5 + pos.getY() - (player.prevPosY + (player.posY-player.prevPosY) * t);
				double z = 0.5 + pos.getZ() - (player.prevPosZ + (player.posZ-player.prevPosZ) * t);
				GL11.glLineWidth(4f);
				bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos(x, y, z).color(255, 0, 0, 255).endVertex();
				bufferbuilder.pos(ix, iy, iz).color(255, 0, 0, 255).endVertex();
				tessellator.draw();

				GL11.glLineWidth(1.5f);
				bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
				for(int i = 0; i < 8; ++i)
				{
					double m = jp.mochisystems.core.math.Math.Clamp((motion+i)/6, 0, 1);
					double tx = x + (ix-x)*m;
					double ty = y + (iy-y)*m;
					double tz = z + (iz-z)*m;
					bufferbuilder.pos(tx, ty, tz).color(255, 255, 255, 255).endVertex();
				}
				tessellator.draw();
			}

			if(AutoRailConnectionManager.isSavedNextRail())
			{
				BlockPos pos = AutoRailConnectionManager.GetNextBlockPos();

				double x = 0.5 + pos.getX() - (player.prevPosX + (player.posX-player.prevPosX) * t);
				double y = 0.5 + pos.getY() - (player.prevPosY + (player.posY-player.prevPosY) * t);
				double z = 0.5 + pos.getZ() - (player.prevPosZ + (player.posZ-player.prevPosZ) * t);

				GL11.glLineWidth(4f);
				bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos(ix, iy, iz).color(0, 170, 50, 255).endVertex();
				bufferbuilder.pos(x, y, z).color(0, 170, 50, 255).endVertex();
				tessellator.draw();

				GL11.glLineWidth(1.4f);
				bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
				for(int i = 0; i < 8; ++i)
				{
					double m = jp.mochisystems.core.math.Math.Clamp((motion+i)/6, 0, 1);
					double tx = ix + (x-ix)*m;
					double ty = iy + (y-iy)*m;
					double tz = iz + (z-iz)*m;
					bufferbuilder.pos(tx, ty, tz).color(255, 255, 255, 255).endVertex();
				}
				tessellator.draw();
			}


			GlStateManager.enableAlpha();
			GlStateManager.popMatrix();
			GL11.glLineWidth(1.0F);
			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
		}
	}

	private void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_)
	{
		int i = hand == EnumHandSide.RIGHT ? 1 : -1;
		GlStateManager.translate((float)i * 0.56F, -0.52F + p_187459_2_ * -0.6F, -0.72F);
	}
	private void transformFirstPerson(EnumHandSide hand, float p_187453_2_)
	{
		int i = hand == EnumHandSide.RIGHT ? 1 : -1;
		float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * (float)Math.PI);
		GlStateManager.rotate((float)i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
		float f1 = MathHelper.sin(MathHelper.sqrt(p_187453_2_) * (float)Math.PI);
		GlStateManager.rotate((float)i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
	}
}
