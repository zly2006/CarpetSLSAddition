package com.github.zly2006.carpetslsaddition.mixin;

import carpet.settings.SettingsManager;
import com.github.zly2006.carpetslsaddition.SettingsManagerAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = SettingsManager.class, remap = false)
public abstract class MixinSettingsManager implements SettingsManagerAccessor {
    @Shadow protected abstract void loadConfigurationFromConf();

    @Override
    public void loadSettings() {
        this.loadConfigurationFromConf();
    }
}
