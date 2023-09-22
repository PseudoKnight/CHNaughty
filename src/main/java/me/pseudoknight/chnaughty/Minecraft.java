package me.pseudoknight.chnaughty;

import com.google.gson.JsonSyntaxException;
import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CRENullPointerException;
import com.laytonsmith.core.natives.interfaces.Mixed;
import com.mojang.datafixers.util.Either;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.protocol.game.PacketPlayOutOpenBook;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Items;
import net.minecraft.world.EnumHand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

class Minecraft {
	static final int VIEW_DISTANCE = Bukkit.getViewDistance() * 16;

	static PlayerConnection GetConnection(MCPlayer player) {
		return ((CraftPlayer) player.getHandle()).getHandle().c;
	}

	static Entity GetEntity(MCEntity entity) {
		return ((CraftEntity) entity.getHandle()).getHandle();
	}

	static EntityPlayer GetPlayer(MCPlayer player) {
		return ((CraftPlayer) player.getHandle()).getHandle();
	}

	static void Sleep(MCPlayer p, MCLocation loc, Target t) {
		EntityPlayer player = GetPlayer(p);
		BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		Either<EntityHuman.EnumBedResult, Unit> result;
		try {
			result = player.startSleepInBed(pos, false);
		} catch(IllegalArgumentException ex) {
			throw new CREException("That is not a bed.", t);
		}
		result.ifLeft((bedresult) -> {
			switch (bedresult) {
				case a -> throw new CREException("It's not possible to sleep here.", t);
				case b -> throw new CREException("It's not possible to sleep now.", t);
				case c -> throw new CREException("That bed is too far away.", t);
				case d -> throw new CREException("That bed is obstructed.", t);
				case e -> throw new CREException("Can't sleep for some reason.", t);
				case f -> throw new CREException("It's not safe to sleep.", t);
			}
		});
	}

	static void OpenBook(MCPlayer p, Mixed data, Target t) {
		if(data instanceof CArray pages) {
			ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bookmeta = (BookMeta) book.getItemMeta();
			if(bookmeta == null) {
				throw new CRENullPointerException("Book meta is null. This shouldn't happen and may be a problem with the server.", t);
			}
			for(int i = 0; i < pages.size(); i++) {
				String text = pages.get(i, t).val();
				if(text.length() > 0 && (text.charAt(0) == '[' || text.charAt(0) == '{')) {
					try {
						bookmeta.spigot().addPage(ComponentSerializer.parse(text));
						continue;
					} catch(IllegalStateException | JsonSyntaxException ignored) {}
				}
				bookmeta.addPage(text);
			}
			bookmeta.setTitle(" ");
			bookmeta.setAuthor(" ");
			book.setItemMeta(bookmeta);
			((Player) p.getHandle()).openBook(book);
		} else {
			EntityPlayer player = GetPlayer(p);
			EnumHand h;
			try {
				h = EnumHand.valueOf(data.val());
			} catch (IllegalArgumentException ex) {
				throw new CREIllegalArgumentException(ex.getMessage(), t);
			}
			net.minecraft.world.item.Item item;
			try {
				item = player.b(h).d(); // mapped getItemInHand and Item
			} catch (NullPointerException ex) {
				throw new CRENullPointerException(ex.getMessage(), t);
			}
			if(item == Items.th) { // mapped written_book
				player.c.a(new PacketPlayOutOpenBook(h)); // mapped PlayerConnection.send
			} else {
				throw new CREIllegalArgumentException("No book in the given hand.", t);
			}
		}
	}
	
	static void SetSky(MCPlayer p, float a, float b) {
		PlayerConnection conn = GetConnection(p);
		conn.a(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.h, a)); // mapped to gamestate 7
		conn.a(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.i, b)); // mapped to gamestate 8
	}

	static void ActionMsg(MCPlayer p, String message) {
		BaseComponent txt = new net.md_5.bungee.api.chat.TextComponent(message);
		((Player) p.getHandle()).spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, txt);
	}
}
