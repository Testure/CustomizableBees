package com.turing.customizablebees.network;

import com.turing.customizablebees.CustomizableBees;
import com.turing.customizablebees.bees.effects.WorldInteractionEffect;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DoParticlePacket implements IMessage {
    private float r;
    private float g;
    private float b;
    private BlockPos pos;

    @Override
    public void fromBytes(ByteBuf buf) {
        r = buf.readFloat();
        g = buf.readFloat();
        b = buf.readFloat();
        pos = new BlockPos(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(r);
        buf.writeFloat(g);
        buf.writeFloat(b);
        buf.writeFloat(pos.getX());
        buf.writeFloat(pos.getY());
        buf.writeFloat(pos.getZ());
    }

    public DoParticlePacket() {

    }

    public DoParticlePacket(float r, float g, float b, BlockPos pos) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.pos = pos;
    }

    public static class Handler implements IMessageHandler<DoParticlePacket, IMessage> {
        @Override
        public IMessage onMessage(DoParticlePacket message, MessageContext context) {
            CustomizableBees.proxy.addScheduledTaskClient(() -> handle(message, context));
            return null;
        }

        private void handle(DoParticlePacket message, MessageContext context) {
            WorldInteractionEffect.doParticle(Minecraft.getMinecraft().world, message.pos, message.r, message.g, message.b);
        }
    }
}
