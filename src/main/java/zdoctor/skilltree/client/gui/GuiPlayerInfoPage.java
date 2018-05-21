package zdoctor.skilltree.client.gui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.skills.pages.SkillPageBase;

@SideOnly(Side.CLIENT)
public class GuiPlayerInfoPage extends GuiSkillPage {

	/** The old x position of the mouse pointer */
	private float oldMouseX;
	/** The old y position of the mouse pointer */
	private float oldMouseY;

	GuiIngame inGameGui = Minecraft.getMinecraft().ingameGUI;

	EntityPlayerSP thePlayer = Minecraft.getMinecraft().player;
	NetworkPlayerInfo info = Minecraft.getMinecraft().getConnection().getPlayerInfo(thePlayer.getUniqueID());
	private GuiPlayerTabOverlay tabOverlay;

	public GuiPlayerInfoPage(SkillPageBase skillPage) {
		super(skillPage);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void drawGuiBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiBackgroundLayer(partialTicks, mouseX, mouseY);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GuiReference.SKILL_TREE_BACKGROUND);
		int i = this.guiLeft;
		int j = this.guiTop;
		this.drawTexturedModalRect(i + 9, j + 18, 179, 140, 51, 72);
		drawEntityOnScreen(i + 32, j + 82, 30, i + 51 - this.oldMouseX, j + 75 - 50 - this.oldMouseY, this.mc.player);

		this.oldMouseX = mouseX;
		this.oldMouseY = mouseY;

