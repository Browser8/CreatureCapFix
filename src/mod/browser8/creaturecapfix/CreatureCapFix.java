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

							"{"
							+ "    com.wurmonline.server.creatures.Creatures cInstance = com.wurmonline.server.creatures.Creatures.getInstance();"
							+ "    if (($1.isAggHuman() || $1.isMonster()) && (float) cInstance.getNumberOfAgg() / (float) com.wurmonline.server.Servers.localServer.maxCreatures > com.wurmonline.server.Servers.localServer.percentAggCreatures / 100.0F) {"
							+ "        return false;"
							+ "    } else if ($2 && cInstance.getNumberOfTyped() >= com.wurmonline.server.Servers.localServer.maxTypedCreatures) {"
							+ "        return false;"
							+ "    } else if ($4) {"
							+ "        return cInstance.getNumberOfKingdomCreatures() < com.wurmonline.server.Servers.localServer.maxCreatures / (com.wurmonline.server.Servers.localServer.PVPSERVER ? 50 : 200);"
							+ "    } else if (cInstance.getNumberOfNice() >= com.wurmonline.server.Servers.localServer.maxCreatures * (1 - com.wurmonline.server.Servers.localServer.percentAggCreatures / 100.0F) - ($3 ? breedingLimit : 0)) {"
							+ "        return false;"
							+ "    } else {"
							+ "        int nums = cInstance.getCreatureByType($1.getTemplateId());"
							+ "        return (float) nums <= (float) com.wurmonline.server.Servers.localServer.maxCreatures * $1.getMaxPercentOfCreatures() && (!$1.usesMaxPopulation() || nums < $1.getMaxPopulationOfCreatures());"
							+ "    }"
							+ "}"

					);
					logger.log(Level.INFO, "Succesfully replaced maySpawnCreatureTemplate.");
				}
			}

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

	}

}
