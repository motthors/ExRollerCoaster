package jp.mochisystems.erc._mc.block;

import jp.mochisystems.core._mc.block.BlockRotatedScanner;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc._mc.tileentity.TileEntityRailModelConstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class BlockRailModelConstructor extends BlockRotatedScanner {

	public BlockRailModelConstructor()
	{
		super();
//		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	protected int GetGuiId()
	{
		return ERC.GUIID_RailModelConstructor;
	}

	@Override
	protected Object GetModCore()
	{
		return ERC.INSTANCE;
	}

	@Override
	protected TileEntity CreateTileEntityScanner()
	{
		return new TileEntityRailModelConstructor();
	}


	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		EnumFacing direction = world.getBlockState(pos).getValue(FACING);
		TileEntityRailModelConstructor tile = (TileEntityRailModelConstructor) world.getTileEntity(pos);
		tile.Init(direction);
	}


	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

}
