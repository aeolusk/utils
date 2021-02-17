package com.celizion.kcg.ems.akka.example;

import java.util.Objects;

import com.celizion.kcg.ems.akka.example.Greeter.Greet;

import akka.actor.typed.ActorRef;

public class Greeted {
	public final String whom;
	public final ActorRef<Greet> from;

	public Greeted(String whom, ActorRef<Greet> from) {
		this.whom = whom;
		this.from = from;
	}

//#greeter
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Greeted greeted = (Greeted) o;
		return Objects.equals(whom, greeted.whom) && Objects.equals(from, greeted.from);
	}

	@Override
	public int hashCode() {
		return Objects.hash(whom, from);
	}

	@Override
	public String toString() {
		return "Greeted{" + "whom='" + whom + '\'' + ", from=" + from + '}';
	}
//#greeter

}
