package com.celizion.kcg.ems.akka;

import akka.actor.ActorPath;

public enum AddressBook {
	// @formatter:off
	ROOT("kcgems"),
	CENTER_EMS_PROXY("cemsproxy"), 
	NMX_PROXY("nmsproxy")
	;
	// @formatter:on

	private final String pathName;
	private ActorPath actorPath;

	private AddressBook(String pathName) {
		this.pathName = pathName;
	}

	public String getPathName() {
		return pathName;
	}

	public ActorPath getActorPath() {
		return actorPath;
	}

	public void setActorPath(ActorPath actorPath) {
		this.actorPath = actorPath;
	}
}
