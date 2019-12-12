package me.pseudoknight.chnaughty;

import com.google.gson.JsonSyntaxException;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CRECastException;
import com.laytonsmith.core.exceptions.CRE.CREException;
import com.laytonsmith.core.exceptions.CRE.CREFormatException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CRENullPointerException;
import com.mojang.datafixers.util.Either;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.BlockStateBoolean;
import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EnumHand;
import net.minecraft.server.v1_15_R1.IBlockData;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.Items;
import net.minecraft.server.v1_15_R1.PacketDataSerializer;
import net.minecraft.server.v1_15_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import net.minecraft.server.v1_15_R1.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_15_R1.PacketPlayOutOpenBook;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.TileEntity;
import net.minecraft.server.v1_15_R1.TileEntitySign;
import net.minecraft.server.v1_15_R1.Unit;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftMetaBook;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Minecraft {
	static final int VIEW_DISTANCE = Bukkit.getViewDistance() * 16;

	static PlayerConnection GetConnection(MCPlayer player) {
		return ((CraftPlayer) player.getHandle()).getHandle().playerConnection;
	}

	static IChatBaseComponent Serialize(String msg) {
		return IChatBaseComponent.ChatSerializer.a(msg);
	}

	static void SendActionBarMessage(MCPlayer player, String msg) {
		GetConnection(player).sendPacket(
				new PacketPlayOutChat(Serialize("{\"text\": \"" + msg + "\"}"), ChatMessageType.GAME_INFO));
	}

	static void SendTitleMessage(MCPlayer player, String title, String subtitle, int fadein, int stay, int fadeout) {
		PlayerConnection connection = GetConnection(player);
		if(fadein > -1) {
			connection.sendPacket(new PacketPlayOutTitle(fadein, stay, fadeout));
		}
		if(subtitle != null) {
			connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, 
					Serialize("{\"text\": \"" + subtitle + "\"}")));
		}
		connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, 
				Serialize("{\"text\": \"" + title + "\"}")));
	}
	
	static void SendListHeaderFooter(MCPlayer player, String header, String footer) {
		PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
		serializer.a("{\"text\": \"" + header + "\"}");
		serializer.a("{\"text\": \"" + footer + "\"}");

		PacketPlayOutPlayerListHeaderFooter listPacket = new PacketPlayOutPlayerListHeaderFooter();
		try {
			listPacket.a(serializer);
			GetConnection(player).sendPacket(listPacket);
		} catch(IOException ex) {
			// failed
		}
	}

	static void Sleep(MCPlayer p, MCLocation loc, Target t) {
		EntityPlayer player = ((CraftPlayer) p.getHandle()).getHandle();
		BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		Either<EntityHuman.EnumBedResult, Unit> result;
		try {
			result = player.sleep(pos);
		} catch(IllegalArgumentException ex) {
			throw new CREException("That is not a bed.", t);
		}
		result.ifRight((unit) -> {
			IBlockData blockData = player.getWorld().getType(pos);
			blockData = blockData.set(BlockStateBoolean.of("occupied"), true);
			player.getWorld().setTypeAndData(pos, blockData, 4);
		}).ifLeft((bedresult) -> {
			switch(bedresult) {
				case NOT_POSSIBLE_HERE:
					throw new CREException("It's not possible to sleep here.", t);
				case NOT_POSSIBLE_NOW:
					throw new CREException("It's not possible to sleep now.", t);
				case TOO_FAR_AWAY:
					throw new CREException("That bed is too far away.", t);
				case OBSTRUCTED:
					throw new CREException("That bed is obstructed.", t);
				case OTHER_PROBLEM:
					throw new CREException("Can't sleep for some reason.", t);
				case NOT_SAFE:
					throw new CREException("It's not safe to sleep.", t);
			}
		});
	}
	
	static void OpenBook(MCPlayer p, CArray pages, Target t) {
		CraftPlayer player = (CraftPlayer) p.getHandle();
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookmeta = (BookMeta) book.getItemMeta();
		if(bookmeta == null) {
			throw new CRENullPointerException("Book meta is null. This shouldn't happen and may be a problem with the server.", t);
		}
		List<IChatBaseComponent> pageList = new ArrayList<>();
		for(int i = 0; i < pages.size(); i++) {
			String text = pages.get(i, t).val();
			IChatBaseComponent component;
			if(text.charAt(0) == '[' || text.charAt(0) == '{') {
				try {
					component = IChatBaseComponent.ChatSerializer.a(text);
					pageList.add(component);
					continue;
				} catch(IllegalStateException | JsonSyntaxException ex) {}
			}
			text = text.length() > 320 ? text.substring(0, 320) : text;
			component = CraftChatMessage.fromString(text, true)[0];
			pageList.add(component);
		}
		((CraftMetaBook) bookmeta).pages = pageList;
		bookmeta.setTitle(" ");
		bookmeta.setAuthor(" ");
		book.setItemMeta(bookmeta);

		ItemStack currentItem = player.getInventory().getItemInMainHand();
		player.getInventory().setItemInMainHand(book);
		try {
			player.getHandle().openBook(CraftItemStack.asNMSCopy(book), EnumHand.MAIN_HAND);
		} finally {
			player.getInventory().setItemInMainHand(currentItem);
		}
	}

	static void OpenBook(MCPlayer p, String hand, Target t) {
		EntityPlayer player = ((CraftPlayer) p.getHandle()).getHandle();
		EnumHand h;
		try {
			h = EnumHand.valueOf(hand);
		} catch (IllegalArgumentException ex) {
			throw new CREIllegalArgumentException(ex.getMessage(), t);
		}
		net.minecraft.server.v1_15_R1.Item item;
		try {
			if(h == EnumHand.MAIN_HAND) {
				item = player.getItemInMainHand().getItem();
			} else {
				item = player.getItemInOffHand().getItem();
			}
		} catch (NullPointerException ex) {
			throw new CRENullPointerException(ex.getMessage(), t);
		}
		if(item == Items.WRITTEN_BOOK) {
			player.playerConnection.sendPacket(new PacketPlayOutOpenBook(h));
		} else {
			throw new CREIllegalArgumentException("No book in the given hand.", t);
		}
	}

	static void OpenSign(MCPlayer p, MCLocation signLoc, Target t) {
		World w = ((CraftWorld) signLoc.getWorld().getHandle()).getHandle();
		TileEntity te = w.getTileEntity(new BlockPosition(signLoc.getBlockX(), signLoc.getBlockY(), signLoc.getBlockZ()));
		if(!(te instanceof TileEntitySign)) {
			throw new CRECastException("This location is not a sign.", t);
		}
		TileEntitySign sign = (TileEntitySign) te;
		sign.isEditable = true;
		((CraftPlayer) p.getHandle()).getHandle().openSign(sign);
	}
	
	static void SwingHand(MCPlayer p, String h, Target t) {
		EnumHand hand;
		try {
			hand = EnumHand.valueOf(h);
		} catch(IllegalArgumentException ex) {
			throw new CREFormatException("Expected main_hand or off_hand but got \"" + h + "\".", t);
		}
		EntityPlayer player = ((CraftPlayer) p.getHandle()).getHandle();
		int handVal = hand.equals(EnumHand.MAIN_HAND) ? 0 : 3;
		player.playerConnection.sendPacket(new PacketPlayOutAnimation(player, handVal)); // send to player
	}
	
	static void SetSky(MCPlayer p, float a, float b) {
		PlayerConnection conn = GetConnection(p);
		conn.sendPacket(new PacketPlayOutGameStateChange(7, a));
		conn.sendPacket(new PacketPlayOutGameStateChange(8, b));
	}
}
