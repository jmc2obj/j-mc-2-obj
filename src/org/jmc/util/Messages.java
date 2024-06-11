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
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
                if (resourceStream != null) {
                    return new PropertyResourceBundle(
                        new BufferedReader(
                            new InputStreamReader(resourceStream, StandardCharsets.UTF_8)
                    ));
                }
            }
            return null;
        }
    }
}
