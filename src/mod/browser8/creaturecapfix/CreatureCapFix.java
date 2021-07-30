package mod.browser8.creaturecapfix;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class CreatureCapFix implements WurmServerMod, PreInitable {
	
	private static Logger logger = Logger.getLogger(CreatureCapFix.class.getName());

	@Override
	public void preInit() {

		try {

			ClassPool classPool = HookManager.getInstance().getClassPool();
			CtClass ctZone = classPool.get("com.wurmonline.server.zones.Zone");
			
			for (CtMethod method  : ctZone.getDeclaredMethods("maySpawnCreatureTemplate")) {
				if (method.getParameterTypes().length == 4) {
					logger.log(Level.INFO, "Replacing maySpawnCreatureTemplate method...");
					method.setBody(

							"{ if (($1.isAggHuman() || $1.isMonster()) && (float) com.wurmonline.server.creatures.Creatures.getInstance().getNumberOfAgg()\r\n"
							+ "				/ (float) com.wurmonline.server.creatures.Creatures.getInstance().getNumberOfCreatures() > com.wurmonline.server.Servers.localServer.percentAggCreatures\r\n"
							+ "						/ 100.0F) {\r\n"
							+ "			return false;\r\n"
							+ "		} else if ($2 && com.wurmonline.server.creatures.Creatures.getInstance().getNumberOfTyped() >= com.wurmonline.server.Servers.localServer.maxTypedCreatures) {\r\n"
							+ "			return false;\r\n"
							+ "		} else if ($4) {\r\n"
							+ "			return com.wurmonline.server.creatures.Creatures.getInstance().getNumberOfKingdomCreatures() < com.wurmonline.server.Servers.localServer.maxCreatures\r\n"
							+ "					/ (com.wurmonline.server.Servers.localServer.PVPSERVER ? 50 : 200);\r\n"
							+ "		} else if (com.wurmonline.server.creatures.Creatures.getInstance().getNumberOfNice() > com.wurmonline.server.Servers.localServer.maxCreatures * (1 - com.wurmonline.server.Servers.localServer.percentAggCreatures / 100)\r\n"
							+ "				- ($3 ? breedingLimit : 0)) {\r\n"
							+ "			return false;\r\n"
							+ "		} else {\r\n"
							+ "			int nums = com.wurmonline.server.creatures.Creatures.getInstance().getCreatureByType($1.getTemplateId());\r\n"
							+ "			return (float) nums <= (float) com.wurmonline.server.Servers.localServer.maxCreatures * $1.getMaxPercentOfCreatures()\r\n"
							+ "					&& (!$1.usesMaxPopulation() || nums < $1.getMaxPopulationOfCreatures());\r\n"
							+ "		}}"

					);
					logger.log(Level.INFO, "Succesfully replaced maySpawnCreatureTemplate.");
				}
			}

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

	}

}
