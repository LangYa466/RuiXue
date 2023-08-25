package me.tiangong;


import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.IntegerValue;
import org.jetbrains.annotations.Nullable;

@ModuleInfo(name = "FakeFPS", description = "FakeFPS", category = ModuleCategory.VULGAR)
public class FakeFPS extends Module {
    private static final IntegerValue FakeFPS = new IntegerValue("FakeFPS", 600, 200, 2000);
    @Nullable
    public static Integer getfps() {
        return FakeFPS.get();
    }
}
