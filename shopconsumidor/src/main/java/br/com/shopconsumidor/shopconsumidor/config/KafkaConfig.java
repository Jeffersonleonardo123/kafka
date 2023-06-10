package br.com.shopconsumidor.shopconsumidor.config;

import br.com.shopconsumidor.shopconsumidor.dto.ShopDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value(value = "${kafka.bootstrapAddress:localhost:9092}")
    private String bootstrapAddress;

    public ProducerFactory<String, ShopDto> producerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress );
        props.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class );
        props.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class );
        props.put( ProducerConfig.CLIENT_ID_CONFIG, "shop-api" );
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, ShopDto> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    public ConsumerFactory<String, ShopDto> consumerFactory(){
        JsonDeserializer<ShopDto> deserializer = new JsonDeserializer<>(ShopDto.class);
        Map<String, Object> props = new HashMap<>();
        props.put( ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress );
        return new DefaultKafkaConsumerFactory<>( props, new StringDeserializer(), deserializer );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ShopDto> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String ,ShopDto> factory= new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
