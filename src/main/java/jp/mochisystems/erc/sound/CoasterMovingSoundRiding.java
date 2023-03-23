package jp.mochisystems.erc.sound;

import jp.mochisystems.erc._mc.entity.EntityCoaster;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CoasterMovingSoundRiding extends MovingSound {
	private final EntityPlayer player;
	private final EntityCoaster entity;

	public CoasterMovingSoundRiding(EntityPlayer player, EntityCoaster entity)
	{
		super(SoundEvents.ENTITY_MINECART_INSIDE, SoundCategory.NEUTRAL);
	    this.player = player;
	    this.entity = entity;
	    this.attenuationType = ISound.AttenuationType.NONE;
	    this.repeat = true;
	    this.repeatDelay = 0;
	    this.pitch = 10.7f;
	}

	public void update() 
	{
		if ((!this.entity.isDead) && (this.player.isRiding()) && (this.player.getRidingEntity() == this.entity))
		{
			float f = (float) entity.GetCoaster().getSpeed();
			if (f >= 0.01D) 
			{
				this.volume = (MathHelper.clamp((float) Math.pow(Math.abs(f), 3.0D) * 0.5F, 0.0F, 1.0F));
				this.pitch = 1.3f;
			} 
			else 
			{
				this.volume = 0.0F;
			}
		} 
		else
		{
			this.donePlaying = true;
		}
	}
}