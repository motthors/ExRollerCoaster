package jp.mochisystems.erc.renderer.rail;

import jp.mochisystems.core.bufferedRenderer.CachedBufferBase;
import jp.mochisystems.core.bufferedRenderer.RendererVbo;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

public class DefaultRailRenderer extends CachedBufferBase implements IRailRenderer{

	protected Rail rail;
	public void SetRail(Rail rail){
		this.rail = rail;
		hash = rail.hashCode();
	}

	private int hash;

	public class Surface{
		Vec3d v1;
		Vec3d v2;
		Vec3d normal;
		Surface(Vec3d v1, Vec3d v2, Vec3d n){this.v1 = v1; this.v2 = v2; this.normal = n;}
	}

	public int GetHash()
	{
		return hash;
	}

	@Override
	public int GetDrawMode(){return GL11.GL_QUADS;}
	@Override
	protected VertexFormat GetVertexFormat(){return DefaultVertexFormats.POSITION_TEX_NORMAL;}
	@Override
	public void setupArrayPointers()
	{
		RendererVbo.SetupArrayPointersForPosTexNormal(GetVertexFormat());
	}

	@Override
	protected void Compile() {
		DrawRailModel();
	}


	private final float mu, xu, mv, xv;

	public DefaultRailRenderer(IBlockState textureSource)
    {
		TextureAtlasSprite icon = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(textureSource);
		mu = icon.getMinU();
        xu = icon.getMaxU();
        mv = icon.getMinV();
        xv = icon.getMaxV();
	}

    @Override
    public void SetDirty()
    {
		hash = rail.hashCode();
		super.SetDirty();
    }

	@Override
	protected void PreRender() {
		GlStateManager.disableCull();
//		GlStateManager.color(1, 1, 1);
	}

	protected void DrawRailModel()
	{
		RailCurveModifier modifier = new RailCurveModifier();
		modifier.SetRail(rail);
		modifier.UpdateRailData();

		double t1 = 0.4 + 0.1;
		double t2 = 0.4 - 0.1;

        Surface[] surface = new Surface[]{
			new Surface(new Vec3d(t1, 0, 0), new Vec3d(t2, 0, 0), new Vec3d(0, 1,0)),
			new Surface(new Vec3d(-t2, 0, 0), new Vec3d(-t1, 0, 0), new Vec3d(0, 1, 0)),
		};

		int pointNum = rail.Curve().GetPointNum();
		double lengthPer1RailPart = 1f / pointNum;

		for (Surface value : surface) {

			Vec3d pos1 = new Vec3d();
			Vec3d pos2 = new Vec3d();
			Vec3d normal = new Vec3d();
			// base 0
			double t = 0;
			pos1.CopyFrom(value.v1);
			pos2.CopyFrom(value.v2);
			normal.CopyFrom(value.normal);


			for (int i = 1; i <= pointNum; ++i) {
				// base n
				modifier.TransformVertex(t, pos1, normal);
				RegisterVertex(modifier.renderPos, modifier.renderNormal, mu, mv);
				modifier.TransformVertex(t, pos2, normal);
				RegisterVertex(modifier.renderPos, modifier.renderNormal, xu, mv);

				// next n+1
//				t = rail.Curve().GetPointList()[i];
				t = (double) i / pointNum;
				pos1.CopyFrom(value.v1);
				pos2.CopyFrom(value.v2);
				pos1.z += i * lengthPer1RailPart;
				pos2.z += i * lengthPer1RailPart;
				modifier.TransformVertex(t, pos2, normal);
				RegisterVertex(modifier.renderPos, modifier.renderNormal, xu, xv);
				modifier.TransformVertex(t, pos1, normal);
				RegisterVertex(modifier.renderPos, modifier.renderNormal, mu, xv);
			}
		}
//		tessellator.draw();
		modifier = null;
	}
}