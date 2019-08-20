package com.torresj.apisensorserver.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

@Configuration
public class MqttConf {

  private MqttConsumer mqttConsumer;

  @Value("${spring.rabbitmq.host}")
  private String host;

  @Value("${spring.rabbitmq.mqtt.port}")
  private String port;

  @Value("${spring.rabbitmq.mqtt.topic}")
  private String topic;

  @Value("${spring.rabbitmq.username}")
  private String user;

  @Value("${spring.rabbitmq.password}")
  private String pass;

  public MqttConf(MqttConsumer mqttConsumer) {
    this.mqttConsumer = mqttConsumer;
  }

  @Bean
  public MqttPahoClientFactory mqttClientFactory() {
    DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    MqttConnectOptions options = new MqttConnectOptions();
    options.setServerURIs(new String[]{"tcp://" + host + ":" + port});
    options.setUserName(user);
    options.setPassword(pass.toCharArray());
    factory.setConnectionOptions(options);
    return factory;
  }

  @Bean
  public IntegrationFlow mqttInFlow() {
    return IntegrationFlows.from(mqttInbound()).transform(p -> p)
        .handle(mqttConsumer, "messageHandler").get();
  }

  @Bean
  public MessageProducerSupport mqttInbound() {
    MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
        "mqttServer",
        mqttClientFactory(), topic);
    adapter.setCompletionTimeout(5000);
    adapter.setConverter(new DefaultPahoMessageConverter());
    adapter.setQos(1);
    return adapter;
  }
}