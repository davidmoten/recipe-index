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
				.doOnNext(s -> {
					if (s.length() == 1 && Character.isLetter(s.charAt(0))) {
						letter.set(s.charAt(0));
					}
				}).filter(s -> s.length() > 1) //
				.map(s -> {
					String result;
					if (Character.isLowerCase(s.charAt(0))|| s.charAt(0) == '&') {
						if (s.contains("..."))
							result = previous + " " + s;
						else
							result = "[HEADING] " + s;
					} else if (s.contains("...")) {
						result = s;
					} else {
						result = null;
					}
					previous.set(s);
					return result;
				}) //
				.filter(s -> s != null) //
				.doOnNext(s -> System.out.println(letter.get() + ": " + s)) //
				.subscribe();
	}
}
