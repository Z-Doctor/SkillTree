package com.zdoctorsmods.skilltreemod.network.packets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.zdoctorsmods.skilltreemod.skills.Skill;
import com.zdoctorsmods.skilltreemod.skills.SkillProgress;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ClientboundUpdateSkillsPacket implements Packet {
    private final boolean reset;
    private final int skillPoints;
    private final Map<ResourceLocation, Skill.Builder> added;
    private final Map<ResourceLocation, SkillProgress> progress;
    private final Set<ResourceLocation> removed;

    public ClientboundUpdateSkillsPacket(boolean pReset, Collection<Skill> pAdded, Set<ResourceLocation> pRemoved,
            Map<ResourceLocation, SkillProgress> changed, int skillPoints) {
        this.reset = pReset;
        ImmutableMap.Builder<ResourceLocation, Skill.Builder> builder = ImmutableMap.builder();

        for (Skill skill : pAdded) {
            builder.put(skill.getId(), skill.deconstruct());
        }

        this.added = builder.build();
        this.removed = ImmutableSet.copyOf(pRemoved);
        this.progress = ImmutableMap.copyOf(changed);
        this.skillPoints = skillPoints;
    }

    public ClientboundUpdateSkillsPacket(FriendlyByteBuf pBuffer) {
        this.reset = pBuffer.readBoolean();
        this.added = pBuffer.readMap(FriendlyByteBuf::readResourceLocation, Skill.Builder::fromNetwork);
        this.removed = pBuffer.readCollection(Sets::newLinkedHashSetWithExpectedSize,
                FriendlyByteBuf::readResourceLocation);
        this.progress = pBuffer.readMap(FriendlyByteBuf::readResourceLocation, SkillProgress::fromNetwork);
        this.skillPoints = pBuffer.readInt();
    }

    public boolean shouldReset() {
        return this.reset;
    }

    public Map<ResourceLocation, Skill.Builder> getAdded() {
        return this.added;
    }

    public Set<ResourceLocation> getRemoved() {
        return this.removed;
    }

    public Map<ResourceLocation, SkillProgress> getProgress() {
        return this.progress;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(this.reset);
        pBuffer.writeMap(this.added, FriendlyByteBuf::writeResourceLocation, (buffer, builder) -> {
            builder.serializeToNetwork(buffer);
        });
        pBuffer.writeCollection(this.removed, FriendlyByteBuf::writeResourceLocation);
        pBuffer.writeMap(this.progress, FriendlyByteBuf::writeResourceLocation, (buffer, progress) -> {
            progress.serializeToNetwork(pBuffer);
        });
        pBuffer.writeInt(skillPoints);
    }
}
