
package jp.mochisystems.erc._mc.gui;

import jp.mochisystems.core._mc.gui.container.ContainerBlockScanner;
import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc.tileentity.TileEntityCoasterModelConstructor;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc._mc.tileentity.TileEntityRailModelConstructor;
import jp.mochisystems.erc._mc.gui.container.DefContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ERC_GUIHandler implements IGuiHandler {
		
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
            case ERC.GUIID_RailBase :
                return new DefContainer(x, y, z, null);
            case ERC.GUIID_RailModelConstructor :
                return new ContainerBlockScanner(player.inventory, (TileEntityBlocksScannerBase) world.getTileEntity(new BlockPos(x, y, z)));
            case ERC.GUIID_CoasterModelConstructor :
                return new ContainerBlockScanner(player.inventory, (TileEntityCoasterModelConstructor) world.getTileEntity(new BlockPos(x, y, z)));

        }
        return null;
    }
    
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
            case ERC.GUIID_RailBase :
                return new GUIRail(x, y, z, ((TileEntityRail)world.getTileEntity(new BlockPos(x, y, z))).getRail());
            case ERC.GUIID_RailModelConstructor :
                return new GUIRailModelConstructor(x, y, z, player.inventory, (TileEntityRailModelConstructor) world.getTileEntity(new BlockPos(x, y, z)));
            case ERC.GUIID_CoasterModelConstructor :
                return new GUICoasterModelConstructor(player.inventory, (TileEntityCoasterModelConstructor) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }
}
