package jp.mochisystems.erc._mc.renderer;

import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.erc._mc.entity.EntityCoaster;
import jp.mochisystems.erc.coaster.Coaster;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderEntityCoaster extends Render<EntityCoaster> {

	public RenderEntityCoaster(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityCoaster entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		Coaster c = entity.GetCoaster();
		if (entity.GetModel() == null)
			return;

		GL11.glPushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1f);
		GL11.glTranslated(
				x+c.position.x-entity.posX,
				y+c.position.y-entity.posY,
				z+c.position.z-entity.posZ);

		entity.GetModel().RenderModel(0, partialTicks);
		entity.GetModel().RenderModel(1, partialTicks);

		GL11.glPopMatrix();

//		renderDebugBox(entity, x, y, z);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityCoaster entity) {
		return entity.Texture;
	}

	private void renderDebugBox(EntityCoaster entity, double x, double y, double z)
	{
		Entity[] ea = entity.getParts();
		if(ea != null) for(int i=0; i<ea.length; i++)
		{
//			renderOffsetAABB(ea[i].getEntityBoundingBox(), x-ea[i].posX, y-ea[i].posY+i, z-ea[i].posZ);
			renderOffsetAABB(ea[i].getEntityBoundingBox(), x-entity.posX, y-entity.posY, z-entity.posZ);
		}
//		renderOffsetAABB(entity.getEntityBoundingBox(), x-entity.posX, y-entity.posY, z-entity.posZ);

		for(int i = 0; i < entity.GetCoaster().SeatNum(); ++i)
		{
			Vec3d p = entity.GetCoaster().GetSeat(i).pos;
			AxisAlignedBB aabb = new AxisAlignedBB(p.x-0.5,p.y-0.5,p.z-0.5,p.x+0.5,p.y+0.5,p.z+0.5);
			renderOffsetAABB(aabb, x-entity.posX, y-entity.posY, z-entity.posZ);

		}
	}


}
