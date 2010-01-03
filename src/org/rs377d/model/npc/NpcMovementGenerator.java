package org.rs377d.model.npc;

import java.util.List;
import java.util.Random;

import org.rs377d.model.World;

public class NpcMovementGenerator implements Runnable
{

	private Random gen = new Random();

	@Override
	public void run()
	{
		for (Npc npc : World.getSingleton().getNpcList())
		{
			if (gen.nextInt(3) == 1)
			{
				List<Integer[]> locations = npc.getWalkableLocations();
				Integer[] step = locations.get(gen.nextInt(locations.size()));
				npc.getWalkingManager().reset();
				npc.getWalkingManager().addStep(step[0], step[1]);
				npc.getWalkingManager().finish();
			}
		}
	}

}
