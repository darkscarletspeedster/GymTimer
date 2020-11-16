package com.example.gymtimer.common;

public class BounceInterpolator implements android.view.animation.Interpolator {
  private double amplitude;
  private double frequency;

  public BounceInterpolator(double amplitude, double frequency) {
    this.amplitude = amplitude;
    this.frequency = frequency;
  }

  public float getInterpolation(float time) {
    return (float) (-1 * Math.pow(Math.E, -time/ amplitude) *
      Math.cos(frequency * time) + 1);
  }
}
