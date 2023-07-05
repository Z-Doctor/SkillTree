package com.zdoctorsmods.skilltreemod.network.packets;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.network.FriendlyByteBuf;

public class ClientboundUpdateLocalizationPacket implements Packet {

    private final boolean firstPacket;

    private final Map<String, Map<String, String>> localizations;
    private final Set<String> removedLanguages;
    private final Map<String, Set<String>> removedLocalizations;
    private final Map<String, Map<String, String>> changedLocalizations;

    public ClientboundUpdateLocalizationPacket(FriendlyByteBuf pBuffer) {
        firstPacket = pBuffer.readBoolean();
        localizations = Maps.newHashMap();
        removedLanguages = Sets.newHashSet();
        removedLocalizations = Maps.newHashMap();
        changedLocalizations = Maps.newHashMap();

        if (firstPacket) {
            readMapMap(localizations, pBuffer);
        } else {
            readChanges(pBuffer);
        }
    }

    public ClientboundUpdateLocalizationPacket(Map<String, Map<String, String>> localizations) {
        firstPacket = true;
        this.localizations = localizations;
        removedLanguages = null;
        removedLocalizations = null;
        changedLocalizations = null;
    }

    public ClientboundUpdateLocalizationPacket(Set<String> removedLanguages,
            Map<String, Set<String>> removedLocalizations, Map<String, Map<String, String>> addedLocalizations,
            Map<String, Map<String, String>> changedLocalizations) {
        firstPacket = false;
        this.removedLanguages = removedLanguages;
        this.removedLocalizations = removedLocalizations;
        this.localizations = addedLocalizations;
        this.changedLocalizations = changedLocalizations;
    }

    public boolean isFirstPacket() {
        return firstPacket;
    }

    public Set<String> getRemovedLanguages() {
        return removedLanguages;
    }

    public Map<String, Set<String>> getRemovedLocalizations() {
        return removedLocalizations;
    }

    public Map<String, Map<String, String>> getChangedLocalizations() {
        return changedLocalizations;
    }

    public Map<String, Map<String, String>> getAddedLocalizations() {
        return localizations;
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(firstPacket);
        if (firstPacket) {
            writeMapMap(localizations, pBuffer);
        } else {
            writeChanges(pBuffer);
        }
    }

    private void writeMapMap(Map<String, Map<String, String>> map, FriendlyByteBuf pBuffer) {
        int languageCount = map.size();
        pBuffer.writeInt(languageCount);
        for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
            byte[] languageCode = entry.getKey().getBytes();
            pBuffer.writeInt(languageCode.length);
            pBuffer.writeByteArray(languageCode);
            pBuffer.writeInt(entry.getValue().size());

            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                byte[] key = entry2.getKey().getBytes();
                pBuffer.writeInt(key.length);
                pBuffer.writeByteArray(key);

                byte[] value = entry2.getValue().getBytes();
                pBuffer.writeInt(value.length);
                pBuffer.writeByteArray(value);
            }
        }
    }

    private void readMapMap(Map<String, Map<String, String>> mapMap, FriendlyByteBuf pBuffer) {
        int languageCount = pBuffer.readInt();
        for (int i = 0; i < languageCount; i++) {
            String languageCode = new String(pBuffer.readByteArray(pBuffer.readInt()));
            Map<String, String> map = Maps.newHashMap();

            int keyCount = pBuffer.readInt();
            for (int j = 0; j < keyCount; j++) {
                String key = new String(pBuffer.readByteArray(pBuffer.readInt()));
                String value = new String(pBuffer.readByteArray(pBuffer.readInt()));
                map.put(key, value);
            }
            mapMap.put(languageCode, map);
        }
    }

    private void writeSet(Set<String> set, FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(set.size());
        set.forEach(lang -> {
            byte[] bytes = lang.getBytes();
            pBuffer.writeInt(bytes.length);
            pBuffer.writeByteArray(bytes);
        });
    }

    private void readSet(Set<String> set, FriendlyByteBuf pBuffer) {
        int removed = pBuffer.readInt();
        for (int i = 0; i < removed; i++) {
            set.add(new String(pBuffer.readByteArray(pBuffer.readInt())));
        }

    }

    private void writeMapSet(Map<String, Set<String>> map, FriendlyByteBuf pBuffer) {
        int removedCount = map.size();
        pBuffer.writeInt(removedCount);
        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            byte[] key = entry.getKey().getBytes();
            pBuffer.writeInt(key.length);
            pBuffer.writeByteArray(key);

            pBuffer.writeInt(entry.getValue().size());
            entry.getValue().forEach(value -> {
                byte[] bytes = value.getBytes();
                pBuffer.writeInt(bytes.length);
                pBuffer.writeByteArray(bytes);
            });
        }
    }

    private void readMapSet(Map<String, Set<String>> map, FriendlyByteBuf pBuffer) {
        int removedCount = pBuffer.readInt();
        for (int i = 0; i < removedCount; i++) {
            String key = new String(pBuffer.readByteArray(pBuffer.readInt()));

            Set<String> set = Sets.newHashSet();
            int removed = pBuffer.readInt();
            for (int j = 0; j < removed; j++) {
                set.add(new String(pBuffer.readByteArray(pBuffer.readInt())));
            }
            map.put(key, set);
        }
    }

    private void writeChanges(FriendlyByteBuf pBuffer) {
        writeMapMap(localizations, pBuffer);
        writeSet(removedLanguages, pBuffer);
        writeMapSet(removedLocalizations, pBuffer);
        writeMapMap(changedLocalizations, pBuffer);
    }

    private void readChanges(FriendlyByteBuf pBuffer) {
        readMapMap(localizations, pBuffer);
        readSet(removedLanguages, pBuffer);
        readMapSet(removedLocalizations, pBuffer);
        readMapMap(changedLocalizations, pBuffer);
    }
}
