package jp.mochisystems.erc._mc.tileentity;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.erc._mc.item.ItemWrench;
import jp.mochisystems.erc._mc.network.MessageSyncRailOptionStC;
import jp.mochisystems.erc._mc.network.ERC_PacketHandler;
import jp.mochisystems.erc.loader.ModelPackLoader;
import jp.mochisystems.erc.rail.*;
import jp.mochisystems.erc.renderer.RailCoreRenderer;
import jp.mochisystems.erc.renderer.rail.BlockModelRailRenderer;
import jp.mochisystems.erc.renderer.rail.DefaultRailRenderer;
import jp.mochisystems.erc.renderer.rail.IRailRenderer;
import jp.mochisystems.erc.renderer.rail.PackModelRailRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public abstract class TileEntityRail extends TileEntity implements IRailController {

	protected abstract Rail GetRailInstance();
	public abstract IBlockState GetBlockStateForTexture();

	protected Rail rail;
	public Rail getRail() { return rail; }

	public String packModelID = "";
	protected ItemStack modelStack = ItemStack.EMPTY;
//	public void StoreModelStack(ItemStack stack)
//	{
//		modelStack = stack;
//	}
	public void SpawnModelStack()
	{
		if(modelStack.isEmpty())
			InventoryHelper.spawnItemStack(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, modelStack);
	}

	private boolean isActive = false;
	public boolean IsActive()
	{
		return isActive;
	}
	public void SetActive(boolean active)
	{
		isActive = active;
	}

	protected IRailRenderer railRenderer;
	protected RailCoreRenderer coreRenderer;
	public void SetRenderer(ItemStack stack)
	{
		modelStack = stack;
		if(!stack.isEmpty()) modelStack.setCount(1);
	}
	public void SetRenderer(IRailRenderer renderer)
	{
		if(_Core.proxy.checkSide().isServer()) return;
		if(this.railRenderer != null) {
			this.railRenderer.DeleteBuffer();
		}

		renderer.SetRail(this.rail);
		this.railRenderer = renderer;
		this.railRenderer.SetDirty();
	}


	@Override
	public World World(){
		return world;
	}


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
	public EnumFacing CoreSide()
	{
		return EnumFacing.UP;
	}

	@Override
	public Rail GetRail(int x, int y, int z)
	{
		if(World() == null) return null;
		TileEntityRail tile = (TileEntityRail)World().getTileEntity(new BlockPos(x, y, z));
		if(tile == null) return null;
		return tile.getRail();
	}
    @Override
    public boolean IsInvalid()
    {
        return isInvalid();
    }

	@Override
    public boolean IsRemote()
    {
        return World().isRemote;
    }

	@Override
    public void markBlockForUpdate()
    {
		markDirty();
		IBlockState state = World().getBlockState(pos);
		World().notifyBlockUpdate(pos, state, state, 3);
    }

    public TileEntityRail()
	{
		this.rail = GetRailInstance();
		this.rail.SetController(this);

		this.railRenderer = new DefaultRailRenderer(GetBlockStateForTexture());
		this.railRenderer.SetRail(this.rail);
		this.railRenderer.SetDirty();

		this.coreRenderer = new RailCoreRenderer(GetBlockStateForTexture());
		this.coreRenderer.SetDirty();
	}

	public void InitRail(Vec3d pos, Vec3d dir, Vec3d up)
	{
		rail.Curve().Init(pos, dir, up, 2);
	}


	@Override
	public double getMaxRenderDistanceSquared()
	{
		return Double.MAX_VALUE;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void invalidate()
	{
		super.invalidate();

		Rail prev = rail.GetPrevRail();
		if(prev!=null)prev.SetNextRail(null);
		Rail next = rail.GetNextRail();
		if(next!=null)next.SetPrevRail(null);
		rail.Break();
		if(railRenderer != null) railRenderer.DeleteBuffer();
		if(coreRenderer != null) coreRenderer.DeleteBuffer();
	}

	@Override
	public void onChunkUnload()
	{
//		rail.Break();
//		rail.Unlink();
	}

	@Override
	public void onLoad()
	{

	}


	@Override
	public void SyncData()
	{
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
	}

	@Override
	public void SyncMiscData()
	{
		MessageSyncRailOptionStC packet = new MessageSyncRailOptionStC(rail);
		ERC_PacketHandler.INSTANCE.sendToAll(packet);
	}

	@Override
	public void UpdateRenderer()
	{
		if(IsRemote()) {
			rail.Curve().Construct();
			railRenderer.SetDirty();
		}
		else SyncData();
	}

	@Override
	public void NotifyChange()
	{
		World world = World();
		Block block = world.getBlockState(pos).getBlock();
		world.notifyNeighborsOfStateChange(pos, block, true);
		world.notifyNeighborsOfStateChange(pos.down(), block, true);
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PRESSPLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound tag)
	{
        super.readFromNBT(tag);
		rail.readFromNBT(tag);
		SetActive(tag.getBoolean("active"));
		// For BlockModel
		if(tag.hasKey("modelStack")){
			packModelID = "";
			modelStack = new ItemStack(tag.getCompoundTag("modelStack"));
			NBTTagCompound nbt = modelStack.getTagCompound().getCompoundTag("model");
			BlockModelRailRenderer renderer = new BlockModelRailRenderer(nbt, pos.getX(), pos.getY(), pos.getZ());
			SetRenderer(renderer);
			if(world != null) renderer.Construct(world);
		}
		// For MeshModel
		if(tag.hasKey("packModelID")){
			modelStack = ItemStack.EMPTY;
			packModelID = tag.getString("packModelID");
			IBakedModel model = ModelPackLoader.Instance.GetRailModelById(packModelID);
			ResourceLocation texture = ModelPackLoader.Instance.GetRailTextureById(packModelID);
			if(model != null && texture != null)
				SetRenderer(new PackModelRailRenderer(model, texture));
		}
		else if(!packModelID.isEmpty()){
			packModelID = "";
			SetRenderer(new DefaultRailRenderer(GetBlockStateForTexture()));
		}

		if(world!=null) UpdateRenderer();
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag)
	{
        super.writeToNBT(tag);
		if(rail != null) rail.writeToNBT(tag);
		tag.setBoolean("active", IsActive());
		if(!modelStack.isEmpty()) tag.setTag("modelStack", modelStack.serializeNBT());
		if(packModelID!=null && !packModelID.isEmpty())
			tag.setString("packModelID", packModelID);
		else{
			packModelID = "";
			tag.removeTag("packModelID");
		}
		return tag;
	}

	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag()
	{
		NBTTagCompound nbt = super.getUpdateTag();
		this.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket(){
		NBTTagCompound nbtTag = new NBTTagCompound();
		writeToNBT(nbtTag);
		return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
	}

	@Override
	public void onDataPacket(@Nonnull NetworkManager net, SPacketUpdateTileEntity pkt){
		NBTTagCompound tag = pkt.getNbtCompound();
		readFromNBT(tag);
	}


	public void Render()
	{
		coreRenderer.Render();
		railRenderer.Render();
	}

	@Nonnull
    @Override
	public IModel GetModel()
	{
		return null;
	}


	public static class Normal extends TileEntityRail{
		@Override
		public Rail GetRailInstance()
		{
			return new Rail();
		}
		@Override
		public IBlockState GetBlockStateForTexture()
		{
			return Blocks.IRON_BLOCK.getDefaultState();
		}
	}

	public static class Accel extends TileEntityRail{
		@Override
		public Rail GetRailInstance()
		{
			return new AccelRail();
		}
		@Override
		public IBlockState GetBlockStateForTexture()
		{
			return Blocks.REDSTONE_BLOCK.getDefaultState();
		}
		@Override
		public void Render()
		{
			if(IsActive()) GlStateManager.color(1f, 1f, 1f);
			else GlStateManager.color(0.3f,0.3f, 0.3f);
			super.Render();
		}
	}

	public static class Const extends TileEntityRail{
		@Override
		public Rail GetRailInstance()
		{
			return new ConstVelocityRail();
		}
		public IBlockState GetBlockStateForTexture()
		{
			return Blocks.OBSIDIAN.getDefaultState();
		}
		@Override
		public void Render()
		{
			if(IsActive()) GlStateManager.color(1f, 1f, 1f);
			else GlStateManager.color(0.3f,0.3f, 0.3f);
			super.Render();
		}
	}

	public static class Detector extends TileEntityRail{
		@Override
		public Rail GetRailInstance()
		{
			return new DetectorRail();
		}
		public IBlockState GetBlockStateForTexture()
		{
			return Blocks.QUARTZ_BLOCK.getDefaultState();
		}
	}


	public static class Invisible extends TileEntityRail{
		@Override
		public Rail GetRailInstance()
		{
			return new Rail();
		}
		public IBlockState GetBlockStateForTexture()
		{
			return Blocks.GLASS.getDefaultState();
		}
		@Override
		public void Render()
		{
			EntityPlayer player = Minecraft.getMinecraft().player;
			if(player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemWrench)
			{
				coreRenderer.Render();
				railRenderer.Render();
			}
		}
	}


	public static class AntiGravity extends TileEntityRail{
		@Override
		public Rail GetRailInstance()
		{
			return new AntiGravityRail();
		}
		public IBlockState GetBlockStateForTexture()
		{
			return Blocks.PORTAL.getDefaultState();
		}
	}



	public static class Branch extends TileEntityRail{
		private IRailRenderer anotherRailRenderer;
		private final Rail sideRail;

		public Branch()
		{
			rail = GetRailInstance();
			sideRail = GetRailInstance();

			rail.SetController(this);
			sideRail.SetController(this);

			((BranchRail)rail).SetSide((BranchRail)sideRail, true);
			((BranchRail)sideRail).SetSide((BranchRail)rail, false);

			TileEntityRail normal = new Normal();
			railRenderer = new DefaultRailRenderer(normal.GetBlockStateForTexture());
			railRenderer.SetRail(rail);
			railRenderer.SetDirty();
			anotherRailRenderer = new DefaultRailRenderer(normal.GetBlockStateForTexture());
			anotherRailRenderer.SetRail(sideRail);
			anotherRailRenderer.SetDirty();

			coreRenderer = new RailCoreRenderer(GetBlockStateForTexture());
			coreRenderer.SetDirty();
		}

		@Override
		public void InitRail(Vec3d pos, Vec3d dir, Vec3d up)
		{
			rail.Curve().Init(pos, dir, up, 2);
			Vec3d side = up.New().cross(dir).normalize().mul(4);

			SetActive(false);

			RailCurve c = sideRail.Curve();
			sideRail.Curve().Init(pos, dir, up, 2);
			c.Next.add(side);
			c.Construct();
		}

		@Override
		public Rail GetRailInstance()
		{
			return new BranchRail();
		}
		@Override
		public Rail getRail() {
			return IsActive() ? sideRail : rail;
		}


		@Override
		public IBlockState GetBlockStateForTexture()
		{
			return Blocks.LAPIS_BLOCK.getDefaultState();
		}
		@Override
		public void SetRenderer(IRailRenderer renderer)
		{
			if(_Core.proxy.checkSide().isServer()) return;
			if(IsActive()){
				if(this.railRenderer != null) this.railRenderer.DeleteBuffer();
				renderer.SetRail(this.rail);
				this.railRenderer = renderer;
				this.railRenderer.SetDirty();
			}
			else {
				if(anotherRailRenderer != null) anotherRailRenderer.DeleteBuffer();
				renderer.SetRail(this.sideRail);
				anotherRailRenderer = renderer;
				anotherRailRenderer.SetDirty();
			}
		}
		@Override
		public void UpdateRenderer()
		{
			if(IsRemote()) {
//				dir.CopyFrom(rail.Curve().Base).sub(rail.Curve().Prev).normalize();
//						.cross(rail.Curve().p0Up).normalize();
				rail.DirectionAt(dir, 0).cross(rail.Curve().p0Up).normalize();
				railRenderer.SetDirty();
				anotherRailRenderer.SetDirty();
			}
			else SyncData();
		}
		@Override
		public void invalidate()
		{
			super.invalidate();
			Rail prev = sideRail.GetPrevRail();
			if(prev!=null)prev.SetNextRail(null);
			Rail next = sideRail.GetNextRail();
			if(next!=null)next.SetPrevRail(null);
			sideRail.Break();
			if(anotherRailRenderer != null) anotherRailRenderer.DeleteBuffer();
		}
		@Override
		public void SyncMiscData()
		{
			super.SyncMiscData();
			MessageSyncRailOptionStC packet = new MessageSyncRailOptionStC(sideRail);
			ERC_PacketHandler.INSTANCE.sendToAll(packet);
		}

		final static float slideLength = 2f;
		final static float railMoveSpeed = 0.1f;
		float slidePosition = 0;
		float railPositionPrev = 0;
		Vec3d dir = new Vec3d();
		@Override
		public void Render()
		{
			coreRenderer.Render();

			if(IsActive() && slidePosition < slideLength){
				railPositionPrev = slidePosition;
				slidePosition += railMoveSpeed;
				if(slidePosition >= slideLength) railPositionPrev = slidePosition = slideLength;
			}
			else if(!IsActive() && slidePosition > 0){
				railPositionPrev = slidePosition;
				slidePosition -= railMoveSpeed;
				if(slidePosition <= 0) railPositionPrev = slidePosition = 0;
			}

			GlStateManager.pushMatrix();

//			GlStateManager.translate(0, -0.5, 0);
//			GL11.glRotated(90*slidePosition, dir.x, dir.y, dir.z);
//			GlStateManager.translate(0, 0.5, 0);
			GlStateManager.translate(dir.x * slidePosition, dir.y * slidePosition, dir.z * slidePosition);
			railRenderer.Render();
//			GL11.glRotated(180, dir.x, dir.y, dir.z);
//			GlStateManager.translate(0, 1, 0);
			GlStateManager.translate(-dir.x * slideLength, -dir.y * slideLength, -dir.z * slideLength);
			anotherRailRenderer.Render();
			GlStateManager.popMatrix();
		}
		@Override
		public void readFromNBT(@Nonnull NBTTagCompound nbt) {
			super.readFromNBT(nbt);
			sideRail.readFromNBT(nbt.getCompoundTag("siderail"));
			railPositionPrev = slidePosition = IsActive() ? slideLength : 0;
		}
		@Nonnull
		@Override
		public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt)
		{
			super.writeToNBT(nbt);
			NBTTagCompound side = new NBTTagCompound();
			sideRail.writeToNBT(side);
			nbt.setTag("siderail", side);
			return nbt;
		}
	}
}
