package io.zipkintracing.tracerresolver.internal;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerFactory;

import io.opentracing.noop.NoopTracerFactory;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.util.concurrent.TimeUnit;


public class ZipkinTracerFactory implements TracerFactory {

  private final static String ZIPKIN_URL = "https://zipkin-staging.onerainc.com/api/v2/spans";

  public Tracer getTracer() {
    try {
      final URLConnectionSender sender = URLConnectionSender.create(ZIPKIN_URL);
      final AsyncReporter reporter = AsyncReporter
                                         .builder(sender)
                                         .messageTimeout(100000L, TimeUnit.MILLISECONDS)
                                         .build();
      final Tracing braveTracing = Tracing.newBuilder()
                                       .traceId128Bit(true)
                                       .localServiceName("SOME SERVICE NAME")
                                       .spanReporter(reporter)
                                       .build();
      return BraveTracer.newBuilder(braveTracing)
                 .build();

    } catch (Exception e) {
      e.printStackTrace();
      return NoopTracerFactory.create();
    }
  }
}

