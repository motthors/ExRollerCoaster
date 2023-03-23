package jp.mochisystems.erc._mc.block.rail;

import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc._core.ERC_Logger;
import jp.mochisystems.erc._mc.item.ItemCoaster;
import jp.mochisystems.erc._mc.item.ItemRailModelChanger;
import jp.mochisystems.erc._mc.item.ItemSwitchingRailModel;
import jp.mochisystems.erc._mc.network.ERC_PacketHandler;
import jp.mochisystems.erc._mc.network.MessageCheckAutoConnectionStC;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.manager.AutoRailConnectionManager;
import jp.mochisystems.erc.rail.Rail;
import jp.mochisystems.erc.renderer.rail.BlockModelRailRenderer;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockRail extends BlockContainer{

	public static final PropertyEnum<EnumFacing> FACING = PropertyDirection.create("facing");

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}

	public static Rail selected;

	public BlockRail()
	{
		super(Material.GROUND);
		setLightOpacity(1);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN));
	}

	protected TileEntityRail GetTileEntityInstance()
	{
		return new TileEntityRail.Normal();
	}


	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}


	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		ERC_Logger.info(world.getTileEntity(pos).toString());
		if(!world.isRemote)
		{
			TileEntityRail tileEntityRail = (TileEntityRail) world.getTileEntity(pos);
			EnumFacing facing = world.getBlockState(pos).getValue(FACING);
			Vec3d railPos = new Vec3d(pos.getX(), pos.getY(), pos.getZ()).add(0.5);
			Vec3d up = new Vec3d(facing.getDirectionVec());
			net.minecraft.util.math.Vec3d look = placer.getLookVec();
			Vec3d railDir = new Vec3d(
					look.x * (up.x != 0 ? 0 : 1),
					look.y * (up.y != 0 ? 0 : 1),
					look.z * (up.z != 0 ? 0 : 1)).normalize();

			tileEntityRail.InitRail(railPos, railDir, up);
//			Rail prev = tileEntityRail.getRail().GetPrevRail();
//			if(prev != null){
//				prev.Curve().RotateUp
//			}
			tileEntityRail.SyncData();
			ERC_PacketHandler.INSTANCE.sendTo(
					new MessageCheckAutoConnectionStC(pos.getX(), pos.getY(), pos.getZ()),
					(EntityPlayerMP) placer
			);
		}
		else
		{
			TileEntityRail tile = (TileEntityRail) world.getTileEntity(pos);
			selected = tile.getRail();
//			ERC_Logger.info("2 "+world.getTileEntity(pos));
//			AutoRailConnectionManager.ConnectToMemorizedPosition(pos.getX(), pos.getY(), pos.getZ());
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (player.getHeldItem(hand).getItem() instanceof ItemCoaster) return false;

		ItemStack stack = player.getHeldItem(hand);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemRailModelChanger){
			if(!world.isRemote) {
				TileEntityRail tile = (TileEntityRail) world.getTileEntity(pos);
				NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("model");
//			BlockModelRailRenderer renderer = new BlockModelRailRenderer(nbt, pos.getX(), pos.getY(), pos.getZ());
//			renderer.SetRail(tile.getRail());
//			renderer.Construct(world);
//			tile.SetRenderer(renderer);
//			tile.StoreModelStack(stack.splitStack(1));
				tile.packModelID = "";
				tile.SetRenderer(stack);
				tile.UpdateRenderer();
				tile.markBlockForUpdate();
				return true;
			}
		}
		if(!stack.isEmpty() && stack.getItem() instanceof ItemSwitchingRailModel) {
			if(!world.isRemote){
				TileEntityRail tile = (TileEntityRail) world.getTileEntity(pos);
				assert tile != null;
				tile.SetRenderer(ItemStack.EMPTY);
				NBTTagCompound nbt = stack.getTagCompound();
				if(nbt == null || !nbt.hasKey("id")){
					tile.packModelID = "";
//					tile.SetRenderer(new DefaultRailRenderer(tile.GetBlockStateForTexture()));
				}
				else {
					tile.packModelID = nbt.getString("id");
//					tile.SetRenderer(new PackModelRailRenderer(
//							ModelPackLoader.Instance.GetRailModelById(packModelID),
//							ModelPackLoader.Instance.GetRailTextureById(packModelID)));
				}
				tile.markBlockForUpdate();
			}
			return true;
		}

		if(!world.isRemote) {
			player.openGui(ERC.INSTANCE, ERC.GUIID_RailBase, world, pos.getX(), pos.getY(), pos.getZ());
		}
		else {
			TileEntityRail tile = (TileEntityRail) world.getTileEntity(pos);
			selected = tile.getRail();
		}

		return true;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		if(world.isRemote && player.isUser())
		{
			selected = null;
			TileEntityRail tile = (TileEntityRail) world.getTileEntity(pos);
			assert tile != null;
			Rail rail = tile.getRail();
//			rail.FixConnection();
			rail.Break();
			AutoRailConnectionManager.SetPrevRailPosConnectedDestroyBlock(rail.GetPrevRail());
			AutoRailConnectionManager.SetNextRailPosConnectedDestroyBlock(rail.GetNextRail());
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityRail tile = (TileEntityRail) world.getTileEntity(pos);
		assert tile != null;
		tile.SpawnModelStack();
	}


	public static final AxisAlignedBB SelectedAABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return SelectedAABB;
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}


	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return GetTileEntityInstance();
	}

	public static boolean isBlockRail(IBlockState state)
	{
		 return state.getBlock() instanceof BlockRail;
	}

    protected Vec3d ConvertVec3FromSide(int meta)
    {
    	switch(meta){
    	case 0:return new Vec3d(0, -1, 0);
    	case 1:return new Vec3d(0, 1, 0);
    	case 2:return new Vec3d(0, 0, -1);
    	case 3:return new Vec3d(0, 0, 1);
    	case 4:return new Vec3d(-1, 0, 0);
    	case 5:return new Vec3d(1, 0, 0);
    	}
		return new Vec3d(0, 0, 0);
    }
}
