package me.pseudoknight.chnaughty;

import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CRENullPointerException;
import com.mojang.datafixers.util.Either;
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
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;

class Minecraft {
	static final int VIEW_DISTANCE = Bukkit.getViewDistance() * 16;

	static PlayerConnection GetConnection(MCPlayer player) {
		return ((CraftPlayer) player.getHandle()).getHandle().b;
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

	static void OpenBook(MCPlayer p, String hand, Target t) {
		EntityPlayer player = GetPlayer(p);
		EnumHand h;
		try {
			h = EnumHand.valueOf(hand);
		} catch (IllegalArgumentException ex) {
			throw new CREIllegalArgumentException(ex.getMessage(), t);
		}
		net.minecraft.world.item.Item item;
		try {
			item = player.b(h).c(); // mapped getItemInHand and item type
		} catch (NullPointerException ex) {
			throw new CRENullPointerException(ex.getMessage(), t);
		}
		if(item == Items.rY) { // mapped written_book
			player.b.a(new PacketPlayOutOpenBook(h)); // mapped PlayerConnection.sendPacket
		} else {
			throw new CREIllegalArgumentException("No book in the given hand.", t);
		}
	}
	
	static void SetSky(MCPlayer p, float a, float b) {
		PlayerConnection conn = GetConnection(p);
		conn.a(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.h, a)); // mapped to gamestate 7
		conn.a(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.i, b)); // mapped to gamestate 8
	}
}
