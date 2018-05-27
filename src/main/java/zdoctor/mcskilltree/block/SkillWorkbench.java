package zdoctor.mcskilltree.block;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import zdoctor.mcskilltree.ModMain;
import zdoctor.mcskilltree.event.CraftingEvent;
import zdoctor.mcskilltree.skills.CraftSkill;
import zdoctor.skilltree.api.SkillTreeApi;

public class SkillWorkbench extends BlockWorkbench {

	public SkillWorkbench() {
		setHardness(2.5F);
		setSoundType(SoundType.WOOD);
		setUnlocalizedName(ModMain.MODID + ".workbench");
		setRegistryName(ModMain.MODID + ":workbench");
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (SkillTreeApi.hasSkill(playerIn, CraftSkill.CRAFT_SKILL)) {
			playerIn.displayGui(new SkillInterfaceCraftingTable(worldIn, pos));
			playerIn.addStat(StatList.CRAFTING_TABLE_INTERACTION);
		}
		return true;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		return new ItemStack(Blocks.CRAFTING_TABLE);
	}

	public static class SkillInterfaceCraftingTable implements IInteractionObject {
		private final World world;
		private final BlockPos position;

		public SkillInterfaceCraftingTable(World worldIn, BlockPos pos) {
			this.world = worldIn;
			this.position = pos;
		}

		public String getName() {
			return "skill_crafting_table";
		}

		public boolean hasCustomName() {
			return false;
		}

		public ITextComponent getDisplayName() {
			return new TextComponentTranslation(Blocks.CRAFTING_TABLE.getUnlocalizedName() + ".name", new Object[0]);
		}

		public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
			return new SkillContainerWorkbench(playerInventory, this.world, this.position);
		}

		public String getGuiID() {
			return "minecraft:crafting_table";
		}

	}

	public static class SkillContainerWorkbench extends ContainerWorkbench {

		private final World world;
		/** Position of the workbench */
		private final BlockPos pos;
		private final EntityPlayer player;

		public SkillContainerWorkbench(InventoryPlayer playerInventory, World worldIn, BlockPos posIn) {
			super(playerInventory, worldIn, posIn);
			this.world = worldIn;
			this.pos = posIn;
			this.player = playerInventory.player;

		}

		@Override
		protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftingMatrix,
				InventoryCraftResult result) {
			if (!world.isRemote) {
				EntityPlayerMP entityplayermp = (EntityPlayerMP) player;
				ItemStack itemstack = ItemStack.EMPTY;
				IRecipe irecipe = CraftingManager.findMatchingRecipe(craftingMatrix, world);

				if (irecipe != null && (irecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting")
						|| entityplayermp.getRecipeBook().isUnlocked(irecipe))) {
					result.setRecipeUsed(irecipe);
					itemstack = irecipe.getCraftingResult(craftingMatrix);
				}

				CraftingEvent event = new CraftingEvent(player, irecipe, itemstack);
				MinecraftForge.EVENT_BUS.post(event);
				if (event.getResult() != Result.DENY) {
					itemstack = event.getRecipeResult();
					result.setInventorySlotContents(0, itemstack);
					entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, itemstack));
				} else {
					System.out.println("Crafting Canceled");
					result.setInventorySlotContents(0, ItemStack.EMPTY);
					entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, ItemStack.EMPTY));
				}
			}
		}

		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			if (this.world.getBlockState(this.pos).getBlock() != ModBlocks.SkillWorkbench) {
				return false;
			} else {
				if (playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
						(double) this.pos.getZ() + 0.5D) <= 64.0D)
					return SkillTreeApi.hasSkill(playerIn, CraftSkill.CRAFT_SKILL);
				return false;
			}
		}

	}
}
