package jp.mochisystems.erc._mc.item;

import jp.mochisystems.erc.loader.ModelPackLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class ItemSwitchingRailModel extends Item {

	public ItemSwitchingRailModel(){}

	public String getItemStackDisplayName(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		return tag == null ?
				super.getItemStackDisplayName(stack)+" : Default"
				:
				super.getItemStackDisplayName(stack) + " : "
				+ stack.getTagCompound().getString("id");
	}
	
//	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
//    {
//    	if (!worldIn.isRemote)
//    	{
//	    	if (!BlockRail.isBlockRail(worldIn.getBlockState(pos))) return EnumActionResult.FAIL;
//
//	    	ERC_MessageRailGUICtS packet = new ERC_MessageRailGUICtS(pos.getX(), pos.getY(), pos.getZ(), GUIRail.editFlag.RailModelIndex.ordinal(), 0);
//	    	ERC_PacketHandler.INSTANCE.sendToServer(packet);
//    	}
//        return EnumActionResult.SUCCESS;
//    }

	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (this.isInCreativeTab(tab))
		{
			items.add(new ItemStack(this));
			for (String id : ModelPackLoader.Instance.GetRailPackIds())
			{
				if(!ModelPackLoader.Instance.IsActivePack_forRail(id)) continue;
				ItemStack stack = new ItemStack(this);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("id", id);
//				nbt.setString("texKey", ModelPackLoader.Instance.GetRailTextureById(id));
				stack.setTagCompound(nbt);
				items.add(stack);
			}
		}
	}
}
