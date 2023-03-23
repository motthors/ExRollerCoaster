//package jp.mochisystems.erc._mc.tileentity;
//
//import jp.mochisystems.core.math.Vec3d;
//import jp.mochisystems.erc._mc.network.ERC_PacketHandler;
//import jp.mochisystems.erc._mc.network.MessageSyncRailOptionStC;
//import jp.mochisystems.erc.rail.BranchRail;
//import jp.mochisystems.erc.rail.IRail;
//import jp.mochisystems.erc.rail.Rail;
//import jp.mochisystems.erc.renderer.rail.DefaultRailRenderer;
//import jp.mochisystems.erc.renderer.rail.RailRenderer;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.init.Blocks;
//import net.minecraft.nbt.NBTTagCompound;
//
//public class TileEntityRailBranch extends TileEntityRail{
//    @Override
//    public IRail GetRailInstance()
//    {
//        return new BranchRail();
//    }
//    @Override
//    protected IBlockState GetStateForBlocKTexture()
//    {
//        return Blocks.LAPIS_BLOCK.getDefaultState();
//    }
//}