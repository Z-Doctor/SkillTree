package zdoctor.mcskilltree.skills.pages;

import zdoctor.mcskilltree.skills.CraftSkill;
import zdoctor.mcskilltree.skills.crafting.ArmorBootsCraftSkill;
import zdoctor.mcskilltree.skills.crafting.ArmorChestPlateCraftSkill;
import zdoctor.mcskilltree.skills.crafting.ArmorHelmetCraftSkill;
import zdoctor.mcskilltree.skills.crafting.ArmorLeggingsCraftSkill;
import zdoctor.mcskilltree.skills.crafting.ArrowCraftSkill;
import zdoctor.mcskilltree.skills.crafting.AxeCraftSkill;
import zdoctor.mcskilltree.skills.crafting.BeaconCraftSkill;
import zdoctor.mcskilltree.skills.crafting.BedCraftSkill;
import zdoctor.mcskilltree.skills.crafting.BoatCraftSkill;
import zdoctor.mcskilltree.skills.crafting.BookCraftSkill;
import zdoctor.mcskilltree.skills.crafting.BookShelfCraftSkill;
import zdoctor.mcskilltree.skills.crafting.BottleCraftSkill;
import zdoctor.mcskilltree.skills.crafting.BowCraftSkill;
import zdoctor.mcskilltree.skills.crafting.BrewingStandCraftSkill;
import zdoctor.mcskilltree.skills.crafting.BucketCraftSkill;
import zdoctor.mcskilltree.skills.crafting.ChestCraftSkill;
import zdoctor.mcskilltree.skills.crafting.ClockCraftSkill;
import zdoctor.mcskilltree.skills.crafting.CompassCraftSkill;
import zdoctor.mcskilltree.skills.crafting.DoorCraftSkill;
import zdoctor.mcskilltree.skills.crafting.EnchantingTableCraftSkill;
import zdoctor.mcskilltree.skills.crafting.EnderChestCraftSkill;
import zdoctor.mcskilltree.skills.crafting.FishingRodCraftSkill;
import zdoctor.mcskilltree.skills.crafting.FurnaceCraftSkill;
import zdoctor.mcskilltree.skills.crafting.HoeCraftSkill;
import zdoctor.mcskilltree.skills.crafting.ItemCrafterSkill;
import zdoctor.mcskilltree.skills.crafting.LeadCraftSkill;
import zdoctor.mcskilltree.skills.crafting.MapCraftSkill;
import zdoctor.mcskilltree.skills.crafting.MinecartCraftSkill;
import zdoctor.mcskilltree.skills.crafting.PaperCraftSkill;
import zdoctor.mcskilltree.skills.crafting.PickaxeCraftSkill;
import zdoctor.mcskilltree.skills.crafting.ShieldCraftSkill;
import zdoctor.mcskilltree.skills.crafting.ShovelCraftSkill;
import zdoctor.mcskilltree.skills.crafting.SpectralArrowCraftSkill;
import zdoctor.mcskilltree.skills.crafting.SwordCraftSkill;
import zdoctor.mcskilltree.skills.crafting.TNTCraftSkill;
import zdoctor.mcskilltree.skills.crafting.TippedArrowCraftSkill;
import zdoctor.mcskilltree.skills.crafting.WritingBookCraftSkill;
import zdoctor.skilltree.skills.pages.SkillPageBase;

public class CraftingSkillPage extends SkillPageBase {

	public static ItemCrafterSkill SWORD_CRAFTER;
	public static ItemCrafterSkill AXE_CRAFTER;
	public static ItemCrafterSkill SHIELD_CRAFTER;
	public static ItemCrafterSkill BOW_CRAFTER;
	public static ItemCrafterSkill ARROW_CRAFTER;
	public static ItemCrafterSkill SPECTRAL_ARROW_CRAFTER;
	public static ItemCrafterSkill TIPPED_ARROW_CRAFTER;

	public static ItemCrafterSkill PICKAXE_CRAFTER;
	public static ItemCrafterSkill SHOVEL_CRAFTER;
	public static ItemCrafterSkill HOE_CRAFTER;

	public static ItemCrafterSkill ENCHANTING_TABLE_CRAFTER;
	public static ItemCrafterSkill PAPER_CRAFTER;
	public static ItemCrafterSkill BOOK_CRAFTER;
	public static ItemCrafterSkill BOOKSHELF_CRAFTER;
	public static ItemCrafterSkill WRITING_BOOK_CRAFTER;

	public static ItemCrafterSkill BREWINGSTAND_CRAFTER;
	public static ItemCrafterSkill BOTTLE_CRAFTER;

	public static ItemCrafterSkill HELMET_CRAFTER;
	public static ItemCrafterSkill CHESTPLATE_CRAFTER;
	public static ItemCrafterSkill LEGGINGS_CRAFTER;
	public static ItemCrafterSkill BOOTS_CRAFTER;

	public static ItemCrafterSkill DOOR_CRAFTER;
	public static ItemCrafterSkill BED_CRAFTER;
	public static ItemCrafterSkill BOAT_CRAFTER;
	public static ItemCrafterSkill MINECART_CRAFTER;
	public static ItemCrafterSkill FISHING_ROD__CRAFTER;
	public static ItemCrafterSkill BUCKET_CRAFTER;
	public static ItemCrafterSkill BEACON_CRAFTER;
	public static ItemCrafterSkill TNT_CRAFTER;
	public static ItemCrafterSkill MAP_CRAFTER;
	public static ItemCrafterSkill COMPASS_CRAFTER;
	public static ItemCrafterSkill CLOCK_CRAFTER;
	public static ItemCrafterSkill LEAD_CRAFTER;

