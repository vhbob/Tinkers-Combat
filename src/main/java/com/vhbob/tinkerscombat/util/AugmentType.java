package com.vhbob.tinkerscombat.util;

public enum AugmentType {

	WEAPON("Weapon"), ARMOR("Armor");

	private String name;

	private AugmentType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
