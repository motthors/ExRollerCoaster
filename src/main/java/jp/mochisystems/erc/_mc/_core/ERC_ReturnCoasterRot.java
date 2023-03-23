package jp.mochisystems.erc._mc._core;

import jp.mochisystems.core.math.Vec3d;

public class ERC_ReturnCoasterRot {
	public Vec3d Pos;
	public float roll;
	public float yaw;
	public float pitch;
	public float prevRoll;
	public float prevYaw;
	public float prevPitch;
//	public DoubleBuffer rotmat;
	// �v���C���[�̐���
	public float viewRoll;
	public float viewYaw;
	public float viewPitch;
	public Vec3d offsetX;
	public Vec3d offsetY;
	public Vec3d offsetZ;
	// ���Ȃ̃I�t�Z�b�g�p
	public Vec3d Dir;
	public Vec3d Pitch;
	
	public ERC_ReturnCoasterRot()
	{
		Pos = new Vec3d(0, 0, 0);
 		
 		roll = 0;
 		yaw = 0;
 		pitch = 0;
 		viewRoll = 0;
 		viewYaw = 0; 
 		viewPitch = 0;
 		
 		offsetX = new Vec3d(1, 0, 0);
 		offsetY = new Vec3d(0, 1, 0);
 		offsetZ = new Vec3d(0, 0, 1);
	}
	
	public float getFixedRoll(float partialTicks)
	{
		return prevRoll + (roll - prevRoll)*partialTicks;
	}
	public float getFixedYaw(float partialTicks)
	{
		return prevYaw + (yaw - prevYaw)*partialTicks;
	}
	public float getFixedPitch(float partialTicks)
	{
		return prevPitch + (pitch - prevPitch)*partialTicks;
	}
}
