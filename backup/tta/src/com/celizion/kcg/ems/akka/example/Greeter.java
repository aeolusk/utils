package com.celizion.kcg.ems.akka.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

// #greeter
public class Greeter extends AbstractBehavior<Greeter.Greet> {

	public static final class Greet {
		public final String whom;
		public final ActorRef<Object> replyTo;

		public Greet(String whom, ActorRef<Object> replyTo) {
			this.whom = whom;
			this.replyTo = replyTo;
		}
	}

	public static Behavior<Greet> create() {
		return Behaviors.setup(Greeter::new);
	}

	private Greeter(ActorContext<Greet> context) {
		super(context);
	}

	@Override
	public Receive<Greet> createReceive() {
		return newReceiveBuilder().onMessage(Greet.class, this::onGreet).build();
	}

	private Behavior<Greet> onGreet(Greet command) {
		getContext().getLog().info("Hello {}!", command.whom);
		// #greeter-send-message
		command.replyTo.tell(new SampleModel("sample data to " + command.whom));
		command.replyTo.tell(new Greeted(command.whom, getContext().getSelf()));
		// #greeter-send-message
		return this;
	}
}
// #greeter
