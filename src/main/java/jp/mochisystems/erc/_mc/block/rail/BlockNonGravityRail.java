package jp.mochisystems.erc._mc.block.rail;

import jp.mochisystems.erc._mc.tileentity.TileEntityRail;

public class BlockNonGravityRail extends BlockRail {

    @Override
    protected TileEntityRail GetTileEntityInstance() {
        return new TileEntityRail.AntiGravity();
    }

}
