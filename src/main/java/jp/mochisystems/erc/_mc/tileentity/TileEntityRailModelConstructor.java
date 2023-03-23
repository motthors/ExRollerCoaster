package jp.mochisystems.erc._mc.tileentity;

import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.core.blockcopier.BlocksScanner;
import jp.mochisystems.core.blockcopier.IBLockCopyHandler;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc.item.ItemRailModelChanger;
import jp.mochisystems.erc.renderer.rail.ERCBlocksScanner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;


public class TileEntityRailModelConstructor extends TileEntityBlocksScannerBase {

    public float widthRatio = 1f;
    public float heightRatio = 1f;

    @Override
    public void Init(EnumFacing side)
    {
        super.Init(side);
        switch (side){
            case SOUTH:
                limitFrame.SetLimit(new Vec3i(0, 0, -1), new Vec3i(0, 0, -1));
                limitFrame.SetReset(new Vec3i(-1, 0, -6), new Vec3i(1, 0, -1));
                break;
            case NORTH:
                limitFrame.SetLimit(new Vec3i(0, 0, 1), new Vec3i(0, 0, 1));
                limitFrame.SetReset(new Vec3i(-1, 0, 1), new Vec3i(1, 0, 6));
                break;
            case DOWN:
                limitFrame.SetLimit(new Vec3i(0, 1, 0), new Vec3i(0, 1, 0));
                limitFrame.SetReset(new Vec3i(-1, 1, 0), new Vec3i(1, 6, 0));
                break;
            case UP:
                limitFrame.SetLimit(new Vec3i(0, -1, 0), new Vec3i(0, -1, 0));
                limitFrame.SetReset(new Vec3i(-1, -6, 0), new Vec3i(1, -1, 0));
                break;
            case WEST:
                limitFrame.SetLimit(new Vec3i(1, 0, 0), new Vec3i(1, 0, 0));
                limitFrame.SetReset(new Vec3i(1, 0, -1), new Vec3i(6, 0, 1));
                break;
            case EAST:
                limitFrame.SetLimit(new Vec3i(-1, 0, 0), new Vec3i(-1, 0, 0));
                limitFrame.SetReset(new Vec3i(-6, 0, -1), new Vec3i(-1, 0, 1));
                break;
        }
        limitFrame.Reset();
    }

	@Override
    protected BlocksScanner InstantiateBlocksCopier(IBLockCopyHandler handler){
	    return new ERCBlocksScanner(handler);
    }

    @Override
    protected boolean isExistCore()
    {
        if(stackSlot.isEmpty()) return false;
        return stackSlot.getItem() instanceof ItemRailModelChanger;
    }

    @Override
    public ItemStack InstantiateModelItem() {
        return new ItemStack(ERC.ItemRailBlockModelChanger);
    }

    @Override
    public void registerExternalParam(NBTTagCompound model, NBTTagCompound nbt)
    {
        super.registerExternalParam(model, nbt);

        // => BlockModelRailRenderer
        model.setFloat("widthratio", widthRatio);
        model.setFloat("heightratio", heightRatio);
    }

    @Override
    public void OnCompleteReceive(NBTTagCompound nbt)
    {
        super.OnCompleteReceive(nbt);
        NBTTagCompound modelNbt = (NBTTagCompound) nbt.getTag("model");
        widthRatio = modelNbt.getFloat("widthratio");
        heightRatio = modelNbt.getFloat("heightratio");
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }


    public void ReadParamFromNBT(NBTTagCompound nbt)
    {
        super.ReadParamFromNBT(nbt);
        widthRatio = nbt.hasKey("widthratio") ? nbt.getFloat("widthratio") : 1f;
        heightRatio = nbt.hasKey("heightratio") ? nbt.getFloat("heightratio") : 1f;
    }

    @Nonnull
    @Override
    public NBTTagCompound WriteParamToNBT(NBTTagCompound nbt)
    {
        super.WriteParamToNBT(nbt);
        nbt.setFloat("widthratio", widthRatio);
        nbt.setFloat("heightratio", heightRatio);
        return nbt;
    }


//    @Override
//    public void clear() {
//    }
//    @Override public int getFieldCount(){return 0;}
}
