package jp.mochisystems.erc._mc.item;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.erc._mc.block.rail.BlockRail;
import jp.mochisystems.erc._mc.entity.EntityCoaster;
import jp.mochisystems.erc.rail.BranchRail;
import jp.mochisystems.erc.rail.RailCurve;
import jp.mochisystems.erc.rail.RailCurveForBranch;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static jp.mochisystems.core._mc.block.BlockRemoteController.*;

public class ItemRailInfoChecker extends Item {

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        String[] usages = _Core.I18n("usage.railcheck").split("\\\\ ");
        for(String s : usages){
            tooltip.add(TextFormatting.AQUA+s);
        }
        tooltip.add("");
        tooltip.add(TextFormatting.GREEN+_Core.I18n("usage.railcheck.1"));
        tooltip.add(TextFormatting.RED+_Core.I18n("usage.railcheck.2"));
        tooltip.add(TextFormatting.AQUA+_Core.I18n("usage.railcheck.3"));
        tooltip.add(TextFormatting.YELLOW+_Core.I18n("usage.railcheck.4"));
        tooltip.add(TextFormatting.LIGHT_PURPLE+_Core.I18n("usage.railcheck.5"));
    }

    @SubscribeEvent
    public void DrawPlacePoint(DrawBlockHighlightEvent event) {
        float t = event.getPartialTicks();
        EntityPlayer player = event.getPlayer();
        ItemStack stack = player.getHeldItemMainhand();
        if (!stack.isEmpty() && stack.getItem() instanceof ItemRailInfoChecker) {
            DrawInfo(player, t);
            return;
        }
        stack = player.getHeldItemOffhand();
        if (!stack.isEmpty() && stack.getItem() instanceof ItemRailInfoChecker) {
            DrawInfo(player, t);
            return;
        }
    }

    public static List<Entity> coasterList = new ArrayList<>();
    public static List<BlockPos> posList = new ArrayList<>();
    private static List<Vec3d> curvePoints = new ArrayList<>();
    public static int frameCount = 0;
    public void onUpdate(ItemStack stack, World worldIn, Entity player, int itemSlot, boolean isSelected)
    {
        if(!worldIn.isRemote) return;
        if(frameCount++ < 20) return;
        frameCount = 0;

        posList.clear();
        for (BlockPos pos : BlockPos.getAllInBox(
                (int)player.posX-20, (int)player.posY-20, (int)player.posZ-20,
                (int)player.posX+20, (int)player.posY+20, (int)player.posZ+20))
        {
            Block block = worldIn.getBlockState(pos).getBlock();
            if(block instanceof BlockRail)
            {
                posList.add(pos);
            }
        }

        coasterList = worldIn.getEntitiesInAABBexcluding(player, new AxisAlignedBB(player.posX-30, player.posY-30, player.posZ-30, player.posX+30, player.posY+30, player.posZ+30),
                e -> e instanceof EntityCoaster);

        if(BlockRail.selected != null)
        {
            curvePoints.clear();
            RailCurve curve = BlockRail.selected.Curve();
            for(int i = 0; i <= curve.GetPointNum(); ++i)
            {
                Vec3d v = new Vec3d();
                curve.PositionAt(v, i / (float)curve.GetPointNum());
                curvePoints.add(v);
            }
        }
    }

    private void DrawInfo(EntityPlayer player, float t)
    {
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        GlStateManager.pushMatrix();

        double x = - (player.prevPosX + (player.posX-player.prevPosX) * t);
        double y = - (player.prevPosY + (player.posY-player.prevPosY) * t);
        double z = - (player.prevPosZ + (player.posZ-player.prevPosZ) * t);
        GlStateManager.translate(x, y, z);
        GL11.glLineWidth(4f);

        if(BlockRail.selected != null){
            RailCurve c = BlockRail.selected.Curve();
            Point(c.Prev);
            Point(c.Base);
            Point(c.End);
            Point(c.Next);
            if(BlockRail.selected instanceof BranchRail) Point(((RailCurveForBranch)c).Mid);
            PosDirLine(c.Base, c.p0Up);
            PosDirLine(c.End, c.p1Up);
            Curve();
        }

        GL11.glLineWidth(1f);
        for(BlockPos pos : posList){
            PointRailBlock(pos);
        }

        for(Entity e : coasterList){
            CheckCoasterInfo((EntityCoaster)e);
        }

        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
    }

    private void Point(Vec3d pos)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        double l = 0.5;
        bufferbuilder.pos(pos.x, pos.y+l, pos.z).color(100, 255, 0, 255).endVertex();
        bufferbuilder.pos(pos.x, pos.y-l, pos.z).color(100, 255, 0, 255).endVertex();
        bufferbuilder.pos(pos.x+l, pos.y, pos.z).color(100, 255, 0, 255).endVertex();
        bufferbuilder.pos(pos.x-l, pos.y, pos.z).color(100, 255, 0, 255).endVertex();
        bufferbuilder.pos(pos.x, pos.y, pos.z+l).color(100, 255, 0, 255).endVertex();
        bufferbuilder.pos(pos.x, pos.y, pos.z-l).color(100, 255, 0, 255).endVertex();
        tessellator.draw();
    }
    private void PosDirLine(Vec3d pos, Vec3d dir)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(pos.x, pos.y, pos.z).color(255, 100, 0, 255).endVertex();
        bufferbuilder.pos(pos.x+dir.x*2, pos.y+dir.y*2, pos.z+dir.z*2).color(255, 100, 0, 255).endVertex();
        tessellator.draw();
    }
    private void PointRailBlock(BlockPos pos)
    {
        Vec3d p = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
        double t = 0.2;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(p.x, p.y, p.z).color(40, 200, 255, 255).endVertex();
        bufferbuilder.pos(p.x+t, p.y+t, p.z+t).color(40, 200, 255, 255).endVertex();

        bufferbuilder.pos(p.x+1, p.y, p.z).color(40, 200, 255, 255).endVertex();
        bufferbuilder.pos(p.x+1-t, p.y+t, p.z+t).color(40, 200, 255, 255).endVertex();

        bufferbuilder.pos(p.x, p.y+1, p.z).color(40, 200, 255, 255).endVertex();
        bufferbuilder.pos(p.x+t, p.y+1-t, p.z+t).color(40, 200, 255, 255).endVertex();

        bufferbuilder.pos(p.x, p.y, p.z+1).color(40, 200, 255, 255).endVertex();
        bufferbuilder.pos(p.x+t, p.y+t, p.z+1-t).color(40, 200, 255, 255).endVertex();

        bufferbuilder.pos(p.x+1, p.y+1, p.z).color(40, 200, 255, 255).endVertex();
        bufferbuilder.pos(p.x+1-t, p.y+1-t, p.z+t).color(40, 200, 255, 255).endVertex();

        bufferbuilder.pos(p.x+1, p.y, p.z+1).color(40, 200, 255, 255).endVertex();
        bufferbuilder.pos(p.x+1-t, p.y+t, p.z+1-t).color(40, 200, 255, 255).endVertex();

        bufferbuilder.pos(p.x, p.y+1, p.z+1).color(40, 200, 255, 255).endVertex();
        bufferbuilder.pos(p.x+t, p.y+1-t, p.z+1-t).color(40, 200, 255, 255).endVertex();

        bufferbuilder.pos(p.x+1, p.y+1, p.z+1).color(40, 200, 255, 255).endVertex();
        bufferbuilder.pos(p.x+1-t, p.y+1-t, p.z+1-t).color(40, 200, 255, 255).endVertex();
        tessellator.draw();
    }
    private void Curve()
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        for(Vec3d v : curvePoints){
            bufferbuilder.pos(v.x, v.y, v.z).color(0, 255, 100, 255).endVertex();
        }
        tessellator.draw();
    }

    private void CheckCoasterInfo(EntityCoaster entity)
    {
		RenderGlobal.drawSelectionBoundingBox(entity.getEntityBoundingBox(), 1f, 1f, 0, 0.25f);
        for(int i = 0; i < entity.GetCoaster().SeatNum(); ++i)
        {
            Vec3d p = entity.GetCoaster().GetSeat(i).pos;
            AxisAlignedBB aabb = new AxisAlignedBB(p.x-0.5,p.y-0.5,p.z-0.5,p.x+0.5,p.y+0.5,p.z+0.5);
            RenderGlobal.drawSelectionBoundingBox(aabb, 1f, 0f, 1f, 0.7f);
        }
    }
}
