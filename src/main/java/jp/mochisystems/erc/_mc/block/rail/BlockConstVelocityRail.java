//package jp.mochisystems.erc._mc.block.rail;
//
//import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
//import jp.mochisystems.erc.rail.ConstVelocityRail;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.IBlockAccess;
//import net.minecraft.world.World;
//
//
//public class BlockConstVelocityRail extends BlockRail {
//
//    @Override
//    public TileEntityRail GetTileEntityInstance()
//    {
//        return new TileEntityRail.Const();
//    }
//
//    @Override
//    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
//    {
//        if (world instanceof World && !((World) world).isRemote)
//        {
//            int power = ((World) world).isBlockIndirectlyGettingPowered(pos);
//
//            if (power > 0 || world.getBlockState(pos).canProvidePower())
//            {
//                TileEntityRail tile = (TileEntityRail)world.getTileEntity(pos);
//                ConstVelocityRail rail = (ConstVelocityRail)tile.getRail();
//                if (rail.IsActive() != power > 0)
//                {
//                    rail.CycleActivation();
//                    tile.SyncMiscData();
//                    ((World) world).playEvent((EntityPlayer)null, 1003, pos, 0);
//                }
//            }
//        }
//    }
//}
