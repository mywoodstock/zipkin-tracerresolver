package io.zipkintracing.tracerresolver.internal;

import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerResolver;


public class ZipkinTracerResolver extends TracerResolver {

  @Override
  protected Tracer resolve() {
    return new ZipkinTracerFactory().getTracer();
  }

}
