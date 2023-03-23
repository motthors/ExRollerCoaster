package jp.mochisystems.erc.sound;

import jp.mochisystems.erc._mc.entity.EntityCoaster;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class CoasterMovingSound extends MovingSound {
	
	private final EntityCoaster entity;

	public CoasterMovingSound(EntityCoaster entity)
	{
	    super(SoundEvents.ENTITY_MINECART_RIDING, SoundCategory.NEUTRAL);
	    this.entity = entity;
	    this.attenuationType = ISound.AttenuationType.NONE;
	    this.repeat = true;
	    this.repeatDelay = 0;
	    this.pitch = 0.2f;
	}

	public void update() {
		if (!this.entity.isDead)
		{
			float f = ((float) this.entity.GetCoaster().getSpeed());
			if (Math.abs(f) >= 0.01D) 
			{
				this.volume = (MathHelper.clamp(Math.abs(f)*0.5F, 0.0F, 1.0F));
			}
			else 
			{
				this.volume = 0.0F;
			}
		} 
		else 
		{
			this.donePlaying = true; //or repeat?
		}
	}
}