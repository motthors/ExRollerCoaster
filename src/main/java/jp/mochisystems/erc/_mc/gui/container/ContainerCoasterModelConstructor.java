//package jp.mochisystems.erc._mc.gui.container;
//
//import jp.mochisystems.core._mc.gui.container.ContainerBlockModelerBase;
//import jp.mochisystems.core._mc.gui.slotCanInsertOnlyItem;
//import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
//import jp.mochisystems.erc._mc.tileentity.TileEntityCoasterModelConstructor;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.entity.player.InventoryPlayer;
//import net.minecraft.inventory.Slot;
//import net.minecraft.item.ItemStack;
//import org.apache.commons.lang3.StringUtils;
//
//public class ContainerCoasterModelConstructor extends ContainerBlockModelerBase
//{
//    private TileEntityCoasterModelConstructor tileFerrisCore;
//    Slot coreSlot;
//
//    public ContainerCoasterModelConstructor(InventoryPlayer playerInventory, TileEntityCoasterModelConstructor tile)
//    {
//        super(playerInventory);
//        this.tileFerrisCore = tile;
//        coreSlot = new slotCanInsertOnlyItem((itemStack -> itemStack.getItem() instanceof IItemBlockModelHolder), tile, 0, 257, 57);
//        this.addSlotToContainer(coreSlot);
//    }
//
//    @Override
//    protected void slideSlotPos()
//    {
//        super.slideSlotPos();
//        coreSlot.yPos = height - 40;
//        coreSlot.xPos = width - 90;
//    }
//
//
//    public boolean canInteractWith(EntityPlayer p_75145_1_)
//    {
//        return true;
//    }
//
//    /**
//     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
//     */
//    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
//    {
//        ItemStack itemstack = null;
//        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);
//
//        if (slot != null && slot.getHasStack())
//        {
//            ItemStack itemstack1 = slot.getStack();
//            itemstack = itemstack1.copy();
//
//            if (p_82846_2_ >= 36)
//            {
//                if (!this.mergeItemStack(itemstack1, 0, 36, true))
//                {
//                    return null;
//                }
//            }
//            // mergeItemStack(入れたいItemStack、移動先のスロットの先頭ID、移動先のスロットの最後尾ID、昇順につめるか)
//            else if (!this.mergeItemStack(itemstack1, 36, 37, false))
//            {
//                return null;
//            }
//
//            if (itemstack1.isEmpty())
//            {
//                slot.putStack((ItemStack)null);
//            }
//            else
//            {
//                slot.onSlotChanged();
//            }
//        }
//
//        return itemstack;
//    }
//
//
////    public void updateItemName(String str)
////    {
////        this.ItemName = str;
////
////        if (this.getSlot(0).getHasStack())
////        {
////            ItemStack itemstack = this.getSlot(0).getStack();
////
////            if (StringUtils.isBlank(str))
////            {
////                itemstack.func_135074_t();
////            }
////            else
////            {
////                itemstack.setStackDisplayName(this.ItemName);
////            }
////        }
////
//////        this.updateRepairOutput();
////    }
//}