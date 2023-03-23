package jp.mochisystems.erc._mc.tileentity;

import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.core.blockcopier.BlocksScanner;
import jp.mochisystems.core.blockcopier.IBLockCopyHandler;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc.coaster.CoasterSettings;
import jp.mochisystems.erc.loader.ModelPackLoader;
import jp.mochisystems.erc.rail.Rail;
import jp.mochisystems.erc.rail.IRailController;
import jp.mochisystems.erc.renderer.RailCoreRenderer;
import jp.mochisystems.erc.renderer.rail.DefaultRailRenderer;
import jp.mochisystems.erc.renderer.rail.ERCBlocksScanner;
import jp.mochisystems.erc.renderer.rail.IRailRenderer;
import jp.mochisystems.erc.renderer.rail.PackModelRailRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class TileEntityCoasterModelConstructor extends TileEntityBlocksScannerBase implements IRailController {

    public IModel blockModel = null;
    public Rail rail;

    protected ItemStack modelStack = ItemStack.EMPTY;
    public void StoreModelStack(ItemStack stack)
    {
        modelStack = stack;
    }

    public String packModelID = "";
    protected IRailRenderer railRenderer;
    protected RailCoreRenderer coreRenderer;
    public void SetRenderer(IRailRenderer renderer)
    {
        if(this.railRenderer != null) this.railRenderer.DeleteBuffer();
        renderer.SetRail(this.rail);
        this.railRenderer = renderer;
        this.railRenderer.SetDirty();
    }
    public void Render()
    {
        coreRenderer.Render();
        railRenderer.Render();
    }
    @Override
    public void UpdateRenderer()
    {
        if(IsRemote()) railRenderer.SetDirty();
        else SyncData();
    }

    // model value
    public final Vec3d modelScale = new Vec3d(1, 1, 1);
    public final Vec3d modelOffset = new Vec3d(0, 0, 0);
    public final Quaternion modelRotate = new Quaternion();
    public final Vec3d registeredModelScale = new Vec3d(1, 1, 1);
    public final Vec3d registeredModelOffset = new Vec3d(0, 0, 0);
    public final Quaternion registeredModelRotate = new Quaternion();

    public int seatNum = 1;
    public CoasterSettings coasterSettings = CoasterSettings.Default();

    public TileEntityCoasterModelConstructor() {
        super();

        rail = new Rail();
        rail.SetController(this);
        coreRenderer = new RailCoreRenderer(Blocks.IRON_BLOCK.getDefaultState());
        railRenderer = new DefaultRailRenderer(Blocks.IRON_BLOCK.getDefaultState());
        Vec3d dir = new Vec3d(0, 0, 1);
        Vec3d up = new Vec3d(0, 1, 0);
        rail.Curve().Init(new Vec3d(0.5, 0.5, -4.5), dir, up, 10);
        rail.Curve().SetPointNum(5);
        rail.Curve().Construct();
        railRenderer.SetRail(rail);
        railRenderer.SetDirty();
    }

    @Override
    public void Init(EnumFacing side)
    {
        super.Init(side);
        limitFrame.SetLimit(new Vec3i(1, 0, 0), new Vec3i(1, 0, 0));
        limitFrame.SetReset(new Vec3i(1, 0, 0), new Vec3i(3, 2, 2));

        limitFrame.Reset();
    }

    @Override
    protected BlocksScanner InstantiateBlocksCopier(IBLockCopyHandler handler) {
        return new ERCBlocksScanner(handler);
    }

    public void ChangeSeatNum(int num) {
        if(seatNum == num) return;

        CoasterSettings.SeatData[] olds = coasterSettings.Seats;
        seatNum = num;

        coasterSettings.setSeatNum(num);
        int newCount = Math.min(olds.length, num);
        if (newCount >= 0) System.arraycopy(olds, 0, coasterSettings.Seats, 0, newCount);
        if(num > olds.length)
        {
            Vec3d v = new Vec3d();
            if(olds.length-1 >= 0) {
                v.CopyFrom(coasterSettings.Seats[olds.length-1].LocalPosition);
                v.add(Vec3d.Left);
            }
            for(int i = olds.length; i < num; ++i) {
                coasterSettings.Seats[i].LocalPosition.CopyFrom(v);
                v.add(Vec3d.Left);
            }
        }
    }

    private void DeleteModelBuffer() {
        if (blockModel != null) blockModel.Invalidate();
        blockModel = null;
        registeredModelScale.CopyFrom(Vec3d.One);
        registeredModelOffset.CopyFrom(Vec3d.Zero);
        registeredModelRotate.Identity();
    }

//    @Override
//    public void readFromNBT(NBTTagCompound nbt) {
//        super.readFromNBT(nbt);
//        ReadParamFromNBT(nbt);
//    }
    @Override
    public void ReadParamFromNBT(NBTTagCompound nbt) {
        super.ReadParamFromNBT(nbt);
        modelScale.ReadFromNBT("modelScale", nbt);
        modelOffset.ReadFromNBT("modelOffset", nbt);
        modelRotate.ReadFromNBT("modelTilt", nbt);

        coasterSettings.FromString(nbt.getString("coasterSettings"));
        seatNum = nbt.getInteger("seatNum");
        ChangeSeatNum(seatNum);

        if (stackSlot != null && stackSlot.hasTagCompound()){
            NBTTagCompound modelNbt = stackSlot.getTagCompound();
            assert modelNbt != null;
            NBTTagCompound partNbt = modelNbt.getCompoundTag("model");
            registeredModelScale.ReadFromNBT("scale", partNbt);
            registeredModelOffset.ReadFromNBT("offset", partNbt);
            registeredModelRotate.ReadFromNBT("tilt", partNbt);
            registeredModelRotate.Inverse();
        }

        if(nbt.hasKey("packModelID")){
            packModelID = nbt.getString("packModelID");
            IBakedModel model = ModelPackLoader.Instance.GetRailModelById(packModelID);
            ResourceLocation texture = ModelPackLoader.Instance.GetRailTextureById(packModelID);
            if(model != null && texture != null)
                SetRenderer(new PackModelRailRenderer(model, texture));
        }
        else if(!packModelID.isEmpty()){
            packModelID = "";
            SetRenderer(new DefaultRailRenderer(Blocks.IRON_BLOCK.getDefaultState()));
        }
    }

    @Override
    public NBTTagCompound WriteParamToNBT(NBTTagCompound nbt) {
        super.WriteParamToNBT(nbt);
        modelScale.WriteToNBT("modelScale", nbt);
        modelOffset.WriteToNBT("modelOffset", nbt);
        modelRotate.WriteToNBT("modelTilt", nbt);

        nbt.setInteger("seatNum", seatNum);
        nbt.setString("coasterSettings", coasterSettings.toString());

        if (!stackSlot.isEmpty()) {
            NBTTagCompound root = stackSlot.getTagCompound();
            if (blockModel == null) blockModel = ((IItemBlockModelHolder) stackSlot.getItem()).GetBlockModel(this);
        }
        if(packModelID!=null && !packModelID.isEmpty())
            nbt.setString("packModelID", packModelID);
        else{
            packModelID = "";
            nbt.removeTag("packModelID");
        }

        return nbt;
    }

    @Override
    protected boolean isExistCore()
	{
		if(stackSlot.isEmpty()) return false;
		return stackSlot.getItem() instanceof IItemBlockModelHolder;
	}

    @Override
    public void update()
    {
        if (!world.isRemote) return;
//        scanner.UpdateProgressStatus();
        if(stackSlot.isEmpty() && blockModel != null) DeleteModelBuffer();
        if(blockModel != null) blockModel.Update();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        DeleteModelBuffer();
        rail.Break();
        railRenderer.DeleteBuffer();
        coreRenderer.DeleteBuffer();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void startScanning()
    {
        super.startScanning();
    }

    public void registerTransformAndSeatData()
    {
//        NBTTagCompound nbt = stackSlot.getTagCompound();
//        NBTTagCompound model = nbt.getCompoundTag("model");
//        int x = model.getInteger("mtybr:sizex");
//        int y = model.getInteger("mtybr:sizey");
//        int z = model.getInteger("mtybr:sizez");
//        coasterSettings.ModelID = "__BLOCKMODEL";
//        coasterSettings.Height = (float) (y * modelScale.y);
//        coasterSettings.Width = (float) (Math.max(x, z) * Math.max(modelScale.x, modelScale.z));
//        nbt.setString("coasterSettings", coasterSettings.toString());
    }

    @Override
    public void registerExternalParam(NBTTagCompound model, NBTTagCompound nbt)
    {
        super.registerExternalParam(model, nbt);

        // => CoasterModel
        modelScale.WriteToNBT("scale", model);
        modelOffset.New().add(0.5, 0, 0.5).WriteToNBT("offset", model);
        modelRotate.WriteToNBT("tilt", model);
        int x = limitFrame.lenX();
        int y = limitFrame.lenY();
        int z = limitFrame.lenZ();
        coasterSettings.ModelID = "__BLOCKMODEL";
        coasterSettings.Weight = (float) (scanner.blockCount * 100 * modelScale.x * modelScale.y * modelScale.z);
        coasterSettings.Height = (float) (y * modelScale.y);
        coasterSettings.Width = (float) (Math.max(x, z) * Math.max(modelScale.x, modelScale.z));
        nbt.setString("coasterSettings", coasterSettings.toString());
    }
    @Override
    public Vec3d GetOriginLocalOffset(){
        double d = (TrueCopy ? 1 : 0);
        return new Vec3d(
            (limitFrame.lenX()/2d) - d,
            (limitFrame.lenY()/2d) - d,
            (limitFrame.lenZ()/2d) - d);
    }


    @Override
    public void OnCompleteReceive(NBTTagCompound nbt, EntityPlayer player)
    {
        super.OnCompleteReceive(nbt, player);
        registerTransformAndSeatData();
        setInventorySlotContents(0, stackSlot);
    }


    @Override
    public ItemStack InstantiateModelItem() {
        return new ItemStack(ERC.itemCoasterModel);
    }

    //////////// IBLockCopyHandler end ///////////


    ////////////// IModelController
    @Override
    public double CorePosX() {
        return pos.getX();
    }

    @Override
    public double CorePosY() {
        return pos.getY();
    }

    @Override
    public double CorePosZ() {
        return pos.getZ();
    }

    @Override
    public EnumFacing CoreSide() {
        return side;
    }

    @Override
    public boolean IsInvalid() {
        return isInvalid();
    }

    @Override
    public boolean IsRemote() {
        return world.isRemote;
    }

    @Override
    public World World() {
        return world;
    }

    @Override
    public void markBlockForUpdate() {
        markDirty();
        IBlockState state = World().getBlockState(pos);
        World().notifyBlockUpdate(pos, state, state, 3);
    }

    @Nonnull
    @Override
    public IModel GetModel() {
        return blockModel;
    }



    ////// for IModel

    @Override
    public void setWorld(@Nonnull World world) {
        super.setWorld(world);
        if(blockModel!=null)blockModel.SetWorld(world);
    }


    @Override
    public void onChunkUnload() {
        if(blockModel!=null)blockModel.Unload();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slotIdx, ItemStack itemstack)
    {
        super.setInventorySlotContents(slotIdx, itemstack);
        if(stackSlot.isEmpty()) return;
        if(blockModel != null) DeleteModelBuffer();
        IItemBlockModelHolder itemModel = (IItemBlockModelHolder)stackSlot.getItem();
        blockModel = itemModel.GetBlockModel(this);
        NBTTagCompound nbt = stackSlot.getTagCompound();
//            itemModel.OnSetInventory(blockModel, 0, itemstack, ChangeUser);
        blockModel.Reset();
        blockModel.readFromNBT(nbt);
        blockModel.SetWorld(world);

        NBTTagCompound model = nbt.getCompoundTag("model");
        registeredModelScale.ReadFromNBT("scale", model);
        registeredModelOffset.ReadFromNBT("offset", model);
        registeredModelRotate.ReadFromNBT("tilt", model);
        registeredModelRotate.Inverse();
        coasterSettings.FromString(nbt.getString("coasterSettings"));
        seatNum = coasterSettings.Seats.length;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

//    @Override
//    public void clear() {
//
//    }

    ////////////// IRailController

    @Override
    public Rail GetRail(int x, int y, int z) {
        return rail;
    }

    @Override
    public void NotifyChange() {

    }

    @Override
    public void SyncData() {

    }

    @Override
    public void SyncMiscData() {

    }

    @Override
    public boolean IsActive(){ return false; }
}
