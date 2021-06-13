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
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.protocol.game.PacketPlayOutOpenBook;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntitySign;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.World;
import net.minecraft.world.EnumHand;
import net.minecraft.SystemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

class Minecraft {
	static final int VIEW_DISTANCE = Bukkit.getViewDistance() * 16;

	static PlayerConnection GetConnection(MCPlayer player) {
		return ((CraftPlayer) player.getHandle()).getHandle().b;
	}

	static IChatBaseComponent Serialize(String msg) {
		return IChatBaseComponent.ChatSerializer.a(msg);
	}

	static void SendActionBarMessage(MCPlayer player, String msg) {
		GetConnection(player).sendPacket(
				new PacketPlayOutChat(Serialize("{\"text\": \"" + msg + "\"}"), ChatMessageType.c, SystemUtils.b));
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
				case a:
					throw new CREException("It's not possible to sleep here.", t);
				case b:
					throw new CREException("It's not possible to sleep now.", t);
				case c:
					throw new CREException("That bed is too far away.", t);
				case d:
					throw new CREException("That bed is obstructed.", t);
				case e:
					throw new CREException("Can't sleep for some reason.", t);
				case f:
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
		for(int i = 0; i < pages.size(); i++) {
			String text = pages.get(i, t).val();
			if(text.charAt(0) == '[' || text.charAt(0) == '{') {
				try {
					bookmeta.spigot().addPage(ComponentSerializer.parse(text));
					continue;
				} catch(IllegalStateException | JsonSyntaxException ex) {}
			}
			bookmeta.addPage(text);
		}
		bookmeta.setTitle(" ");
		bookmeta.setAuthor(" ");
		book.setItemMeta(bookmeta);

		ItemStack currentItem = player.getInventory().getItemInMainHand();
		player.getInventory().setItemInMainHand(book);
		try {
			player.getHandle().openBook(CraftItemStack.asNMSCopy(book), EnumHand.a);
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
		net.minecraft.world.item.Item item;
		try {
			if(h == EnumHand.a) {
				item = player.getItemInMainHand().getItem();
			} else {
				item = player.getItemInOffHand().getItem();
			}
		} catch (NullPointerException ex) {
			throw new CRENullPointerException(ex.getMessage(), t);
		}
		if(item == Items.rh) {
			player.b.sendPacket(new PacketPlayOutOpenBook(h));
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
		sign.f = true;
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
		int handVal = hand.equals(EnumHand.a) ? 0 : 3;
		player.b.sendPacket(new PacketPlayOutAnimation(player, handVal)); // send to player
	}
	
	static void SetSky(MCPlayer p, float a, float b) {
		PlayerConnection conn = GetConnection(p);
		conn.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.h, a));
		conn.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.i, b));
	}
}
