import java.util.concurrent.atomic.AtomicReference;

import com.github.davidmoten.rx.Strings;
import com.github.davidmoten.rx.Transformers;

public class Main {

	public static class Recipe {
		final String letter;
		final String name;
		final int year;
		final String month;
		final int page;

		public Recipe(String letter, String name, int year, String month, int page) {
			this.letter = letter;
			this.name = name;
			this.year = year;
			this.month = month;
			this.page = page;
		}

	}

	public static void main(String[] args) {
		AtomicReference<Character> letter = new AtomicReference<>();
		AtomicReference<String> previous = new AtomicReference<String>();

		Strings //
				.from(Main.class.getResourceAsStream("/2012.txt")) //
				.compose(Transformers.split("\n")) //
				.map(s -> s.trim()) //
				.filter(s -> s.length() > 0) //
				.filter(s -> !Character.isDigit(s.charAt(0))) //
				.filter(s -> !s.equals("recipe index.")) //
				.doOnNext(s -> {
					if (s.length() == 1 && Character.isLetter(s.charAt(0))) {
						letter.set(s.charAt(0));
					}
				}).filter(s -> s.length() > 1) //
				.map(s -> {
					String result;
					boolean closed = false;
					char ch = s.charAt(0);
					if (Character.isLowerCase(ch) || ch == '&' || ch == '(' || ch == '\'') {
						if (hasPageRef(s)) {
							result = previous + " " + s;
							closed = true;
						} else if (s.contains(", see")) {
							result = "[SEE] " + s;
							closed = true;
						} else {
							result = "[HEADING] " + s;
							closed = true;
						}
					} else if (hasPageRef(s)) {
						result = s;
						closed = true;
					} else {
						result = null;
					}
					if (closed)
						previous.set("");
					else
						previous.set((previous + " " + s).trim());
					return result;
				}) //
				.filter(s -> s != null) //
				.doOnNext(s -> System.out.println(letter.get() + ": " + s)) //
				.subscribe();
	}

	private static boolean hasPageRef(String s) {
		return s.contains(":");
	}
}
