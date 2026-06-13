package com.Pink_Cats.createentitycontrol.addition;

import com.Pink_Cats.createentitycontrol.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class BlockSelectorDetails {
    private BlockSelectorDetails() {
    }

    public static Component describeLimitSelector(String selector, Map<String, Integer> blockCounts,
                                                  Function<String, Component> blockNameTranslator) {
        if (!selector.startsWith("#")) {
            return blockNameTranslator.apply(selector);
        }

        List<Map.Entry<String, Integer>> matches = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : blockCounts.entrySet()) {
            if (Config.matchesBlockSelector(selector, entry.getKey())) {
                matches.add(entry);
            }
        }
        matches.sort(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                .thenComparing(Map.Entry::getKey));

        MutableComponent details = Component.literal(selector);
        if (matches.isEmpty()) {
            return details;
        }
        details.append(": ");
        int shown = Math.min(matches.size(), 5);
        for (int i = 0; i < shown; i++) {
            Map.Entry<String, Integer> match = matches.get(i);
            if (i > 0) {
                details.append(", ");
            }
            details.append(blockNameTranslator.apply(match.getKey()));
            details.append(Component.literal(" x" + match.getValue()));
        }
        if (matches.size() > shown) {
            details.append(", +" + (matches.size() - shown));
        }
        return details;
    }
}
