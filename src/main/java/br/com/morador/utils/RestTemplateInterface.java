package br.com.morador.utils;

public interface RestTemplateInterface<T, O> {
	
	public T restTemplate(O clazz);

}
