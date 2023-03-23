package jp.mochisystems.erc._mc.gui;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.GUIBlockScannerBase;
import jp.mochisystems.core._mc.gui.GuiToggleButton;
import jp.mochisystems.core.blockcopier.LimitFrame;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.NbtParamsShadow;
import jp.mochisystems.core.util.gui.*;
import jp.mochisystems.erc._mc.tileentity.TileEntityCoasterModelConstructor;
import jp.mochisystems.erc.coaster.CoasterSettings;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class GUICoasterModelConstructor extends GUIBlockScannerBase {

    private final TileEntityCoasterModelConstructor tile;
    private CoasterSettings.SeatData currentSelectedSeatData = new CoasterSettings.SeatData();//Dummy

    private final static int inCopy = 1;
    private final static int inCopy_frame = 1000;
    private final static int inModifyTransform = 2;
    private final static int inModifySeats = 3;
    private final static int inModifySeatsEditor = 4;
    private final static int inModifyEngine = 5;
    private final static int inEngineSetting = 6;
    private int Mode = inCopy;

    public int currentSelectedSeatIdx = 0;

    private final NbtParamsShadow.Param<Integer> seatNum;

    private final NbtParamsShadow.Class<Vec3d> modelScale;
    private final NbtParamsShadow.Class<Vec3d> modelOffset;
    private final NbtParamsShadow.Class<Quaternion> modelRotate;

    private final Vec3d _seatOffset = new Vec3d();
    private final NbtParamsShadow.Class<Vec3d> seatOffset;
    private final Vec3d _seatRotate = new Vec3d();
    private final NbtParamsShadow.Class<Vec3d> seatRotate;

    public GUICoasterModelConstructor(InventoryPlayer playerInventory, TileEntityCoasterModelConstructor tile) {
        super(playerInventory, tile);
        this.tile = tile;
        seatNum = shadow.Create("seatNum", n->n::setInteger, NBTTagCompound::getInteger);
        modelScale = shadow.Create("modelScale", tile.modelScale, v -> v::WriteToNBT);
        modelOffset = shadow.Create("modelOffset", tile.modelOffset, v -> v::WriteToNBT);
        modelRotate = shadow.Create("modelTilt", tile.modelRotate, v -> v::WriteToNBT);

        seatOffset = shadow.Create("", _seatOffset, v -> v::WriteToNBT);
        seatRotate = shadow.Create("", _seatRotate, v -> v::WriteToNBT);
    }

    protected void UpdateCameraPosFromCore(Vec3d dest, float tick) {
        switch (Mode){
            case inCopy:
                LimitFrame frame = tile.GetLimitFrame();
                float x = (frame.getxx()+frame.getmx())/2f;
                float y = (frame.getxy()+frame.getmy())/2f;
                float z = (frame.getxz()+frame.getmz())/2f;
                dest.SetFrom(x+0.5, y+0.5, z+0.5);
                break;
            case inModifyTransform:
            case inModifySeats:
            case inModifyEngine:
                dest.SetFrom(-3.5, 0.5, 0.5);
                if(currentSelectedSeatData != null)
                    dest.add(currentSelectedSeatData.LocalPosition);
                break;
        }
        dest.add(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
    }


    @Override
    protected int FrameLengthFront() {
       return tile.GetLimitFrame().lenZ();
    }
    @Override
    protected int FrameLengthSide() {
        return tile.GetLimitFrame().lenX();
    }
    @Override
    protected int FrameLengthHeight() {
        return tile.GetLimitFrame().lenY();
    }
    @Override
    protected void AddFrameFront(int add){
        frame.AddLengths(0, 0, 0, 0, 0, add);

    }
    @Override
    protected void AddFrameSide(int add){
        frame.AddLengths(0, 0, 0, add, 0, 0);
    }
    @Override
    protected void AddFrameHeight(int add){
        frame.AddLengths(0, 0, 0, 0, add, 0);
    }

    @Override
    public void initGui() {
        super.initGui();

        int gDef = -1;


        Canvas.Register(inCopy,
                new GuiButtonWrapper(0,  20, height - 50, 60, 26, "Copy",
                        this::StartScanning));



        ////////////////// Modify Model //////////////////

        //modelScale
        GuiUtil.Vec3(_Core.I18n("gui.core.text.scale"), modelScale, Canvas, fontRenderer,
                2, 2, inModifyTransform, 0.01f,
                this::SyncClient);

        //model offset
        GuiUtil.Vec3(_Core.I18n("gui.core.text.offset"), modelOffset, Canvas, fontRenderer,
                2, 50, inModifyTransform, 0.01f,
                this::SyncClient);

        //model rotate
        GuiUtil.Quaternion(_Core.I18n("gui.core.text.rotate"), modelRotate, Canvas, fontRenderer,
                2, 100, inModifyTransform, 0.05f,
                this::SyncClient);

        Canvas.Register(inModifyTransform, new GuiButtonWrapper(0,
                35, 150, 42, 12, _Core.I18n("gui.core.text.reset"),
                () -> {
                    modelScale.Get().CopyFrom(Vec3d.One);
                    modelOffset.Get().CopyFrom(Vec3d.Zero);
                    modelRotate.Get().Identity();
                    SyncClient();
                }));


        Canvas.Register(inModifyTransform,
                new GuiButtonWrapper(0,  20, height - 50, 60, 26, _Core.I18n("gui.scan.text.update"),
                        this::StartScanning));




        // engine settings
        GuiUtil.addCheckButton(Canvas, fontRenderer, inModifyEngine,
                60, 5,
                () -> tile.coasterSettings.MaxEngineLevel != 0, _Core.I18n("gui.constructor.coaster.engine"),
                b -> {
                    if(!b) {
                        Canvas.DisableGroup(inEngineSetting);
                        tile.coasterSettings.MaxEngineLevel = 0;
                    }else{
                        Canvas.ActiveGroup(inEngineSetting);
                        tile.coasterSettings.AccelUnit = 0.1f;
                        tile.coasterSettings.MaxEngineLevel = 5;
                    }
                }
                );

        GuiUtil.AddInspector(Canvas, fontRenderer, _Core.I18n("gui.constructor.coaster.engine.power"), inEngineSetting,
                2, 30, false,
                () -> tile.coasterSettings.AccelUnit,
                f -> tile.coasterSettings.AccelUnit = f, 0.01f,
                this::SyncToServer);

        Canvas.Register(inEngineSetting, new GuiLabel(_Core.I18n("gui.constructor.coaster.engine.notches"), fontRenderer,
                2, 60, -1));
        Canvas.Register(inEngineSetting,
            new GuiFormattedTextField(0, fontRenderer, 22, 74, 20, 11, -1, 3,
                    () -> String.format("%3d", tile.coasterSettings.MaxEngineLevel),
                    s -> s.matches(GuiFormattedTextField.regexInteger),
                    s -> tile.coasterSettings.MaxEngineLevel = Integer.parseInt(s))
        );
        GuiUtil.addButton2(Canvas, inEngineSetting,
                10, 72,
                () -> {if(tile.coasterSettings.MaxEngineLevel>1) tile.coasterSettings.MaxEngineLevel--; SyncToServer();},
                () -> {if(tile.coasterSettings.MaxEngineLevel<100) tile.coasterSettings.MaxEngineLevel++; SyncToServer();});


        Canvas.Register(inEngineSetting,
                new GuiButtonWrapper(0,  20, height - 50, 60, 26, _Core.I18n("gui.scan.text.update"),
                        this::StartScanning));






        ////////////////// Modify Seats //////////////////

        Canvas.Register(inModifySeats,
                new GuiFormattedTextField(2, fontRenderer, 25, 4, 25, 20, 0xffffff, 3,
                        () -> String.format("%d", tile.seatNum),
                        s -> s.matches(GuiFormattedTextField.regexInteger),
                        t -> SetSeatNum(Integer.parseInt(t))));
        Canvas.Register(inModifySeats,
                new GuiButtonWrapper(-1, 0, 0, 20, 15,
                        "-1",
                        () -> SetSeatNum(tile.seatNum - 1)));
        Canvas.Register(inModifySeats,
                new GuiButtonWrapper(-1, 50, 0, 20, 15,
                        "+1",
                        () -> SetSeatNum(tile.seatNum + 1)));

        Canvas.Register(inModifySeatsEditor,
                new GuiButtonWrapper(-1, 0, 17, 20, 15,
                        "<",
                        () -> {
                            int next = currentSelectedSeatIdx - 1;
                            next = next % tile.seatNum;
                            currentSelectedSeatIdx = next;
                            ActivateSeatDataToEditor();
                        }));
        Canvas.Register(inModifySeatsEditor,
                new GuiButtonWrapper(-1, 50, 17, 20, 15,
                        ">",
                        () -> {
                            int next = currentSelectedSeatIdx + 1;
                            next = next % tile.seatNum;
                            currentSelectedSeatIdx = next;
                            ActivateSeatDataToEditor();
                        }));

        Canvas.Register(inModifySeats,
                new GuiButtonWrapper(0,  20, height - 50, 60, 26,
                        _Core.I18n("gui.scan.text.update"),
                        this::StartScanning));


        ////////////
        //seat offset
        GuiUtil.Vec3(_Core.I18n("gui.core.text.offset"), seatOffset, Canvas, fontRenderer,
                2, 40, inModifySeatsEditor, 0.05f,
                () -> {});

        //seat rotate
        GuiUtil.Vec3(_Core.I18n("gui.core.text.rotate"), seatRotate, Canvas, fontRenderer,
                2, 95, inModifySeatsEditor, 0.05f,
                () -> {});

        Canvas.Register(inModifySeatsEditor, new GuiButtonWrapper(0,
                35, 150, 42, 12, _Core.I18n("gui.core.text.reset"),
                () -> {
                    currentSelectedSeatData.LocalPosition.CopyFrom(Vec3d.Zero);
                    currentSelectedSeatData.LocalRotationDegree.CopyFrom(Vec3d.Zero);
                }));


        Canvas.MoveGroup(inModifySeats, width - 80, 10, false);
        Canvas.MoveGroup(inModifySeatsEditor, width - 80, 10, false);
        Canvas.MoveGroup(inModifyEngine, width - 80, 10, false);
        Canvas.MoveGroup(inEngineSetting, width - 80, 10, false);
        Canvas.DisableGroup(inModifySeatsEditor);


        ////////////////// Change Mode //////////////////
        Canvas.Register(gDef, new GuiLabel(_Core.I18n("gui.constructor.coaster.mode"), fontRenderer, 10, 8, -1));
        Canvas.Register(gDef,
                new GuiToggleButton(0,  10, 20,
                        60, 22,
                        _Core.I18n("gui.constructor.coaster.mode.model"),
                        _Core.I18n("gui.constructor.coaster.mode.model"),
                        () -> Mode == inCopy,
                        isOn -> { if(isOn) {
                            ChangePage(inCopy);
                        }}));
        Canvas.Register(gDef,
                new GuiToggleButton(0,  10, 50,
                        60, 22,
                        _Core.I18n("gui.constructor.coaster.mode.transform"),
                        _Core.I18n("gui.constructor.coaster.mode.transform"),
                        () -> Mode == inModifyTransform,
                        isOn -> { if(isOn) {
                            ChangePage(inModifyTransform);
                        } } ));
        Canvas.Register(gDef,
                new GuiToggleButton(0,  10, 80,
                        60, 22,
                        _Core.I18n("gui.constructor.coaster.mode.seats"),
                        _Core.I18n("gui.constructor.coaster.mode.seats"),
                        () -> Mode == inModifySeats,
                        isOn -> { if(isOn) {
                            ChangePage(inModifySeats);
                        } } ));
        Canvas.Register(gDef,
                new GuiToggleButton(0,  10, 110,
                        60, 22,
                        _Core.I18n("gui.constructor.coaster.mode.engine"),
                        _Core.I18n("gui.constructor.coaster.mode.engine"),
                        () -> Mode == inModifyEngine,
                        isOn -> { if(isOn) {
                            ChangePage(inModifyEngine);
                        } } ));

        Canvas.ActiveGroup(inCopy);
        Canvas.ActiveGroup(inCopy_frame);
        Canvas.MoveGroup(inCopy, width - 80, 0, false);
        Canvas.MoveGroup(inModifyTransform, width - 80, 0, false);

    }

    @Override
    protected void MakeFrameControl(int group){
        super.MakeFrameControl(inCopy);
    }
    @Override
    protected void MakeFrameDirectSlide(int group){
        super.MakeFrameDirectSlide(inCopy_frame);
    }

    @Override
    protected void MakeOptionControl(int group){
        if(!_Core.CONFIG_ANNOTATIONS.isProGui) return;
        GuiUtil.addCheckButton(Canvas, fontRenderer, inCopy, 60, 120,
                trueCopy::Get,
                _Core.I18n("gui.scan.text.truecopy"),
                trueCopy::Set);
    }
    @Override
    protected void MakeScanControl(int group){}


    private void ChangePage(int mode)
    {
        Canvas.DisableGroup(Mode);
        Canvas.DisableGroup(inModifySeatsEditor);
        Canvas.DisableGroup(inEngineSetting);
        Mode = mode;
        Canvas.ActiveGroup(mode);

        if(mode == inCopy) Canvas.ActiveGroup(inCopy_frame);
        else Canvas.DisableGroup(inCopy_frame);

        switch(mode){
            case inModifySeats:
                ActivateSeatDataToEditor();
                break;
            case inModifyEngine:
                if(tile.coasterSettings.MaxEngineLevel > 0){
                    Canvas.ActiveGroup(inEngineSetting);
                }else{
                    Canvas.DisableGroup(inEngineSetting);
                }
                break;
        }
    }

    private void SetSeatNum(int next)
    {
        next = Math.Clamp(next, 0, 1000);
        if(currentSelectedSeatIdx > next) currentSelectedSeatIdx = next;
        seatNum.Set(next);
        tile.ChangeSeatNum(next);
        ActivateSeatDataToEditor();
    }
    private void ActivateSeatDataToEditor()
    {
        if(tile.coasterSettings.Seats.length==0) {
            currentSelectedSeatData = new CoasterSettings.SeatData();//Dummy
            seatOffset.ResetRef(_seatOffset);
            seatRotate.ResetRef(_seatRotate);
            Canvas.DisableGroup(inModifySeatsEditor);
        }
        else {
            currentSelectedSeatIdx = Math.Clamp(currentSelectedSeatIdx, 0, tile.coasterSettings.Seats.length-1);
            currentSelectedSeatData = tile.coasterSettings.Seats[currentSelectedSeatIdx];
            seatOffset.ResetRef(currentSelectedSeatData.LocalPosition);
            seatRotate.ResetRef(currentSelectedSeatData.LocalRotationDegree);
            Canvas.ActiveGroup(inModifySeatsEditor);
        }
    }


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}