package jp.mochisystems.erc._mc.gui;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.GUIBlockScannerBase;
import jp.mochisystems.core._mc.message.MessageChangeLimitLine;
import jp.mochisystems.core._mc.message.PacketHandler;
import jp.mochisystems.core.blockcopier.LimitFrame;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.util.NbtParamsShadow;
import jp.mochisystems.core.util.gui.*;
import jp.mochisystems.erc._mc.tileentity.TileEntityRailModelConstructor;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class GUIRailModelConstructor extends GUIBlockScannerBase {

//    private final TileEntityRailModelConstructor tile;

    private final NbtParamsShadow.Param<Float> widthRatio;
    private final NbtParamsShadow.Param<Float> heightRatio;
    private final NbtParamsShadow.Param<Integer> copyNum;


    public GUIRailModelConstructor(int x, int y, int z, InventoryPlayer playerInventory, TileEntityRailModelConstructor tile)
    {
        super(playerInventory, tile);
        widthRatio = shadow.Create("widthratio", n->n::setFloat, NBTTagCompound::getFloat);
        heightRatio = shadow.Create("heightratio", n->n::setFloat, NBTTagCompound::getFloat);
        copyNum = shadow.Create("copyNum", n->n::setInteger, NBTTagCompound::getInteger);
    }


    @Override
    protected void AddFrameFront(int add){

        switch(tile.GetSide()){
            case DOWN: frame.AddLengths(0, 0, 0, 0, add, 0); break;
            case UP: frame.AddLengths(0, -add, 0, 0, 0, 0); break;
            case WEST: frame.AddLengths(0, 0, 0, add, 0, 0); break;
            case EAST: frame.AddLengths(-add, 0, 0, 0, 0, 0); break;
            case SOUTH: frame.AddLengths(0, 0, -add, 0, 0, 0); break;
            case NORTH: frame.AddLengths(0, 0, 0, 0, 0, add); break;
        }
    }




	@Override
	public void initGui()
    {
        super.initGui();


        Canvas.Register(-1, new GuiDragChangerLabel(
                _Core.I18n("gui.core.text.width")+" "+_Core.I18n("gui.core.text.scale"),
                fontRenderer, 2, 95, 0xffffff,
                    d -> UpdateWidthRatio(d * 0.05f),
                    ()->{}));
        Canvas.Register(-1, new GuiFormattedTextField(0, fontRenderer, 50, 105, 50, 10, 0xffffff, 5,
                () -> String.format("%5.2f", widthRatio.Get()),
                s -> s.matches(GuiFormattedTextField.regexNumber),
                t -> widthRatio.Set(Float.parseFloat(t))));
        GuiUtil.addButton6(Canvas, -1,42, 115,
                () -> UpdateWidthRatio(-1),
                () -> UpdateWidthRatio(-0.1f),
                () -> UpdateWidthRatio(-0.01f),
                () -> UpdateWidthRatio(0.01f),
                () -> UpdateWidthRatio(0.1f),
                () -> UpdateWidthRatio(1)
        );


        Canvas.Register(-1, new GuiDragChangerLabel(
                _Core.I18n("gui.core.text.height")+" "+_Core.I18n("gui.core.text.scale"),
                fontRenderer, 2, 130, 0xffffff,
                d -> UpdateHeightRatio(d * 0.05f),
                ()->{} ));
        Canvas.Register(-1, new GuiFormattedTextField(0, fontRenderer, 50, 140, 50, 10, 0xffffff, 5,
                () -> String.format("%5.2f", heightRatio.Get()),
                s -> s.matches(GuiFormattedTextField.regexNumber),
                t -> heightRatio.Set(Float.parseFloat(t))));
        GuiUtil.addButton6(Canvas, -1,42, 150,
                () -> UpdateHeightRatio(-1),
                () -> UpdateHeightRatio(-0.1f),
                () -> UpdateHeightRatio(-0.01f),
                () -> UpdateHeightRatio(0.01f),
                () -> UpdateHeightRatio(0.1f),
                () -> UpdateHeightRatio(1)
        );


        Canvas.Register(-1, new GuiLabel(_Core.I18n("gui.scan.text.repeat"), fontRenderer, width-80, 130, 0xffffff));
        Canvas.Register(-1, new GuiFormattedTextField(0, fontRenderer, width-35, 130, 50, 5, 0xffffff, 5,
                () -> String.format("%5d", copyNum.Get()),
                s -> s.matches(GuiFormattedTextField.regexInteger),
                t -> copyNum.Set(Integer.parseInt(t))));
        GuiUtil.addButton4(Canvas, -1,width-70, 140,
                () -> UpdateRepeatCount(-5),
                () -> UpdateRepeatCount(-1),
                () -> UpdateRepeatCount(1),
                () -> UpdateRepeatCount(5)
        );

 }

    private void UpdateWidthRatio(float add)
    {
        float w = widthRatio.Get();
        w += add;
        w = Math.Clamp(w, 0.01f, 100);
        widthRatio.Set(w);
    }

    private void UpdateHeightRatio(float add)
    {
        float h = heightRatio.Get();
        h += add;
        h = Math.Clamp(h, 0.01f, 100);
        heightRatio.Set(h);
    }

    private void UpdateRepeatCount(int add)
    {
        int value = copyNum.Get() + add;
        copyNum.Set(Math.Clamp(value, 1, 500));
    }
//
//    private int GetLen(Direction dir)
//    {
//        LimitFrame frame = tile.GetLimitFrame();
//        EnumFacing side = tile.GetSide();
//        switch(dir) {
//            case Width:
//                side = side.rotateY();
//                break;
//            case Height:
//                if (side == EnumFacing.UP) side = EnumFacing.NORTH;
//                else if (side == EnumFacing.DOWN) side = EnumFacing.SOUTH;
//                side = EnumFacing.UP;
//                break;
//        }
//        return frame.GetLenByDirection(side);
//    }
//
//
//    private void SetLimitLine(Direction dir, int length)
//    {
//        LimitFrame frame = tile.GetLimitFrame();
//        EnumFacing side = tile.GetSide();
//        switch(dir){
//            case Width:
//                side = side.rotateYCCW();
//                frame.SetByDirection(side, -length/2, length/2);
//                break;
//            case Height:
//                if(side == EnumFacing.UP) side = EnumFacing.NORTH;
//                else if(side == EnumFacing.DOWN) side = EnumFacing.SOUTH;
//                else side = EnumFacing.UP;
//                frame.SetByDirection(side, -length/2, length/2);
//                break;
//            case Length:
//                frame.SetByDirection(side, 1, length);
//        }
//        MessageChangeLimitLine m = new MessageChangeLimitLine(tile.getPos(), frame);
//        PacketHandler.INSTANCE.sendToServer(m);
//    }
//
//    private void ChangeLimitLine(Direction dir, int add)
//    {
//        LimitFrame frame = tile.GetLimitFrame();
//        EnumFacing side = tile.GetSide();
//        switch(dir){
//            case Width:
//                side = side.rotateYCCW();
//                frame.AddByDirection(side, -add, add);
//                break;
//            case Height:
//                if(side == EnumFacing.UP) side = EnumFacing.NORTH;
//                else if(side == EnumFacing.DOWN) side = EnumFacing.SOUTH;
//                else side = EnumFacing.UP;
//                frame.AddByDirection(side, -add, add);
//                break;
//            case Length:
//                frame.AddByDirection(side, 0, add);
//        }
//        MessageChangeLimitLine m = new MessageChangeLimitLine(tile.getPos(), frame);
//        PacketHandler.INSTANCE.sendToServer(m);
//    }



}
