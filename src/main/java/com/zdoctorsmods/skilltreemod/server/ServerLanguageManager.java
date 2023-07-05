package com.zdoctorsmods.skilltreemod.server;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.zdoctorsmods.skilltreemod.network.packets.ClientboundUpdateLocalizationPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

// TODO Determine if real time change detection is worth implementing
public class ServerLanguageManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = (new GsonBuilder()).create();

    private Map<String, Map<String, String>> localizations = Maps.newHashMap();

    private Set<String> removedLanguages = Sets.newHashSet();
    private Map<String, Set<String>> removedLocalizations = Maps.newHashMap();
    private Map<String, Map<String, String>> addedLocalizations = Maps.newHashMap();
    private Map<String, Map<String, String>> changedLocalizations = Maps.newHashMap();

    public ServerLanguageManager() {
        super(GSON, "lang");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager,
            ProfilerFiller pProfiler) {
        LOGGER.debug("Looking for Language Translations");
        Map<String, Map<String, String>> localizations = Maps.newHashMap();

        pObject.forEach((location, element) -> {
            try {
                JsonObject jsonObject = GsonHelper.convertToJsonObject(element, "language code");
                String languageCode = location.getPath();
                if (languageCode.contains("/"))
                    languageCode = languageCode.substring(languageCode.lastIndexOf("/") + 1, languageCode.length());

                Map<String, String> translations = localizations.getOrDefault(languageCode, Maps.newHashMap());
                localizations.put(languageCode, translations);

                LOGGER.debug("Found language {} in {}", languageCode, location.getPath());

                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    String key = entry.getKey();
                    String localization = entry.getValue().getAsString();

                    if (translations.containsKey(key)) {
                        LOGGER.error("Duplicate localization detected for {} in {}", key, location.toString());
                    } else {
                        translations.put(key, localization);
                        LOGGER.debug("Registered translation {}", key);
                    }
                }
            } catch (Exception exception) {
                LOGGER.error("Parsing error lang file {}: {}", location, exception.getMessage());
            }
        });
        removedLanguages = Sets.newHashSet(this.localizations.keySet());
        removedLanguages.removeAll(localizations.keySet());

        removedLocalizations = Maps.newHashMap();
        addedLocalizations = Maps.newHashMap();
        changedLocalizations = Maps.newHashMap();

        for (Map.Entry<String, Map<String, String>> entry : localizations.entrySet()) {
            String langCode = entry.getKey();
            if (this.localizations.containsKey(langCode)) {
                Set<String> removedKeys = this.localizations.get(langCode).keySet();
                removedKeys.removeAll(localizations.keySet());
                removedLocalizations.put(langCode, removedKeys);

                Set<String> changedKeys = this.localizations.get(langCode).keySet();
                changedKeys.removeAll(removedKeys);
                changedKeys.removeIf(
                        key -> !this.localizations.get(langCode).get(key).equals(localizations.get(langCode).get(key)));
                Map<String, String> changedMap = Maps.newHashMap();
                changedKeys.forEach(changed -> changedMap.put(changed, (localizations.get(langCode).get(changed))));
                changedLocalizations.put(langCode, changedMap);
            } else {
                addedLocalizations.put(langCode, entry.getValue());
            }
        }

        this.localizations = localizations;
    }

    public boolean isDirty() {
        return !(removedLanguages.isEmpty() && removedLocalizations.isEmpty() && addedLocalizations.isEmpty()
                && changedLocalizations.isEmpty());
    }

    public void flush() {
        removedLanguages.clear();
        removedLocalizations.clear();
        addedLocalizations.clear();
        changedLocalizations.clear();
    }

    public ClientboundUpdateLocalizationPacket getUpdatePacket(boolean isFirstPacket) {
        if (isFirstPacket)
            return new ClientboundUpdateLocalizationPacket(localizations);
        else if (isDirty())
            return new ClientboundUpdateLocalizationPacket(removedLanguages, removedLocalizations, addedLocalizations,
                    changedLocalizations);
        else
            return null;
    }
}
