package me.pseudoknight.chnaughty;

import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.core.constructs.Target;

public abstract class NMS {

	private static NMS Impl;

	public static NMS GetImpl() {
		if(Impl == null) {
			try {
				Class.forName("io.papermc.paper.configuration.Configuration");
				Impl = new PaperImpl();
			} catch (ClassNotFoundException e) {
				Impl = new SpigotImpl();
			}
		}
		return Impl;
	}

	abstract void relativeTeleport(MCPlayer p, MCLocation loc, Target t);
	abstract double[] getTPS();
	abstract void setStingerCount(MCPlayer p, int count, Target t);
	abstract void setEntitySize(MCEntity e, float width, float height);
}
