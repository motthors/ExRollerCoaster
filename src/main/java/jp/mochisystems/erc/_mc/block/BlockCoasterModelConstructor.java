package jp.mochisystems.erc._mc.block;

import jp.mochisystems.core._mc.block.BlockBlockScannerBase;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc.item.ItemRailModelChanger;
import jp.mochisystems.erc._mc.item.ItemSwitchingRailModel;
import jp.mochisystems.erc._mc.tileentity.TileEntityCoasterModelConstructor;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.renderer.rail.BlockModelRailRenderer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class BlockCoasterModelConstructor extends BlockBlockScannerBase {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockCoasterModelConstructor()
	{
		super();
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	protected int GetGuiId()
	{
		return ERC.GUIID_CoasterModelConstructor;
	}

	@Override
	protected Object GetModCore()
	{
		return ERC.INSTANCE;
	}

	@Override
	protected TileEntity CreateTileEntityScanner()
	{
		return new TileEntityCoasterModelConstructor();
	}


	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		EnumFacing direction = EnumFacing.fromAngle(placer.rotationYaw).getOpposite();
		world.setBlockState(pos, state.withProperty(FACING, direction), 2);
		TileEntityCoasterModelConstructor tile = (TileEntityCoasterModelConstructor) world.getTileEntity(pos);
		tile.Init(direction);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemRailModelChanger){
			TileEntityCoasterModelConstructor tile = (TileEntityCoasterModelConstructor) world.getTileEntity(pos);
			NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().getTag("model");
			BlockModelRailRenderer renderer = new BlockModelRailRenderer(nbt, pos.getX(), pos.getY(), pos.getZ());
			renderer.SetRail(tile.rail);
			renderer.Construct(world);
			tile.SetRenderer(renderer);
			tile.StoreModelStack(stack.splitStack(1));
			return true;
		}

		if(!stack.isEmpty() && stack.getItem() instanceof ItemSwitchingRailModel) {
			if(!world.isRemote){
				TileEntityCoasterModelConstructor tile = (TileEntityCoasterModelConstructor) world.getTileEntity(pos);
				assert tile != null;
				NBTTagCompound nbt = stack.getTagCompound();
				if(nbt == null || !nbt.hasKey("id")){
					tile.packModelID = "";
				}
				else {
					tile.packModelID = nbt.getString("id");
				}
				tile.markBlockForUpdate();
			}
			return true;
		}

		player.openGui(GetModCore(), GetGuiId(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}


	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

}
