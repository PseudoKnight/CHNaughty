package me.pseudoknight.chnaughty;

import com.laytonsmith.PureUtilities.Common.ReflectionUtils;
import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.core.constructs.Target;
import io.papermc.paper.entity.TeleportFlag;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class PaperImpl extends NMS {

	private final Method fixedDimensions;

	PaperImpl() {
		try {
			Class c = Class.forName("net.minecraft.world.entity.EntityDimensions");
			fixedDimensions = c.getDeclaredMethod("fixed", float.class, float.class);
		} catch(ClassNotFoundException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void relativeTeleport(MCPlayer p, MCLocation loc, Target t) {
		((Player) p.getHandle()).teleport((Location) loc.getHandle(), PlayerTeleportEvent.TeleportCause.PLUGIN,
				TeleportFlag.Relative.values());
	}

	@Override
	public double[] getTPS() {
		return Bukkit.getServer().getTPS();
	}

	@Override
	void setStingerCount(MCPlayer p, int count, Target t) {
		((Player) p.getHandle()).setBeeStingersInBody(count);
	}

	@Override
	void setEntitySize(MCEntity e, float width, float height) {
		org.bukkit.entity.Entity entity = ((org.bukkit.entity.Entity) e.getHandle());
		try {
			ReflectionUtils.set(Entity.class, ReflectionUtils.invokeMethod(entity, "getHandle"),
					"dimensions", fixedDimensions.invoke(null, width, height));
		} catch (InvocationTargetException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}
}
