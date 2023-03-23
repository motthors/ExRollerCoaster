package jp.mochisystems.erc._mc.item;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import jp.mochisystems.erc.coaster.CoasterSettings;
import jp.mochisystems.erc.model.CoasterBlockModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nonnull;

public class ItemCoasterModel extends Item implements IItemBlockModelHolder {


    @Override
    public IModel GetBlockModel(IModelController controller) {
        return new CoasterBlockModel(controller);
    }


    public static CoasterSettings GetCoasterSettingsFromNBT(NBTTagCompound nbt)
    {
        if(!nbt.hasKey("coasterSettings")) return null;
        CoasterSettings settings = CoasterSettings.Default();
       settings.FromString(nbt.getString("coasterSettings"));
        return settings;
    }

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
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
        if (target instanceof EntityPig)
        {
            EntityPig entitypig = (EntityPig)target;

            if (!entitypig.getSaddled() && !entitypig.isChild())
            {
                entitypig.setSaddled(true);
                entitypig.world.playSound(playerIn, entitypig.posX, entitypig.posY, entitypig.posZ, SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5F, 1.0F);
                stack.shrink(1);
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
