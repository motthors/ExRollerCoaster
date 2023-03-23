package jp.mochisystems.erc._mc.block.rail;


import jp.mochisystems.erc._mc.network.ERC_PacketHandler;
import jp.mochisystems.erc._mc.network.MessageSyncRailOptionStC;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailRedPowerHandlerBase extends BlockRail{

    @Override
    public boolean getWeakChanges(IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        TileEntityRail tile = GetTileEntityInstance();
        return tile;
    }

    @Override
//    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        if (!world.isRemote)
        {
            boolean powered = world.isBlockPowered(pos);
//            IBlockState neighborState = world.getBlockState(fromPos);
//            EnumFacing side = EnumFacing.getFacingFromVector(pos.getX()-fromPos.getX(), pos.getY()-fromPos.getY(), pos.getZ()-fromPos.getZ());
//            int powerFrom = getPowerOnSide(world, fromPos, side);

            TileEntityRail tile = (TileEntityRail)world.getTileEntity(pos);
            Rail rail = tile.getRail();
            boolean nextActive = powered;
            boolean needUpdate = tile.IsActive() != nextActive;
            if (needUpdate)
            {
                tile.SetActive(nextActive);
                ERC_PacketHandler.INSTANCE.sendToAll(new MessageSyncRailOptionStC(rail));
                world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, powered ? 0.6F : 0.5F);
            }
        }
    }

    protected int getPowerOnSide(IBlockAccess worldIn, BlockPos fromPos, EnumFacing side)
    {
        IBlockState iblockstate = worldIn.getBlockState(fromPos);
        Block block = iblockstate.getBlock();

        if (block == Blocks.REDSTONE_BLOCK)
        {
            return 15;
        }
        else
        {
            return block == Blocks.REDSTONE_WIRE ? iblockstate.getValue(BlockRedstoneWire.POWER) : worldIn.getStrongPower(fromPos, side);
        }
    }



    public static class BlockAccelRail extends BlockRailRedPowerHandlerBase {
        @Override
        protected TileEntityRail GetTileEntityInstance()
        {
            return new TileEntityRail.Accel();
        }
    }

    public static class BlockConstVelocityRail extends BlockRailRedPowerHandlerBase {
        @Override
        protected TileEntityRail GetTileEntityInstance()
        {
            return new TileEntityRail.Const();
        }
    }

    public static class BlockBranchRail extends BlockRailRedPowerHandlerBase {

        @Override
        protected TileEntityRail GetTileEntityInstance()
        {
            return new TileEntityRail.Branch();
        }
    }
}
