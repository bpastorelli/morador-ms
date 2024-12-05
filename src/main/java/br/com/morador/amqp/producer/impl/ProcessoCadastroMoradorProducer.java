package br.com.morador.amqp.producer.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import br.com.morador.amqp.producer.KafkaTemplateAbstract;
import br.com.morador.dto.ProcessoCadastroDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProcessoCadastroMoradorProducer extends KafkaTemplateAbstract<ProcessoCadastroDto> {
	
	@Value("${processo.topic.name}")
	private String topic;
	
	public void producer(ProcessoCadastroDto dto) {
		
		kafkaTemplate.send(topic, dto).addCallback(
				success -> log.info("Message send " + success.getProducerRecord().value()),
				failure -> log.info("Message failure " + failure.getMessage())
		);	
		
	}

	@Async("asyncKafka")
	public void producerAsync(ProcessoCadastroDto dto) {
		
		Runnable runnable = () -> kafkaTemplate.send(topic, dto).addCallback(new ListenableFutureCallback<>() {

			@Override
			public void onSuccess(SendResult<String, ProcessoCadastroDto> result) {
				
				log.info("Mensagem enviada: " + result.getProducerRecord().value());
				
			}

			@Override
			public void onFailure(Throwable ex) {
				
				if(ex != null)
					log.error(ex.getMessage());
				
			}

        });
	    new Thread(runnable).start();
		
	}
	
}

