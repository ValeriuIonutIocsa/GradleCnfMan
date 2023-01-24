package com.utils.gradle.run_cnf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

final class EclipseRunCnfUtils {

    private EclipseRunCnfUtils() {
    }

    static void writeEclipseRunCnfAttribute(
            final String tagName,
            final String key,
            final String value,
            final String[] listEntryValueArray,
            final Element documentElement) {

        final Document document = documentElement.getOwnerDocument();
        final Element element = document.createElement(tagName);
        element.setAttribute("key", key);
        if (value != null) {
            element.setAttribute("value", value);
        }

        if (listEntryValueArray != null) {

            for (final String listEntryValue : listEntryValueArray) {

                final Element listEntryElement = document.createElement("listEntry");
                listEntryElement.setAttribute("value", listEntryValue);

                element.appendChild(listEntryElement);
            }
        }

        documentElement.appendChild(element);
    }
}