		// this.mc.getTextureManager().bindTexture(GuiSkillTree.SKILL_TREE_TABS);
		// this.drawTexturedModalRect(i - 24, j + 18, 4, 64, 25, 28);
		int points = SkillTreeApi.getPlayerSkillPoints(thePlayer);
		// fontRenderer.drawString("P:" + points, i - 20, j + 28, 0, false);
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.9F, 0.9F, 0.9F);
		fontRenderer.drawString(I18n.format("skilltree.points.info", points), i + 80, j + 30, 0, false);

		drawPlayerAttributeInfo(SharedMonsterAttributes.MAX_HEALTH, "skilltree.maxhealth.info", 40);
		drawPlayerAttributeInfo(SharedMonsterAttributes.ATTACK_DAMAGE, "skilltree.attackdamage.info", 50);
		drawPlayerAttributeInfo(SharedMonsterAttributes.ATTACK_SPEED, "skilltree.attackspeed.info", 60);
		drawPlayerAttributeInfo(SharedMonsterAttributes.ARMOR, "skilltree.armor.info", 70);
		drawPlayerAttributeInfo(SharedMonsterAttributes.ARMOR_TOUGHNESS, "skilltree.armortoughness.info", 80);
		drawPlayerAttributeInfo(SharedMonsterAttributes.MOVEMENT_SPEED, "skilltree.movespeed.info", 90);
		drawPlayerAttributeInfo(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, "skilltree.knockbackresistance.info",
				100);
		drawPlayerAttributeInfo(SharedMonsterAttributes.LUCK, "skilltree.luck.info", 110);

		GlStateManager.popMatrix();
	}

	protected void drawPlayerAttributeInfo(IAttribute attribute, String translateKey, int offset) {
		int i = this.guiLeft;
		int j = this.guiTop;
		IAttributeInstance playerAttribute = thePlayer.getEntityAttribute(attribute);
		String pattern = "####0.0#";
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat
				.getNumberInstance(new Locale(System.getProperty("user.country"), System.getProperty("user.language")));
		decimalFormat.applyPattern(pattern);
		String base = decimalFormat.format(playerAttribute.getBaseValue());
		String skillDelta = decimalFormat.format(playerAttribute.getAttributeValue() - playerAttribute.getBaseValue());
		String sign = playerAttribute.getAttributeValue() - playerAttribute.getBaseValue() < 0 ? "" : "+";
		fontRenderer.drawString(I18n.format(translateKey, base, sign, skillDelta), i + 80, j + offset, 0, false);
	}

	@Override
	public void drawGuiForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiForegroundLayer(mouseX, mouseY);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GuiReference.SKILL_TREE_BACKGROUND);
	}

	protected void renderPlayerHealthAndArmor() {
		this.mc.renderEngine.bindTexture(Gui.ICONS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// FoodStats var7 = entitylittlemaid.getFoodStats();
		// int var8 = var7.getFoodLevel();
		// int var9 = var7.getPrevFoodLevel();
		IAttributeInstance var10 = thePlayer.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
		float var14 = (float) var10.getAttributeValue();
		float var15 = thePlayer.getAbsorptionAmount();
		int var16 = MathHelper.ceil((var14 + var15) / 2.0F / 10.0F);
		int var17 = Math.max(10 - (var16 - 2), 3);
		float var19 = var15;
		int var21 = -1;

		if (thePlayer.isPotionActive(Potion.getPotionFromResourceLocation("regeneration"))) {
			var21 = inGameGui.getUpdateCounter() % MathHelper.ceil(var14 + 5.0F);
		}

		int startX = 59;
		int startY = 10;

		int drawX;
		int drawY;

		// LP
		// Health
		int lhealth = (int) thePlayer.getHealth();
		int llasthealth = info.getLastHealth();

		boolean flag = info.getHealthBlinkTime() > 0 && inGameGui.getUpdateCounter() / 3L % 2L == 1L;

		for (int li = MathHelper.ceil((var14 + var15) / 2.0F) - 1; li >= 0; --li) {
			int heartSprite = 16;
			if (flag) {
				heartSprite += 9;
			}

			if (thePlayer.isPotionActive(MobEffects.POISON)) {
				heartSprite += 36;
			} else if (thePlayer.isPotionActive(Potion.getPotionFromResourceLocation("wither"))) {
				heartSprite += 72;
			}

			int var25 = MathHelper.ceil((li + 1) / 10.0F);
			drawX = startX + li % 10 * 8;
			drawY = startY + var25 * var17;

			if (lhealth <= 4) {
				drawY += thePlayer.world.rand.nextInt(2);
			}
			if (li == var21) {
				drawY -= 2;
			}

			this.drawTexturedModalRect(drawX, drawY, flag ? 25 : 16, 0, 9, 9);

			if (flag) {
				if (li * 2 + 1 < llasthealth) {
					this.drawTexturedModalRect(drawX, drawY, heartSprite + 54, 0, 9, 9);
				}
				if (li * 2 + 1 == llasthealth) {
					this.drawTexturedModalRect(drawX, drawY, heartSprite + 63, 0, 9, 9);
				}
			}

			if (var19 > 0.0F) {
				if (var19 == var15 && var15 % 2.0F == 1.0F) {
					this.drawTexturedModalRect(drawX, drawY, heartSprite + 153, 0, 9, 9);
				} else {
					this.drawTexturedModalRect(drawX, drawY, heartSprite + 144, 0, 9, 9);
				}

				var19 -= 2.0F;
			} else {
				if (li * 2 + 1 < lhealth) {
					this.drawTexturedModalRect(drawX, drawY, heartSprite + 36, 0, 9, 9);
				}
				if (li * 2 + 1 == lhealth) {
					this.drawTexturedModalRect(drawX, drawY, heartSprite + 45, 0, 9, 9);
				}
			}
		}

		// boolean flag = false;
		// // Armor Points
		// int larmor = thePlayer.getTotalArmorValue();
		// ldrawy = guiTop + 26;
		// for (int li = 0; li < 10; ++li) {
		// if (larmor > 0) {
		// flag = true;
		// ldrawx = guiLeft + li * 8 + 86;
		//
		// if (li * 2 + 1 < larmor) {
		// this.drawTexturedModalRect(ldrawx, ldrawy, 34, 9, 9, 9);
		// }
		// if (li * 2 + 1 == larmor) {
		// this.drawTexturedModalRect(ldrawx, ldrawy, 25, 9, 9, 9);
		// }
		// if (li * 2 + 1 > larmor) {
		// this.drawTexturedModalRect(ldrawx, ldrawy, 16, 9, 9, 9);
		// }
		// }
		// }
		//
		// if (flag)
		// ldrawy += 10;
		//
		// // Air
		// if (thePlayer.isInsideOfMaterial(Material.WATER)) {
		// int var23 = thePlayer.getAir();
		// int var35 = MathHelper.ceil((var23 - 2) * 10.0D / 300.0D);
		// int var25 = MathHelper.ceil(var23 * 10.0D / 300.0D) - var35;
		//
		// for (int var26 = 0; var26 < var35 + var25; ++var26) {
		// ldrawx = guiLeft + var26 * 8 + 86;
		// if (var26 < var35) {
		// this.drawTexturedModalRect(ldrawx, ldrawy, 16, 18, 9, 9);
		// } else {
		// this.drawTexturedModalRect(ldrawx, ldrawy, 25, 18, 9, 9);
		// }
		// }
		// }
	}

	/**
	 * Draws an entity on the screen looking toward the cursor.
	 */
	public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY,
			EntityLivingBase ent) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate(posX, posY, 50.0F);
		GlStateManager.scale((-scale), scale, scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		float f = ent.renderYawOffset;
		float f1 = ent.rotationYaw;
		float f2 = ent.rotationPitch;
		float f3 = ent.prevRotationYawHead;
		float f4 = ent.rotationYawHead;
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-((float) Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
		ent.renderYawOffset = (float) Math.atan(mouseX / 40.0F) * 20.0F;
		ent.rotationYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
		ent.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
		ent.rotationYawHead = ent.rotationYaw;
		ent.prevRotationYawHead = ent.rotationYaw;
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		rendermanager.setRenderShadow(true);
		ent.renderYawOffset = f;
		ent.rotationYaw = f1;
		ent.rotationPitch = f2;
		ent.prevRotationYawHead = f3;
		ent.rotationYawHead = f4;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
}
