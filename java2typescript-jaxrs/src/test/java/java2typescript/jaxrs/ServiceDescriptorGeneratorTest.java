package java2typescript.jaxrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.cxf.helpers.IOUtils;
import org.junit.Test;

import java2typescript.function.TestResource;
import java2typescript.jackson.module.grammar.Module;
import java2typescript.jackson.module.grammar.base.AbstractNamedType;

public class ServiceDescriptorGeneratorTest {

  @Test
  public void generateTypeScriptDefinitionsTest() throws Exception {
    ServiceDescriptorGenerator descGen = new ServiceDescriptorGenerator(Arrays.asList(TestResource.class));
    assertNotNull(descGen);
    Module tsModule = descGen.generateTypeScript("Pre", "ctx", "ctx");
    assertModule(tsModule);
  }

  private void assertModule(Module tsModule) {
    try (StringWriter writer = new StringWriter()) {
      assertModule(tsModule, writer);
      String expected = loadResource("java2typescript/jaxrs/output.ts");
      String actual = writer.toString();
      assertEquals(expected, actual);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  private void assertModule(Module tsModule, Writer writer) {
    assertNotNull(tsModule);
    final Consumer<AbstractNamedType> write = (t) -> {
      try {
        t.writeDef(writer);
      } catch (Exception e) {
        fail(e.getMessage());
      }
    };
    tsModule.getModules().values().forEach(m -> assertModule(m, writer));
    write.accept(tsModule);
    tsModule.getNamedTypes().values().stream().sorted((a, b) -> a.getDefName().compareToIgnoreCase(b.getDefName())).forEach(write);
  }

  private String loadResource(String resource) {
    try (InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resource));
         StringWriter writer = new StringWriter()) {
      IOUtils.copy(reader, writer, 4096);
      return writer.toString();
    } catch (Exception e) {
      fail(e.getMessage());
    }
    return null;
  }
}
