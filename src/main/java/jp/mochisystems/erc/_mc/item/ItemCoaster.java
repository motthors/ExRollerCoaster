package jp.mochisystems.erc._mc.item;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc.entity.EntityCoaster;
import jp.mochisystems.erc._mc.block.rail.BlockRail;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.coaster.Coaster;
import jp.mochisystems.erc.loader.ModelPackLoader;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCoaster extends Item{

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		String[] usages = _Core.I18n("usage.coaster").split("\\\\ ");
		for(String s : usages){
			tooltip.add(TextFormatting.AQUA+s);
		}
	}

	public String getItemStackDisplayName(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		return tag == null ?
				super.getItemStackDisplayName(stack)
				:
				super.getItemStackDisplayName(stack) + " : "
				+ stack.getTagCompound().getString("id");
	}

	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (this.isInCreativeTab(tab))
		{
			for (String id : ModelPackLoader.Instance.GetCoasterPackIds())
			{
				if(!ModelPackLoader.Instance.IsActivePack_forCoaster(id)) continue;
				ItemStack stack = new ItemStack(this);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("id", id);
				stack.setTagCompound(nbt);
				items.add(stack);
			}
		}
	}

    public ItemCoaster()
	{
		super();
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote && player.isSneaking())
		{
			player.openGui(ERC.INSTANCE, ERC.GUIID_ItemCoasterTypeSelector, world, pos.getX(), pos.getY(), pos.getZ());
			return EnumActionResult.SUCCESS;
		}

		ItemStack stack = player.getHeldItem(hand);
		if (!BlockRail.isBlockRail(world.getBlockState(pos))) return EnumActionResult.FAIL;
		if(stack.isEmpty()) return EnumActionResult.FAIL;

		if (!world.isRemote)
		{
			TileEntityRail tile = (TileEntityRail) world.getTileEntity(pos);
			EntityCoaster entityCoaster = new EntityCoaster(world);
			entityCoaster.setLocationAndAngles(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 0, 0);
			world.spawnEntity(entityCoaster);

			String id = ItemCoaster.GetModelId(stack);
			Coaster.InitForParentCoasterFirstPlacing(entityCoaster.GetCoaster(), tile.getRail(), id);
		}

		stack.shrink(1);
		return EnumActionResult.SUCCESS;
	}

	public static String GetModelId(ItemStack stack)
	{
		if(!stack.hasTagCompound()) return ModelPackLoader.defaultCoasterID;
		NBTTagCompound tag = stack.getTagCompound();
		return tag.getString("id");
	}
//	public void setCoasterOnRail(int x, int y, int z, int parentID)
//	{
//		ERC_MessageSpawnRequestWithCoasterOpCtS packet =
//				new ERC_MessageSpawnRequestWithCoasterOpCtS(ModelPackLoader.GetHeadCoasterSettings(modelIDs[modelIndex]), x, y, z, parentID);
//		ERC_PacketHandler.INSTANCE.sendToServer(packet);
//	}
	
}
