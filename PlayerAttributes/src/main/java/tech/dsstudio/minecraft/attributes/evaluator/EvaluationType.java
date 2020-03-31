package tech.dsstudio.minecraft.attributes.evaluator;

public enum EvaluationType {
	/**
	 * When player lands an attack to a mob
	 */
	ATTACK_DAMAGE,
	/**
	 * When player being hurt
	 */
	DEFENSE_DAMAGE,
	/**
	 * When player do things and depletes stamina
	 */
	STAMINA_DEPLETION,
	/**
	 * When player eat food and replenish stamina
	 */
	STAMINA_REPLENISHMENT,

	/**
	 * When player equips something that affect their attack attribute
	 */
	ATTACK,
	/**
	 * When player equips something that affect their defense attribute
	 */
	DEFENSE,
	/**
	 * When player equips something that affect their dexterity attribute
	 */
	DEXTERITY,
	/**
	 * When player equips something that affect their durability attribute
	 */
	DURABILITY,
	/**
	 * When player equips something that affect their intelligence attribute
	 */
	INTELLIGENCE,
	/**
	 * When player equips something that affect their charisma attribute
	 */
	CHARISMA,

	/**
	 * For unit testing and validation.
	 * Don't use if you are not sure what does this do
	 */
	DEBUG,
}
