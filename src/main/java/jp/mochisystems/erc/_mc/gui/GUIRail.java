package jp.mochisystems.erc._mc.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.GUICanvasGroupControl;
import jp.mochisystems.core._mc.gui.GuiToggleButton;
import jp.mochisystems.core._mc.gui.container.DefContainer;
import jp.mochisystems.core._mc.message.MessageSyncNbtCtS;
import jp.mochisystems.core._mc.message.PacketHandler;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.NbtParamsShadow;
import jp.mochisystems.core.util.gui.*;
import jp.mochisystems.erc._mc.block.rail.BlockRail;
import jp.mochisystems.erc._mc.network.ERC_MessageRailGUICtS;
import jp.mochisystems.erc._mc.network.ERC_PacketHandler;
import jp.mochisystems.erc._mc.network.MessageOpenGuiCtS;
import jp.mochisystems.erc.rail.AccelRail;
import jp.mochisystems.erc.rail.AntiGravityRail;
import jp.mochisystems.erc.rail.ConstVelocityRail;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GUIRail extends GUICanvasGroupControl {

	Rail rail;
	static int cameraMode = 0;
	static Vec3d cameraPoint = new Vec3d();
	static Rail currentEditingRail;
	public static boolean IsEditing(Rail rail){
		return currentEditingRail == rail;
	}


	public enum editFlag{
		CONTROLPOINT, POW,
		ROTRED, ROTGREEN, ROTBLUE, SET_TWIST,
		SMOOTH, RESET, SPECIAL, RailModelIndex,
		NEXT, PREV,
		OFFSET_X, OFFSET_Y, OFFSET_Z, SETOFFSET
	}

	private GuiButtonWrapper offset_x_p;
	private GuiButtonWrapper offset_x_m;
	private GuiButtonWrapper offset_y_p;
	private GuiButtonWrapper offset_y_m;
	private GuiButtonWrapper offset_z_p;
	private GuiButtonWrapper offset_z_m;

    private static int buttonid;
    private static final int buttonidoffset = 0;
    
    int offsetX;
    int offsetY;

    class GUIName{
		String name;
		int x;
		int y;
		int flag;
		int baseID;
		GUIName(String str,int x, int y, editFlag flag, int base){name=str; this.x=x; this.y=y; this.flag = flag.ordinal(); this.baseID = base;}
	}
    Map<Integer, GUIName> GUINameMap = new HashMap<Integer, GUIName>();

	protected void UpdateCameraPosFromCore(Vec3d dest, float tick)
	{
		dest.CopyFrom(rail.Curve().Base);
		dest.CopyFrom(cameraPoint);
	}
	protected boolean CanDrag(int x, int y, int buttonId)
	{
		return 0 < x && x < width-80 && 16 < y && y < height-57;
	}

	public GUIRail(int x, int y, int z, Rail rail)
    {
        super(new DefContainer());
		this.rail = rail;

//		rail.FixConnection();
		BlockRail.selected = rail;

		NBTTagCompound nbt = new NBTTagCompound();
		rail.writeToNBT(nbt);
		shadow = new NbtParamsShadow(nbt);
		gravity = shadow.Create("gravity", rail.Gravity(), v->v::WriteToNBT);
		offset = shadow.Create("", rail.Curve().Base, v->v::WriteToNBT);
	}

	NbtParamsShadow shadow;
	NbtParamsShadow.Class<Vec3d> gravity;
	NbtParamsShadow.Class<Vec3d> offset;

	@Override
	public void initGui()
    {
		super.initGui();
		xSize = width;
		ySize = height;
		guiLeft = 0;
		guiTop = 0;

		currentEditingRail = rail;

		int gDef = -1;
		int gOffset = 1;
		GUINameMap.clear();
		offsetX = 20;
		offsetY = 20;

		offset_x_p = new GuiButtonWrapper(0, 0, 0, 13, 13, "+", () -> SendPacket(editFlag.OFFSET_X, +1));
		offset_x_m = new GuiButtonWrapper(0, 0, 0, 13, 13, "-", () -> SendPacket(editFlag.OFFSET_X, -1));
		offset_y_p = new GuiButtonWrapper(0, 0, 0, 13, 13, "+", () -> SendPacket(editFlag.OFFSET_Y, +1));
		offset_y_m = new GuiButtonWrapper(0, 0, 0, 13, 13, "-", () -> SendPacket(editFlag.OFFSET_Y, -1));
		offset_z_p = new GuiButtonWrapper(0, 0, 0, 13, 13, "+", () -> SendPacket(editFlag.OFFSET_Z, +1));
		offset_z_m = new GuiButtonWrapper(0, 0, 0, 13, 13, "-", () -> SendPacket(editFlag.OFFSET_Z, -1));
		Canvas.Register(gOffset, offset_x_p);
		Canvas.Register(gOffset, offset_x_m);
		Canvas.Register(gOffset, offset_y_p);
		Canvas.Register(gOffset, offset_y_m);
		Canvas.Register(gOffset, offset_z_p);
		Canvas.Register(gOffset, offset_z_m);
		Canvas.ActiveGroup(gOffset);

		GuiUtil.Vec3(_Core.I18n("gui.core.text.offset"), offset, Canvas, fontRenderer,
				2, 120, gDef, 0.1f,
				() -> SendPacket(editFlag.SETOFFSET, offset.Get()));



		switch(cameraMode){
			case 0: cameraPoint.CopyFrom(rail.Curve().Base); break;
			case 1: rail.Curve().PositionAt(cameraPoint, 0.5); break;
			case 2: cameraPoint.CopyFrom(rail.Curve().End); break;
		}
		Canvas.Register(1, new GuiToggleButton(0, 0, 10, 50, 15,
				"Start", "Start",
				() -> cameraMode == 0,
				isOn -> {cameraMode = 0; cameraPoint.CopyFrom(rail.Curve().Base);} ));
		Canvas.Register(1, new GuiToggleButton(0, 0, 30, 50, 15,
				"Center", "Center",
				() -> cameraMode == 1,
				isOn -> {cameraMode = 1; rail.Curve().PositionAt(cameraPoint, 0.5);} ));
		Canvas.Register(1, new GuiToggleButton(0, 0, 50, 50, 15,
				"End", "End",
				() -> cameraMode == 2,
				isOn -> {cameraMode = 2; cameraPoint.CopyFrom(rail.Curve().End);} ));

//		addButton4("Rotation", editFlag.ROTRED);
//        addButton4("", editFlag.ROTGREEN, 14);

		Canvas.Register(gDef, new GuiDragChangerLabel("Twist", fontRenderer, 2, 20, -1,
				i -> {
					rail.UpdateTwist(i*0.05f + rail.Curve().Twist());
					rail.GetController().UpdateRenderer();
					Rail next = rail.GetNextRail();
					if(next != null) next.GetController().UpdateRenderer();
				},
				() -> SendPacket(editFlag.SET_TWIST, (int)(rail.Curve().Twist()*100f))));
		addButton4("", editFlag.ROTBLUE, 14);


//		addButton1(60, 13, "smooth", editFlag.SMOOTH);
		addButton1(60, 13, _Core.I18n("gui.core.text.reset"), editFlag.RESET);

		if(rail.GetPrevRail() != null) {
			this.buttonList.add(new GuiButtonExt(buttonid++, width / 2 - 30 - 50, 0, 60, 13, "PREV"));
			GUINameMap.put(buttonid - 1, new GUIName("", -100, -100, editFlag.PREV, -1));
		}
		if(rail.GetNextRail() != null) {
			this.buttonList.add(new GuiButtonExt(buttonid++, width / 2 - 30 + 50, 0, 60, 13, "NEXT"));
			GUINameMap.put(buttonid - 1, new GUIName("", -100, -100, editFlag.NEXT, -1));
		}

		Canvas.MoveGroup(gDef, width-100, 0, false);

		if(rail instanceof AccelRail)
		{
			AccelRail RAIL = (AccelRail) rail;
			GuiUtil.AddInspector(Canvas, fontRenderer,
					"Accel", gDef, 5, 170, false,
					() -> (float)RAIL.getAccel(),
					RAIL::setAccel,
					0.05f,
					() -> SendPacket(GUIRail.editFlag.SPECIAL, (int) (RAIL.getAccel()*100))
			);
		}
		else if(rail instanceof ConstVelocityRail)
		{
			ConstVelocityRail RAIL = (ConstVelocityRail) rail;
			GuiUtil.AddInspector(Canvas, fontRenderer,
					"Accel", gDef, 5, 170, false,
					() -> (float)RAIL.GetVelocity(),
					RAIL::SetVelocity,
					0.05f,
					() -> SendPacket(GUIRail.editFlag.SPECIAL, (int) (RAIL.GetVelocity()*100))
			);
		}
		else if(rail instanceof AntiGravityRail)
		{
			AntiGravityRail RAIL = (AntiGravityRail) rail;
			GuiUtil.Vec3("Gravity Direction", gravity, Canvas, fontRenderer,
					5, 170, gDef, 0.01f,
					() -> {
						shadow.WriteAll();
						MessageSyncNbtCtS packet = new MessageSyncNbtCtS((TileEntity) rail.GetController(), shadow.GetNbtTag());
						PacketHandler.INSTANCE.sendToServer(packet);
					});
		}
    }
    public void addButton1(int lenx, int leny, String str, editFlag flag)
    {
		offsetY +=17;
		IGuiElement button = new GuiButtonWrapper(0, offsetX, offsetY, lenx, leny, str, ()->SendPacket(flag, 0));
		Canvas.Register(-1, button);

	}
	public void addButton2(String str, editFlag flag)
    {
		offsetY +=27;
		IGuiElement button1 = new GuiButtonWrapper(0, offsetX, offsetY, 18, 13, "-", ()->SendPacket(flag, 0));
		IGuiElement button2 = new GuiButtonWrapper(0, offsetX +42, offsetY, 18, 13, "+", ()->SendPacket(flag, 1));
		Canvas.Register(-1, button1);
		Canvas.Register(-1, button2);
		if(!"".equals(str)) Canvas.Register(-1, new GuiLabel(str, fontRenderer, 5, offsetY -10-guiTop, -1));
	}
	
	public void addButton4(String str, editFlag flag)
	{
		addButton4(str, flag, 27);
	}
    public void addButton4(String str, editFlag flag, int yShift)
    {
    	offsetY +=yShift;
    	IGuiElement button1 = new GuiButtonWrapper(0, offsetX -13 , offsetY, 17, 13, "<<", ()->SendPacket(flag, 0));
		IGuiElement button2 = new GuiButtonWrapper(0, offsetX +6, offsetY, 13, 13, "<", ()->SendPacket(flag, 1));
		IGuiElement button3 = new GuiButtonWrapper(0, offsetX +41, offsetY, 13, 13, ">", ()->SendPacket(flag, 2));
		IGuiElement button4 = new GuiButtonWrapper(0, offsetX +56, offsetY, 17, 13, ">>", ()->SendPacket(flag, 3));
		Canvas.Register(-1, button1);
		Canvas.Register(-1, button2);
		Canvas.Register(-1, button3);
		Canvas.Register(-1, button4);
		if(!"".equals(str)) Canvas.Register(-1, new GuiLabel(str, fontRenderer, 5, offsetY -10-guiTop, -1));
	}
    
    
    @Override
	public void onGuiClosed()
    {
		super.onGuiClosed();
		currentEditingRail = null;
	}
    

	@Override
    public void drawWorldBackground(int p_146270_1_)
    {
		int color = 0xE0101010;
		drawRect(width-100, 0, width, height, color); // bottom

//		int l = width - 100 + 41;
//		int t =  82;
//		drawRect(l, t, l+19, t+11, 0xEEFF0000);
//		drawRect(l, t+14, l+19, t+25, 0xEE00FF00);
//		drawRect(l, t+28, l+19, t+39, 0xEE0000FF);

	}
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        for(GUIName g :  GUINameMap.values())
        {
        	this.fontRenderer.drawString(g.name,g.x,g.y,0xF0F0F0);
        }
	}


	@Override
	public void drawScreen(int x, int y, float partialTick)
	{
		super.drawScreen(x, y, partialTick);

		UpdatePosOfOffsetButtons(offset_x_p, offsetPos.CopyFrom(Vec3d.Left).mul(+3) .add(rail.Curve().Base).sub(cameraPoint), partialTick);
		UpdatePosOfOffsetButtons(offset_x_m, offsetPos.CopyFrom(Vec3d.Left).mul(-3) .add(rail.Curve().Base).sub(cameraPoint), partialTick);
		UpdatePosOfOffsetButtons(offset_y_p, offsetPos.CopyFrom(Vec3d.Up).mul(+3)   .add(rail.Curve().Base).sub(cameraPoint), partialTick);
		UpdatePosOfOffsetButtons(offset_y_m, offsetPos.CopyFrom(Vec3d.Up).mul(-3)   .add(rail.Curve().Base).sub(cameraPoint), partialTick);
		UpdatePosOfOffsetButtons(offset_z_p, offsetPos.CopyFrom(Vec3d.Front).mul(+3).add(rail.Curve().Base).sub(cameraPoint), partialTick);
		UpdatePosOfOffsetButtons(offset_z_m, offsetPos.CopyFrom(Vec3d.Front).mul(-3).add(rail.Curve().Base).sub(cameraPoint), partialTick);
	}


	Vec3d offsetPos = new Vec3d();

	private void UpdatePosOfOffsetButtons(GuiButton button, Vec3d offsetPos, float partialTick)
	{
		double yaw = Math.Lerp(partialTick, mc.player.prevRotationYaw, mc.player.rotationYaw);
		double pitch = Math.Lerp(partialTick, mc.player.prevRotationPitch, mc.player.rotationPitch);
		offsetPos.Rotate(Vec3d.Up, Math.toRadians(yaw));
		offsetPos.Rotate(Vec3d.Right, Math.toRadians(pitch));
		float fov = mc.gameSettings.fovSetting;
		double cot = 1f / java.lang.Math.tan(Math.toRadians(fov * 0.5));
		double compressRatio = cot * (this.width / 4f) / (GetCameraDistance()+offsetPos.z);
		int x = -(int)(offsetPos.x * compressRatio) + this.width / 2 + 4;
		int y = -(int)(offsetPos.y * compressRatio) + this.height / 2 - 3;
		button.x = x-7;
		button.y = y-7;
	}

 
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
	}
	@Override
	protected void actionPerformed(GuiButton button) {
		GUIName obj = GUINameMap.get(button.id);
		switch(GUIRail.editFlag.values()[obj.flag])
		{
			case PREV:
				Rail prev = rail.GetPrevRail();
				if(prev == null) return;
				int x = (int)prev.GetController().CorePosX();
				int y = (int)prev.GetController().CorePosY();
				int z = (int)prev.GetController().CorePosZ();
				ERC_PacketHandler.INSTANCE.sendToServer(new MessageOpenGuiCtS(x,y,z));
				return;
			case NEXT:
				Rail next = rail.GetNextRail();
				if(next == null) return;
				x = (int)next.GetController().CorePosX();
				y = (int)next.GetController().CorePosY();
				z = (int)next.GetController().CorePosZ();
				ERC_PacketHandler.INSTANCE.sendToServer(new MessageOpenGuiCtS(x,y,z));
				return;
		}
		int data = (button.id - obj.baseID);
		ERC_MessageRailGUICtS packet = new ERC_MessageRailGUICtS(rail.GetController(), obj.flag, data);
		ERC_PacketHandler.INSTANCE.sendToServer(packet);
	}

	public void SendPacket(editFlag flag, int data)
	{
		ERC_MessageRailGUICtS packet = new ERC_MessageRailGUICtS(rail.GetController(), flag.ordinal(), data);
		ERC_PacketHandler.INSTANCE.sendToServer(packet);
	}
	public void SendPacket(editFlag flag, Vec3d v)
	{
		ERC_MessageRailGUICtS packet = new ERC_MessageRailGUICtS(rail.GetController(), flag.ordinal(), v);
		ERC_PacketHandler.INSTANCE.sendToServer(packet);
	}
}
