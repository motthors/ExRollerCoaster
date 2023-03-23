package jp.mochisystems.erc._mc.item;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.blockcopier.DefBlockModel;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import jp.mochisystems.erc._mc.block.BlockCoasterModelConstructor;
import jp.mochisystems.erc._mc.block.rail.BlockRail;
import jp.mochisystems.erc._mc.tileentity.TileEntityCoasterModelConstructor;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.renderer.rail.BlockModelRailRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemRailModelChanger extends Item implements IItemBlockModelHolder {

	public ItemRailModelChanger(){}


	@Nonnull
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		if(stack.getTagCompound()!=null && stack.getTagCompound().getCompoundTag("model")!=null){
			return _Core.I18n(this.getUnlocalizedNameInefficiently(stack) + ".name").trim()
					+ " : " + stack.getTagCompound().getCompoundTag("model").getString("ModelName");
		}
		return super.getItemStackDisplayName(stack);
	}

	@Override
	public IModel GetBlockModel(IModelController controller) {
		return new DefBlockModel(controller);
	}

}
