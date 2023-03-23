//package jp.mochisystems.erc._mc.entity;
//
//import java.util.List;
//
//import jp.mochisystems.core.util.IModel;
//import jp.mochisystems.erc._mc._core.ERC_Logger;
//import jp.mochisystems.erc._mc.item.itemSUSHI;
//import jp.mochisystems.erc.coaster.Seat;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLiving;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.ItemLead;
//import net.minecraft.item.ItemMonsterPlacer;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.util.DamageSource;
//import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.World;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//public class EntityCoasterSeat extends Entity implements Seat.ISeatController{
//
//    public Seat seat;
//    private EntityCoaster parent;
//
//    public EntityCoasterSeat(World world)
//    {
//        super(world);
//		setSize(1.1f, 0.8f);
//    }
//
//	public EntityCoasterSeat(World world, EntityCoaster parent)
//    {
//		super(world);
//        this.parent = parent;
//        setSize(1.1f, 0.8f);
//	}
//
//	public void SetSeat(Seat seat)
//	{
//		this.seat = seat;
//	}
//
//
//	@Override
//	protected void entityInit()
//	{
//		this.dataWatcher.addObject(dwParentId, 0);
//		this.dataWatcher.addObject(dwSeatId, 0);
//		this.dataWatcher.addObject(dwlastx, 0f);
//		this.dataWatcher.addObject(dwlasty, 0f);
//		this.dataWatcher.addObject(dwlastz, 0f);
//	}
////	private static final DataParameter<Float> PosT = EntityDataManager.<Float>createKey(EntityMinecart.class, DataSerializers.FLOAT);
//	private final int dwParentId = 19;
//	private final int dwSeatId = 21;
//	private final int dwlastx = 22;
//	private final int dwlasty = 23;
//	private final int dwlastz = 24;
//
//
////	public void setCoasterSettings(CoasterSettings settings, int idx)
////	{
////		if(op==null)return;
////		if(op.offsetX==null)return;
////		if(op.offsetX.length <= idx)return;
////		setSize(op.size[idx], op.size[idx]);
////		if(world.isRemote)return;
////		setOffsetX(op.offsetX[idx]);
////		setOffsetY(op.offsetY[idx]);
////		setOffsetZ(op.offsetZ[idx]);
////		setRotX(op.rotX[idx]);
////		setRotY(op.rotY[idx]);
////		setRotZ(op.rotZ[idx]);
////		canRide = op.canRide;
////	}
//
//
//    protected void setSize(float w, float h)
//    {
////    	w*=10.0;h*=10.0;
//        if (w != this.width || h != this.height)
//        {
//            this.width = w;// + 40f;
//            this.height = h;
//    		this.boundingBox.minX = -w/2 + this.posX;
//    		this.boundingBox.minY = +h/2 + this.posY;
//    		this.boundingBox.minZ = -w/2 + this.posZ;
//    		this.boundingBox.maxX = +w/2 + this.posX;
//    		this.boundingBox.maxY = +h/2 + this.posY;
//    		this.boundingBox.maxZ = +w/2 + this.posZ;
//        }
//        this.myEntitySize = Entity.EnumEntitySize.SIZE_2;
//    }
//
//	public boolean canBeCollidedWith()
//    {
//        return true;
//    }
//	public AxisAlignedBB getBoundingBox()
//    {
//        return boundingBox;
//    }
//
//	private boolean canBeRidden()
//    {
//		return !world.isRemote;
//    }
//
//    public boolean attackEntityFrom(DamageSource ds, float damage)
//    {
//    	boolean ret = false;
//        if(parent!=null) ret = parent.attackEntityFrom(ds, damage);
//        else setDead();
//        return ret;
//    }
//
//    public boolean interactFirst(EntityPlayer player)
//    {
////    	if(parent.requestConnectCoaster(player))return true;
//		if(tryChangingCoasterModel(player))return true;
//    	if(isRiddenSUSHI(player))return true;
//    	if(requestRidingMob(player))return true;
//    	if(!canBeRidden())return true;
//
//        // プレイヤーが座ってる　＋　右クリックしたプレイヤーと違うプレイヤーが座ってる
//        if(this.isRidden())
//        {
//            if (this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
//            {
//                return true;
//            }
//            // 右クリックしたプレイヤー以外の何かが乗ってる
//            else if (this.riddenByEntity != player)
//            {
//                //���낷
//                riddenByEntity.mountEntity(null);
//                riddenByEntity = null;
//                return true;
//            }
//            //何かが乗ってる　自分かもしれない
//            else
//            {
//                return true;
//            }
//        }
//        else
//        {
////            if (!world.isRemote)
//            {
//            	RollingSeatManager.ResetAngles();
//                player.mountEntity(this);
//            }
//            return true;
//        }
//    }
//
//    private boolean isRidden()
//    {
//        return this.riddenByEntity != null;
//    }
//
//    private boolean tryChangingCoasterModel(EntityPlayer player)
//	{
//		if(player.getHeldItem()==null)return false;
//		if(!player.getHeldItem().hasTagCompound())return false;
//		if(!(player.getHeldItem().getItem() instanceof IItemBlockModelHolder)) return false;
//		if(parent == null) return false;
//		IModel model = ((IItemBlockModelHolder)player.getHeldItem().getItem()).GetBlockModel(parent);
//		parent.ChangeModel(model, player.getHeldItem().getTagCompound());
//		player.swingItem();
//		return true;
//	}
//
//    private boolean isRiddenSUSHI(EntityPlayer player)
//	{
//		if(player.getHeldItem()==null)return false;
//		if(player.getHeldItem().getItem() instanceof itemSUSHI)
//		{
//			if(!world.isRemote)
//			{
//				entitySUSHI e = new entitySUSHI(world,posX,posY,posZ);
//				world.spawnEntityInWorld(e);
//				e.mountEntity(this);
//				if(!player.capabilities.isCreativeMode)--player.getHeldItem().stackSize;
//			}
//			player.swingItem();
//			return true;
//		}
//		return false;
//	}
//
//	private boolean requestRidingMob(EntityPlayer player)
//	{
//		if(world.isRemote)return false;
//		ItemStack is = player.getHeldItem();
//		if(is==null)return false;
//		if(is.getItem() instanceof ItemMonsterPlacer)
//		{
//			Entity entity = ItemMonsterPlacer.spawnCreature(world, is.getItemDamage(), posX, posY, posZ);
//			entity.mountEntity(this);
//			if (!player.capabilities.isCreativeMode)--is.stackSize;
//			player.swingItem();
//			return true;
//		}
//		if(is.getItem() instanceof ItemLead)
//		{
//	        double d0 = 7.0D;
//			@SuppressWarnings("unchecked")
//			List<EntityLiving> list = world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(posX-d0, posY-d0, posZ-d0, posX+d0, posY+d0, posZ+d0));
//	        if (list != null)
//	        {
//				for (EntityLiving entityliving : list) {
//					if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player) {
//						entityliving.mountEntity(this);
//						entityliving.clearLeashed(true, !player.capabilities.isCreativeMode);
//						player.swingItem();
//						return true;
//					}
//				}
//	        }
//		}
//		return false;
//	}
//
//    @Override
//    public void Destroy() {
//        setDead();
//    }
//
//	@Override
//	public void onUpdate()
//	{
//		syncToClient();
//		if(seat == null) return;
//		seat.GetParent().Update(world.getWorldTime());
//		savePrevData();
//		if(parent==null || parent.isDead)
//		{
////			if(!isDead)setDead();
//			return;
//		}
////        seat.Update(world.getWorldTime());
//        Vec3d pos = seat.setting.LocalPosition;
//		double x = pos.x + parent.posX;
//		double y = pos.y + parent.posY;
//		double z = pos.z + parent.posZ;
//        this.setPosition(x, y, z);
//	}
//
//	private int UpdatePacketCounter = 1;
//	private int DeadCounter = 10;
//	protected void syncToClient()
//	{
//		if(this.UpdatePacketCounter--<=0)
//		{
//			if(parent == null && DeadCounter-- <= 0)
//			{
//				ERC_Logger.debugInfo("cant find parent");
//				setDead();
//			}
//			else DeadCounter = 10;
//
//			if(!world.isRemote)
//			{
//				UpdatePacketCounter = 40;
//				if(parent==null) searchParent();
//				if(parent!=null) SetParentEntityId(parent.getEntityId());
////                ERC_MessageCoasterMisc packet = new ERC_MessageCoasterMisc(this,4);
////                ERC_PacketHandler.INSTANCE.sendToAll(packet);
//			}
//			else
//			{
//				if(parent != null) {
//					UpdatePacketCounter = 40;
//					return;
//				}
//				ERC_Logger.debugInfo("sync client");
//				int entityId = GetParentEntityId();
//				Entity entity = world.getEntityByID(entityId);
//				if(entity == null) return;
//				if(!(entity instanceof EntityCoaster))return;
//				parent = (EntityCoaster)entity;
//				parent.SetSeat(this, getSeatIndex());
//				ERC_Logger.debugInfo("sync client complete sync seat");
//			}
//		}
//	}
//
//	// onEntityUpdateの代わり
//	private void savePrevData()
//    {
//    	this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//        this.prevRotationYaw = this.rotationYaw;
//        this.prevRotationPitch = this.rotationPitch;
////        this.prevRotationRoll = this.rotationRoll;
//    }
//
//	private boolean searchParent()
//	{
//		double x = posX - getLastParentPosX();
//		double y = posY - getLastParentPosY();
//		double z = posZ - getLastParentPosZ();
//		double s = Math.max(Math.abs(x), Math.abs(y));
//		s = Math.max(s, Math.abs(z));
//		@SuppressWarnings("unchecked")
//		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(x-s, y-s, z-s, x+s, y+s, z+s));
//		for(Entity e : list)
//		{
//			if(e instanceof EntityCoaster)
//			{
//				parent = (EntityCoaster) e;
//				parent.SetSeat(this, getSeatIndex());
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public double getMountedYOffset()
//    {
//        return (double)this.height * 0.4;
//    }
//
//    @Override
//	public void setPosition(double x, double y, double z)
//	{
//		super.setPosition(x, y, z);
//	}
//	@Override
//	public void setLocationAndAngles(double x, double y, double z, float u, float v)
//	{
//		super.setLocationAndAngles(x, y, z, u ,v);
//	}
//
//	@Override
//	public void updateRiderPosition()
//	{
//		// nothing
//		if(seat == null)return;
//    	if (this.riddenByEntity == null) return;
//
//
//		this.riddenByEntity.rotationYaw = 0;
//		this.riddenByEntity.rotationPitch = 0;
//		this.riddenByEntity.prevRotationYaw = 0;
//		this.riddenByEntity.prevRotationPitch = 0;
//
//		Vec3d up = seat.GetParent().attitude.up;
//		double height = riddenByEntity.getMountedYOffset();
//		double x = posX + up.x * height;
//		double y = posY + up.y * height;
//		double z = posZ + up.z * height;
//		this.riddenByEntity.setPosition(x, y, z);
//
////            double coasterSpeed = seat.GetParent().getSpeed();
////            this.riddenByEntity.motionX = coasterSpeed;
////            this.riddenByEntity.motionY = coasterSpeed;
////            this.riddenByEntity.motionZ = coasterSpeed;
//
////            if(world.isRemote && riddenByEntity instanceof EntityLivingBase)
////            {
////                EntityLivingBase el = (EntityLivingBase) this.riddenByEntity;
////                el.renderYawOffset = parent.ERCPosMat.yaw;
////                if(riddenByEntity == Minecraft.getMinecraft().thePlayer)
////                    el.rotationYawHead = AutoRailConnectionManager.rotationViewYaw + el.renderYawOffset;
////            }
//
////    	ERC_CoasterAndRailManager.setRotRoll(rotationRoll, prevRotationRoll);
//	}
//
//    @SideOnly(Side.CLIENT)
//    public void setAngles(float deltax, float deltay)
//    {
////    	ERC_CoasterAndRailManager.setAngles(deltax, deltay);
//    }
//
//	@Override
//	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pit, int p_70056_9_)
//    {
//    	//�d�l�Ƃ��ĉ��������@�T�[�o�[����̋K���Entity�����Ŏg���Ă���A�����𖳌��ɂ��邽��
////		ERC_Logger.debugInfo("catch!");
////		super.setPositionAndRotation2(CorePosX, y, z, yaw, pit, p_70056_9_);
//    }
//
////	public float getRoll(float partialTicks)
////	{
////		return offsetRot + parentCoaster.prevRotationRoll + (parentCoaster.rotationRoll - parentCoaster.prevRotationRoll)*partialTicks;
////	}
//
//	@Override
//	protected void readEntityFromNBT(NBTTagCompound nbt)
//	{
//		setSeatIndex(nbt.getInteger("seatindex"));
//		setOffsetX(nbt.getFloat("parentlastx"));
//		setOffsetY(nbt.getFloat("parentlasty"));
//		setOffsetZ(nbt.getFloat("parentlastz"));
////		setRotX(nbt.getFloat("seatrotx"));
////		setRotY(nbt.getFloat("seatroty"));
////		setRotZ(nbt.getFloat("seatrotz"));
//	}
//
//	@Override
//	protected void writeEntityToNBT(NBTTagCompound nbt)
//	{
//		nbt.setInteger("seatindex", getSeatIndex());
//		nbt.setFloat("parentlastx", getLastParentPosX());
//		nbt.setFloat("parentlasty", getLastParentPosY());
//		nbt.setFloat("parentlastz", getLastParentPosZ());
////		nbt.setFloat("seatrotx", getRotX());
////		nbt.setFloat("seatroty", getRotY());
////		nbt.setFloat("seatrotz", getRotZ());
//	}
//
//
////	public void SyncCoasterMisc_Send(ByteBuf buf, int flag)
////	{
////		switch(flag)
////		{
////		case 3 : //CtS �\��
////			break;
////		case 4 : //StC �e���N���ɋ�����
////			buf.writeInt(parent.getEntityId());
////			break;
////		}
////	}
////	public void SyncCoasterMisc_Receive(ByteBuf buf, int flag)
////	{
////		switch(flag)
////		{
////		case 3:
////			ERC_MessageCoasterMisc packet = new ERC_MessageCoasterMisc(this,4);
////			ERC_PacketHandler.INSTANCE.sendToAll(packet);
//////			ERC_Logger.info("server repost parentID to client");
////			break;
////		case 4 :
////			int parentid = buf.readInt();
////			parent = (EntityCoaster) world.getEntityByID(parentid);
////			if(parent==null){
////				ERC_Logger.warn("parentCoaster id is Invalid.  id:"+parentid);
////				return;
////			}
////			parent.addSeat(this, getSeatIndex());
//////			ERC_Logger.info("client get parentCoaster");
////			return;
////		}
////	}
//
//	////////////////////////////////////////datawatcher
//	private int GetParentEntityId()
//	{
//		return dataWatcher.getWatchableObjectInt(dwParentId);
//	}
//	private void SetParentEntityId(int id)
//	{
//		dataWatcher.updateObject(dwParentId, id);
//	}
//	private int getSeatIndex()
//	{
//		return dataWatcher.getWatchableObjectInt(dwSeatId);
//	}
//	private void setSeatIndex(int idx)
//	{
//		dataWatcher.updateObject(dwSeatId, idx);
//	}
//
//	private float getLastParentPosX()
//	{
//		return dataWatcher.getWatchableObjectFloat(dwlastx);
//	}
//	private void setOffsetX(float offsetx)
//	{
//		dataWatcher.updateObject(dwlastx, offsetx);
//	}
//
//	private float getLastParentPosY()
//	{
//		return dataWatcher.getWatchableObjectFloat(dwlasty);
//	}
//	private void setOffsetY(float offsety)
//	{
//		dataWatcher.updateObject(dwlasty, offsety);
//	}
//
//	private float getLastParentPosZ()
//	{
//		return dataWatcher.getWatchableObjectFloat(dwlastz);
//	}
//	private void setOffsetZ(float offsetz)
//	{
//		dataWatcher.updateObject(dwlastz, offsetz);
//	}
////
////	public float getRotX()
////	{
////		return dataWatcher.getWatchableObjectFloat(25);
////	}
////	public void setRotX(float rot)
////	{
////		dataWatcher.updateObject(25, Float.valueOf(rot));
////	}
////
////	public float getRotY()
////	{
////		return dataWatcher.getWatchableObjectFloat(26);
////	}
////	public void setRotY(float rot)
////	{
////		dataWatcher.updateObject(26, Float.valueOf(rot));
////	}
////
////	public float getRotZ()
////	{
////		return dataWatcher.getWatchableObjectFloat(27);
////	}
////	public void setRotZ(float rot)
////	{
////		dataWatcher.updateObject(27, Float.valueOf(rot));
////	}
////
//
//}
