package ch.uzh.ifi.access.student.model;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@SuppressWarnings("unused")
@Value
@Data
@Builder
public class SubmissionEvaluation {

	public static SubmissionEvaluation NO_SUBMISSION = new SubmissionEvaluation(new Points(0, 0), 0, Instant.MIN,
			Collections.emptyList());

	private Points points;

	private int maxScore;

	private Instant timestamp;

	private List<String> hints;

	@JsonProperty
	public boolean hasSubmitted() {
		return !NO_SUBMISSION.equals(this);
	}

	public double getScore() {
		if (points.getMax() == 0) {
			return 0.0;
		}
		return Math.round((points.getCorrect() / (double) points.getMax() * maxScore) * 4) / 4d;
	}

	public List<String> getHints() {
		return hints != null && hints.size() > 1 ? Arrays.asList(hints.get(0)) : hints;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Points {
		private int correct;
		private int max;

		public boolean isEverythingCorrect() {
			return correct == max;
		}
	}
}
