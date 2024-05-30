package org.jmc.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle.Control;

public class Messages {
    private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME, new UTF8Control());

    private Messages() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    private static class UTF8Control extends Control {
        @Override
        public ResourceBundle newBundle(String baseName, java.util.Locale locale, String format, ClassLoader loader, boolean reload)
                throws java.io.IOException {
            String bundleName = baseName;
            if (!locale.getLanguage().equals("en")) {
                bundleName = toBundleName(baseName, locale);
            }
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            loader.getResourceAsStream(resourceName),
                            StandardCharsets.UTF_8))) {
                bundle = new PropertyResourceBundle(reader);
            } catch (Exception e) {
                throw e;
            }
            return bundle;
        }
    }
}
