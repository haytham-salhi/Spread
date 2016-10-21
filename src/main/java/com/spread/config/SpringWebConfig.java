package com.spread.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan({"com.spread.frontcontrollers"})
public class SpringWebConfig extends WebMvcConfigurerAdapter {

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/jsp/");
		viewResolver.setSuffix(".jsp");
		
		return viewResolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations(
				"/WEB-INF/static-pages/", "/WEB-INF/images/");
	}

//	@Override
//	public void configureMessageConverters(
//			List<HttpMessageConverter<?>> converters) {
//
//		ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
//		ArrayList<MediaType> supportedMediaTypes = new ArrayList<>();
//		supportedMediaTypes.add(MediaType.IMAGE_JPEG);
//		supportedMediaTypes.add(MediaType.IMAGE_PNG);
//		byteArrayHttpMessageConverter
//				.setSupportedMediaTypes(supportedMediaTypes);
//		converters.add(byteArrayHttpMessageConverter);
//
//		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(
//				Charset.forName("UTF-8"));
//		converters.add(stringConverter);
//
//		final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//		final ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//		objectMapper.configure(
//				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		converter.setObjectMapper(objectMapper);
//		converters.add(converter);
//		super.configureMessageConverters(converters);
//	}
}

