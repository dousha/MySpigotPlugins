package tech.dsstudio.minecraft.dialog.sessions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import tech.dsstudio.minecraft.dialog.SessionContext;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SurveySessionContext implements SessionContext {
	public class SurveyEntry {
		public SurveyEntry(String question) {
			this.question = question;
			this.formatter = (msg) -> true;
		}

		public SurveyEntry(String question, Predicate<String> formatter) {
			this.question = question;
			this.formatter = formatter;
		}

		public SurveyEntry(ConfigurationSection section) {
			this.question = section.getString("question");
			this.formatter = (msg) -> true;
		}

		public boolean answer(String answer) {
			if (this.formatter.test(answer)) {
				this.answer = answer;
				return true;
			} else {
				return false;
			}
		}

		public String getQuestion() {
			return question;
		}

		public String getAnswer() {
			return answer;
		}

		private String question;
		private String answer;
		private Predicate<String> formatter;
	}

	public SurveySessionContext(Consumer<List<String>> callback) {
		this.callback = callback;
		this.entries = null;
	}

	public SurveySessionContext(Consumer<List<String>> callback, List<SurveyEntry> entries) {
		this.entries = entries;
		this.callback = callback;
	}

	@Override
	public void initialize(Player player) {
		this.uuid = player.getUniqueId();
		player.sendMessage(entries.get(currentIndex).question);
	}

	@Override
	public void terminate(UUID uuid) {
		callback.accept(null);
	}

	@Override
	public boolean advance(Player player, String msg) {
		if (entries.get(currentIndex).formatter.test(msg)) {
			++currentIndex;
			if (currentIndex == entries.size()) {
				return false;
			} else {
				player.sendMessage(entries.get(currentIndex).question);
				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public boolean disclose() {
		return false;
	}

	private Consumer<List<String>> callback;
	private List<SurveyEntry> entries;
	private int currentIndex = 0;
	private UUID uuid;
}
