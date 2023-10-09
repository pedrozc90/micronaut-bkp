package com.pedrozc90.core.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.StringReader;

public class XmlUtils {

    @SuppressWarnings(value = { "unchecked" })
    public static <T> T toObject(final byte[] content, final Class<T> clazz) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(clazz);
        return (T) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(content));
    }

    @SuppressWarnings(value = { "unchecked" })
    public static <T> T toObject(final String content, final Class<T> clazz) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(clazz);
        return (T) context.createUnmarshaller().unmarshal(new StringReader(content));
    }

}
