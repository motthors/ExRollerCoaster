package jp.mochisystems.erc.renderer;

import jp.mochisystems.core.bufferedRenderer.CachedBufferBase;
import jp.mochisystems.core.bufferedRenderer.RendererVbo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;


public class RailCoreRenderer extends CachedBufferBase {

    private final IBlockState textureSource;

    public RailCoreRenderer(IBlockState textureSource)
    {
        this.textureSource = textureSource;
    }

    @Override
    public int GetDrawMode(){return GL11.GL_TRIANGLES;}
    @Override
    protected VertexFormat GetVertexFormat(){return DefaultVertexFormats.POSITION_TEX_NORMAL;}
    @Override
    public void setupArrayPointers()
    {
        RendererVbo.SetupArrayPointersForPosTexNormal(GetVertexFormat());
    }

    @Override
    public int GetHash()
    {
        return "ERC.RailCore".hashCode()+textureSource.toString().hashCode();
    }


    protected void PreRender(){
//        GlStateManager.translate(-20, -20, -20);
    }

    protected void Compile() {
        double x = 0;
        double y = 0;
        double z = 0;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        TextureAtlasSprite icon = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(textureSource);
        float mu = icon.getMinU();
        float xu = icon.getMaxU();
        float mv = icon.getMinV();
        float xv = icon.getMaxV();
        double cu = (mu+xu)/2f;
        double cv = (mv+xv)/2f;
        double _w1 = 0.2;
        double _w2 = -_w1;
//        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_NORMAL);
        /*t*/buffer.pos(x+0.0, y+_w1, z+0.0).tex(cu, cv).normal(0, +1, 0).endVertex();
        /*1*/buffer.pos(x+0.0, y+0.0, z+_w2).tex(mu, mv).normal(0, 0, -1).endVertex();
        /*2*/buffer.pos(x+_w2, y+0.0, z+0.0).tex(xu, mv).normal(-1, 0, 0).endVertex();
        /*t*/buffer.pos(x+0.0, y+_w1, z+0.0).tex(cu, cv).normal(0, +1, 0).endVertex();
        /*2*/buffer.pos(x+_w2, y+0.0, z+0.0).tex(xu, mv).normal(-1, 0, 0).endVertex();
        /*3*/buffer.pos(x+0.0, y+0.0, z+_w1).tex(xu, xv).normal(0, 0, +1).endVertex();
        /*t*/buffer.pos(x+0.0, y+_w1, z+0.0).tex(cu, cv).normal(0, +1, 0).endVertex();
        /*3*/buffer.pos(x+0.0, y+0.0, z+_w1).tex(xu, xv).normal(0, 0, +1).endVertex();
        /*4*/buffer.pos(x+_w1, y+0.0, z+0.0).tex(mu, xv).normal(+1, 0, 0).endVertex();
        /*t*/buffer.pos(x+0.0, y+_w1, z+0.0).tex(cu, cv).normal(0, +1, 0).endVertex();
        /*4*/buffer.pos(x+_w1, y+0.0, z+0.0).tex(mu, xv).normal(+1, 0, 0).endVertex();
        /*1*/buffer.pos(x+0.0, y+0.0, z+_w2).tex(mu, mv).normal(0, 0, -1).endVertex();
//        tessellator.draw();
//        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_NORMAL);
        /*b*/buffer.pos(x+0.0, y+_w2, z+0.0).tex(cu, cv).normal(0, -1, 0).endVertex();
        /*2*/buffer.pos(x+_w2, y+0.0, z+0.0).tex(xu, mv).normal(-1, 0, 0).endVertex();
        /*1*/buffer.pos(x+0.0, y+0.0, z+_w2).tex(mu, mv).normal(0, 0, -1).endVertex();
        /*b*/buffer.pos(x+0.0, y+_w2, z+0.0).tex(cu, cv).normal(0, -1, 0).endVertex();
        /*3*/buffer.pos(x+0.0, y+0.0, z+_w1).tex(xu, xv).normal(0, 0, +1).endVertex();
        /*2*/buffer.pos(x+_w2, y+0.0, z+0.0).tex(xu, mv).normal(-1, 0, 0).endVertex();
        /*b*/buffer.pos(x+0.0, y+_w2, z+0.0).tex(cu, cv).normal(0, -1, 0).endVertex();
        /*4*/buffer.pos(x+_w1, y+0.0, z+0.0).tex(mu, xv).normal(+1, 0, 0).endVertex();
        /*3*/buffer.pos(x+0.0, y+0.0, z+_w1).tex(xu, xv).normal(0, 0, +1).endVertex();
        /*b*/buffer.pos(x+0.0, y+_w2, z+0.0).tex(cu, cv).normal(0, -1, 0).endVertex();
        /*1*/buffer.pos(x+0.0, y+0.0, z+_w2).tex(mu, mv).normal(0, 0, -1).endVertex();
        /*4*/buffer.pos(x+_w1, y+0.0, z+0.0).tex(mu, xv).normal(+1, 0, 0).endVertex();
//        tessellator.draw();


//        { //test from cloudrenderer
//            Tessellator tessellator = Tessellator.getInstance();
//            BufferBuilder buffer = tessellator.getBuffer();
//            float sectX0 = -2;
//            float sectX1 = 2;
//            float sectZ0 = -2;
//            float sectZ1 = 2;
//            float bCol = 0.7f;
//            float ALPHA = 0.5f;
//            float u0 = sectX0 * 0.1f;
//            float u1 = sectX1 * 0.1f;
//            float v0 = sectZ0 * 0.1f;
//            float v1 = sectZ1 * 0.1f;
//            buffer.pos(sectX0, 0, sectZ0).tex(u0, v0).color(bCol, bCol, bCol, ALPHA).endVertex();
//            buffer.pos(sectX1, 0, sectZ0).tex(u1, v0).color(bCol, bCol, bCol, ALPHA).endVertex();
//            buffer.pos(sectX1, 0, sectZ1).tex(u1, v1).color(bCol, bCol, bCol, ALPHA).endVertex();
//            buffer.pos(sectX0, 0, sectZ1).tex(u0, v1).color(bCol, bCol, bCol, ALPHA).endVertex();
//        }
    }
}
