package io.zipkintracing.tracerresolver.internal;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerFactory;

import io.opentracing.noop.NoopTracerFactory;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ZipkinTracerFactory implements TracerFactory {

  private final static String ZIPKIN_ENDPOINT_KEY = "zipkin.endpoint";
  private final static String ZIPKIN_SERVICENAME_KEY = "zipkin.servicename";

  public static final String[] PROPERTIES = {
      ZIPKIN_ENDPOINT_KEY,
      ZIPKIN_SERVICENAME_KEY
  };

  private final static String ZIPKIN_URL = "https://zipkin-staging.onerainc.com/api/v2/spans";

  private final static Logger logger = Logger.getLogger(ZipkinTracerFactory.class.getName());

  public Tracer getTracer() {

    Properties props = ZipkinTracerParameters.loadConfigurationFile();
    for (String paramName : PROPERTIES) {
      String paramValue = System.getProperty(paramName);
      if (paramValue != null) {
        props.setProperty(paramName, paramValue);
      }
    }
    for (String propName: props.stringPropertyNames()) {
      logger.log(Level.INFO, "Retrieved Tracer parameter " + propName + "=" + props.getProperty(propName));
    }

    String zipkinEndpoint = ZIPKIN_URL;
    if (props.containsKey(ZIPKIN_ENDPOINT_KEY)) {
      zipkinEndpoint = props.getProperty(ZIPKIN_ENDPOINT_KEY);
    }

    String zipkinServicename = "default_service_name";
    if (props.containsKey(ZIPKIN_SERVICENAME_KEY)) {
      zipkinServicename = props.getProperty(ZIPKIN_SERVICENAME_KEY);
    }

    try {
      final URLConnectionSender sender = URLConnectionSender.create(zipkinEndpoint);
      final AsyncReporter reporter = AsyncReporter
                                         .builder(sender)
                                         .messageTimeout(100000L, TimeUnit.MILLISECONDS)
                                         .build();
      final Tracing braveTracing = Tracing.newBuilder()
                                       .traceId128Bit(true)
                                       .localServiceName(zipkinServicename)
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