	public static ItemCrafterSkill FURNACE_CRAFTER;
	public static ItemCrafterSkill CHEST_CRAFTER;
	public static ItemCrafterSkill ENDERCHEST_CRAFTER;

	public CraftingSkillPage() {
		super("CraftingPage");
	}

	@Override
	public void registerSkills() {
		SWORD_CRAFTER = new SwordCraftSkill();
		AXE_CRAFTER = new AxeCraftSkill();
		SHIELD_CRAFTER = new ShieldCraftSkill();
		BOW_CRAFTER = new BowCraftSkill();
		ARROW_CRAFTER = new ArrowCraftSkill();
		SPECTRAL_ARROW_CRAFTER = new SpectralArrowCraftSkill();
		TIPPED_ARROW_CRAFTER = new TippedArrowCraftSkill();

		PICKAXE_CRAFTER = new PickaxeCraftSkill();
		SHOVEL_CRAFTER = new ShovelCraftSkill();
		HOE_CRAFTER = new HoeCraftSkill();

		ENCHANTING_TABLE_CRAFTER = new EnchantingTableCraftSkill();
		PAPER_CRAFTER = new PaperCraftSkill();
		BOOK_CRAFTER = new BookCraftSkill();
		BOOKSHELF_CRAFTER = new BookShelfCraftSkill();
		WRITING_BOOK_CRAFTER = new WritingBookCraftSkill();

		BREWINGSTAND_CRAFTER = new BrewingStandCraftSkill();
		BOTTLE_CRAFTER = new BottleCraftSkill();

		HELMET_CRAFTER = new ArmorHelmetCraftSkill();
		CHESTPLATE_CRAFTER = new ArmorChestPlateCraftSkill();
		LEGGINGS_CRAFTER = new ArmorLeggingsCraftSkill();
		BOOTS_CRAFTER = new ArmorBootsCraftSkill();

		DOOR_CRAFTER = new DoorCraftSkill();
		BED_CRAFTER = new BedCraftSkill();
		BOAT_CRAFTER = new BoatCraftSkill();
		MINECART_CRAFTER = new MinecartCraftSkill();
		FISHING_ROD__CRAFTER = new FishingRodCraftSkill();
		BUCKET_CRAFTER = new BucketCraftSkill();
		BEACON_CRAFTER = new BeaconCraftSkill();
		TNT_CRAFTER = new TNTCraftSkill();
		MAP_CRAFTER = new MapCraftSkill();
		COMPASS_CRAFTER = new CompassCraftSkill();
		CLOCK_CRAFTER = new ClockCraftSkill();
		LEAD_CRAFTER = new LeadCraftSkill();

		FURNACE_CRAFTER = new FurnaceCraftSkill();
		CHEST_CRAFTER = new ChestCraftSkill();
		ENDERCHEST_CRAFTER = new EnderChestCraftSkill();
	}

	@Override
	public void loadPage() {
		addSkill(CraftSkill.CRAFT_SKILL, 0, 0);

		addSkill(SWORD_CRAFTER, 2, 0);
		addSkill(SHIELD_CRAFTER, 3, 1);
		addSkill(BOW_CRAFTER, 3, 0);
		addSkill(ARROW_CRAFTER, 4, 0);
		addSkill(TIPPED_ARROW_CRAFTER, 5, 0);
		addSkill(SPECTRAL_ARROW_CRAFTER, 5, 1);

		addSkill(PICKAXE_CRAFTER, 2, 1);
		addSkill(AXE_CRAFTER, 2, 2);
		addSkill(SHOVEL_CRAFTER, 2, 3);
		addSkill(HOE_CRAFTER, 2, 4);

		addSkill(ENCHANTING_TABLE_CRAFTER, 4, 2);
		addSkill(PAPER_CRAFTER, 5, 2);
		addSkill(BOOK_CRAFTER, 6, 2);
		addSkill(BOOKSHELF_CRAFTER, 7, 2);
		addSkill(WRITING_BOOK_CRAFTER, 7, 3);

		addSkill(BREWINGSTAND_CRAFTER, 4, 4);
		addSkill(BOTTLE_CRAFTER, 5, 4);

		addSkill(HELMET_CRAFTER, 1, 0);
		addSkill(CHESTPLATE_CRAFTER, 1, 1);
		addSkill(LEGGINGS_CRAFTER, 1, 2);
		addSkill(BOOTS_CRAFTER, 1, 3);

		addSkill(BED_CRAFTER, 10, 0);
		addSkill(DOOR_CRAFTER, 11, 0);
		addSkill(BOAT_CRAFTER, 10, 1);
		addSkill(MINECART_CRAFTER, 11, 1);
		addSkill(FISHING_ROD__CRAFTER, 10, 2);
		addSkill(BUCKET_CRAFTER, 11, 2);
		addSkill(BEACON_CRAFTER, 10, 3);
		addSkill(TNT_CRAFTER, 11, 3);
		addSkill(MAP_CRAFTER, 10, 4);
		addSkill(COMPASS_CRAFTER, 11, 4);
		addSkill(CLOCK_CRAFTER, 10, 5);
		addSkill(LEAD_CRAFTER, 11, 5);

		addSkill(CHEST_CRAFTER, 4, 5);
		addSkill(ENDERCHEST_CRAFTER, 5, 5);
	}

	@Override
	public BackgroundType getBackgroundType() {
		return BackgroundType.ENDSTONE;
	}

}
