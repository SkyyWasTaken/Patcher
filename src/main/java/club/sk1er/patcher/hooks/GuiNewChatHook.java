package club.sk1er.patcher.hooks;

import club.sk1er.mods.core.universal.ChatColor;
import club.sk1er.patcher.config.PatcherConfig;
import com.google.common.collect.Queues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import java.util.Deque;

public class GuiNewChatHook {

    public static final Deque<IChatComponent> messageQueue = Queues.newArrayDeque();
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static long lastMessageAddedTime = 0L;

    public static void processMessageQueue() {
        if (!messageQueue.isEmpty()) {
            final long currentTime = System.currentTimeMillis();
            if ((currentTime - lastMessageAddedTime) >= getChatDelayMillis()) {
                mc.ingameGUI.getChatGUI().printChatMessage(messageQueue.remove());
                lastMessageAddedTime = currentTime;
            }
        }
    }

    public static void queueMessage(IChatComponent message) {
        if (PatcherConfig.chatDelay <= 0.0D) {
            mc.ingameGUI.getChatGUI().printChatMessage(message);
        } else {
            final long currentTime = System.currentTimeMillis();
            if ((currentTime - lastMessageAddedTime) >= getChatDelayMillis()) {
                mc.ingameGUI.getChatGUI().printChatMessage(message);
                lastMessageAddedTime = currentTime;
            } else {
                messageQueue.add(message);
            }
        }
    }

    public static void drawMessageQueue() {
        final int chatWidth = MathHelper.ceiling_float_int(mc.ingameGUI.getChatGUI().getChatWidth() / mc.ingameGUI.getChatGUI().getChatScale());

        if (!messageQueue.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 50);
            Gui.drawRect(-2, 0, chatWidth + 4, 9, 2130706432);
            GlStateManager.enableBlend();
            GlStateManager.translate(0, 0, 50);
            final String text = ChatColor.GRAY + "[+" + messageQueue.size() + " pending lines]";
            mc.fontRendererObj.drawStringWithShadow(text, 0, 1, -1);
            GlStateManager.popMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
        }
    }

    public static void mouseClicked() {
        if (!messageQueue.isEmpty()) {
            mc.ingameGUI.getChatGUI().printChatMessage(messageQueue.remove());
            lastMessageAddedTime = System.currentTimeMillis();
        }
    }

    private static long getChatDelayMillis() {
        return (long) (PatcherConfig.chatDelay * 1000.0D);
    }
}
