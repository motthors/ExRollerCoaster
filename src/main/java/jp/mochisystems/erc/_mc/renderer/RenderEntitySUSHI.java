package jp.mochisystems.erc._mc.renderer;

import jp.mochisystems.core.math.Math;
import jp.mochisystems.erc._mc.entity.EntitySUSHI;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderEntitySUSHI extends Render<EntitySUSHI> {

	public RenderEntitySUSHI(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntitySUSHI entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y-0.2f, (float)z);
		double rot = Math.Lerp(partialTicks, entity.prevRotation, entity.rotation);
		GL11.glRotatef((float)rot, 0f, -1f, 0f);
// 		GL11.glRotatef(coaster.ERCPosMat.getFixedPitch(t),1f, 0f, 0f);
// 		GL11.glRotatef(coaster.ERCPosMat.getFixedRoll(t), 0f, 0f, 1f);

//		GL11.glScalef(1.2f, 1.2f, 1.2f);
//		FMLClientHandler.instance().getClient().renderEngine.bindTexture(entity.tex);
		entity.GetCurrentModel().Render();
		GL11.glPopMatrix();
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntitySUSHI entity) {
		return entity.tex;
	}

}
