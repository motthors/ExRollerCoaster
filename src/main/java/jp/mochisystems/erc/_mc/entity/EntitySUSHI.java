package jp.mochisystems.erc._mc.entity;

import java.util.Random;

import com.google.common.collect.ImmutableMap;
import jp.mochisystems.core.bufferedRenderer.CachedBufferBase;
import jp.mochisystems.core._mc.renderer.MeshBuffer;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc._core.ERC_Logger;
import jp.mochisystems.erc.loader.ModelPackLoader;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.vecmath.Vector2f;

public class EntitySUSHI extends Entity {
	
	@SideOnly(Side.CLIENT)
	public static ResourceLocation tex;
	@SideOnly(Side.CLIENT)
	public static MeshBuffer[] models;

	@SideOnly(Side.CLIENT)
	public static void clientInitSUSHI()
	{
		tex = new ResourceLocation(ERC.MODID,"textures/entities/check.png");
		try {
			models = new MeshBuffer[5];
			String[] ids = {
				"sushi/sushi_m.obj",
				"sushi/sushi_t.obj",
				"sushi/sushi_w.obj",
				"sushi/sushi_e2.obj",
				"sushi/sushi_g.obj"
			};
			for(int i = 0; i < ids.length; i++) {
				String id = ids[i];
				OBJModel model = (OBJModel)ModelLoaderRegistry.getModel(new ResourceLocation(ERC.MODID, id));
				model.getMatLib().getMaterial(OBJModel.Material.DEFAULT_NAME).setTexture(new OBJModel.Texture("builtin/white", new Vector2f(0, 0), new Vector2f(1, 1), 0));
//				ImmutableMap<String, String> customs = ImmutableMap.of("flip-v", "true");
//				model.process(customs);
				IBakedModel baked = model.bake(model.getDefaultState(), DefaultVertexFormats.POSITION_TEX_NORMAL, name -> ModelPackLoader.Dummy.Instance);
				models[i] = new MeshBuffer(baked, tex, id);
			}
		}
		catch(Exception e){
			ERC_Logger.warn("Loading SUSHI renderer is failure");
		}
	}

	public CachedBufferBase GetCurrentModel()
	{
		return models[getId()];
	}
	
	public float rotation;
	public float prevRotation;
	float speed;
	
	public EntitySUSHI(World world)
	{
		super(world);
		setSize(1.1f, 0.4f);
		
	}
	public EntitySUSHI(World world, double posX, double posY, double posZ)
	{
		this(world);
		setPosition(posX, posY, posZ);
	}
	
	@Override
	protected void entityInit()
	{
		Random r = new Random();

		dataManager.register(SPEED, 0f);
		dataManager.register(MODEL_ID, (int) java.lang.Math.floor(r.nextInt(44)/10d));
		if(world.isRemote) GetCurrentModel().SetDirty();
	}
	private static final DataParameter<Integer> MODEL_ID = EntityDataManager.createKey(EntitySUSHI.class, DataSerializers.VARINT);
	private static final DataParameter<Float> SPEED = EntityDataManager.createKey(EntitySUSHI.class, DataSerializers.FLOAT);


	@Override
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }
	
	@Override
    public boolean attackEntityFrom(DamageSource source, float p_70097_2_)
    {
    	boolean flag = source.getTrueSource() instanceof EntityPlayer;

	    if (flag)
	    {
	        setDead();
//			if(world.isRemote)GetCurrentModel().DeleteBuffer();
	        boolean flag1 = source.isCreativePlayer();
	        if(!flag1 && !world.isRemote)entityDropItem(new ItemStack(ERC.itemSUSHI,1,0), 0f);
	    	return true;
		}
	    
    	return false;
    }
	
	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand)
	{
		float currentSpeed = getSpeed();
		if(!player.isSneaking())
		{
			if(currentSpeed==0) setSpeed(3.0f);
			else if(currentSpeed>0) setSpeed(-3.0f);
			else if(currentSpeed<0) setSpeed(0);
		}
		else
		{
			setSpeed(currentSpeed*1.1f);
		}
		speed = getSpeed();
		player.swingArm(hand);
		return EnumActionResult.SUCCESS;
	}
	
	public void onUpdate()
	{
//		setDead();
		prevRotation = rotation;
		rotation += speed;
		Math.fixrot(rotation, prevRotation);
	}

	public ResourceLocation getTexture()
	{
		return tex;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setSpeed(nbt.getFloat("speed"));
		int id = nbt.getInteger("modelid");
		if(id>0)setId(id);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setFloat("speed", getSpeed());
		nbt.setInteger("modelid", getId());
	}

	public float getSpeed()
	{
		return dataManager.get(SPEED);
	}
	public void setSpeed(float speed)
	{
		dataManager.set(SPEED, speed);
	}
	
	public int getId()
	{
		return dataManager.get(MODEL_ID);
	}
	public void setId(int id)
	{
		dataManager.set(MODEL_ID, id);
	}
}
