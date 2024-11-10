package me.pseudoknight.chnaughty;

import com.laytonsmith.PureUtilities.Common.ReflectionUtils;
import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.core.constructs.Target;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class PaperImpl extends NMS {

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
			Class<?> c = Class.forName("net.minecraft.world.entity.EntityDimensions");
			Method m = c.getDeclaredMethod("fixed", float.class, float.class);
			ReflectionUtils.set(Entity.class, ReflectionUtils.invokeMethod(entity, "getHandle"),
					"dimensions", m.invoke(null, width, height));
		} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}
}
