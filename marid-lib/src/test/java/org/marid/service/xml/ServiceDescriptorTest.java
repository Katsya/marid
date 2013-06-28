/*
 * Copyright (C) 2013 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.service.xml;

import org.junit.Test;
import org.marid.service.ServiceDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

/**
 * @author Dmitry Ovchinnikov
 */
public class ServiceDescriptorTest {

    @Test
    public void test1() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JAXBContext ctx = JAXBContext.newInstance(ServiceDescriptor.class);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        ServiceDescriptor sd = new ServiceDescriptor();
        sd.addService("a", "b");
        sd.setDelegateId("x");
        marshaller.marshal(sd, bos);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        ServiceDescriptor sdc = (ServiceDescriptor) unmarshaller.unmarshal(bis);
        assertEquals(sd, sdc);
    }
}