package application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomMagic {
    private final String name;
    private final List<PrimitiveMagic> components = new ArrayList<>();
    private static final int MAX_COMPONENTS = 6;

    
    public CustomMagic(String name) {
        this.name = name;
    }

    
    public String getName() {
        return name;
    }

    
    public List<PrimitiveMagic> getComponents() {
        return components;
    }

    
    public int getTotalMP() {
        return components.stream().mapToInt(PrimitiveMagic::getCostMP).sum();
    }

    
    public boolean addComponent(PrimitiveMagic magic) {
        if (components.size() >= MAX_COMPONENTS) return false;
        if (containsMagicId(magic.getMagicId())) return false;
        components.add(magic);
        return true;
    }

    
    public boolean removeComponentById(int magicId) {
        return components.removeIf(m -> m.getMagicId() == magicId);
    }

    
    public boolean containsMagicId(int magicId) {
        return components.stream().anyMatch(m -> m.getMagicId() == magicId);
    }
    
    //属性が複数ある場合の対応
    public List<String> getElementList() {
        Set<String> allElements = new HashSet<>();
        for (PrimitiveMagic m : components) {
            allElements.addAll(m.getElements());
        }
        return new ArrayList<>(allElements);
    }


    
    public boolean isValid() {
        if (components.size() > MAX_COMPONENTS) return false;
        Set<Integer> ids = new HashSet<>();
        for (PrimitiveMagic m : components) {
            if (!ids.add(m.getMagicId())) return false;
        }
        return true;
    }

    
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("「").append(name).append("」：");
        for (PrimitiveMagic m : components) {
            sb.append("[").append(m.getName()).append("]");
        }
        sb.append(" MP合計: ").append(getTotalMP());
        return sb.toString();
    }

    
    
    //全体攻撃かどうかの判定
    public boolean isAoE() {
        return components.stream().anyMatch(PrimitiveMagic::isAoE);
    }
    
    
    //対象選択が必要かどうかの判定
    public boolean requiresTargetSelection() {
        return components.stream().anyMatch(p ->
            !p.isAoE() && !p.getEffectType().isFriendly()
        );
    }
    
    public boolean affectsEnemiesWithBuff() {
        return components.stream().anyMatch(p ->
            p.getName().contains("ケイオスフィルド") ||
            p.getName().contains("サバイバー")
        );
    }


}