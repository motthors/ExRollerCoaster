package jp.mochisystems.erc._mc.renderer;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.erc._mc.tileentity.TileEntityCoasterModelConstructor;
import jp.mochisystems.erc.coaster.CoasterSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RendererCoasterModelConstructor extends TileEntitySpecialRenderer<TileEntityCoasterModelConstructor> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(_Core.MODID,"textures/limitline.png");

    EntityOtherPlayerMP player;
    RenderPlayer playerRenderer;
    final Quaternion.MatBuffer renderRotBuf = new Quaternion.MatBuffer();

    @Override
    public void render(TileEntityCoasterModelConstructor tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        this.bindTexture(TEXTURE);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1f);

        tile.GetLimitFrame().render();

        _Core.BindBlocksTextureMap();

        GL11.glPushMatrix();
        GL11.glTranslated(-3.5, 0.5, 1);
        tile.Render();
        GL11.glPopMatrix();

        if(player == null){
            player = new EntityOtherPlayerMP(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player.getGameProfile());
            playerRenderer = new RenderPlayer(Minecraft.getMinecraft().getRenderManager());
            Entity dummy = new EntityBoat(Minecraft.getMinecraft().world);
            player.startRiding(dummy);
        }
        for (int i = 0; i < tile.coasterSettings.Seats.length; ++i) {
            GlStateManager.pushMatrix();
            GL11.glTranslated(-3.5, 0.5-0.65, 1);
            CoasterSettings.SeatData data = tile.coasterSettings.Seats[i];
            GL11.glTranslated(data.LocalPosition.x, data.LocalPosition.y, data.LocalPosition.z);
            GL11.glRotated(data.LocalRotationDegree.z, 0, 0, 1);
            GL11.glRotated(data.LocalRotationDegree.y, 0, 1, 0);
            GL11.glRotated(data.LocalRotationDegree.x, 1, 0, 0);
//            player.setPosition(0, 1, 0);
            playerRenderer.doRender(player, 0, 0, 0, 0, 1);
            GlStateManager.popMatrix();
        }

        GL11.glEnable(GL11.GL_CULL_FACE);

        if(tile.blockModel != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslated(-3, 0.5, 1.5);

            GL11.glTranslated(tile.modelOffset.x, tile.modelOffset.y, tile.modelOffset.z);
            GL11.glMultMatrix(renderRotBuf.Fix(tile.modelRotate));
            GL11.glScaled(tile.modelScale.x, tile.modelScale.y, tile.modelScale.z);

            GL11.glScaled(1f/tile.registeredModelScale.x, 1f/tile.registeredModelScale.y, 1f/tile.registeredModelScale.z);
            GL11.glMultMatrix(renderRotBuf.Fix(tile.registeredModelRotate));
            GL11.glTranslated(-tile.registeredModelOffset.x, -tile.registeredModelOffset.y, -tile.registeredModelOffset.z);


            tile.blockModel.RenderModel(0, partialTicks);
            tile.blockModel.RenderModel(1, partialTicks);
            GL11.glPopMatrix();
        }


        GL11.glPopMatrix();
    }
}
