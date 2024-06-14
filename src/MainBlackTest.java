import javafx.scene.control.TextField;
import org.junit.*;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;

public class MainBlackTest extends ApplicationTest {


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
    public void testCalculateShortestPath_1() throws InterruptedException {
        TextField filePathField = lookup("#filePath").query();
        clickOn(filePathField).write("D:\\课件\\大三下\\软件工程\\lab1\\2021111662-lab1\\code\\src\\text.txt");
        clickOn("Load File");
        clickOn("Calculate shortest path");
        TextField InputField = lookup("#input").query();
        clickOn(InputField).write("seek,to");
        TextField resultField = lookup("#result").query();
        clickOn("Execute");
        FxAssert.verifyThat(resultField, TextInputControlMatchers.hasText("Shortest path(s) from seek to to:4"));
    }
    @Test
    public void testCalculateShortestPath_2() {
        TextField filePathField = lookup("#filePath").query();
        clickOn(filePathField).write("D:\\课件\\大三下\\软件工程\\lab1\\2021111662-lab1\\code\\src\\text.txt");
        clickOn("Load File");
        clickOn("Calculate shortest path");
        TextField InputField = lookup("#input").query();
        clickOn(InputField).write("New");
        TextField resultField = lookup("#result").query();
        clickOn("Execute");
        FxAssert.verifyThat(resultField, TextInputControlMatchers.hasText("Please input two words connected with comma!"));
    }
    @Test
    public void testCalculateShortestPath_3() {
        TextField filePathField = lookup("#filePath").query();
        clickOn(filePathField).write("D:\\课件\\大三下\\软件工程\\lab1\\2021111662-lab1\\code\\src\\text.txt");
        clickOn("Load File");
        clickOn("Calculate shortest path");
        TextField InputField = lookup("#input").query();
        clickOn(InputField).write("New like");
        TextField resultField = lookup("#result").query();
        clickOn("Execute");
        FxAssert.verifyThat(resultField, TextInputControlMatchers.hasText("Please input two words connected with comma!"));
    }
    @Test
    public void testCalculateShortestPath_4() {
        TextField filePathField = lookup("#filePath").query();
        clickOn(filePathField).write("D:\\课件\\大三下\\软件工程\\lab1\\2021111662-lab1\\code\\src\\text.txt");
        clickOn("Load File");
        clickOn("Calculate shortest path");
        TextField InputField = lookup("#input").query();
        clickOn(InputField).write("New,and,please");
        TextField resultField = lookup("#result").query();
        clickOn("Execute");
        FxAssert.verifyThat(resultField, TextInputControlMatchers.hasText("Please input two words connected with comma!"));
    }

}