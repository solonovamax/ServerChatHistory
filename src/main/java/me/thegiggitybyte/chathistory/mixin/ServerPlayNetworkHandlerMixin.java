package me.thegiggitybyte.chathistory.mixin;

import me.thegiggitybyte.chathistory.ChatHistory;
import me.thegiggitybyte.chathistory.ChatMessage;
import net.minecraft.network.MessageType;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    
    @Shadow
    public ServerPlayerEntity player;
    
    @Inject(
            method = "handleMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"
            )
    )
    public void cachePlayerMessage(TextStream.Message messageStream, CallbackInfo ci) {
        var content = new TranslatableText("chat.type.text", this.player.getDisplayName(), messageStream.getRaw());
        var message = new ChatMessage(content, MessageType.CHAT, this.player.getUuid());
        
        ChatHistory.MESSAGE_CACHE.add(message);
    }
}
