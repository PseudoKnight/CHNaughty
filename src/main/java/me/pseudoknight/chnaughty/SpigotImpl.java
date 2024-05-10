package me.pseudoknight.chnaughty;

import com.laytonsmith.PureUtilities.Common.ReflectionUtils;
import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.core.constructs.Target;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.ChunkCoordIntPair;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R4.CraftServer;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.EnumSet;

class SpigotImpl extends NMS {

	@Override
	public void relativeTeleport(MCPlayer p, MCLocation loc, Target t) {
		CraftPlayer player = (CraftPlayer) p.getHandle();

		// Spigot impl
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float yaw = loc.getYaw();
		float pitch = loc.getPitch();

		ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(new BlockPosition(
				loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
		EntityPlayer entityPlayer = player.getHandle();
		// mapped according to vanilla teleport command
		// EntityPlayer, World/WorldServer, ChunkProviderServer, post-teleport, entity int id
		entityPlayer.z().l().a(TicketType.g, chunkcoordintpair, 1, entityPlayer.al());
		player.eject();
		if (player.isSleeping()) {
			player.wakeup(true);
		}
		entityPlayer.c.teleport(x, y, z, yaw, pitch, EnumSet.allOf(RelativeMovement.class), PlayerTeleportEvent.TeleportCause.PLUGIN);
		entityPlayer.n(yaw);
	}

	@Override
	public double[] getTPS() {
		return ((CraftServer) Bukkit.getServer()).getServer().recentTps;
	}

	@Override
	void setStingerCount(MCPlayer p, int count, Target t) {
		EntityPlayer player = ((CraftPlayer) p.getHandle()).getHandle();
		player.r(count); // mapped to LivingEntity.setStingerCount()
	}

	@Override
	void setEntitySize(MCEntity e, float width, float height) {
		Entity entity = ((CraftEntity) e.getHandle()).getHandle();
		// mapped to Entity.dimensions field
		ReflectionUtils.set(Entity.class, entity, "bf", EntitySize.b(width, height));
	}
}
