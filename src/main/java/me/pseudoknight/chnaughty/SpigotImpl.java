package me.pseudoknight.chnaughty;

import com.laytonsmith.PureUtilities.Common.ReflectionUtils;
import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.abstraction.bukkit.BukkitMCServer;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREUnsupportedOperationException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntitySize;
import org.bukkit.Bukkit;

class SpigotImpl extends NMS {

	private final Class craftServer;

	private final String setStingerCount;
	private final String dimensions;

	SpigotImpl() {
		String version = ((BukkitMCServer) Static.getServer()).getCraftBukkitPackage().split("\\.")[3];

		try {
			String craftBukkitPackage = "org.bukkit.craftbukkit." + version;
			craftServer = Class.forName(craftBukkitPackage + ".CraftServer");
		} catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		// mapped to EntityLiving's second Integer DataWatcher
		setStingerCount = switch(version) {
			case "v1_20_R4" -> "r";
			case "v1_21_R1" -> "q";
			default -> "p";
		};

		// mapped to Entity.dimensions field
		dimensions = switch(version) {
			case "v1_20_R4" -> "bf";
			case "v1_21_R1" -> "bd";
			case "v1_21_R2", "v1_21_R3" -> "bb";
			case "v1_21_R4" -> "be";
			case "v1_21_R5" -> "by";
			default -> "bz";
		};
	}

	@Override
	void relativeTeleport(MCPlayer p, MCLocation loc, Target t) {
		throw new CREUnsupportedOperationException("relative_teleport() unsupported on Spigot", t);
	}

	@Override
	public double[] getTPS() {
		return ((MinecraftServer) ReflectionUtils.invokeMethod(craftServer, Bukkit.getServer(), "getServer")).recentTps;
	}

	@Override
	void setStingerCount(MCPlayer p, int count, Target t) {
		EntityPlayer player = ReflectionUtils.invokeMethod(p.getHandle(), "getHandle");
		ReflectionUtils.invokeMethod(EntityLiving.class, player, setStingerCount, new Class[]{int.class}, new Object[]{count});
	}

	@Override
	void setEntitySize(MCEntity e, float width, float height) {
		Entity entity = ReflectionUtils.invokeMethod(e.getHandle(), "getHandle");
		ReflectionUtils.set(Entity.class, entity, dimensions, EntitySize.c(width, height));
	}
}
