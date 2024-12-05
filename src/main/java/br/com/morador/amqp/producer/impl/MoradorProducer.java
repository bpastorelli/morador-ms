package br.com.morador.amqp.producer.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import br.com.morador.amqp.producer.KafkaTemplateAbstract;
import br.com.morador.dto.MoradorDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MoradorProducer extends KafkaTemplateAbstract<MoradorDto> {
	
	@Value("${residencia.topic.name}")
	private String topic;
	
	public void producer(@Payload MoradorDto dto) {
		
		kafkaTemplate.send(topic, dto).addCallback(
				success -> log.info("Mensagem publicada: {}", success.getProducerRecord().value()),
				failure -> log.info("Erro ao publicar mensagem: {}", failure.getMessage())
		);	
		
	}

	@Async("asyncKafka")
	public void producerAsync(@Payload MoradorDto dto) {
		
		Runnable runnable = () -> kafkaTemplate.send(topic, dto).addCallback(new ListenableFutureCallback<>() {

			@Override
			public void onSuccess(SendResult<String, MoradorDto> result) {
				
				log.info("Mensagem publicada: {}", result.getProducerRecord().value());
				
			}

			@Override
			public void onFailure(Throwable ex) {
				
				if(ex != null)
					log.error("Erro ao publicar mensagem: {}", ex.getMessage());
				
			}

        });
	    new Thread(runnable).start();
		
	}
	
}

