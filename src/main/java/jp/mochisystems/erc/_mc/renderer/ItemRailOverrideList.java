package jp.mochisystems.erc._mc.renderer;

import jp.mochisystems.erc.loader.ModelPackLoader;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRailOverrideList extends ItemOverrideList {

    public ItemRailOverrideList(List<ItemOverride> overridesIn)
    {
        super(overridesIn);
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
    {
        if (!stack.isEmpty() && stack.getItem().hasCustomProperties())
        {
            ResourceLocation location = null;
            if (!this.getOverrides().isEmpty()) {
                for (ItemOverride itemoverride : this.getOverrides()){
                    NBTTagCompound nbt = stack.getTagCompound();
                    if(nbt == null) continue;
                    String id = nbt.getString("id");
                    String name = ModelPackLoader.Instance.GetRailIconName(id);
                    if(itemoverride.getLocation().toString().contains(name)){
                        location = itemoverride.getLocation();
                        break;
                    }
                }
            }

            if (location != null)
            {
                return net.minecraft.client.Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getModel(net.minecraftforge.client.model.ModelLoader.getInventoryVariant(location.toString()));
            }
        }
        return originalModel;
    }
}
