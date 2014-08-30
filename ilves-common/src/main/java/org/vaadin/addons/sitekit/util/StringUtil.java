/**
 * Copyright 2013 Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addons.sitekit.util;

/**
 * String utlities.
 *
 * @author Tommi S.E. Laukkanen
 */
public class StringUtil {

    /**
     * HTML encodes given string.
     * @param string string to encode
     * @return the encoded string.
     */
    public static String htmlEncode(final String string) {
        final StringBuffer out = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            final char c = string.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>') {
                out.append("&#" + (int) c + ";");
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * Converts bytes to HEX String.
     *
     * @param bytes the bytes
     * @return the HEX string
     */
    public static String toHexString(byte[] bytes) {
        char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j*2] = hexArray[v/16];
            hexChars[j*2 + 1] = hexArray[v%16];
        }
        return new String(hexChars);
    }

    /**
     * Converts camel case string to localization key convetion.
     * This can be used to generate localization keys from property names.
     *
     * @param camelCaseString the camel case string
     * @return the localization key convention string
     */
    public static String fromCamelCaseToLocalizationKeyConvetion(final String camelCaseString) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < camelCaseString.length(); i++) {
            final char character = camelCaseString.charAt(i);
            if (Character.isLowerCase(character)) {
                stringBuilder.append(character);
            } else {
                if (i != 0) {
                    stringBuilder.append('-');
                }
                stringBuilder.append(Character.toLowerCase(character));
            }
        }
        return stringBuilder.toString();
    }
}
