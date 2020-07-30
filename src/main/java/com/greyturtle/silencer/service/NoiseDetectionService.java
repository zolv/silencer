package com.greyturtle.silencer.service;

import com.greyturtle.silencer.di.Context;
import com.greyturtle.silencer.di.Service;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoiseDetectionService implements Service {

  private GpioController gpio;

  private GpioPinDigitalInput soundInputPin;

  private Supplier< SilencerService> silencerService = () -> Context.get().getInstance(SilencerService.class.getSimpleName());
  
  public NoiseDetectionService() {
    initializeGpio();

  }

  private void initializeGpio() {
    this.gpio = GpioFactory.getInstance();

    initializeRedButton();
  }

  private void initializeRedButton() {
    this.soundInputPin = this.gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
    this.soundInputPin.setShutdownOptions(true);

    this.soundInputPin.addListener(new GpioPinListenerDigital() {

      @Override
      public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        silencerService.get().silence();
      }

    });
  }

  public void deinitialize() {
    this.gpio.shutdown();
  }
}
