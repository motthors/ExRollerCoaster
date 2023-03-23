package jp.mochisystems.erc.renderer.rail;

import jp.mochisystems.core.blockcopier.BlocksScanner;
import jp.mochisystems.core.blockcopier.IBLockCopyHandler;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.erc._mc.block.BlockCoasterModelConstructor;
import jp.mochisystems.erc._mc.block.BlockRailModelConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ERCBlocksScanner extends BlocksScanner {

    int slotIdx;
    int layer;

    public ERCBlocksScanner(IBLockCopyHandler handler) {
        super(handler);
    }

    //	private int ConnectorNum;
//    private class Connector{
//        Vec3d pos;
//        String name;
//    }
//	public ArrayList<Connector> connectors = new ArrayList<>();

    public void SetIndex(int slotIdx, int layer)
    {
        this.slotIdx = slotIdx;
        this.layer = layer;
    }

	protected void allocBlockArray(int x, int y, int z)
	{
//        connectors.clear();
		super.allocBlockArray(x, y, z);
    }

    @Override
    protected boolean isExcludedBlock(Block block)
    {
        if(block instanceof BlockRailModelConstructor) return true;
        if(block instanceof BlockCoasterModelConstructor) return true;

        return super.isExcludedBlock(block);
    }

}
