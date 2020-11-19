package zdoctor.mcskilltree.skilltree.tabs;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.mcskilltree.api.ISkillTreeTabGui;
import zdoctor.mcskilltree.client.gui.skilltree.SkillTreeTabGui;
import zdoctor.mcskilltree.client.gui.skilltree.SkillTreeTabType;
import zdoctor.mcskilltree.skilltree.SkillTree;
import zdoctor.mcskilltree.skilltree.SkillTreeBackground;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class PlayerInfoTab extends SkillTree {

    public PlayerInfoTab() {
        super(0, SkillTreeTabType.LEFT, "player_info", new ItemStack(Items.WRITABLE_BOOK));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ISkillTreeTabGui getTabGui() {
        if (tabGui == null)
            tabGui = new PlayerInfoTabGui();
        return tabGui;
    }

    @OnlyIn(Dist.CLIENT)
    public static class PlayerInfoTabGui extends SkillTreeTabGui {
        // TODO Add a way to register ITextComponents to be rendered
        // TODO Register one for player skill points
        protected long healthUpdateCounter;
        protected int ticks;
        protected int playerHealth;
        protected long lastSystemTime;
        protected final Random rand = new Random();

        public static int left_height = 39;
        public static int right_height = 39;


        protected int lastPlayerHealth;

        public PlayerInfoTabGui() {
            super(SkillTree.PLAYER_INFO);
            this.scrollX = this.scrollY = 4;
        }

        public static void drawEntityOnScreen(int left, int top, int scale, float mouseX, float mouseY, LivingEntity entity) {
            float angle1 = (float) Math.atan(mouseX / 40.0F);
            float angle2 = (float) Math.atan(mouseY / 40.0F);
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float) left, (float) top, 1050.0F);
            RenderSystem.scalef(1.0F, 1.0F, -1.0F);
            MatrixStack matrixstack = new MatrixStack();
            matrixstack.translate(0.0D, 0.0D, 1000.0D);
            matrixstack.scale((float) scale, (float) scale, (float) scale);
            Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
            Quaternion quaternion1 = Vector3f.XP.rotationDegrees(angle2 * 20.0F);
            quaternion.multiply(quaternion1);
            matrixstack.rotate(quaternion);
            float f2 = entity.renderYawOffset;
            float f3 = entity.rotationYaw;
            float f4 = entity.rotationPitch;
            float f5 = entity.prevRotationYawHead;
            float f6 = entity.rotationYawHead;
            entity.renderYawOffset = 180.0F + angle1 * 20.0F;
            entity.rotationYaw = 180.0F + angle1 * 40.0F;
            entity.rotationPitch = -angle2 * 20.0F;
            entity.rotationYawHead = entity.rotationYaw;
            entity.prevRotationYawHead = entity.rotationYaw;
            EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
            quaternion1.conjugate();
            entityrenderermanager.setCameraOrientation(quaternion1);
            entityrenderermanager.setRenderShadow(false);
            IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
            entityrenderermanager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
            irendertypebuffer$impl.finish();
            entityrenderermanager.setRenderShadow(true);
            entity.renderYawOffset = f2;
            entity.rotationYaw = f3;
            entity.rotationPitch = f4;
            entity.prevRotationYawHead = f5;
            entity.rotationYawHead = f6;
            RenderSystem.popMatrix();
        }


        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            super.render(mouseX, mouseY, partialTicks);
        }

        @Override
        public void renderAdjusted(int mouseX, int mouseY, float partialTicks) {
            // TODO Finish showing player data
            super.renderAdjusted(mouseX, mouseY, partialTicks);

            int xOffset = MathHelper.floor(this.scrollX);
            int yOffset = MathHelper.floor(this.scrollY);
            int x = (xOffset % 16) + 24;
            int y = (yOffset % 16) + 64;

            if (minecraft.player != null)
                drawEntityOnScreen(x, y, 30, 26 - mouseX, 26 - mouseY, minecraft.player);

            FontRenderer fontRenderer = minecraft.fontRenderer;
            int row = 0;
            int offset = minecraft.fontRenderer.FONT_HEIGHT;
            //drawString(fontRenderer, "0", 0, offset * row++, 0x2D2D2D, false);

            // TODO Add way for players to add to this, and store a better way
            IAttribute[] attributes = {
                    SharedMonsterAttributes.MAX_HEALTH, SharedMonsterAttributes.ATTACK_DAMAGE,
                    SharedMonsterAttributes.ATTACK_SPEED, SharedMonsterAttributes.ARMOR,
                    SharedMonsterAttributes.ARMOR_TOUGHNESS, SharedMonsterAttributes.MOVEMENT_SPEED,
                    SharedMonsterAttributes.KNOCKBACK_RESISTANCE, SharedMonsterAttributes.LUCK
            };

            for (IAttribute attribute : attributes) {
                drawPlayerAttributeInfo(fontRenderer, 55, 5 + offset * row++, attribute, attribute.getName());
            }

        }

        @Override
        public void preDrawSkills(int x, int y, int mouseX, int mouseY) {
            minecraft.textureManager.bindTexture(SkillTreeBackground.WINDOW);
            draw2DTex(x, y, 179f, 140f, 51, 72, 256, 256);
            drawPlayerHealthAndArmor(x, y + 73);
        }

        protected void drawPlayerAttributeInfo(FontRenderer fontRenderer, int posX, int posY, IAttribute attribute, String translateKey) {
            IAttributeInstance playerAttribute = minecraft.player.getAttribute(attribute);
            String pattern = "####0.0#";
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat
                    .getNumberInstance(new Locale(System.getProperty("user.country"), System.getProperty("user.language")));
            decimalFormat.applyPattern(pattern);
            String base = decimalFormat.format(playerAttribute.getBaseValue());
            String skillDelta = decimalFormat.format(playerAttribute.getValue() - playerAttribute.getBaseValue());
            String sign = playerAttribute.getValue() - playerAttribute.getBaseValue() < 0 ? "" : "+";
            String translatedKey = I18n.format(translateKey, base, sign, skillDelta);
            fontRenderer.drawString(translatedKey, posX, posY, 0x2D2D2D);
        }


        protected void drawPlayerHealthAndArmor(int left, int top) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(left, top, 0f);
            RenderSystem.scalef(0.6f, 0.6f, 0.6f);
            RenderSystem.enableBlend();

            ClientPlayerEntity player = minecraft.player;
            if (player == null) {
                return;
            }

            IngameGui inGameGui = minecraft.ingameGUI;
            ticks = inGameGui.getTicks();
            minecraft.textureManager.bindTexture(GUI_ICONS_LOCATION);

            // Draw Health
            int health = MathHelper.ceil(player.getHealth());

            boolean highlight = healthUpdateCounter > (long) ticks && (healthUpdateCounter - (long) ticks) / 3L % 2L == 1L;

            if (health < playerHealth && player.hurtResistantTime > 0) {
                lastSystemTime = Util.milliTime();
                healthUpdateCounter = ticks + 20;
            } else if (health > this.playerHealth && player.hurtResistantTime > 0) {
                lastSystemTime = Util.milliTime();
                healthUpdateCounter = ticks + 10;
            }

            if (Util.milliTime() - this.lastSystemTime > 1000L) {
                playerHealth = health;
                lastPlayerHealth = health;
                lastSystemTime = Util.milliTime();
            }

            playerHealth = health;
            int healthLast = lastPlayerHealth;

            IAttributeInstance attrMaxHealth = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
            float healthMax = (float) attrMaxHealth.getValue();
            float absorb = MathHelper.ceil(player.getAbsorptionAmount());

            int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);

            rand.setSeed(ticks * 312871);

            left_height += (healthRows * rowHeight);
            if (rowHeight != 10) left_height += 10 - rowHeight;

            int regen = -1;
            if (player.isPotionActive(Effects.REGENERATION)) {
                regen = ticks % 25;
            }

            final int TOP = 9 * (Objects.requireNonNull(minecraft.world).getWorldInfo().isHardcore() ? 5 : 0);
            final int BACKGROUND = (highlight ? 25 : 16);
            int MARGIN = 16;
            if (player.isPotionActive(Effects.POISON)) MARGIN += 36;
            else if (player.isPotionActive(Effects.WITHER)) MARGIN += 72;
            float absorbRemaining = absorb;

            for (int i = MathHelper.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
                //int b0 = (highlight ? 1 : 0);
                int row = MathHelper.ceil((float) (i + 1) / 10.0F) - 1;
                int x = i % 10 * 8;
                int y = row * rowHeight;

                if (health <= 4) y += rand.nextInt(2);
                if (i == regen) y -= 2;

                blit(x, y, BACKGROUND, TOP, 9, 9);

                if (highlight) {
                    if (i * 2 + 1 < healthLast)
                        blit(x, y, MARGIN + 54, TOP, 9, 9); //6
                    else if (i * 2 + 1 == healthLast)
                        blit(x, y, MARGIN + 63, TOP, 9, 9); //7
                }

                if (absorbRemaining > 0.0F) {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                        blit(x, y, MARGIN + 153, TOP, 9, 9); //17
                        absorbRemaining -= 1.0F;
                    } else {
                        blit(x, y, MARGIN + 144, TOP, 9, 9); //16
                        absorbRemaining -= 2.0F;
                    }
                } else {
                    if (i * 2 + 1 < health)
                        blit(x, y, MARGIN + 36, TOP, 9, 9); //4
                    else if (i * 2 + 1 == health)
                        blit(x, y, MARGIN + 45, TOP, 9, 9); //5
                }
            }

            // Draw Food
//            RenderSystem.translatef(0, 16, 0);

            FoodStats stats = player.getFoodStats();
            int level = stats.getFoodLevel();

            int rightOffset = left / 2 + 79;
            for (int i = 0; i < 10; ++i) {
                int idx = i * 2 + 1;
                int x = rightOffset - i * 8 - 9;
                int y = 9;
                int icon = 16;
                byte background = 0;

                if (player.isPotionActive(Effects.HUNGER)) {
                    icon += 36;
                    background = 13;
                }

                if (player.getFoodStats().getSaturationLevel() <= 0.0F && ticks % (level * 3 + 1) == 0) {
                    y += rand.nextInt(3) - 1;
                }

                blit(x, y, 16 + background * 9, 27, 9, 9);

                if (idx < level)
                    blit(x, y, icon + 36, 27, 9, 9);
                else if (idx == level)
                    blit(x, y, icon + 45, 27, 9, 9);
            }

            RenderSystem.disableBlend();

            RenderSystem.popMatrix();
        }
    }


}
