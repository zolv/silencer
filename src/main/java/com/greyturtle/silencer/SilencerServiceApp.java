package com.greyturtle.silencer;

import com.greyturtle.silencer.common.service.JsonMapperService;
import com.greyturtle.silencer.di.Context;
import com.greyturtle.silencer.service.NoiseDetectionService;
import com.greyturtle.silencer.service.SilencerService;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import java.util.Optional;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SilencerServiceApp {

  public static void main(final String[] args) {
    start(8080, (args.length > 0) && args[0].contentEquals("withBalance"));
  }

  public static void start(final int port, final boolean withBalance) {
    final Context context = Context.get();

    context.getWithBalance().set(withBalance);

    context.registerSingleton(JsonMapperService.class.getSimpleName(), new JsonMapperService());
    context.registerSingleton(SilencerService.class.getSimpleName(), new SilencerService());
    context.registerSingleton(NoiseDetectionService.class.getSimpleName(), new NoiseDetectionService());

    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    context.registerSingleton(Validator.class.getSimpleName(), factory.getValidator());

    final Javalin server = Javalin.create();
    context.setServerInstance(server);

    server.start(port);
    server.get(
        "/",
        ctx -> {
          ctx.result("Hello World");
        });

    server.exception(
        NotFoundResponse.class,
        (e, ctx) -> {
          ctx.status(404);
        });
  }

  public static void stop() {
    Optional.ofNullable(Context.get().getServerInstance()).ifPresent(server -> server.stop());
  }
}
