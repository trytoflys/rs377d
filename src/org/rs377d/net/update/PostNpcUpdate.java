package org.rs377d.net.update;

import org.rs377d.model.npc.Npc;

public class PostNpcUpdate implements Runnable
{
	
	private PlayerUpdateCoordinator coordinator;
	private Npc npc;
	
	public PostNpcUpdate(PlayerUpdateCoordinator coordinator, Npc npc)
	{
		this.coordinator = coordinator;
		this.npc = npc;
	}
	
	@Override
	public void run()
	{
		npc.getUpdateFlags().reset();
		coordinator.getLatch().countDown();
	}

}
