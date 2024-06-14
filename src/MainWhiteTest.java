import org.junit.Test;
import javafx.scene.control.TextField;
import org.junit.*;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;
import static org.junit.Assert.*;

public class MainWhiteTest extends ApplicationTest  {

    @Override
    public void start(Stage stage) {
        new Main().start(stage);
    }

    @Before
    public void setUp() throws Exception {
        ApplicationTest.launch(Main.class);
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
    }
    @AfterClass
    public static void afterAll() throws Exception {
        // 关闭所有应用程序实例，确保所有窗口都关闭
        FxToolkit.cleanupStages();
    }

    @Test
    public void testQueryBridgeWords_1()  {
        TextField filePathField = lookup("#filePath").query();
        clickOn(filePathField).write("D:\\课件\\大三下\\软件工程\\lab1\\2021111662-lab1\\code\\src\\text.txt");
        clickOn("Load File");
        clickOn("Query Bridge Words");
        TextField InputField = lookup("#input").query();
        clickOn(InputField).write("us,it");
        TextField resultField = lookup("#result").query();
        clickOn("Execute");
        FxAssert.verifyThat(resultField, TextInputControlMatchers.hasText("No us or it in the graph!"));
    }
    @Test
    public void testQueryBridgeWords_2()  {
        TextField filePathField = lookup("#filePath").query();
        clickOn(filePathField).write("D:\\课件\\大三下\\软件工程\\lab1\\2021111662-lab1\\code\\src\\text.txt");
        clickOn("Load File");
        clickOn("Query Bridge Words");
        TextField InputField = lookup("#input").query();
        clickOn(InputField).write("world,it");
        TextField resultField = lookup("#result").query();
        clickOn("Execute");
        FxAssert.verifyThat(resultField, TextInputControlMatchers.hasText("No world or it in the graph!"));
    }
    @Test
    public void testQueryBridgeWords_3()  {
        TextField filePathField = lookup("#filePath").query();
        clickOn(filePathField).write("D:\\课件\\大三下\\软件工程\\lab1\\2021111662-lab1\\code\\src\\text.txt");
        clickOn("Load File");
        clickOn("Query Bridge Words");
        TextField InputField = lookup("#input").query();
        clickOn(InputField).write("think,so");
        TextField resultField = lookup("#result").query();
        clickOn("Execute");
        FxAssert.verifyThat(resultField, TextInputControlMatchers.hasText("No bridge words from \"think \" to \" so\" !"));
    }
    @Test
    public void testQueryBridgeWords_4()  {
        TextField filePathField = lookup("#filePath").query();
        clickOn(filePathField).write("D:\\课件\\大三下\\软件工程\\lab1\\2021111662-lab1\\code\\src\\text.txt");
        clickOn("Load File");
        clickOn("Query Bridge Words");
        TextField InputField = lookup("#input").query();
        clickOn(InputField).write("with,so");
        TextField resultField = lookup("#result").query();
        clickOn("Execute");
        FxAssert.verifyThat(resultField, TextInputControlMatchers.hasText("No bridge words from \"with \" to \" so\" !"));
    }
    @Test
    public void testQueryBridgeWords_5()  {
        TextField filePathField = lookup("#filePath").query();
        clickOn(filePathField).write("D:\\课件\\大三下\\软件工程\\lab1\\2021111662-lab1\\code\\src\\text.txt");
        clickOn("Load File");
        clickOn("Query Bridge Words");
        TextField InputField = lookup("#input").query();
        clickOn(InputField).write("so,think");
        TextField resultField = lookup("#result").query();
        clickOn("Execute");
        FxAssert.verifyThat(resultField, TextInputControlMatchers.hasText("No bridge words from \"so \" to \" think\" !"));
    }
    @Test
    public void testQueryBridgeWords_6()  {
        TextField filePathField = lookup("#filePath").query();
        clickOn(filePathField).write("D:\\课件\\大三下\\软件工程\\lab1\\2021111662-lab1\\code\\src\\text.txt");
        clickOn("Load File");
        clickOn("Query Bridge Words");
        TextField InputField = lookup("#input").query();
        clickOn(InputField).write("you,so");
        TextField resultField = lookup("#result").query();
        clickOn("Execute");
        FxAssert.verifyThat(resultField, TextInputControlMatchers.hasText("The bridge words from \" you\" to \"so \" are: think."));
    }

    @Test
    public void testQueryBridgeWords_7()  {
        TextField filePathField = lookup("#filePath").query();
        clickOn(filePathField).write("D:\\课件\\大三下\\软件工程\\lab1\\2021111662-lab1\\code\\src\\text.txt");
        clickOn("Load File");
        clickOn("Query Bridge Words");
        TextField InputField = lookup("#input").query();
        clickOn(InputField).write("new,and");
        TextField resultField = lookup("#result").query();
        clickOn("Execute");
        FxAssert.verifyThat(resultField, TextInputControlMatchers.hasText("The bridge words from \" new\" to \"and \" are: world,civilizations,life."));
    }
}