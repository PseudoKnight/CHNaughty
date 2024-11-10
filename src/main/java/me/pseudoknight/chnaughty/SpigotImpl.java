package me.pseudoknight.chnaughty;

import com.laytonsmith.PureUtilities.Common.ReflectionUtils;
import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.core.constructs.Target;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R2.CraftServer;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;

class SpigotImpl extends NMS {

	@Override
	public double[] getTPS() {
		return ((CraftServer) Bukkit.getServer()).getServer().recentTps;
	}

	@Override
	void setStingerCount(MCPlayer p, int count, Target t) {
		EntityPlayer player = ((CraftPlayer) p.getHandle()).getHandle();
		player.p(count); // mapped to EntityLiving's second Integer DataWatcher
	}

	@Override
	void setEntitySize(MCEntity e, float width, float height) {
		Entity entity = ((CraftEntity) e.getHandle()).getHandle();
		// mapped to Entity.dimensions field
		ReflectionUtils.set(Entity.class, entity, "bb", EntitySize.b(width, height));
	}
}
