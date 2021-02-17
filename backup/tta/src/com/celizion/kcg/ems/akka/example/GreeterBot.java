package com.celizion.kcg.ems.akka.example;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class GreeterBot extends AbstractBehavior<Object> {

	public static Behavior<Object> create(int max) {
		return Behaviors.setup(context -> new GreeterBot(context, max));
	}

	private final int max;
	private int greetingCounter;

	private GreeterBot(ActorContext<Object> context, int max) {
		super(context);
		this.max = max;
	}

	@Override
	public Receive<Object> createReceive() {
		return newReceiveBuilder().onMessage(Greeted.class, this::onGreeted)
				.onMessage(SampleModel.class, this::onSampleModel).build();
	}

	private Behavior<Object> onGreeted(Greeted message) {
		greetingCounter++;

		getContext().getLog().info("[{}] Greeting {} for {}", getContext().getSelf().path(), greetingCounter,
				message.whom);
		if (greetingCounter == max) {
			return Behaviors.stopped();
		} else {
			message.from.tell(new Greeter.Greet(message.whom, getContext().getSelf()));
			return this;
		}
	}

	private Behavior<Object> onSampleModel(SampleModel message) {
		getContext().getLog().info("{}", message);
		return this;
	}
}
