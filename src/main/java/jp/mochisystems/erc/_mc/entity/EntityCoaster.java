package jp.mochisystems.erc._mc.entity;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.eventhandler.TickEventHandler;
import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.manager.RollingSeatManager;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.*;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc.item.ItemCoaster;
import jp.mochisystems.erc._mc.item.ItemCoasterModel;
import jp.mochisystems.erc._mc.item.itemSUSHI;
import jp.mochisystems.erc._mc.network.ERC_PacketHandler;
import jp.mochisystems.erc._mc.network.MessageAccelCoaster;
import jp.mochisystems.erc._mc.network.MessageSendModelDataStC;
import jp.mochisystems.erc._mc.network.MessageSyncCoasterPosStC;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc.coaster.Coaster;
import jp.mochisystems.erc.coaster.CoasterSettings;
import jp.mochisystems.erc.coaster.Seat;
import jp.mochisystems.erc.loader.ModelPackLoader;
import jp.mochisystems.erc.model.MeshModel;
import jp.mochisystems.erc.rail.IRailController;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;


public class EntityCoaster extends Entity implements IModelController, Coaster.ICoasterController, IRollSeat, IEntityMultiPart {

	private final Coaster coaster;
	private IModel model;

	private float prevInputForward;

	public Coaster GetCoaster(){ return coaster; }

	public EntityCoaster(World worldIn)
	{
		super(worldIn);
		this.coaster = new Coaster(this);
		this.setSize(1.4f, 1.4f);
		setRenderDistanceWeight(Double.MAX_VALUE);
	}


