package org.rs377d.net.update;

import org.rs377d.model.npc.Npc;

public class PreNpcUpdate implements Runnable
{

	private PlayerUpdateCoordinator coordinator;
	private Npc npc;

	public PreNpcUpdate(PlayerUpdateCoordinator coordinator, Npc npc)
	{
		this.coordinator = coordinator;
		this.npc = npc;
	}

	@Override
	public void run()
	{
		npc.getWalkingManager().processNextMovement();
		coordinator.getLatch().countDown();
	}

}
