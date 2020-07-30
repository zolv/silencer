package com.greyturtle.silencer.service;

import com.greyturtle.silencer.di.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;

@Slf4j
public class SilencerService implements Service {
  private final String binPath = "play";

  public void silence() {
    final String messageFile = "silence-1.mp3";

    final List<String> cmd = new ArrayList<>();
    cmd.add(binPath);
    cmd.add(messageFile);
    try {
      new ProcessExecutor().command(cmd.toArray(new String[cmd.size()])).timeout(10, TimeUnit.SECONDS)
          .execute();
    } catch (InvalidExitValueException | IOException | InterruptedException | TimeoutException e) {
      e.printStackTrace();
    }
  }

}
