package java2typescript.jaxrs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Test;

import java2typescript.jackson.module.grammar.Module;

public class ServiceDescriptorGeneratorTest {

  @Test
  public void generateTypeScriptDefinitionsTest() throws Exception {
    ServiceDescriptorGenerator descGen = new ServiceDescriptorGenerator(Arrays.asList(TestResource.class));
    assertNotNull(descGen);
    Module tsModule = descGen.generateTypeScript("fm", "myMod", "/a/t/");
    assertNotNull(tsModule);
    StringWriter writer = new StringWriter();
    tsModule.write(writer);
    writer.close();
    assertTrue(writer.getBuffer().length() > 0);
  }

}
