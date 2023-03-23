package jp.mochisystems.erc._mc.item;

import jp.mochisystems.erc._mc.entity.EntitySUSHI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class itemSUSHI extends Item{

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	ItemStack stack = player.getHeldItem(hand);
    	if (!worldIn.isRemote)
    	{
    		Entity e = new EntitySUSHI(worldIn,pos.getX()+0.5,pos.getY()+0.8,pos.getZ()+0.5);
			worldIn.spawnEntity(e);
    	}
		stack.shrink(1);
    	return EnumActionResult.SUCCESS;
    }
}
	