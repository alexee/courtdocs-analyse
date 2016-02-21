package ee.alexn.courtanalyse.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class StopWordService {

	private Set<String> stopWords;

	public boolean isStopWord(String word) {
		if (stopWords == null) {
			stopWords = loadStopWords();
		}
		return stopWords.contains(word.toLowerCase());
	}

	private Set<String> loadStopWords() {
		ClassPathResource stopWordsResource = new ClassPathResource("stopwords.txt");
		try {
			return new HashSet<String>(IOUtils.readLines(stopWordsResource.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	
}
