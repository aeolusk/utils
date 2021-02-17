package com.celizion.kcg.ems.akka.example;

public class SampleModel {
	public final String name;

	public SampleModel(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "SampleModel [name=" + name + "]";
	}
}
