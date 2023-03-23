package jp.mochisystems.erc._mc.gui;

import jp.mochisystems.erc._mc.entity.EntityCoaster;
import jp.mochisystems.erc.coaster.Coaster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class GuiInGameEngineControl {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            FontRenderer fontrenderer = mc.fontRenderer;
            ScaledResolution res = event.getResolution();
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            int ch = height >> 1;

            if (!(player.getRidingEntity() instanceof EntityCoaster)) return;
            Coaster coaster = ((EntityCoaster) player.getRidingEntity()).GetCoaster();
            if (coaster.GetSettings().MaxEngineLevel <= 0) return;

            int level = 80 * coaster.engineLevel / coaster.GetSettings().MaxEngineLevel;
            int rev = 80 * coaster.engineLevel / 3;
            GuiUtils.drawGradientRect(0, width-32, ch-42, width-3, ch+42, 0xdd000000, 0xdd000000);
            GuiUtils.drawGradientRect(0, width-15, ch+40-level, width-5, ch+40, 0xffff4400, 0xffcccc00);
            GuiUtils.drawGradientRect(0, width-15, ch+40+rev, width-5, ch+40, 0xff0044ff, 0xff4488ff);

            fontrenderer.drawString("[W]", width-30, ch - 40, 0xFFFFFF);
            fontrenderer.drawString("[S]", width-30, ch + 33, 0xFFFFFF);

            GL11.glRotated(90, 0, 0, 1);
            fontrenderer.drawString("ENGINE", ch-15, -width+22, 0xFFFFFF);
            GL11.glRotated(-90, 0, 0, 1);

        }
    }
}
