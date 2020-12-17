package cl.skaberen.google.docs.example.google_docs_example.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import cl.skaberen.google.docs.example.google_docs_example.configurations.intersept.GoogleSheetsInterceptor;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

	@Bean
	public HandlerInterceptor getControllerInterceptor() {
		HandlerInterceptor c = new GoogleSheetsInterceptor();
		return c;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getControllerInterceptor()).addPathPatterns("/api/google/validate/*");
	}
}
