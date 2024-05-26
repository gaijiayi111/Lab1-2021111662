import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Main extends Application {
    // 路径信息类，包含权重和颜色信息
    static class PathInfo {
        Integer weight;
        ArrayList<String> color;
        PathInfo(Integer weight, String color) {
            this.weight = weight;
            this.color = new ArrayList<>();
            this.color.add(color);
        }
    }
    public static class ShortestPathResult {//保存计算出的最短路径的结果
        private List<List<String>> paths;
        private int shortestDistance;
        public ShortestPathResult(List<List<String>> paths, int shortestDistance) {
            this.paths = paths;
            this.shortestDistance = shortestDistance;
        }
        public List<List<String>> getPaths() {
            return paths;
        }
        public int getShortestDistance() {
            return shortestDistance;
        }
    }
    static Map<String, Map<String, PathInfo>> directedGraph = new HashMap<>();
    private static Random random = new Random();
    private static List<String> visitedEdges = new ArrayList<>();
    private static List<String> traversalPath = new ArrayList<>();
    private static List<String> contents = new ArrayList<>();
    private Thread traversalThread;
    private static TextField processField = new TextField();
    private static Label prompt = new Label();
    private static volatile boolean stopTraversal;// 用于控制函数是否停止的标志
    @Override
    public void start(Stage primaryStage) {
        // 创建文件路径输入框和按钮
        TextField filePathField = new TextField();
        Button loadFileButton = new Button("Load File");
        loadFileButton.setOnAction(event -> {
            String filePath = filePathField.getText();
            if (!filePath.isEmpty()) {
                try {
                    buildDirectedGraph(filePath);
                    openFunctionButtonsWindow(primaryStage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                prompt.setText("Please enter a valid file path!");
            }
        });
        // 创建布局
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(filePathField, loadFileButton, prompt);
        // 创建场景
        Scene scene = new Scene(root, 600, 400);
        // 设置舞台
        primaryStage.setTitle("Graph");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void openFunctionButtonsWindow(Stage primaryStage) {
        // 创建第二个页面的按钮
        Button backButton = new Button("back");
        backButton.setOnAction(event -> {
            // 关闭第二个页面并显示第一个页面
            Stage secondStage = (Stage) backButton.getScene().getWindow();
            secondStage.close();
            primaryStage.show();
        });
        Stage functionButtonsStage = new Stage();

        Button button1 = new Button("Show directed graph");
        button1.setOnAction(event -> {
            convertToPic("graph.dot");
            showDirectedGraph();
        });

        Button button2 = new Button("Query Bridge Words");
        button2.setOnAction(event -> {
            openFunctionResultWindow("Please input two words connected by comma", 2);
        });

        Button button3 = new Button("Generate new text");
        button3.setOnAction(event -> {
            openFunctionResultWindow("Please input a sentence", 3);
        });

        Button button4 = new Button(" Calculate shortest path");
        button4.setOnAction(event -> {
            openFunctionResultWindow("Please input two words connected by comma", 4);
        });

        Button button5 = new Button("Random Walk");
        button5.setOnAction(event -> {
            openFunctionResultWindow("Random Walk", 5);
        });

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(button1, button2, button3, button4, button5, backButton);
        Scene scene = new Scene(root, 600, 300);
        functionButtonsStage.setScene(scene);
        functionButtonsStage.setTitle("Function Buttons");
        primaryStage.hide();//隐藏主界面
        functionButtonsStage.show();
    }
    private String execute(String input, int funNo) {//执行函参数的功能函数
        String [] parameter = input.split(",");
        switch (funNo) {
            case 2:
                String result_2 = queryBridgeWords(parameter[0], parameter[1]);
                return result_2;
            case 3:
                String result_3 = generateNewText(input);
                return result_3;
            case 4:
                String result_4 = calcShortestPath(parameter[0], parameter[1]);
                String[] words = result_4.split("[^a-zA-Z]+");
                if(!words[0].equals("No")){
                    convertToPic("graph.dot");
                    showDirectedGraph();
                }
                return result_4;
            default:
                return "False";
        }
    }
    private void openFunctionResultWindow(String prompt, int funNo){
        Stage primaryStage = new Stage();
        // 创建文本框和标签
        TextField inputField = new TextField();
        TextField resultField = new TextField();
        resultField.setEditable(false); // 设置结果文本框为不可编辑
        processField.setEditable(false); // 设置结果文本框为不可编辑
        // 创建布局
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        if (funNo!=5){
            // 创建标签
            Label inputLabel = new Label(prompt);
            Label resultLabel = new Label("Result:");

            // 创建按钮
            Button calculateButton = new Button("Execute");
            calculateButton.setOnAction(event -> {
                // 获取输入值，并在结果文本框中显示
                String input = inputField.getText();
                String result = execute(input, funNo);
                resultField.setText(result); // 显示结果
            });
            root.getChildren().addAll(inputLabel, inputField, resultLabel, resultField, calculateButton) ;
            // 设置舞台
            Scene scene = new Scene(root, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Function");
            primaryStage.show();
        }else{
            Button startButton = new Button("Execute");
            Button stopButton = new Button("Stop");
            Label resultLabel = new Label("Result:");
            Label processLabel = new Label("Processing:");
            startButton.setOnAction(event -> {
                processField.setText(null);
                stopTraversal = false;
                // 禁用开始按钮
                startButton.setDisable(true);
                // 启用停止按钮
                stopButton.setDisable(false);
                traversalThread = new Thread(() -> randomWalk());
                traversalThread.start();
            });
            stopButton.setOnAction(event -> {
                // 停止遍历
                stopTraversal = true;
                // 禁用停止按钮
                stopButton.setDisable(true);
                // 启用开始按钮
                startButton.setDisable(false);
                String text = String.join(" ",traversalPath);
                resultField.setText(text);
            });
            root.getChildren().addAll(startButton, stopButton, resultLabel, resultField, processLabel, processField) ;
            // 设置舞台
            Scene scene = new Scene(root, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Function");
            primaryStage.show();

        }

    }
    public static void convertToPic(String dotFilePath) {
        /*
         * 函数名：convertToPic
         * 参数：dotFilePath：图转化为dot文件的存储路径
         * 返回类型：void
         * 功能：将嵌套链表的图转化为dot文件存储，便于后续图的可视化
         *  */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dotFilePath))) {
            writer.write("digraph G {\n");
            // 输出节点
            for (String node : directedGraph.keySet()) {
                writer.write("  \"" + node + "\";\n");
            }
            // 输出边
            for (String source : directedGraph.keySet()) {
                Map<String, PathInfo> edges = directedGraph.get(source);
                for (String target : edges.keySet()) {
                    int weight = edges.get(target).weight;
                    for (String item:edges.get(target).color){
                        writer.write("  \"" + source + "\" -> \"" + target + "\" [label=\"" + weight + "\"" + "," + "color=\"" + item + "\"];\n");
                    }
                }
            }
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 使用 Graphviz 将 DOT 文件转换为图片文件
        ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", "-o", "graph.png", dotFilePath);
        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void buildDirectedGraph(String filePath) throws IOException {
        /*
         * 函数名：buildDirectedGraph
         * 参数：待分析的文件路径
         * 功能：将文件中的文本转化为嵌套哈希表存储
         *  */
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
                contentBuilder.append(System.lineSeparator());
            }
        }
        String[] words = contentBuilder.toString().split("[^a-zA-Z]+");
        for (int i = 0; i < words.length - 1; i++) {
            String currentWord = words[i].toLowerCase();
            String nextWord = words[i + 1].toLowerCase();
            // 更新节点之间的边权重
            if (!currentWord.isEmpty() && !nextWord.isEmpty()) {
                directedGraph.computeIfAbsent(currentWord, k -> new HashMap<>())//将每个节点的哈希值初始化为一个空的邻接表
                        .merge(nextWord, new PathInfo(1,"black"), (oldPathInfo, newPathInfo) -> {
                            oldPathInfo.weight += newPathInfo.weight;
                            return oldPathInfo;
                        });//嵌套的哈希表，最里层哈希表为记录两个节点相邻次数（即边的权重）
            }
        }
    }
    public static void showDirectedGraph() {
        Stage primaryStage = new Stage();
        // 创建 ImageView 组件
        ImageView imageView = new ImageView();
        imageView.setImage(null);
        try {
            // 加载 PNG 文件并设置给 ImageView
            Image image = new Image(new FileInputStream("graph.png"));
            imageView.setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageView.setPreserveRatio(true);

        // 创建布局
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER); // 将对齐方式设置为居中
        root.getChildren().add(imageView);

        // 创建场景
        Scene scene = new Scene(root, 800, 800);
        imageView.fitWidthProperty().bind(scene.widthProperty());
        imageView.fitHeightProperty().bind(scene.heightProperty());

        // 设置舞台
        primaryStage.setScene(scene);
        primaryStage.setTitle("Directed Graph");
        primaryStage.show();
    }
    public static String queryBridgeWords(String word1, String word2) {
        /*
         * 函数名：queryBridgeWords
         * 参数：Map<String, Map<String, Integer>> graph：图的邻接表，word1, word2：待查询的词
         * 返回类型：String
         * 功能：查找两个词的桥接词，并输出相应结果
         *  */
        // 检查输入的单词是否在图中出现
        if (!directedGraph.containsKey(word1) || !directedGraph.containsKey(word2)) {
            ArrayList<String> wordsNotExit = new ArrayList<>();
            if (!directedGraph.containsKey(word1)){
                wordsNotExit.add(word1);
            }
            if (!directedGraph.containsKey(word2)){
                wordsNotExit.add(word2);
            }
            if (wordsNotExit.size()!= 2)
                return "No " + (directedGraph.containsKey(word1) ? "\"" + word2 + "\"" : "\"" + word1 + "\"") + " in the graph!";
            else
                return "No " + "\"" + word1 + "\"" + "and" + "\"" + word2 + "\"" + " in the graph!";
        }
        List<String> bridgeWords = new ArrayList<>();
        // 遍历 word1 的邻居，查找桥接词
        Map<String, PathInfo> neighborsOfWord1 = directedGraph.get(word1);
        for (String bridgeWord : neighborsOfWord1.keySet()) {
            if (directedGraph.containsKey(bridgeWord) && directedGraph.get(bridgeWord).containsKey(word2)) {
                bridgeWords.add(bridgeWord);
            }
        }
        // 如果不存在桥接词，则返回相应的提示
        if (bridgeWords.isEmpty()) {
            return "No bridge words from \"" + word1 + " \" to \" " + word2 + "\" !";
        }
        // 存在桥接词，将结果转换为字符串返回
        StringBuilder result = new StringBuilder("The bridge words from \" " + word1 + "\" to \"" + word2 + " \" are: \"");
        for (int i = 0; i < bridgeWords.size(); i++) {
            result.append(bridgeWords.get(i));
            if (i < bridgeWords.size() - 1) {
                result.append("\", \"");
            } else {
                result.append("\".");
            }
        }
        return result.toString();
    }
    public static String generateNewText(String inputText) {
        /*
         * 函数名：generateNewText
         * 参数：String inputText：用户输入的文本
         * 返回类型：String
         * 功能：根据bridge word生成新文本
         *  */
        // 将输入文本按照单词分割
        String[] words = inputText.split("[^a-zA-Z]+");
        // 构建处理后的文本
        StringBuilder result = new StringBuilder();
        // 对每对相邻单词进行处理
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            if (directedGraph.containsKey(word1) && directedGraph.containsKey(word2)) {// 检查相邻单词是否在图中出现
                String [] line = queryBridgeWords(word1, word2).split(" ");
                System.out.println(queryBridgeWords(word1, word2));
                if (!line[0].equals("No")){
                    List<String> bridgeWords = Arrays.asList(queryBridgeWords(word1, word2).replace("\"", "").replace(".", "").split(":")[1].trim().split("\\s*,\\s*"));
                    // 如果存在桥接词，则随机选择一个插入
                    Random random = new Random();
                    String bridgeWord = bridgeWords.get(random.nextInt(bridgeWords.size()));
                    result.append(word1).append(" ").append(bridgeWord).append(" ");
                }else{
                    result.append(word1).append(" ");
                }
            } else {
                result.append(word1).append(" ");// 单词不在图中出现，保持不变
            }
        }
        // 处理最后一个单词
        if (words.length > 0) {
            result.append(words[words.length - 1]);
        }
        return result.toString();
    }
    public static String calcShortestPath(String word1, String word2){
        List<List<String>> shortestPaths = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        List<String> currentPath = new ArrayList<>();
        currentPath.add(word1);
        traversalPath.clear();
        Instant start = Instant.now();
        int shortestDistance = dijkstraShortestPaths(word1, word2, visited, currentPath, shortestPaths);
        ShortestPathResult result = new ShortestPathResult(shortestPaths, shortestDistance);
        for (String source : directedGraph.keySet()) {
            Map<String, PathInfo> edges = directedGraph.get(source);
            for (String target : edges.keySet()) {
                edges.get(target).color.clear();
                edges.get(target).color.add("black");
            }
        }
        if (result.getPaths().isEmpty()) {
            return "No path from " + word1 + " to " + word2 + " exists.";
        } else {
            for (List<String> shortestPath : shortestPaths){
                String []color = {"red", "blue", "brown", "green", "pink", "purple", "orange"};
                System.out.println(shortestPaths);
                for (int i = 0; i < shortestPath.size() - 1; i++) {
                    String source = shortestPath.get(i);
                    String target = shortestPath.get(i + 1);
                    Map<String, PathInfo> edges = directedGraph.get(source);
                    if (edges != null && edges.containsKey(target)) {
                        edges.get(target).color.remove("black");
                        edges.get(target).color.add(color[shortestPaths.indexOf(shortestPath)% color.length]);
                    }
                }
            }
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            return "Shortest path(s) from " + word1 + " to " + word2 + ":" + shortestDistance + ",Execution time: " + duration.toMillis() + " ms";
        }
    }
    public static int dijkstraShortestPaths(String currentNode, String endNode, Set<String> visited, List<String> currentPath, List<List<String>> shortestPaths) {
        if (currentNode.equals(endNode)) {
            int currentDistance = calculatePathDistance(currentPath);
            if (shortestPaths.isEmpty() || currentDistance < calculatePathDistance(shortestPaths.get(0))) {
                shortestPaths.clear();
                shortestPaths.add(new ArrayList<>(currentPath));
            } else if (currentDistance == calculatePathDistance(shortestPaths.get(0))) {
                shortestPaths.add(new ArrayList<>(currentPath));
            }
            return currentDistance;
        } else {
            visited.add(currentNode);
            Map<String, PathInfo> neighbors = directedGraph.get(currentNode);
            if (neighbors != null) {
                int shortestDistance = Integer.MAX_VALUE;
                for (String neighbor : neighbors.keySet()) {
                    if (!visited.contains(neighbor)) {
                        currentPath.add(neighbor);
                        int newDist = dijkstraShortestPaths(neighbor, endNode, visited, currentPath, shortestPaths);
                        shortestDistance = Math.min(shortestDistance, newDist);
                        currentPath.remove(currentPath.size() - 1);
                    }
                }
                visited.remove(currentNode);
                return shortestDistance;
            }
            return Integer.MAX_VALUE;
        }
    }
    public static int calculatePathDistance(List<String> path) {//计算路径长度
        int distance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String source = path.get(i);
            String target = path.get(i + 1);
            Map<String, PathInfo> edges = directedGraph.get(source);
            if (edges != null && edges.containsKey(target)) {
                distance += edges.get(target).weight;
            }
        }
        return distance;
    }
    public static String randomWalk(){
        List<String> nodes = new ArrayList<>(directedGraph.keySet());
        String startNode = nodes.get(random.nextInt(nodes.size()));//随机选取起始节点
        visitedEdges.clear();
        traversalPath.clear();
        // 标记当前节点为已访问
        traversalPath.add(startNode);
        String currentNode = startNode;
        String nextNode;
        Map<String, PathInfo> neighbors = directedGraph.get(startNode);
        while ((!stopTraversal) && neighbors != null && !neighbors.isEmpty()) {
            List<String> neighborList = new ArrayList<>(neighbors.keySet());
            nextNode = neighborList.get(random.nextInt(neighborList.size()));//随机选取一个邻居进行遍历
            System.out.println(nextNode);
            if (!(visitedEdges.contains(currentNode +  "->" + nextNode))){
                visitedEdges.add(currentNode +  "->" + nextNode);
                System.out.println(visitedEdges);
                traversalPath.add(nextNode);
                neighbors = directedGraph.get(nextNode);
                currentNode = nextNode;
            }else{
                traversalPath.add(nextNode);
                System.out.println(currentNode+','+nextNode);
                System.out.println(visitedEdges);
                System.out.println("repeat edges");
                break;

            }
            try {
                Thread.sleep(100); // 使图的遍历变慢
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("No neighbors");
        if (!stopTraversal)
            processField.setText("finish!");
        String text = String.join(" ",traversalPath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
                writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
