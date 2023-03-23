package jp.mochisystems.erc.renderer.rail;

import jp.mochisystems.core.bufferedRenderer.CachedBufferBase;
import jp.mochisystems.core.bufferedRenderer.RendererVbo;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;
import scala.Array;

import java.util.Arrays;
import java.util.List;

public class PackModelRailRenderer extends CachedBufferBase implements IRailRenderer{

    protected Rail rail;
    public void SetRail(Rail rail){
        this.rail = rail;
        hash = rail.hashCode();
    }
    private int hash;

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

    IBakedModel sourceModel;
    ResourceLocation textureSource;
    public PackModelRailRenderer(IBakedModel sourceModel, ResourceLocation textureSource) {
        this.textureSource = textureSource;
        this.sourceModel = sourceModel;
    }


    @Override
    protected void PreRender() {
        TileEntityRendererDispatcher.instance.renderEngine.bindTexture(textureSource);
    }

    protected void DrawRailModel()
    {
        RailCurveModifier modifier = new RailCurveModifier();
        modifier.SetRail(rail);
        modifier.UpdateRailData();

        int pointNum = rail.Curve().GetPointNum();
        double lengthPer1RailPart = 1f / pointNum;


        Vec3d[] pos = new Vec3d[4];
        Vec3d[] normal = new Vec3d[4];
        for(int i=0; i < 4; ++i){
            pos[i] = new Vec3d();
            normal[i] = new Vec3d();
        }

        List<BakedQuad> quads = sourceModel.getQuads(null, null, 0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        for(int count = 0; count < pointNum; count++)
        {
            for (BakedQuad quad : quads)
            {
                int[] vert = quad.getVertexData();
                int[] newvert = Arrays.copyOf(vert, vert.length);
                pos[0].SetFrom(Float.intBitsToFloat(vert[0]),Float.intBitsToFloat(vert[1]),Float.intBitsToFloat(vert[2]));
                pos[1].SetFrom(Float.intBitsToFloat(vert[6]),Float.intBitsToFloat(vert[7]),Float.intBitsToFloat(vert[8]));
                pos[2].SetFrom(Float.intBitsToFloat(vert[12]),Float.intBitsToFloat(vert[13]),Float.intBitsToFloat(vert[14]));
                pos[3].SetFrom(Float.intBitsToFloat(vert[18]),Float.intBitsToFloat(vert[19]),Float.intBitsToFloat(vert[20]));
                for(int i=0; i < 4; ++i) {
                    int X = (vert[i*6+5]) & 0xff;
                    int Y = (vert[i*6+5] >> 8) & 0xff;
                    int Z = vert[i*6+5] >> 16;
                    normal[i].SetFrom(X / 127f, Y / 127f, Z / 127f);
                }
                double t = 0;
                for (int i = 0; i < pos.length; ++i) {
                    // base n
                    t = (pos[i].z + count) * lengthPer1RailPart;
                    modifier.TransformVertex(t, pos[i], normal[i]);
                    pos[i].CopyFrom(modifier.renderPos);
                    normal[i].CopyFrom(modifier.renderNormal);
                }
                newvert[0] = Float.floatToIntBits((float)pos[0].x);
                newvert[1] = Float.floatToIntBits((float)pos[0].y);
                newvert[2] = Float.floatToIntBits((float)pos[0].z);
                newvert[5] = ((int)(normal[0].z*127)&0xff)<<16
                            | ((int)(normal[0].y*127)&0xff)<<8
                            | ((int)(normal[0].x*127)&0xff);

                newvert[6] = Float.floatToIntBits((float)pos[1].x);
                newvert[7] = Float.floatToIntBits((float)pos[1].y);
                newvert[8] = Float.floatToIntBits((float)pos[1].z);
                newvert[11] = ((int)(normal[1].z*127)&0xff)<<16
                            | ((int)(normal[1].y*127)&0xff)<<8
                            | ((int)(normal[1].x*127)&0xff);

                newvert[12] = Float.floatToIntBits((float)pos[2].x);
                newvert[13] = Float.floatToIntBits((float)pos[2].y);
                newvert[14] = Float.floatToIntBits((float)pos[2].z);
                newvert[17] = ((int)(normal[2].z*127)&0xff)<<16
                            | ((int)(normal[2].y*127)&0xff)<<8
                            | ((int)(normal[2].x*127)&0xff);

                newvert[18] = Float.floatToIntBits((float)pos[3].x);
                newvert[19] = Float.floatToIntBits((float)pos[3].y);
                newvert[20] = Float.floatToIntBits((float)pos[3].z);
                newvert[23] = ((int)(normal[3].z*127)&0xff)<<16
                            | ((int)(normal[3].y*127)&0xff)<<8
                            | ((int)(normal[3].x*127)&0xff);
                buffer.addVertexData(newvert);
            }
        }

    }
}
