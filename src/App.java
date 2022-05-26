import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    static final char[] alphabet = "абвгдеёжзийклмнопрстуфхцшщъыьэюя.,”:-!?\s".toCharArray();
    static final HashMap<Character, Integer> hashMap = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);
    static String mode;
    static Boolean isBruteForce;
    static String text;
    static Path filePath;
    static int offset;

    static final String ENCODE_TEXT = "encode";
    static final String DECODE_TEXT = "decode";

    public static void main(String[] args) {
        getText();
        setupMode();
        generateMap();

        if(isBruteForce) {
            bruteForce();
        } else if(mode.equals(ENCODE_TEXT)) {
            encode();
        } else if(mode.equals(DECODE_TEXT)) {
            decode();
        }
    }

    private static void bruteForce() {
        Pattern pattern = Pattern.compile("[.,!?]\\s[а-яА-Я]|\\sи\\s|\\sв\\s|\\sне\\s|\\sна\\s");
        Pattern antiPattern = Pattern.compile("ёё|шш|щщ|ъъ|ыы|ьь|ээ|\\sь|\\sъ|\\sы|ъ\\s|ъе|ъё|ъю|ъя");
        int fileCounter = 0;

        for (int i = 1; i <= alphabet.length; i++) {
            String textWithOffset = getTextWithOffset(text, i);

            if(antiPattern.matcher(textWithOffset).find()) continue;

            int coincidenceCounter = 0;
            Matcher matcher = pattern.matcher(textWithOffset);

            while(matcher.find()) {
                coincidenceCounter++;
            }

            if(coincidenceCounter >= 1) {
                writeFileWithPostfix(textWithOffset, "weight_" + coincidenceCounter + "_id_" + fileCounter);
                fileCounter++;
            }
        }

        System.out.println("Всего создано файлов: " + fileCounter);
        System.out.println("Чем больше показатель weight в имени файла, тем больше шанс успешной расшифровки.");
    }

    private static void setupMode() {
        while(true) {
            System.out.println("Хотите использовать режим brute force? (yes/no)");
            String answer = scanner.nextLine();

            if("yes".equals(answer)) {
                isBruteForce = true;
                return;
            } else if("no".equals(answer)) {
                isBruteForce = false;
                break;
            }
        }

        while(true) {
            System.out.println("Введите " + ENCODE_TEXT + " для кодирования или " + DECODE_TEXT + " для расшифровки");
            String answer = scanner.nextLine();

            if(ENCODE_TEXT.equals(answer) || DECODE_TEXT.equals(answer)) {
                mode = answer;
                break;
            }
        }

        System.out.println("Введите ключ");

        while(!scanner.hasNextInt()) {
            System.out.println("Недопустимый формат ключа, попробуйте еще раз");
            scanner.next();
        }

        offset = scanner.nextInt();
    }

    private static void getText() {
        while(true) {
            System.out.println("Введите путь к файлу или exit для выхода");
            String pathString = scanner.nextLine();

            if("exit".equals(pathString)) break;

            filePath = Path.of(pathString);

            try {
                text = Files.readString(filePath);
                break;
            } catch (IOException e) {
                System.out.println("Ошибка чтения файла " + filePath);
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
        int offsetForEncode = offset < 0 ? alphabet.length + offset % alphabet.length : offset;
        String result = getTextWithOffset(text, offsetForEncode);
        writeFileWithPostfix(result, "encoded");
    }

    private static void writeFileWithPostfix(String text, String postfix) {
        try {
            String[] separatedFileName = filePath
                .getFileName()
                .toString()
                .split("\\.");

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
}
