//package erc.model;
//
//import erc.renderer.rail.IRailRenderer;
//import mochisystems.math.Vec3d;
//import org.lwjgl.opengl.GL11;
//
//import cpw.mods.fml.client.FMLClientHandler;
//import mochisystems.math.Math;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.client.model.IModelCustom;
//
//public class ERC_ModelAddedRail extends IRailRenderer {
//
//	private IModelCustom modelRail;
//	private ResourceLocation TextureResource;
//	private int ModelNum;
//	private Vec3d[] pos;
//	private Vec3d[] rot;
//	private double[] Position;
//
//	@SuppressWarnings("unused")
//	private ERC_ModelAddedRail(){} //���[�h����t�@�C�������w��C���X�^���X�������ۂł���H
//
//	public ERC_ModelAddedRail(IModelCustom Obj, ResourceLocation Tex)
//	{
//		modelRail = Obj;
//		TextureResource = Tex;
//	}
//
//	private void renderModel()
//	{
//		modelRail.renderAll();
//	}
//
//	public void render(double x, double y, double z, double yaw, double pitch, double roll, double length)
//	{
// 		GL11.glPushMatrix();
//		GL11.glTranslated(x, y, z);
//// 		if(coaster.ERCPosMat != null)
////		{
//// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
////		}
//		GL11.glRotated(yaw, 0, -1, 0);
// 		GL11.glRotated(pitch,1, 0, 0);
// 		GL11.glRotated(roll, 0, 0, 1);
//
//		GL11.glScaled(1.0, 1.0, length);
//		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureResource);
//		this.renderModel();
//		GL11.glPopMatrix();
//	}
//
//	public void setModelNum(int PosNum_org)
//	{
//		ModelNum = PosNum_org-1;
//		pos = new Vec3d[ModelNum];
//		rot = new Vec3d[ModelNum];
//		Position = new double[ModelNum];
//		for(int i=0;i<ModelNum;++i)pos[i] = new Vec3d(0, 0, 0);
//		for(int i=0;i<ModelNum;++i)rot[i] = new Vec3d(0, 0, 0);
//	}
//
//	@Override
//	public void construct(int idx, Vec3d Pos, Vec3d Dir, Vec3d Cross, double exParam)
//	{
//		if(idx>=ModelNum)return;
//		// �ʒu
//		pos[idx] = Pos;
//		// �p�CorePosX
//		Vec3d crossHorz = new Vec3d(0, 1, 0).cross(Dir);
//		Vec3d dir_horz = new Vec3d(Dir.x, 0, Dir.z);
//		rot[idx].x = -java.lang.Math.toDegrees( java.lang.Math.atan2(Dir.x, Dir.z) );
//		rot[idx].y = java.lang.Math.toDegrees( Math.AngleBetweenTwoVec(Dir, dir_horz) * (Dir.y>0?-1f:1f) );
//		rot[idx].z = java.lang.Math.toDegrees( Math.AngleBetweenTwoVec(Cross, crossHorz) * (Cross.y>0?1f:-1f) );
//		// ����
//		Position[idx] = exParam;
//	}
//
//	public void render(Tessellator tess, float mu, float xu, float mv, float xv)
//	{
//		for(int i=0;i<ModelNum;++i)
//			render(pos[i].x, pos[i].y, pos[i].z,
//				rot[i].x, rot[i].y, rot[i].z, Position[i]);
//	}
//}