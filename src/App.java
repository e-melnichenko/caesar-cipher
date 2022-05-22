import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    static final char[] alphabet = "абвгдеёжзийклмнопрстуфхцшщъыьэюя.,”:-!?\s".toCharArray();
    static final HashMap<Character, Integer> hashMap = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);
    static String mode;
    static String text;
    static Path filePath;
    static int offset;

    static final String ENCODE_TEXT = "encode";
    static final String DECODE_TEXT = "decode";

    public static void main(String[] args) {
        getText();
        setupMode();
        generateMap();

        if(mode.equals(ENCODE_TEXT)) {
            encode();
        } else if(mode.equals(DECODE_TEXT)) {
            decode();
        }
//        System.out.println("encoded: " + encodedText);

//        String decodedText = decode(encodedText);
//        System.out.println("decoded: " + decodedText);
    }

    private static void setupMode() {
        while(true) {
            System.out.println("Введите " + ENCODE_TEXT + " для кодирования или " + DECODE_TEXT + " для расшифровки");
//            String answer = scanner.nextLine();
            String answer = ENCODE_TEXT;
            if(ENCODE_TEXT.equals(answer) || DECODE_TEXT.equals(answer)) {
                mode = answer;
                break;
            }
        }

        offset = 4;
//        System.out.println("Введите ключ");
//
//        while(!scanner.hasNextInt()) {
//            System.out.println("Недопустимый формат ключа, попробуйте еще раз");
//            scanner.next();
//        }
//
//        offset = scanner.nextInt();
    }

    private static void getText() {
        while(true) {
            System.out.println("Введите путь к файлу или exit для выхода");
//            String pathString = scanner.nextLine();
            String pathString = "C:\\javarush-test\\file1.txt";
            if("exit".equals(pathString)) break;

            filePath = Path.of(pathString);

            try {
                text = Files.readString(filePath);
                break;
            } catch (IOException e) {
                System.out.println("Что-то пошло не так...");
                e.printStackTrace();
            }
        }
    }

    private static void generateMap() {
        for (int i = 0; i < alphabet.length; i++) {
            hashMap.put(alphabet[i], i);
        }
    }

    private static void decode() {
        int offsetForDecode = alphabet.length - offset % alphabet.length;
        String result = getTextWithOffset(text, offsetForDecode);
        writeFileWithPostfix(result, "decoded");
    }

    private static void encode() {
        String result = getTextWithOffset(text, offset);
        writeFileWithPostfix(result, "encoded");
    }

    private static void writeFileWithPostfix(String text, String postfix) {
        try {
            String[] separatedFileName = filePath
                .getFileName()
                .toString()
                .split("\\.");
//            todo builder or cycle
            String newFileName = separatedFileName[0] + "_" + postfix + "." + separatedFileName[1];
            Path newFilePath = filePath.resolveSibling(newFileName);
            Files.writeString(newFilePath, text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Файл создан по адресу " + newFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getTextWithOffset(String text, int offset) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char currentChar = Character.toLowerCase(text.charAt(i));

            if(!hashMap.containsKey(currentChar)) continue;

            int charIndexWithOffset = (hashMap.get(currentChar) + offset) % alphabet.length;
            builder.append(alphabet[charIndexWithOffset]);
        }

        return builder.toString();
    }
//    todo считать с файла
//    todo записать в файл
}
