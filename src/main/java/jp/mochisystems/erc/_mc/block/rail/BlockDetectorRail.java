package jp.mochisystems.erc._mc.block.rail;

import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.rail.DetectorRail;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockDetectorRail extends BlockRail {

    @Override
    protected TileEntityRail GetTileEntityInstance() {
        return new TileEntityRail.Detector();
    }

    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return false;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        DetectorRail rail = ((DetectorRail) ((TileEntityRail) blockAccess.getTileEntity(pos)).getRail());
        return rail.ExistCoaster() ? 15 : 0;
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        DetectorRail rail = ((DetectorRail) ((TileEntityRail) blockAccess.getTileEntity(pos)).getRail());
        return rail.ExistCoaster() ? 15 : 0;
    }

}