	@Override
	public void setWorld(@Nonnull World world) {
		super.setWorld(world);
		model.SetWorld(world);
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox()
	{
		return null; //this.getEntityBoundingBox();
	}

//	@Override
//	public AxisAlignedBB getEntityBoundingBox()
//	{
//		return super.getEntityBoundingBox();
//	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn)
	{
		return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
	}

	@Override @Nonnull
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, @Nonnull EnumHand hand)
	{
		if (player.isSneaking() && model != null) {
			if(player.world.isRemote)
				GUIHandler.OpenBlockModelGuiInClient(model);
			return false;
		}

		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty()) {
			if(stack.getItem() instanceof ItemCoasterModel && !IsRemote())
			{
				ChangeModel(stack); //コースター用ブロックモデルは設計図扱いにしたいのでアイテム消費しない
			}
			else if(stack.getItem() instanceof IItemBlockModelHolder && !IsRemote())
			{
				ChangeModel(stack.splitStack(1));
				player.setHeldItem(hand, stack.copy());
				return true;
			}
			else if(stack.getItem() instanceof ItemCoaster && !IsRemote())
			{
				EntityCoaster entityConnect = new EntityCoaster(world);
				entityConnect.setLocationAndAngles(posX, posY, posZ, 0, 0);
				world.spawnEntity(entityConnect);

				Coaster connectCoaster = entityConnect.GetCoaster();
				Coaster.InitForConnectToSpawnedParent(connectCoaster, this.GetCoaster(), ItemCoaster.GetModelId(stack));
//				ERC_PacketHandler.INSTANCE.sendToAll(new MessageRequestConnectingCoasterStC(this, entityConnect));
				return true;
			}
			else if(stack.getItem() instanceof ItemLead && !IsRemote())
			{
				if(!this.world.isRemote) {
					for (EntityLiving entityliving : world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB( player.posX - 7.0D,  player.posY - 7.0D,  player.posZ - 7.0D, player.posX + 7.0D,  player.posY + 7.0D, player.posZ + 7.0D))) {
						if (entityliving.getLeashed() && entityliving.getLeashHolder() == player) {
							entityliving.startRiding(this);
						}
					}
				}
				return true;
			}
			else if(stack.getItem() instanceof ItemMonsterPlacer) {
				if(!this.world.isRemote) {
					Entity entity = ItemMonsterPlacer.spawnCreature(world, ItemMonsterPlacer.getNamedIdFrom(stack), posX, posY, posZ);
					if (entity != null) entity.startRiding(this);
				}
				return true;
			}
			else if(stack.getItem() instanceof itemSUSHI) {
				if(!this.world.isRemote) {
					Entity e = new EntitySUSHI(world,posX,posY,posZ);
					world.spawnEntity(e);
					if (e != null) e.startRiding(this);
				}
			}
		}

		if (!this.world.isRemote)
		{
			CoasterSettings.SeatData[] seats = coaster.GetSettings().Seats;
			if(seats.length > 0){
				double d0 = Minecraft.getMinecraft().playerController.getBlockReachDistance();
				net.minecraft.util.math.Vec3d playerPos = player.getPositionEyes(1);
				net.minecraft.util.math.Vec3d look = player.getLook(1.0F);
				net.minecraft.util.math.Vec3d handDir = playerPos.addVector(look.x * d0, look.y * d0, look.z * d0);
				for(int i=0; i < seats.length; ++i)
				{
					Seat seat = coaster.GetSeat(i);
					Vec3d p = seat.pos;
					AxisAlignedBB aabb = new AxisAlignedBB(p.x-0.5, p.y-0.5, p.z-0.5, p.x+0.5, p.y+0.5, p.z+0.5);
					if (aabb.contains(player.getPositionEyes(0)))
					{
						reservedRidingSeatIndex = i;
						player.startRiding(this);
						return true;
					}
					RayTraceResult rayResult = aabb.calculateIntercept(playerPos, handDir);
					if(rayResult != null)
					{
						reservedRidingSeatIndex = i;
						player.startRiding(this);
						return true;
					}
				}
			}
		}
		return true;
	}

	@Override
	protected boolean canFitPassenger(@Nonnull Entity passenger)
	{
		return this.getPassengers().size() < coaster.GetSettings().Seats.length;
	}

	Entity[] passengerSeats = new Entity[0];
	static int reservedRidingSeatIndex;
	@Override
	protected void addPassenger(@Nonnull Entity passenger)
	{
		LoadSettings();
		if(passengerSeats[reservedRidingSeatIndex] == null)
		{
			passengerSeats[reservedRidingSeatIndex] = passenger;
		}
		else return;

		ERC.proxy.OnRideCoaster(passenger, this);
		passenger.rotationYaw = 0;
		passenger.rotationPitch = 0;
		super.addPassenger(passenger);
	}

	@Override
	public void removePassengers()
	{
		Arrays.fill(passengerSeats, null);
		super.removePassengers();
	}

	@Override
	protected void removePassenger(@Nonnull Entity passenger)
	{
//		for(int i = 0; i < passengerSeats.length; ++i)
//		{
//			if(passengerSeats[i] == passenger)
//			{
//				passengerSeats[i] = null;
//				break;
//			}
//		}
		super.removePassenger(passenger);
		if(world.isRemote && Minecraft.getMinecraft().player == passenger)
			RollingSeatManager.ResetAngles();
	}


	@Override
	public boolean isPassenger(@Nonnull Entity in)
	{
		for(Entity e : passengerSeats) if(in.equals(e)) return true;
		return false;
	}

	@Nullable
	@Override
	public Entity getControllingPassenger()
	{
		return null;
	}

	@Override
	protected boolean canTriggerWalking()
    {
        return false;
    }

	@Override
	protected void entityInit()
    {
		dataManager.register(SETTINGS, "");
		dataManager.register(COASTER_ID, -1);
		dataManager.register(PARENT_ID, -1);
		dataManager.register(RAIL_POS_X, 0);
		dataManager.register(RAIL_POS_Y, 0);
		dataManager.register(RAIL_POS_Z, 0);
//		dataManager.register(RAIL_POS_T, 0f);
//		dataManager.register(SPEED, 0f);
		dataManager.register(CUSTOM_BLOCK_MODEL, ItemStack.EMPTY);
	}
	private static final DataParameter<Integer> COASTER_ID = EntityDataManager.createKey(EntityCoaster.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> PARENT_ID = EntityDataManager.createKey(EntityCoaster.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> RAIL_POS_X = EntityDataManager.createKey(EntityCoaster.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> RAIL_POS_Y = EntityDataManager.createKey(EntityCoaster.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> RAIL_POS_Z = EntityDataManager.createKey(EntityCoaster.class, DataSerializers.VARINT);
//	private static final DataParameter<Float> RAIL_POS_T = EntityDataManager.createKey(EntityCoaster.class, DataSerializers.FLOAT);
//	private static final DataParameter<Float> SPEED = EntityDataManager.createKey(EntityCoaster.class, DataSerializers.FLOAT);
	private static final DataParameter<String> SETTINGS = EntityDataManager.createKey(EntityCoaster.class, DataSerializers.STRING);
	private static final DataParameter<ItemStack> CUSTOM_BLOCK_MODEL = EntityDataManager.createKey(EntityCoaster.class, DataSerializers.ITEM_STACK);


	@Override
	public boolean canBeCollidedWith()
	{
		return !this.isDead;
	}

	@Override
	public void setDead() // <= Entity
	{
		SetDead();
	}

	@Override
    public void SetDead() // <= ICoasterController
    {
		super.setDead();
        coaster.Destroy();
		if(model!=null && !IsRemote()){
			NBTTagCompound nbt = modelStack.getTagCompound();
			model.writeToNBT(nbt);
			modelStack.setTagCompound(nbt);
			entityDropItem(modelStack, 0.5f);

			model.Invalidate();
		}

	}

	@Override
	public void OnLostRail()
	{
		if(!IsRemote()) {
			ItemStack stack = new ItemStack(ERC.itemCoaster);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("id", coaster.GetSettings().ModelID);
			stack.setTagCompound(nbt);
			entityDropItem(stack, 0.5f);
		}
		this.SetDead();
	}

	@Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount)
    {
		if (!getWorld().isRemote && !this.isDead)
		{
			if (this.isEntityInvulnerable(source)) return false;

			boolean canDamage =
					source.getTrueSource() instanceof EntityPlayer;
			boolean isCreative = canDamage && source.isCreativePlayer();
			
			if (canDamage)// || this.getDamage() > 40.0F)
			{
				if (!isCreative && this.world.getGameRules().getBoolean("doEntityDrops"))
				{
					ItemStack stack = new ItemStack(ERC.itemCoaster);
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setString("id", coaster.GetSettings().ModelID);
					stack.setTagCompound(nbt);
					entityDropItem(stack, 0.5f);
				}
//				this.removePassengers();
				setDead();

			}
			return true;
    	}
		return false;
    }

	@Override
	public boolean canBePushed()
	{
		return false;
	}

    @Override
	public void applyEntityCollision(@Nonnull Entity entityIn)
	{
		//押せるようにするならここ書く
	}

	@SideOnly(Side.CLIENT)
	public void performHurtAnimation()
	{
		//破壊モーションするならここ書く
//		this.setForwardDirection(-this.getForwardDirection());
//		this.setTimeSinceHit(10);
//		this.setDamageTaken(this.getDamageTaken() * 11.0F);
	}

	@Override
    public void onUpdate() {
		super.onUpdate();
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
//		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
//			setDead();
//			return;
//		}

		if(IsRemote() && coaster.GetSettings().MaxEngineLevel > 0)
		{
			for(Entity entity : passengerSeats) {
				if (entity == Minecraft.getMinecraft().player) {
					EntityLivingBase player = (EntityLivingBase) entity;
					if (prevInputForward == 0 && player.moveForward != 0) {
						if (coaster.engineLevel <= coaster.GetSettings().MaxEngineLevel && coaster.engineLevel >= -3) {
							coaster.engineLevel += player.moveForward > 0 ? 1 : -1;
							coaster.engineLevel = Math.Clamp(coaster.engineLevel, -3, coaster.GetSettings().MaxEngineLevel);
//						player.sendMessage(new TextComponentString("Accel >>> "+coaster.engineLevel));
							ERC_PacketHandler.INSTANCE.sendToServer(new MessageAccelCoaster(this.getEntityId(), coaster.engineLevel));
						}
					}
					prevInputForward = player.moveForward;
				}
			}
		}

		coaster.Update(TickEventHandler.getTickCounter());
		if (world.isRemote){
			if (model == null) {
				LoadSettings();
				if(!modelStack.isEmpty()) {
					ChangeModel(modelStack);
				}
				else SetModelFromSettings(false);
			}
			else model.Update();
		}else {
			if(model != null) model.Update();
		}

		Vec3d pos = coaster.position;
		this.setPosition(pos.x, pos.y, pos.z);


		//move()をOverrideすればうまくやると当たり判定による移動できる？
		this.move(MoverType.SELF, posX - prevPosX, posY - prevPosY, posZ - prevPosZ);
		//setEntityBoundingBox(new AxisAlignedBB(posX - prevPosX, posY - prevPosY, posZ - prevPosZ);

		if(model != null) model.SetRotation(coaster.attitude);
//		Coaster c = coaster.GetNext();
//		for (MultiPartEntityPart part : partArray) {
//			part.prevPosX = part.posX;
//			part.prevPosY = part.posY;
//			part.prevPosZ = part.posZ;
//			part.posX = c.position.x;
//			part.posY = c.position.y;
//			part.posZ = c.position.z;
//			float w = 0.5f;
//			float h = 1;
//			part.setEntityBoundingBox(new AxisAlignedBB(part.posX-w, part.posY, part.posZ-w, part.posX+w, part.posY+h, part.posZ+w));
//			c = c.GetNext();
//		}
	}

    @Override
	public void onEntityUpdate()
	{
		// 既定のEntityの動きをリセット
	}





	@Override
	public void setPosition(double x, double y, double z)
	{
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}

	@Override
	public void move(@Nonnull MoverType type, double x, double y, double z)
	{
		// posからAABBの更新をするとこ
//		AxisAlignedBB aabb = this.getEntityBoundingBox().offset(x, y, z);
//		AxisAlignedBB aabb = this.getEntityBoundingBox();
		double mx = posX;
		double my = posY;
		double mz = posZ;
		double xx = posX;
		double xy = posY;
		double xz = posZ;

		if(world.isRemote && isPassenger(_Core.proxy.getClientPlayer())){
			//乗っている時に手がAABBに埋もれないため
			mx -= 0.1; my -= 0.1; mz -= 0.1; xx += 0.1; xy += 0.1; xz += 0.1;
		}
		else {
			for(int i = 0; i < coaster.SeatNum(); ++i){
				mx = java.lang.Math.min(mx, coaster.GetSeat(i).pos.x);
				my = java.lang.Math.min(my, coaster.GetSeat(i).pos.y);
				mz = java.lang.Math.min(mz, coaster.GetSeat(i).pos.z);
				xx = java.lang.Math.max(xx, coaster.GetSeat(i).pos.x);
				xy = java.lang.Math.max(xy, coaster.GetSeat(i).pos.y);
				xz = java.lang.Math.max(xz, coaster.GetSeat(i).pos.z);
			}
			mx -= 1; my -= 1; mz -= 1; xx += 1; xy += 1; xz += 1;
		}
		this.setEntityBoundingBox(new AxisAlignedBB(mx,my,mz,xx,xy,xz));
		if (this.isAddedToWorld() && !this.world.isRemote) this.world.updateEntityWithOptionalForce(this, false); // Forge - Process chunk registration after moving.
	}

	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch)
	{
//		ERC_Logger.debugInfo("EntityCoaster ; call setLocationAndAngles");
		super.setLocationAndAngles(x, y, z, yaw, pitch);
		if(coaster != null)coaster.position.SetFrom(x, y, z);
	}

	@Override
	public double getMountedYOffset()
	{
		return 0;
	}

	@Override
	public void updatePassenger(@Nonnull Entity passenger)
	{
		if (!this.isPassenger(passenger)) return;
		int i = 0;
		for(; i < passengerSeats.length; ++i) {
			if (passenger.equals(passengerSeats[i])) break;
		}
		Seat seat = coaster.GetSeat(i);
		passenger.setPosition(
				seat.pos.x - coaster.attitudeMat.up.x * 0.65,
				seat.pos.y - coaster.attitudeMat.up.y * 0.65,
				seat.pos.z - coaster.attitudeMat.up.z * 0.65);
//		if(passenger.getRidingEntity()==this)
//			ObfuscationReflectionHelper.setPrivateValue(Entity.class, passenger, seats[i], "ridingEntity");

		if(world.isRemote && Minecraft.getMinecraft().player == passenger)
		{
			RollingSeatManager.SetAttitude(coaster.attitude, coaster.prevAttitude, seat.setting.LocalRotation);
		}
	}

	protected void applyYawToEntity(Entity passenger)
	{
		passenger.setRenderYawOffset(0);
		float clamped = MathHelper.clamp(passenger.rotationYaw, -140.0F, 140.0F);
		passenger.prevRotationYaw += clamped - passenger.rotationYaw;
		passenger.rotationYaw += clamped - passenger.rotationYaw;
		passenger.setRotationYawHead(passenger.rotationYaw);
	}
	@SideOnly(Side.CLIENT)
	public void applyOrientationToEntity(@Nonnull Entity entityToUpdate)
	{
		this.applyYawToEntity(entityToUpdate);
	}


	private IBakedModel Model;
	public ResourceLocation Texture;
	public void SetModelFromSettings(boolean needUpdate)
	{

		if(needUpdate || Model == null) Model = ModelPackLoader.Instance.GetModelById(coaster.GetSettings());
		if(needUpdate || Texture == null) Texture = ModelPackLoader.Instance.GetTextureById(coaster.GetSettings());
		if(Texture == null || Model == null){
			setDead();
			return;
		}
		model = new MeshModel(this, Model, Texture, coaster.GetSettings().ModelID);
	}


	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
	{
//		this.lerpX = x;
//		this.lerpY = y;
//		this.lerpZ = z;
//		this.lerpYaw = (double)yaw;
//		this.lerpPitch = (double)pitch;
//		this.turnProgress = posRotationIncrements + 2;
//		this.motionX = this.velocityX;
//		this.motionY = this.velocityY;
//		this.motionZ = this.velocityZ;
	}

//    @Override
//    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pit, int p_70056_9_)
//    {
//        //nothing　サーバーからの規定のEntity同期で使われており、同期を無効にするため
//    }

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender() //たまに暗くなる原因ここっぽい
	{
//		return super.getBrightnessForRender();
		int x = (int)java.lang.Math.round(this.posX);
		int y = (int)java.lang.Math.round(this.posY);
		int z = (int)java.lang.Math.round(this.posZ);
		return this.getWorld().getCombinedLight(new BlockPos(x, y, z), 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance)
	{
		return true;
	}

	@Override
	protected void readEntityFromNBT(@Nonnull NBTTagCompound nbt) {
		coaster.setSpeed(nbt.getDouble("speed"));
		coaster.setLengthT(nbt.getDouble("lengthT"));
		coaster.setPosition(nbt.getFloat("railt"));
		coaster.UpdateSettings(nbt.getString("settings"));
		dataManager.set(COASTER_ID, nbt.getInteger("coaster_id"));
		dataManager.set(PARENT_ID, nbt.getInteger("parent_id"));
		dataManager.set(RAIL_POS_X, nbt.getInteger("railx"));
		dataManager.set(RAIL_POS_Y, nbt.getInteger("raily"));
		dataManager.set(RAIL_POS_Z, nbt.getInteger("railz"));
		StoreSettings(coaster.GetSettings());

		if(nbt.hasKey("modelStack")){
			modelStack = new ItemStack(nbt.getCompoundTag("modelStack"));
			ChangeModel(modelStack);
		}
		dataManager.set(CUSTOM_BLOCK_MODEL, modelStack);
	}

	@Override
	protected void writeEntityToNBT(@Nonnull NBTTagCompound nbt) {
		if(coaster.GetCurrentRail() != null) {
			IRailController c = coaster.GetCurrentRail().GetController();
			nbt.setInteger("railx", (int) c.CorePosX());
			nbt.setInteger("raily", (int) c.CorePosY());
			nbt.setInteger("railz", (int) c.CorePosZ());
			nbt.setFloat("railt", (float)coaster.pos.t());
        }
	    nbt.setDouble("speed", coaster.getSpeed());
		nbt.setDouble("lengthT", coaster.pos.Len());
        nbt.setString("settings", coaster.GetSettings().toString());
		nbt.setInteger("coaster_id", coaster.coasterId);
		nbt.setInteger("parent_id", coaster.parentId);

		if(!modelStack.isEmpty()) nbt.setTag("modelStack", modelStack.serializeNBT());
	}

	@Nonnull
	public World getWorld()
	{
		return world;
	}






	///////////////////// ICoasterController
	@Override
	public Rail GetCurrentRail()
	{
		if(coaster.GetCurrentRail() != null) return coaster.GetCurrentRail();
		TileEntityRail tile = (TileEntityRail)World().getTileEntity(
				new BlockPos(
						dataManager.get(RAIL_POS_X),
						dataManager.get(RAIL_POS_Y),
						dataManager.get(RAIL_POS_Z)));
		if(tile == null) return null;
		return tile.getRail();
	}


	@Override
	public void StoreRailPos(Rail rail)
	{
		if(rail == null) return;
		IRailController c = rail.GetController();
		dataManager.set(RAIL_POS_X, (int)c.CorePosX());
		dataManager.set(RAIL_POS_Y, (int)c.CorePosY());
		dataManager.set(RAIL_POS_Z, (int)c.CorePosZ());
	}
	@Override
	public void LoadRailInfoAndGetRail()
	{
		TileEntity tile = world.getTileEntity(new BlockPos(
				dataManager.get(RAIL_POS_X),
				dataManager.get(RAIL_POS_Y),
				dataManager.get(RAIL_POS_Z)));
		if(tile instanceof TileEntityRail) {
			coaster.SetNewRail(((TileEntityRail)tile).getRail());
		}
	}

	@Override
	public void StoreRailInfoAll(float t, float speed, Rail rail)
	{
		StoreRailPos(rail);
//		dataManager.set(RAIL_POS_T, t);
//		dataManager.set(SPEED, speed);
	}
	@Override
	public void SyncRailPos()
	{
		ERC_PacketHandler.INSTANCE.sendToAll(
				new MessageSyncCoasterPosStC(this)
		);
//		LoadRailInfoAndGetRail();
//		coaster.pos.Load(dataManager.get(RAIL_POS_T));
//		coaster.setSpeed(dataManager.get(SPEED));
	}


	@Override
	public void StoreCoasterInfo(int coasterId, int parentId)
	{
		dataManager.set(COASTER_ID, coasterId);
		dataManager.set(PARENT_ID, parentId);
//		Logger.debugInfo("store id : "+coasterId+" : "+parentId);
	}
	@Override
	public void LoadCoasterInfo()
	{
		coaster.coasterId = dataManager.get(COASTER_ID);
		coaster.parentId = dataManager.get(PARENT_ID);
//		Logger.debugInfo("load id : "+coaster.coasterId+" : "+coaster.parentId);
	}

	@Override
	public void StoreSettings(CoasterSettings settings)
	{
//		Logger.debugInfo("store : "+settings.toString());
//		setSize(settings.Width, settings.Height);
		dataManager.set(SETTINGS, settings.toString());
		if(passengerSeats.length != settings.Seats.length)
			passengerSeats = new Entity[settings.Seats.length];
	}
	@Override
	public void LoadSettings()
	{
//		Logger.debugInfo("load : "+dataManager.get(SETTINGS));
		coaster.UpdateSettings(dataManager.get(SETTINGS));
		if(passengerSeats.length != coaster.GetSettings().Seats.length)
			passengerSeats = new Entity[coaster.GetSettings().Seats.length];
		modelStack = dataManager.get(CUSTOM_BLOCK_MODEL);
	}




	////////////////////// IModelController

	ItemStack modelStack = ItemStack.EMPTY;
	public void ChangeModel(@Nonnull ItemStack stack)
	{
		if(stack.isEmpty())
			return;
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null) return;
		if(!(stack.getItem() instanceof IItemBlockModelHolder)) return;
		if(model != null) {
			if(!IsRemote() && !modelStack.isEmpty() && !(modelStack.getItem() instanceof ItemCoasterModel)) {
				NBTTagCompound pop = new NBTTagCompound();
				model.writeToNBT(pop);
				modelStack.setTagCompound(pop);
				entityDropItem(modelStack, 0.5f);
			}
			model.Invalidate();
		}
		modelStack = stack;
		dataManager.set(CUSTOM_BLOCK_MODEL, modelStack);
		IItemBlockModelHolder itemModel = (IItemBlockModelHolder)stack.getItem();
		model = itemModel.GetBlockModel(this);
		model.Reset();
		model.SetWorld(World());
		model.readFromNBT(nbt);
		coaster.SetSettingData(ItemCoasterModel.GetCoasterSettingsFromNBT(nbt));

		markBlockForUpdate();
	}
//	public void OnMarkUpdateStack()
//	{
//		modelStack = dataManager.get(CUSTOM_BLOCK_MODEL);
//		NBTTagCompound nbt = modelStack.getTagCompound();
//		IItemBlockModelHolder itemModel = (IItemBlockModelHolder)modelStack.getItem();
//		model = itemModel.GetBlockModel(this);
//		model.Reset();
//		model.SetWorld(World());
//		model.readFromNBT(nbt);
//		coaster.SetSettingData(ItemCoasterModel.GetCoasterSettingsFromNBT(nbt));
//	}

	@Override
	public double CorePosX() {
		return posX;
	}

	@Override
	public double CorePosY() {
		return posY;
	}

	@Override
	public double CorePosZ() {
		return posZ;
	}

	@Override
	public EnumFacing CoreSide() {
		return EnumFacing.NORTH;
	}

	@Override
	public boolean IsInvalid() {
		return isDead;
	}

	@Override
	public boolean IsRemote() {
		return getWorld().isRemote;
	}

	@Override
	public void markBlockForUpdate() {
		// Send Something message
		if(!world.isRemote){
			NBTTagCompound nbt = modelStack.getTagCompound();
			model.writeToNBT(nbt);
			modelStack.setTagCompound(nbt);
			dataManager.set(CUSTOM_BLOCK_MODEL, modelStack);
			ERC_PacketHandler.INSTANCE.sendToAll(new MessageSendModelDataStC(getEntityId(), modelStack));
		}
	}

	@Override
	public World World() {
		return getWorld();
	}

	@Nonnull
    @Override
	public IModel GetModel() {
		return model;
	}


	@Override
	public CommonAddress GetCommonAddress()
	{
		return new CommonAddress().Init(getEntityId(), 0);
	}


	//// IRollSeat
	@Override
	public Quaternion GetSeatRotation(Entity rider)
	{
		int idx = 0;
		for(; idx < passengerSeats.length; ++idx) {
			if (rider.equals(passengerSeats[idx])) break;
		}
		return coaster.GetSettings().Seats[idx].LocalRotation;
	}
	@Override
	public Vec3d RemovePassenger(Entity rider)
	{
		int idx = 0;
		for(; idx < passengerSeats.length; ++idx) {
			if (rider.equals(passengerSeats[idx])) break;
		}

		if(passengerSeats.length <= idx) return coaster.position;
		passengerSeats[idx] = null;
		return coaster.GetSeat(idx).pos;
	}


	@Override
	public Quaternion Attitude(Quaternion out, float partialTicks)
	{
		Quaternion.Lerp(out, coaster.prevAttitude, coaster.attitude, partialTicks);
		return out;
	}

	///////////////////////////////////////IEntityMultiPart

	@Override
	public boolean attackEntityFromPart(@Nonnull MultiPartEntityPart dragonPart, DamageSource source, float damage)
	{
		return false;
	}





//	public static class EntitySeat extends Entity {
//		public EntityCoaster parent;
//
//		public EntitySeat(World worldIn) {
//			super(worldIn);
//			setSize(1, 1);
//		}
//		public void SetParent(EntityCoaster coaster){ parent = coaster; }
//
//		public void UpdateSeatEntity(){super.onEntityUpdate();}
//
//		@Override
//		public void onEntityUpdate(){}
//
//
//		@Override
//		protected void entityInit() {
//
//		}
//
//		@Override
//		public boolean isPassenger(@Nonnull Entity in)
//		{
//			return parent.isPassenger(in);
//		}
//
//		@Override
//		protected void readEntityFromNBT(NBTTagCompound compound) {
//
//		}
//
//		@Override
//		protected void writeEntityToNBT(NBTTagCompound compound) {
//
//		}
//	}
}
