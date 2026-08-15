package com.freefish.torchesbecomesunlight.client.render.gui.recipebook;

import com.mojang.serialization.Codec;

public enum CookingPotRecipeBookTab
{
	MEALS("meals"),
	DRINKS("drinks"),
	MISC("misc");

	/** 1.21 的配方序列化走 Codec，这里按原 JSON 里的名字字段编解码 */
	public static final Codec<CookingPotRecipeBookTab> CODEC =
			Codec.STRING.xmap(CookingPotRecipeBookTab::findByName, tab -> tab == null ? "" : tab.name);

	public final String name;

	CookingPotRecipeBookTab(String name) {
		this.name = name;
	}

	public static CookingPotRecipeBookTab findByName(String name) {
		for (CookingPotRecipeBookTab value : values()) {
			if (value.name.equals(name)) {
				return value;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
