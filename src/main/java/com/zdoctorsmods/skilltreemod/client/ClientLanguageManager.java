package com.zdoctorsmods.skilltreemod.client;

import java.util.Map;

import org.slf4j.Logger;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.zdoctorsmods.skilltreemod.network.packets.ClientBoundUpdateLocalizationPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientLanguageManager implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<String, Map<String, String>> languages = Maps.newHashMap();
    private Language orginalBackup;

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        orginalBackup = null;
        applyTranslations();
    }

    private void applyTranslations() {
        String language = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
        Map<String, String> translations = languages.getOrDefault(language, languages.getOrDefault("en_us", null));

        if (translations != null) {
            LOGGER.debug("Injecting language {}", language);

            if (orginalBackup == null)
                orginalBackup = Language.getInstance();

            Language.inject(new Language() {
                @Override
                public String getOrDefault(String pId) {
                    String found = orginalBackup.getOrDefault(pId);
                    if (found.equals(pId))
                        return translations.getOrDefault(pId, pId);
                    else
                        return found;
                }

                @Override
                public boolean has(String pId) {
                    return orginalBackup.has(pId) || translations.containsKey(pId);
                }

                @Override
                public boolean isDefaultRightToLeft() {
                    return orginalBackup.isDefaultRightToLeft();
                }

                @Override
                public FormattedCharSequence getVisualOrder(FormattedText p_128116_) {
                    return orginalBackup.getVisualOrder(p_128116_);
                }
            });
        }

    }

    public void update(ClientBoundUpdateLocalizationPacket packet) {
        if (packet.isFirstPacket()) {
            languages = packet.getAddedLocalizations();
        } else {
            packet.getRemovedLanguages().forEach(lang -> languages.remove(lang));
            packet.getRemovedLocalizations().forEach((key, set) -> {
                if (languages.containsKey(key)) {
                    set.forEach(removed -> languages.get(key).remove(removed));
                }
            });
            packet.getAddedLocalizations().forEach((lang, map) -> {
                if (languages.containsKey(lang)) {
                    languages.get(lang).putAll(map);
                } else {
                    languages.put(lang, map);
                }
            });
            packet.getChangedLocalizations().forEach((langCode, changed) -> {
                if (languages.containsKey(langCode)) {
                    changed.forEach((key, value) -> {
                        languages.get(langCode).put(key, value);
                    });
                }
            });
        }
        applyTranslations();
    }

}
