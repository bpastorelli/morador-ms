package br.com.morador.converter;

public interface Converter<T, Z> {
	
	T convert(Z object);

}
