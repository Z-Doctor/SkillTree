package zdoctor.skilltree.api.enums;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum BackgroundType {
	DEFAULT,
	SANDSTONE,
	ENDSTONE,
	DIRT,
	NETHERRACK,
	STONE,
	CUSTOM;

	private static int count = 0;
	private int column;

	private BackgroundType() {
		column = getCount();
	}

	private int getCount() {
		return count++;
	}

	@SideOnly(Side.CLIENT)
	public int getColumn() {
		return column > 3 ? column - 4 : column;
	}

	@SideOnly(Side.CLIENT)
	public int getRow() {
		return column > 3 ? 1 : 0;
	}
}
