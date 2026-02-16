package application;

public class Enemy {
	private AttackType attackType;
	public final int id;
    public final String name;
    public final int level;
    public final int hp;
    public int currentHp;
    public final int atk;
    public final double normal, flame, thunder, ice, holy;
    public final int itemId;
    public final String imagePath;
    public final double dropRate; // 素材ドロップ率
    public final int exp;         // ★追加：撃破時に得られる経験値

    public Enemy(int id, String name, int level, int hp, int atk,
                 double normal, double flame, double thunder, double ice, double holy,
                 int itemId, String imagePath, double dropRate, int exp) { // ★引数追加
        this.id = id;
        this.name = name;
        this.level = level;
        this.hp = hp;
        this.currentHp = hp;
        this.atk = atk;
        this.normal = normal;
        this.flame = flame;
        this.thunder = thunder;
        this.ice = ice;
        this.holy = holy;
        this.itemId = itemId;
        this.imagePath = imagePath;
        this.dropRate = dropRate;
        this.exp = exp;
        this.attackType = setAttackType(id);
    }
    
    public Enemy(Enemy other) {
    	this.id = other.id;
        this.name = other.name;
        this.level = other.level;
        this.hp = other.hp;
        this.currentHp = other.hp;
        this.atk = other.atk;
        this.normal = other.normal;
        this.flame = other.flame;
        this.thunder = other.thunder;
        this.ice = other.ice;
        this.holy = other.holy;
        this.itemId = other.itemId;
        this.imagePath = other.imagePath;
        this.dropRate = other.dropRate;
        this.exp = other.exp; 
        this.attackType = setAttackType(id);
    }


    public Character toCharacter(int displaySlotId) {
        Character c = new Character(name, hp, currentHp, 0, atk, 10, false);

        // 属性耐性をすべてセット
        c.setElementResistance("物理", normal);
        c.setElementResistance("火", flame);
        c.setElementResistance("雷", thunder);
        c.setElementResistance("氷", ice);
        c.setElementResistance("聖", holy);
        
        c.setAttackType(this.attackType);

        c.setDisplaySlotId(displaySlotId);
        return c;
    }
    
    public enum AttackType {
    	SCRATCH, 
        BITE,
        SLASH, 
        BOSS1,
        BOSS2,
        BOSS3,
        BOSS4,
        ONI,
        THUNDERBIRD,
        GHOSTSHIPCAPTAIN,
        EXBOSS1,
        EXBOSS2
    }
    
    public AttackType setAttackType(int enemyId) {
        switch (enemyId) {
            case 1: return AttackType.SCRATCH;
            case 2: return AttackType.SCRATCH;
            case 3: return AttackType.BITE;
            case 4: return AttackType.BITE;
            case 5: return AttackType.BOSS1;
            case 6: return AttackType.BITE;
            case 7: return AttackType.SCRATCH;
            case 8: return AttackType.SLASH;
            case 9: return AttackType.GHOSTSHIPCAPTAIN;
            case 10: return AttackType.BOSS2;
            case 11: return AttackType.SLASH;
            case 12: return AttackType.SCRATCH;
            case 13: return AttackType.SCRATCH;
            case 14: return AttackType.THUNDERBIRD;
            case 15: return AttackType.BOSS3;
            case 16: return AttackType.GHOSTSHIPCAPTAIN;
            case 17: return AttackType.SCRATCH;
            case 18: return AttackType.BITE;
            case 19: return AttackType.ONI;
            case 20: return AttackType.BOSS4;
            case 21: return AttackType.BOSS4;
            case 22: return AttackType.BOSS4;
            default: return null;
        }
    }

    
    public AttackType getAttackType() {
        return attackType;
    }



    // ★経験値取得用メソッド
    public int getExp() {
        return exp;
    }

	public String getName() {
		return name;
	}
	
	public int getId() {
	    return id;
	}

}