package com.zdoctorsmods.skilltreemod.network.packets;

import com.zdoctorsmods.skilltreemod.skills.Skill;
import com.zdoctorsmods.skilltreemod.skills.SkillAction;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundClientSkillPacket implements Packet {

    private final ResourceLocation id;
    private final SkillAction action;
    private ServerPlayer sender;

    public ServerboundClientSkillPacket(Skill skill, SkillAction action) {
        this.id = skill.getId();
        this.action = action;
    }

    public ServerboundClientSkillPacket(FriendlyByteBuf pBuffer) {
        this.id = pBuffer.readResourceLocation();
        this.action = pBuffer.readEnum(SkillAction.class);
    }

    public SkillAction getAction() {
        return action;
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public void setSender(ServerPlayer sender) {
        this.sender = sender;
    }

    @Override
    public ServerPlayer getSender() {
        return sender;
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceLocation(this.id);
        pBuffer.writeEnum(this.action);
    }

}
