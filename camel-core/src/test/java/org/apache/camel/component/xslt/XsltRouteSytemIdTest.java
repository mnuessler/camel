package org.apache.camel.component.xslt;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;

import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.trans.CompilerInfo;

import org.apache.camel.builder.RouteBuilder;

public class XsltRouteSytemIdTest extends XsltRouteTest {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .to("xslt:org/apache/camel/component/xslt/transform.xsl?transformerFactoryClass="
                        + MockXsltTransformerFactoryImpl.class.getName())
                    .multicast()
                        .beanRef("testBean")
                        .to("mock:result");
            }
        };
    }

    public static class MockXsltTransformerFactoryImpl extends TransformerFactoryImpl {

        @Override
        public Templates newTemplates(Source source)
                throws TransformerConfigurationException {
            checkSystemId(source);
            return super.newTemplates(source);
        }

        @Override
        public Templates newTemplates(Source source, CompilerInfo info)
                throws TransformerConfigurationException {
            checkSystemId(source);
            return super.newTemplates(source, info);
        }

        private void checkSystemId(Source source) {
            String systemId = source.getSystemId();
            assertThat("system ID not set", systemId, notNullValue());
            try {
                new URL(systemId);
            } catch (MalformedURLException e) {
                fail("system ID is not a valid URL: " + systemId);
            }
        }

    }

}
