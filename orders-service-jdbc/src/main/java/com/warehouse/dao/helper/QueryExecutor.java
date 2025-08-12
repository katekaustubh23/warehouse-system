package com.warehouse.dao.helper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
public class QueryExecutor {

	private final Configuration freeMarker;

	public QueryExecutor() {
		this.freeMarker = createFreeMarkerConfiguration();
	}

	private Configuration createFreeMarkerConfiguration() {
		try {
			FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
			factory.setTemplateLoaderPath("classpath:/queries"); // ✅ Folder for templates
			return factory.createConfiguration(); // ✅ Infosight-style: no version, no extras
		} catch (IOException | TemplateException e) {
			throw new RuntimeException("Failed to create FreeMarker configuration", e);
		}
	}

	public String render(String templateName, Map<String, Object> params) {
		try (StringWriter writer = new StringWriter()) {
			Template template = freeMarker.getTemplate(templateName);
			template.process(params, writer);
			return writer.toString();
		} catch (IOException | TemplateException e) {
			throw new RuntimeException("Failed to render SQL template: " + templateName, e);
		}
	}
}
