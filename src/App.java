import java.util.HashMap;

public class App {
    static final char[] alphabet = "абвгдеёжзийклмнопрстуфхцшщъыьэюя.,”:-!?\s".toCharArray();
    static final HashMap<Character, Integer> hashMap = new HashMap<>();
    static String text = "Привет! как ты?";
    static int offset = 60;

    public static void main(String[] args) {
        generateMap();
        String encodedText = encode(text);
        System.out.println("encoded: " + encodedText);

        String decodedText = decode(encodedText);
        System.out.println("decoded: " + decodedText);
    }

    private static void generateMap() {
        for (int i = 0; i < alphabet.length; i++) {
            hashMap.put(alphabet[i], i);
        }
    }

    private static String decode(String text) {
        int offsetForDecode = alphabet.length - offset % alphabet.length;
        return getTextWithOffset(text, offsetForDecode);
    }

    private static String encode(String text) {
        return getTextWithOffset(text, offset);
    }

    private static String getTextWithOffset(String text, int offset) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            int charIndexWithOffset = (hashMap.get(Character.toLowerCase(currentChar)) + offset) % alphabet.length;
            builder.append(alphabet[charIndexWithOffset]);
        }

        return builder.toString();
    }
//    todo считать с файла
//    todo записать в файл
}
