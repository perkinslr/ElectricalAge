package mods.eln.transparentnode.turret;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

import org.lwjgl.opengl.GL11;

import mods.eln.Eln;
import mods.eln.misc.Obj3D;
import mods.eln.misc.Obj3D.Obj3DPart;
import mods.eln.misc.UtilsClient;
import mods.eln.node.transparent.TransparentNodeDescriptor;

public class TurretDescriptor extends TransparentNodeDescriptor {
	class Properties {
		public float actionAngle;
		public float detectionDistance;
		public float aimDistance;
		public float impulsePower;
		public float gunMinElevation;
		public float gunMaxElevation;
		public float laserViewLightThreshold;
		public float turretSeekAnimationSpeed;
		public float turretAimAnimationSpeed;
		public float gunArmAnimationSpeed;
		public float gunDisarmAnimationSpeed;
		public float gunAimAnimationSpeed;
		
		public Properties() {
			actionAngle = 60;
			detectionDistance = 12;
			aimDistance = 15;
			impulsePower = 2000;
			gunMinElevation = -40;
			gunMaxElevation = 80;
			laserViewLightThreshold = 10;
			turretSeekAnimationSpeed = 40;
			turretAimAnimationSpeed = 80;
			gunArmAnimationSpeed = 4;
			gunDisarmAnimationSpeed = 1;
			gunAimAnimationSpeed = 150;
		}
	}
	
	private Obj3D obj;
	private Obj3DPart turret, holder, joint, leftGun, rightGun, sensor;
	
	private Properties properties;
	
	public TurretDescriptor(String name, String modelName, String description) {
		super(name, TurretElement.class, TurretRender.class);
		
		obj = Eln.obj.getObj(modelName);
		turret = obj.getPart("Turret");
		holder = obj.getPart("Holder");
		joint = obj.getPart("Joint");
		leftGun = obj.getPart("LeftGun");
		rightGun = obj.getPart("RightGun");
		sensor = obj.getPart("Sensor");
		
		properties = new Properties();
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		draw(null);
	}
	
	public void draw(TurretRender render) {
		float turretAngle = render != null ? render.getTurretAngle() : 0;
		float gunPosition = render != null ? render.getGunPosition() : 0;
		float gunAngle = render != null ? -render.getGunElevation() : 0;
		
		if (holder != null) holder.draw();
		if (joint != null) joint.draw();
		GL11.glPushMatrix();
		GL11.glRotatef(turretAngle, 0f, 1f, 0f);

		if (turret != null) turret.draw();
		if (sensor != null) UtilsClient.drawLight(sensor);

		GL11.glRotatef(gunAngle, 0f, 0f, 1f);
		/*GL11.glColor4f(1f, 0f, 0f, 0.2f);
		UtilsClient.disableLight();
		UtilsClient.enableBlend();
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(-0.3f, 0.15f, 0f);
		GL11.glVertex3f(-8f, 0f, 0f);
		GL11.glEnd();
		UtilsClient.enableLight();
		UtilsClient.disableBlend();
		GL11.glColor3f(1f, 1f, 1f);*/
		
		GL11.glTranslatef(0f, 0f, gunPosition / 4f);
		if (leftGun != null) leftGun.draw();
		GL11.glTranslatef(0f, 0f, -gunPosition / 2f);
		if (rightGun != null) rightGun.draw();

		GL11.glPopMatrix();
	}
}