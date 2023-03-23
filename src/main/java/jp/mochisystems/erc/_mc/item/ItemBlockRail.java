package jp.mochisystems.erc._mc.item;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc.manager.AutoRailConnectionManager;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockRail extends ItemBlock {
    public ItemBlockRail(Block block) {
        super(block);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if(player.isSneaking())
        {
            AutoRailConnectionManager.ResetData();
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, player.getHeldItem(hand));
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        String id = null;
        if(this.block == ERC.railNormal) id = "usage.rail";
        if(this.block == ERC.railAccel) id = "usage.rail.accel";
        if(this.block == ERC.railConst) id = "usage.rail.const";
        if(this.block == ERC.railBranch) id = "usage.rail.branch";
        if(this.block == ERC.railDetect) id = "usage.rail.detect";
        if(this.block == ERC.railInvisible) id = "usage.rail.invisible";
        if(this.block == ERC.railNonGravity) id = "usage.rail.gravity";
        String[] usages = _Core.I18n(id).split("\\\\ ");
        for(String s : usages){
            tooltip.add(TextFormatting.AQUA+s);
        }
    }
}
