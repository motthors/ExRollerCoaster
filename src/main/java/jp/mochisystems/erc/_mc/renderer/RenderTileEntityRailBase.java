package jp.mochisystems.erc._mc.renderer;

import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.erc._mc.gui.GUIRail;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderTileEntityRailBase extends TileEntitySpecialRenderer{


	@Override
	public void render(@Nonnull TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x+0.5, y+0.5, z+0.5);

		TileEntityRail tile = (TileEntityRail)te;
		tile.Render();

		if(GUIRail.IsEditing(tile.getRail())){
//    		DrawRotaArrow(tile);
    	}

		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}
	
	public void DrawRotaArrow(TileEntityRail tile)
	{
//      	Vec3d d = tile.getRail().GetBasePoint().Dir();
//		Vec3d u = tile.getRail().GetBasePoint().Up();
//      	Vec3d p = tile.getRail().GetBasePoint().Side();
//
//      	d = d.normalize();
//      	u = u.normalize();
//      	p = p.normalize();
//
//		GlStateManager.disableTexture2D();
//		GlStateManager.disableLighting();
//		Tessellator tessellator = Tessellator.getInstance();
//		BufferBuilder bufferbuilder = tessellator.getBuffer();
//		GL11.glLineWidth(4.0F);
//		bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
//		for(int i = 0; i < 360; i+=5)
//		{
//			double rad = Math.toRadians(i);
//			bufferbuilder.pos(
//					d.x*Math.sin(rad)*2 + p.x*Math.cos(rad)*2,
//					d.y*Math.sin(rad)*2 + p.y*Math.cos(rad)*2,
//					d.z*Math.sin(rad)*2 + p.z*Math.cos(rad)*2).color(255, 0, 0, 255).endVertex();
//		}
//		tessellator.draw();
//		bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
//		for(int i = 0; i < 360; i+=5)
//		{
//			double rad = Math.toRadians(i);
//			bufferbuilder.pos(
//					u.x*Math.sin(rad)*2 + p.x*Math.cos(rad)*2,
//					u.y*Math.sin(rad)*2 + p.y*Math.cos(rad)*2,
//					u.z*Math.sin(rad)*2 + p.z*Math.cos(rad)*2).color(0, 0, 255, 255).endVertex();
//		}
//		tessellator.draw();
//		bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
//		for(int i = 0; i < 360; i+=5)
//		{
//			double rad = Math.toRadians(i);
//			bufferbuilder.pos(
//					d.x*Math.sin(rad)*2 + u.x*Math.cos(rad)*2,
//					d.y*Math.sin(rad)*2 + u.y*Math.cos(rad)*2,
//					d.z*Math.sin(rad)*2 + u.z*Math.cos(rad)*2).color(0, 255, 0, 255).endVertex();
//		}
//		tessellator.draw();
//		GlStateManager.enableLighting();
//		GlStateManager.enableTexture2D();
	}

}
