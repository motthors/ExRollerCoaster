package jp.mochisystems.erc._mc.item;

import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ERC_ItemSmoothAll extends Item {

	@Override
	public EnumActionResult onItemUseFirst(
			EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ, EnumHand hand)
	{
		if(!world.isRemote)
		{
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof TileEntityRail)
			{
				Rail rail = ((TileEntityRail) tile).getRail();
				if(rail == null) return EnumActionResult.FAIL;
				smoothBack(0, rail, rail, world);
				smoothNext(0, rail, rail, world);
			}
		}
			
		
		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}

	private void smoothBack(int num, Rail root, Rail rail, World world)
	{
//		if(num >= 100)return;
//		if(rail == null)return;
//		if(num != 0 && root.equals(rail))return;
//
//		rail.Smoothing();
//		rail.ConstructCurve();
//		rail.SyncData();
//		Rail prev = rail.GetPrevRail();
//		if(prev != null)
//		{
//			prev.SetNextPoint(rail.GetBasePoint());
//			prev.ConstructCurve();
//			prev.SyncData();
//		}
//		smoothBack(num++, root, prev, world);
	}

	private void smoothNext(int num, Rail root, Rail rail, World world)
	{
//		if(num >= 100)return;
//		if(rail == null)return;
//		if(num != 0 && root.equals(rail))return;
//
//		Rail next = rail.GetNextRail();
//		if(next != null)
//		{
//			smoothNext(num++, root, next, world);
//			rail.SetNextPoint(next.GetBasePoint());
//		}
//
//		rail.Smoothing();
//		rail.ConstructCurve();
//		rail.SyncData();
	}
}
