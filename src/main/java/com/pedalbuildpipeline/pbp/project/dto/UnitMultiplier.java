package com.pedalbuildpipeline.pbp.project.dto;

public enum UnitMultiplier {
  PICO(1e-12),
  NANO(1e-9),
  MICRO(1e-6),
  MILLI(0.001),
  NONE(1),
  KILO(1000),
  MEGA(1000000);

  private double multiplier;

  UnitMultiplier(double multiplier) {
    this.multiplier = multiplier;
  }

  public double getMultiplier() {
    return multiplier;
  }
}
